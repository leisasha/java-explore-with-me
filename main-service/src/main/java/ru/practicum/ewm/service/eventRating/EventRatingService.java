package ru.practicum.ewm.service.eventRating;

import ru.practicum.ewm.dto.eventRating.EventRatingDto;

import java.util.List;

public interface EventRatingService {
    void addLike(Long userId, Long eventId);

    void addDislike(Long userId, Long eventId);

    List<EventRatingDto> getTopEvents(int limit);

    void deleteRatingByUserAndEvent(Long userId, Long eventId);
}
