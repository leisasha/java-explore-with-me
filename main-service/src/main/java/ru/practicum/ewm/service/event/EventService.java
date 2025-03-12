package ru.practicum.ewm.service.event;

import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto eventDto);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest eventDto);

    List<EventShortDto> getUserEvents(Long userId, int from, int size);


    List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Boolean onlyAvailable, String sort,
                                        int from, int size);

    EventFullDto getEventById(Long id);


    List<EventFullDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest eventFullDto);

    EventFullDto getUserEvent(Long userId, Long eventId);

    List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest updateRequest);
}
