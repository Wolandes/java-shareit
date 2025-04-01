package item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(
                itemRepository, userRepository, itemMapper, commentMapper, bookingRepository,
                commentRepository, new BookingMapper(), itemRequestRepository);
    }

    @Test
    void saveItem_shouldReturnItemDto_whenItemIsValid() {
        Long userId = 1L;
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setRequestId(1L);

        User user = new User();
        user.setId(userId);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(new ItemRequest()));
        when(itemMapper.toItemFromItemCreateDto(itemCreateDto)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.saveItem(userId, itemCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());

        verify(itemRepository).save(item);
    }

    @Test
    void saveItem_shouldThrowNotFoundException_whenUserDoesNotExist() {
        Long userId = 1L;
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.saveItem(userId, itemCreateDto));
    }

    @Test
    void updateItem_shouldReturnUpdatedItemDto_whenItemIsUpdated() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Updated Item");

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Test Item");
        existingItem.setDescription("Test Description");
        existingItem.setAvailable(true);
        existingItem.setOwner(new User());
        existingItem.getOwner().setId(userId);

        Item updatedItem = new Item();
        updatedItem.setId(itemId);
        updatedItem.setName("Updated Item");
        updatedItem.setDescription("Test Description");
        updatedItem.setAvailable(true);
        updatedItem.setOwner(new User());
        updatedItem.getOwner().setId(userId);

        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(itemId);
        updatedItemDto.setName("Updated Item");
        updatedItemDto.setDescription("Test Description");
        updatedItemDto.setAvailable(true);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(updatedItem)).thenReturn(updatedItem);
        when(itemMapper.toItemDto(updatedItem)).thenReturn(updatedItemDto);

        ItemDto result = itemService.updateItem(itemId, userId, itemUpdateDto);

        assertNotNull(result);
        assertEquals("Updated Item", result.getName());

        verify(itemRepository).save(updatedItem);
    }

    @Test
    void updateItem_shouldThrowNotFoundException_whenUserIsNotOwner() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Updated Item");

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Test Item");
        existingItem.setDescription("Test Description");
        existingItem.setAvailable(true);
        existingItem.setOwner(new User());
        existingItem.getOwner().setId(2L); // Несоответствующий владелец

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemId, userId, itemUpdateDto));
    }
}
