package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    private CategoryDto category;
    private UserShortDto initiator;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private String state;
    private LocationEventDto location;
    private Long confirmedRequests;
    private Long views;
}
