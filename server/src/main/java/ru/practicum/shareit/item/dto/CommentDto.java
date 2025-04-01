package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
public class CommentDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    String text;
    String authorName;
    LocalDateTime created;
}
