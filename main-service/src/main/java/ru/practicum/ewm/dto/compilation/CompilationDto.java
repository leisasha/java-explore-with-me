package ru.practicum.ewm.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.dto.event.EventShortDto;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    @NotNull(message = "id обязательно")
    private Long id;

    @NotBlank(message = "title обязательно")
    private String title;

    @NotNull(message = "pinned обязательно")
    private Boolean pinned;

    private Set<EventShortDto> events;
}
