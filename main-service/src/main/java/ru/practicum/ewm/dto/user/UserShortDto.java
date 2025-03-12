package ru.practicum.ewm.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {
    @NotNull(message = "id обязательно")
    private Long id;

    @NotBlank(message = "Имя обязательно")
    private String name;
}
