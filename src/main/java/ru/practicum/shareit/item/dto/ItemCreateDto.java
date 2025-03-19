package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ItemCreateDto {
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
}
