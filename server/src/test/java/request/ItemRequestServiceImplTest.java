package request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequestCreateDto itemRequestCreateDto;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("New request");

        itemRequestDto = new ItemRequestDto();  // Инициализация объекта
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("New request");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("New request");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        item = new Item();
        item.setId(1L);
        item.setName("Item 1");
    }

    @Test
    void createRequest_shouldReturnCreatedRequest() {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(itemRequestMapper.toItemRequest(itemRequestCreateDto)).thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestService.createRequest(1L, itemRequestCreateDto);

        assertNotNull(result);
        assertEquals(itemRequestDto.getId(), result.getId());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
    }

    @Test
    void getOwnRequests_shouldReturnListOfItemRequestDtos() {
        item.setRequest(itemRequest);

        List<ItemRequest> itemRequests = Collections.singletonList(itemRequest);
        List<ItemDto> itemDtos = Collections.singletonList(new ItemDto());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(1L)).thenReturn(itemRequests);
        when(itemRepository.findByRequestIds(Collections.singletonList(itemRequest.getId())))
                .thenReturn(Collections.singletonList(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDtos.get(0));
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);

        itemRequestDto.setItems(itemDtos);

        List<ItemRequestDto> result = itemRequestService.getOwnRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequest.getId(), result.get(0).getId());
        assertEquals(itemRequest.getDescription(), result.get(0).getDescription());
        assertEquals(1, result.get(0).getItems().size());
    }

    @Test
    void getAllRequests_shouldReturnListOfItemRequestDtos() {
        item.setRequest(itemRequest);

        List<ItemRequest> itemRequests = Collections.singletonList(itemRequest);
        List<ItemDto> itemDtos = Collections.singletonList(new ItemDto());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(1L)).thenReturn(itemRequests);
        when(itemRepository.findByRequestIds(Collections.singletonList(itemRequest.getId())))
                .thenReturn(Collections.singletonList(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDtos.get(0));
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);

        itemRequestDto.setItems(itemDtos);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequest.getId(), result.get(0).getId());
        assertEquals(itemRequest.getDescription(), result.get(0).getDescription());
        assertEquals(1, result.get(0).getItems().size());
    }

    @Test
    void getRequestById_shouldReturnItemRequestDto() {
        List<Item> items = new ArrayList<>();
        items.add(item);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(java.util.Optional.of(itemRequest));
        when(itemRepository.findByRequestId(itemRequest.getId())).thenReturn(items);
        when(itemMapper.toItemDto(item)).thenReturn(new ItemDto());
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestService.getRequestById(1L, 1L);

        assertNotNull(result);
        assertEquals(itemRequestDto.getId(), result.getId());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
    }

    @Test
    void getRequestById_shouldThrowNotFoundException_whenRequestNotFound() {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(1L, 1L);
        });
    }
}
