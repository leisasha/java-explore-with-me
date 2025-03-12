package ru.practicum.ewm.controller.priv;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@Positive @PathVariable Long userId, @Valid @RequestBody NewEventDto eventDto) {
        return eventService.createEvent(userId, eventDto);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest eventDto) {
        return eventService.updateEvent(userId, eventId, eventDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getUserEvents(
            @Positive @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return eventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getUserEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getUserEvent(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventParticipants(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return eventService.getEventParticipants(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult changeRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest updateRequest) {
        return eventService.changeRequestStatus(userId, eventId, updateRequest);
    }

}
