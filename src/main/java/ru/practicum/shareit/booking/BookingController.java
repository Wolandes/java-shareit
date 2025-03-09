package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long idUser,
                                    @Validated @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Добавлется бронирование от пользователя с id: " + idUser);
        return bookingService.createBooking(idUser, bookingCreateDto);
    }

    @GetMapping("/{id}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long idUser,
                                 @PathVariable Long id) {
        log.info("Получение бронирование с id: " + id);
        return bookingService.getBooking(idUser, id);
    }

    @GetMapping
    public List<BookingDto> getAllBooking(@RequestHeader("X-Sharer-User-Id") Long idUser) {
        log.info("Получение всех забронированных предметов от пользователя с id: " + idUser);
        return bookingService.getAllBookings(idUser);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long idUser,
                                     @PathVariable Long bookingId,
                                     @RequestParam boolean approved) {
        log.info("Добавляется бронирование с id: " + bookingId);
        return bookingService.approveBooking(idUser, bookingId, approved);
    }
}
