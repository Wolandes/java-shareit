package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@EqualsAndHashCode
public class ItemRequestDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    User requester;
    String description;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    LocalDateTime created;
}
