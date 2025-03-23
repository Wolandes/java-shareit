package booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;

    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void createBooking_shouldReturnBookingDto_whenBookingIsCreated() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingCreateDto.setItemId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingMapper.toBookingFromBookingCreate(bookingCreateDto)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingDto(booking)).thenReturn(new BookingDto() {
            {
                setId(1L);
                setStart(booking.getStart());
                setEnd(booking.getEnd());
                setStatus(booking.getStatus());
                setItem(item);
                setBooker(user);
            }
        });

        BookingDto result = bookingService.createBooking(1L, bookingCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void getBooking_shouldReturnBookingDto_whenBookingExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(booking)).thenReturn(new BookingDto() {
            {
                setId(1L);
                setStart(booking.getStart());
                setEnd(booking.getEnd());
                setStatus(booking.getStatus());
                setItem(item);
                setBooker(user);
            }
        });

        BookingDto result = bookingService.getBooking(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        verify(bookingRepository).findById(1L);
    }

    @Test
    void getBooking_shouldThrowException_whenUserHasNoAccessToBooking() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "Another User", "another@example.com")));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingService.getBooking(2L, 1L);
        });

        assertEquals("Пользователь с id 2 не имеет доступа к бронированию id 1", exception.getMessage());
    }

    @Test
    void createBooking_shouldThrowNotFoundException_whenUserNotFound() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingCreateDto.setItemId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(1L, bookingCreateDto);
        });

        assertEquals("Не найден пользователь c id: 1", exception.getMessage());
    }
}
