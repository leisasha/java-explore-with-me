package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank(message = "title обязательно")
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank(message = "annotation обязательно")
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotBlank(message = "description обязательно")
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull(message = "category обязательно")
    private Long category;

    @NotNull(message = "eventDate обязательно")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Boolean paid = false;

    @PositiveOrZero
    private Integer participantLimit = 0;

    private Boolean requestModeration = true;

    @NotNull(message = "location обязательно")
    private LocationEventDto location;
}
