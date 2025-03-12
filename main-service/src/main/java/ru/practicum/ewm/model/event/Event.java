package ru.practicum.ewm.model.event;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "annotation")
    private String annotation;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Column(name = "eventDate")
    private LocalDateTime eventDate;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participantLimit")
    private Integer participantLimit;

    @Column(name = "requestModeration")
    private Boolean requestModeration;

    @Column(name = "createdOn")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private EventState state;

    private Double latitude;
    private Double longitude;
}
