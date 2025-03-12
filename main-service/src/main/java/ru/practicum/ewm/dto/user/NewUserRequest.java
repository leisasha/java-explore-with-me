package ru.practicum.ewm.dto.user;

import jakarta.validation.constraints.Email;
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
public class NewUserRequest {
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    @Size(min = 6, max = 254)
    private String email;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 250)
    private String name;
}
