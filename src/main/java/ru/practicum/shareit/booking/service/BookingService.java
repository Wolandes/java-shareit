package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long idUser, BookingCreateDto bookingCreateDto);

    BookingDto getBooking(Long idUser, Long idBooking);

    BookingDto approveBooking(Long idUser, Long idBooking, Boolean approve);

    List<BookingDto> getAllBookings(Long idUser);
}
