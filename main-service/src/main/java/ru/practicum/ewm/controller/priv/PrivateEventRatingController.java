package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.eventRating.EventRatingService;

@RestController
@RequestMapping("/users/{userId}/eventsRating/{eventId}")
@RequiredArgsConstructor
public class PrivateEventRatingController {
    private final EventRatingService eventRatingService;

    @PostMapping("/like")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> likeEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        eventRatingService.addLike(userId, eventId);
        return ResponseEntity.ok("{\"message\":\"Лайк добавлен\"}");
    }

    @PostMapping("/dislike")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> dislikeEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        eventRatingService.addDislike(userId, eventId);
        return ResponseEntity.ok("{\"message\":\"Дизлайк добавлен\"}");
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteRatingByUserAndEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        eventRatingService.deleteRatingByUserAndEvent(userId, eventId);
        return ResponseEntity.ok("{\"message\":\"Рейтинг для события убран\"}");
    }
}
