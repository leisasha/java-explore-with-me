package ru.practicum.ewm.service.participationRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.model.participationRequest.ParticipationRequest;
import ru.practicum.ewm.model.participationRequest.RequestStatus;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Нельзя отправить повторный запрос");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может подать запрос на участие");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Участвовать можно только в опубликованном событии");
        }

        if (event.getParticipantLimit() > 0 &&
                requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит участников мероприятия");
        }

        RequestStatus status = event.getRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED;
        if (event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }

        ParticipationRequest request = new ParticipationRequest(null, event, user, LocalDateTime.now(), status);
        requestRepository.save(request);

        return new ParticipationRequestDto(request.getId(), eventId, userId, request.getStatus().name(), request.getCreated());
    }

    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return requestRepository.findByRequesterId(userId)
                .stream()
                .map(request -> new ParticipationRequestDto(request.getId(), request.getEvent().getId(),
                        request.getRequester().getId(), request.getStatus().name(), request.getCreated()))
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ConflictException("User can only cancel their own requests");
        }

        request.setStatus(RequestStatus.CANCELED);
        requestRepository.save(request);

        return new ParticipationRequestDto(request.getId(), request.getEvent().getId(), request.getRequester().getId(),
                request.getStatus().name(), request.getCreated());
    }
}
