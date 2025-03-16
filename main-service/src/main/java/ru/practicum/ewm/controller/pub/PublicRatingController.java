package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.eventRating.EventRatingDto;
import ru.practicum.ewm.dto.user.UserRatingDto;
import ru.practicum.ewm.service.eventRating.EventRatingService;
import ru.practicum.ewm.service.userRating.UserRatingService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PublicRatingController {
    private final UserRatingService userRatingService;
    private final EventRatingService eventRatingService;

    @GetMapping("/users/top")
    @ResponseStatus(HttpStatus.OK)
    public List<UserRatingDto> getTopUsers(@RequestParam(defaultValue = "10") int limit) {
        List<UserRatingDto> result = userRatingService.getTopUsers(limit);
        return result;
    }

    @GetMapping("/events/top")
    @ResponseStatus(HttpStatus.OK)
    public List<EventRatingDto> getTopEvents(@RequestParam(defaultValue = "10") int limit) {
        return eventRatingService.getTopEvents(limit);
    }
}
