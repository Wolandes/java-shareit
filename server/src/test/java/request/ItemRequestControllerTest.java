package request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemRequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemRequestService requestService;

    private ItemRequestController requestController;

    @BeforeEach
    void setUp() {
        requestController = new ItemRequestController(requestService);
        mockMvc = MockMvcBuilders.standaloneSetup(requestController).build();
    }

    @Test
    void createRequest_shouldReturnItemRequestDto_whenRequestIsValid() throws Exception {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("New request");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("New request");

        when(requestService.createRequest(anyLong(), eq(itemRequestCreateDto))).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content("{\"description\": \"New request\"}"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("New request"));

        verify(requestService).createRequest(anyLong(), eq(itemRequestCreateDto));
    }

    @Test
    void getAllRequests_shouldReturnListOfItemRequestDtos_whenRequestsExist() throws Exception {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("New request");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("New request");

        when(requestService.createRequest(eq(1L), eq(itemRequestCreateDto))).thenReturn(itemRequestDto);

        when(requestService.getOwnRequests(1L)).thenReturn(Collections.singletonList(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andDo(print())
                .andExpect(jsonPath("$[0].id").value(1L))  // Проверяем, что id равен 1L
                .andExpect(jsonPath("$[0].description").value("New request"));  // Проверяем описание
    }
}
