package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TODO Sprint add-controllers.
 */
@Data
@EqualsAndHashCode
public class ItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    String name;
    String description;
    Boolean available;
}
