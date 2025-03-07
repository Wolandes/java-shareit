package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

public interface BookingService {
    Booking createBooking(Booking booking);
    Booking findById(Long id);
}
