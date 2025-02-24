package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TODO Sprint add-controllers.
 */
@Data
@EqualsAndHashCode
public class Item {
    long id;
    String name;
    String description;
    Boolean available;
    Long ownerId;
}
