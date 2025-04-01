package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ItemCreateDto {
    String name;
    String description;
    Boolean available;
    Long requestId;
}
