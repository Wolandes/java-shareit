package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto createBooking(Long idUser, BookingCreateDto bookingCreateDto) {
        User user = checkCreateUser(idUser);
        Item item = checkCreateItem(bookingCreateDto.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Товар не доступен");
        }
        Booking booking = bookingMapper.toBookingFromBookingCreate(bookingCreateDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking = bookingRepository.save(booking);
        log.info("Добавилось бронирование с id: " + booking.getId());
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(Long idUser, Long idBooking) {
        checkCreateUser(idUser);
        Booking booking = checkCreateBooking(idBooking);
        if (!booking.getBooker().getId().equals(idUser) && !booking.getItem().getOwner().getId().equals(idUser)) {
            throw new ValidationException("Пользователь с id " + idUser + " не имеет доступа к бронированию id " + idBooking);
        }
        log.info("Найдено бронирование с id: " + booking.getId());
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookings(Long idUser) {
        checkCreateUser(idUser);
        return bookingMapper.toListBookingDto(bookingRepository.findAllByBookerId(idUser));
    }

    @Override
    public BookingDto approveBooking(Long idUser, Long idBooking, Boolean approve) {
        Booking booking = checkCreateBooking(idBooking);
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(idUser)) {
            throw new ValidationException("Пользователь не является владельцем вещи");
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
            item.setAvailable(false);
            log.info("Добавилось бронирование с id: " + booking.getId());
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            log.info("Бронирование с id: " + booking.getId() + " отклонено");
        }
        return bookingMapper.toBookingDto(booking);
    }

    private Booking checkCreateBooking(Long idBooking) {
        return bookingRepository.findById(idBooking)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено с id: " + idBooking));
    }

    private User checkCreateUser(Long idUser) {
        return userRepository.findById(idUser).orElseThrow(() -> new NotFoundException("Не найден пользователь c id: " + idUser));
    }

    private Item checkCreateItem(Long idItem) {
        return itemRepository.findById(idItem).orElseThrow(() -> new NotFoundException("Не найден предмет c id: " + idItem));
    }
}
