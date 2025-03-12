package ru.practicum.ewm.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {
    @Size(min = 1, max = 50)
    private String title;
    private Boolean pinned;
    private List<Long> events;
}
