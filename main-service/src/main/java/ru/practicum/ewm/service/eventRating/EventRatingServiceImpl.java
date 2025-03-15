package ru.practicum.ewm.service.eventRating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.eventRating.EventRatingDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.eventRating.EventRating;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.eventRating.EventRatingId;
import ru.practicum.ewm.model.participationRequest.RequestStatus;
import ru.practicum.ewm.repository.EventRatingRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventRatingServiceImpl implements EventRatingService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventRatingRepository eventRatingRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    @Transactional
    public void addLike(Long userId, Long eventId) {
        if (!hasUserAttendedEvent(userId, eventId)) {
            throw new ValidationException("Лайк можно поставить только после посещения события.");
        }
        processRating(userId, eventId, true);
    }

    @Transactional
    public void addDislike(Long userId, Long eventId) {
        if (!hasUserAttendedEvent(userId, eventId)) {
            throw new ValidationException("Дизлайк можно поставить только после посещения события.");
        }
        processRating(userId, eventId, false);
    }

    @Transactional(readOnly = true)
    public List<EventRatingDto> getTopEvents(int limit) {
        List<Object[]> results = eventRatingRepository.findEventRatings();
        return results.stream()
                .limit(limit)
                .map(row -> {
                    Event event = (Event) row[0];
                    Long rating = (Long) row[1];
                    return new EventRatingDto(event.getId(), event.getTitle(), rating);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRatingByUserAndEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        eventRatingRepository.deleteByUserAndEvent(userId, eventId);

        event.setRating(0L);
        eventRepository.save(event);
    }

    private boolean hasUserAttendedEvent(Long userId, Long eventId) {
        return participationRequestRepository.existsByRequesterIdAndEventIdAndStatus(userId, eventId, RequestStatus.CONFIRMED);
    }

    private void processRating(Long userId, Long eventId, boolean isLike) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        EventRating rating;
        EventRatingId ratingId = new EventRatingId(userId, eventId);
        Optional<EventRating> existingRating = eventRatingRepository
                .findById(ratingId);
        if (existingRating.isPresent()) {
            rating = existingRating.get();
            rating.setLiked(isLike);
        } else {
            rating = new EventRating();
            rating.setId(ratingId);
            rating.setUser(user);
            rating.setEvent(event);
            rating.setLiked(isLike);
        }

        eventRatingRepository.save(rating);
        updateEventRating(event);
    }

    private void updateEventRating(Event event) {
        long likes = eventRatingRepository.countByEventAndLiked(event, true);
        long dislikes = eventRatingRepository.countByEventAndLiked(event, false);

        event.setRating(likes - dislikes);
        eventRepository.save(event);
    }
}
