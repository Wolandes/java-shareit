package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

@Data
@EqualsAndHashCode
public class UserUpdateDto {
    String name;
    @Email
    String email;
}
