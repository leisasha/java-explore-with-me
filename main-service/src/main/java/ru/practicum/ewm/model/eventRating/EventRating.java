package ru.practicum.ewm.model.eventRating;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.event.Event;

@Entity
@Table(name = "event_ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRating {
    @EmbeddedId
    private EventRatingId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "is_like")
    private boolean liked;
}
