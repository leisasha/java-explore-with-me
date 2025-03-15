package ru.practicum.ewm.dto.eventRating;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRatingDto {
    private Long id;
    private String title;
    private Long rating;
}
