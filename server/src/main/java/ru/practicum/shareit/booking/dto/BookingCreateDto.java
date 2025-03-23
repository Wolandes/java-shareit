package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
public class BookingCreateDto {
    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;

    public boolean isStartBeforeEnd() {
        return start.isBefore(end);
    }
}
