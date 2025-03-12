package ru.practicum.ewm.controller.pub;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.service.event.EventService;
import ru.practicum.stats.EndpointHitDto;
import ru.practicum.stats.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(
            @RequestParam(required = false, defaultValue = "") String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false, defaultValue = "EVENT_DATE") String sort,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size,
            HttpServletRequest request) {

        statsClient.saveHit(new EndpointHitDto(
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now())
        );

        return eventService.getPublicEvents(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size
        );
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@Positive @PathVariable Long id, HttpServletRequest request) {
        statsClient.saveHit(new EndpointHitDto(
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now())
        );
        return eventService.getEventById(id);
    }
}
