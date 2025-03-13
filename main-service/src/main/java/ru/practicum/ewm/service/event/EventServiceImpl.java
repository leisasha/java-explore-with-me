package ru.practicum.ewm.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.model.participationRequest.ParticipationRequest;
import ru.practicum.ewm.model.participationRequest.RequestStatus;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.stats.StatsClient;
import ru.practicum.stats.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatsClient statsClient;

    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto eventDto) {
        if (eventDto.getEventDate() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (eventDto.getEventDate().isBefore(now.plusHours(2))) {
                throw new ValidationException("Дата события должна быть минимум через 2 часа от текущего момента");
            }
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Event event = EventMapper.toEvent(eventDto, category, user);

        return EventMapper.toEventFullDto(eventRepository.save(event), 0L, 0L);
    }

    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest eventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Пользователь не является инициатором события");
        }
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Опубликованное событие нельзя редактировать");
        }
        if (eventDto.getEventDate() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (eventDto.getEventDate().isBefore(now.plusHours(2))) {
                throw new ValidationException("Дата события должна быть минимум через 2 часа от текущего момента");
            }
        }

        Optional.ofNullable(eventDto.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(eventDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(eventDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(eventDto.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(eventDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(eventDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(eventDto.getRequestModeration()).ifPresent(event::setRequestModeration);

        if (eventDto.getCategory() != null) {
            Category category = categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена"));
            event.setCategory(category);
        }

        if (eventDto.getLocation() != null) {
            event.setLatitude(eventDto.getLocation().getLat());
            event.setLongitude(eventDto.getLocation().getLon());
        }

        if (eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case "SEND_TO_REVIEW":
                    event.setState(EventState.PENDING);
                    break;
                case "CANCEL_REVIEW":
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new ValidationException("Некорректное значение stateAction");
            }
        }

        eventRepository.save(event);

        Long confirmedRequests = participationRequestRepository.countByEventIdAndStatus(
                event.getId(),
                RequestStatus.CONFIRMED
        );
        Long views = getEventViews(event.getId());

        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        Page<Event> page = eventRepository.findByInitiatorId(userId, pageable);

        return page.getContent()
                .stream()
                .map(event -> {
                    Long confirmedRequests = participationRequestRepository.countByEventIdAndStatus(
                            event.getId(),
                            RequestStatus.CONFIRMED
                    );
                    Long views = getEventViews(event.getId());
                    return EventMapper.toEventShortDto(event, confirmedRequests, views);
                })
                .collect(Collectors.toList());
    }

    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с ID=" + eventId + " не найдено или недоступно"));

        Long confirmedRequests = participationRequestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        Long views = getEventViews(eventId);

        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с ID=" + eventId + " не найдено или недоступно"));

        List<ParticipationRequest> requests = participationRequestRepository.findByEventId(eventId);

        return requests.stream()
                .map(participationRequest -> {
                    return new ParticipationRequestDto(
                            participationRequest.getId(),
                            participationRequest.getEvent().getId(),
                            participationRequest.getRequester().getId(),
                            participationRequest.getStatus().name(),
                            participationRequest.getCreated()
                    );
                })
                .collect(Collectors.toList());
    }


    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort,
                                               int from, int size) {

        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Дата окончания не может быть раньше даты начала");
        }

        Pageable pageable;
        if ("EVENT_DATE".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
        } else if ("VIEWS".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(from / size, size, Sort.by("views").descending());
        } else {
            pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        }

        Page<Event> eventsPage = eventRepository.findPublicEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable);

        List<Event> events = eventsPage.getContent();

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Long, Long> views = getViewsForEvents(events);

        return events.stream()
                .map(event -> EventMapper.toEventShortDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    public EventFullDto getEventById(Long id) {
        Event event = eventRepository.findPublicEventsById(id)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        Long confirmedRequests = participationRequestRepository.countByEventIdAndStatus(id, RequestStatus.CONFIRMED);
        Long views = getEventViews(id);

        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }


    public List<EventFullDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {

        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Дата окончания не может быть раньше даты начала");
        }

        List<EventState> eventStates = null;
        if (states != null) {
            eventStates = states.stream()
                    .map(EventState::valueOf)
                    .collect(Collectors.toList());
        }

        Pageable pageable = PageRequest.of((size > 0) ? from / size : 0, size, Sort.by("eventDate").ascending());

        Page<Event> eventsPage = eventRepository.findAdminEvents(
                users, eventStates, categories, rangeStart, rangeEnd, pageable);

        List<Event> events = eventsPage.getContent();

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Long, Long> views = getViewsForEvents(events);

        return events.stream()
                .map(event -> EventMapper.toEventFullDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Transactional
    public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest eventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        if (eventDto.getEventDate() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (eventDto.getEventDate().isBefore(now.plusHours(1))) {
                throw new ValidationException("Дата начала события должна быть минимум за 1 час до публикации");
            }
            event.setEventDate(eventDto.getEventDate());
        }

        if (eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case "PUBLISH_EVENT":
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;

                case "REJECT_EVENT":
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Событие можно отклонить, только если оно еще не опубликовано");
                    }
                    event.setState(EventState.CANCELED);
                    break;

                default:
                    throw new ValidationException("Некорректное значение stateAction");
            }
        }

        if (eventDto.getCategory() != null) {
            Category category = categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена"));
            event.setCategory(category);
        }

        Optional.ofNullable(eventDto.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(eventDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(eventDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(eventDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(eventDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(eventDto.getRequestModeration()).ifPresent(event::setRequestModeration);

        if (eventDto.getLocation() != null) {
            event.setLatitude(eventDto.getLocation().getLat());
            event.setLongitude(eventDto.getLocation().getLon());
        }

        eventRepository.save(event);

        Long confirmedRequests = participationRequestRepository.countByEventIdAndStatus(
                event.getId(),
                RequestStatus.CONFIRMED
        );
        Long views = getEventViews(event.getId());

        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Transactional
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено или недоступно"));

        List<ParticipationRequest> requests = participationRequestRepository.findByIdIn(updateRequest.getRequestIds());

        if (requests.isEmpty()) {
            throw new NotFoundException("Заявки на участие не найдены");
        }

        long confirmedCount = participationRequestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED.name()) && confirmedCount >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит одобренных заявок");
        }

        for (ParticipationRequest request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
            }
        }

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            updateRequest.setStatus(RequestStatus.CONFIRMED.name());
        }

        for (ParticipationRequest request : requests) {
            request.setStatus(RequestStatus.valueOf(updateRequest.getStatus()));
        }

        participationRequestRepository.saveAll(requests);


        if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED.name())) {
            long newConfirmedCount = confirmedCount + requests.size();
            if (newConfirmedCount >= event.getParticipantLimit()) {
                List<ParticipationRequest> pendingRequests = participationRequestRepository.findByEventIdAndStatus(eventId, RequestStatus.PENDING);
                for (ParticipationRequest pendingRequest : pendingRequests) {
                    pendingRequest.setStatus(RequestStatus.REJECTED);
                }
                participationRequestRepository.saveAll(pendingRequests);
            }
        }

        Set<ParticipationRequestDto> confirmedRequests = requests.stream()
                .filter(request -> request.getStatus() == RequestStatus.CONFIRMED)
                .map(request -> new ParticipationRequestDto(
                        request.getId(),
                        request.getEvent().getId(),
                        request.getRequester().getId(),
                        request.getStatus().name(),
                        request.getCreated()
                ))
                .collect(Collectors.toSet());

        Set<ParticipationRequestDto> rejectedRequests = requests.stream()
                .filter(request -> request.getStatus() == RequestStatus.REJECTED)
                .map(request -> new ParticipationRequestDto(
                        request.getId(),
                        request.getEvent().getId(),
                        request.getRequester().getId(),
                        request.getStatus().name(),
                        request.getCreated()
                ))
                .collect(Collectors.toSet());

        return new EventRequestStatusUpdateResult(
                confirmedRequests,
                rejectedRequests
        );
    }


    private Long getEventViews(Long eventId) {
        String start = LocalDateTime.now().minusYears(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<ViewStatsDto> stats = statsClient.getStats(start, end, List.of("/events/" + eventId), true);
        return stats.isEmpty() ? 0 : stats.get(0).getHits();
    }

    private Map<Long, Long> getConfirmedRequests(List<Event> events) {
        if (events.isEmpty()) return Collections.emptyMap();

        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<Object[]> results = participationRequestRepository.countConfirmedRequestsForEvents(eventIds);

        return results.stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
    }

    private Map<Long, Long> getViewsForEvents(List<Event> events) {
        if (events.isEmpty()) return Collections.emptyMap();

        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        String start = LocalDateTime.now().minusYears(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<ViewStatsDto> stats = statsClient.getStats(start, end, uris, true);

        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> Long.parseLong(stat.getUri().replace("/events/", "")),
                        ViewStatsDto::getHits
                ));
    }
}
