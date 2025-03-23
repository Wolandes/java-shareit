package booking;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.BookingController;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    void getAllBooking_shouldReturnListOfBookingDtos_whenBookingsExist() throws Exception {
        Long userId = 1L;

        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        bookingDto1.setStart(LocalDateTime.now().plusDays(1));
        bookingDto1.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto1.setStatus(BookingStatus.WAITING);  // Устанавливаем статус

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        bookingDto2.setStart(LocalDateTime.now().plusDays(3));
        bookingDto2.setEnd(LocalDateTime.now().plusDays(4));
        bookingDto2.setStatus(BookingStatus.WAITING);  // Устанавливаем статус

        when(bookingService.getAllBookings(eq(userId))).thenReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[0].status").value(BookingStatus.WAITING.name()))  // Проверяем статус
                .andExpect(jsonPath("$[1].status").value(BookingStatus.WAITING.name()));  // Проверяем статус

        verify(bookingService).getAllBookings(eq(userId));
    }

    @Test
    void approveBooking_shouldReturnBookingDto_whenBookingIsApproved() throws Exception {
        Long bookingId = 1L;
        Long userId = 1L;
        boolean approved = true;

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);  // Устанавливаем статус

        when(bookingService.approveBooking(eq(userId), eq(bookingId), eq(approved))).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value(approved ? BookingStatus.APPROVED.name() : BookingStatus.REJECTED.name()));  // Проверяем статус

        verify(bookingService).approveBooking(eq(userId), eq(bookingId), eq(approved));
    }
}
