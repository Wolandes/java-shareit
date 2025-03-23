package ru.practicum.shareit.user.dto;

import lombok.*;

@Data
@EqualsAndHashCode
public class UserUpdateDto {
    String name;
    String email;
}
