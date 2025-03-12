package ru.practicum.ewm.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long id;
    @NotBlank(message = "Имя обязательно")
    @Size(min = 1, max = 50)
    private String name;
}
