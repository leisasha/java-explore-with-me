package ru.practicum.ewm.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    @NotBlank(message = "title обязательно")
    @Size(min = 1, max = 50)
    private String title;
    private Boolean pinned = false;
    private List<Long> events;
}
