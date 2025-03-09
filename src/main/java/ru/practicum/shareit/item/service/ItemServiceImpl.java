package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final BookingMapper bookingMapper;

    @Override
    public ItemDto saveItem(Long idUser, ItemCreateDto itemCreateDto) {
        User user = checkCreateUser(idUser);
        Item item = itemMapper.toItemFromItemCreateDto(itemCreateDto);
        item.setOwner(user);
        item = itemRepository.save(item);
        log.info("Предмет добавлен с id: " + item.getId());
        return itemMapper.toItemDto(item);
    }

    public ItemDto findById(Long idItem, Long idUser) {
        checkCreateUser(idUser);
        Item item = checkCreateItem(idItem);
        List<Comment> comments = commentRepository.findByItemId(idItem);
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatus(idItem, BookingStatus.APPROVED);

        Booking lastBooking = null;
        Booking nextBooking = null;
        LocalDateTime now = LocalDateTime.now();

        if (!bookings.isEmpty()) {
            bookings.sort(Comparator.comparing(Booking::getStart));

            for (Booking booking : bookings) {
                if (booking.getStart().isBefore(now)) {
                    lastBooking = booking;
                } else if (nextBooking == null) {
                    nextBooking = booking;
                    break;
                }
            }
        }

        ItemDto itemDto = itemMapper.toItemDto(item);
        itemDto.setComments(commentMapper.toListCommentsDto(comments));

        if (nextBooking != null) {
            itemDto.setNextBooking(bookingMapper.toBookingDto(nextBooking));
        }
        if (lastBooking != null) {
            itemDto.setLastBooking(bookingMapper.toBookingDto(lastBooking));
        }

        log.info("Найден предмет с id: {}. Количество комментариев: {}", idItem, comments.size());
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItems(Long idUser) {
        checkCreateUser(idUser);
        return itemMapper.toListItemDto(itemRepository.findAllByOwnerId(idUser));
    }

    @Override
    public ItemDto updateItem(Long idItem, Long idUser, ItemUpdateDto itemUpdateDto) {
        Item item = checkCreateItem(idItem);
        if (!Objects.equals(item.getOwner().getId(), idUser)) {
            throw new NotFoundException("Пользователь с id: " + idUser + " не является владельцем предмета с id: " + idItem);
        }
        if (itemUpdateDto.getName() != null) {
            item.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }
        item = itemRepository.save(item);
        log.info("Предмет обновлен с id: " + idItem);
        return itemMapper.toItemDto(item);
    }

    @Override
    public void deleteById(Long id, Long idUser) {
        checkCreateUser(idUser);
        Item item = checkCreateItem(id);
        if (!Objects.equals(item.getOwner().getId(), idUser)) {
            throw new ValidationException("Пользователь с id: " + idUser + " не является владельцем предмета с id: " + id);
        }
        itemRepository.deleteById(id);
        log.info("Предмет удален с id: " + id);
    }

    @Override
    public List<ItemDto> searchListItems(Long idUser, String text) {
        checkCreateUser(idUser);
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.search(text);
        log.info("Поиск предметов по тексту: " + text);
        return itemMapper.toListItemDto(items);
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        User user = checkCreateUser(userId);
        Item item = checkCreateItem(itemId);

        boolean hasPastBooking = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now());

        if (!hasPastBooking) {
            throw new ValidationException("Комментарий можно оставить только после завершенного бронирования.");
        }

        Comment comment = commentMapper.toCommentFromCommentCreateDto(commentCreateDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);

        log.info("Возвращение комментария с id: " + comment.getId());
        return commentMapper.toCommentDto(comment);
    }

    private User checkCreateUser(Long idUser) {
        return userRepository.findById(idUser).orElseThrow(() -> new NotFoundException("Не найден пользователь c id: " + idUser));
    }

    private Item checkCreateItem(Long idItem) {
        return itemRepository.findById(idItem).orElseThrow(() -> new NotFoundException("Не найден предмет c id: " + idItem));
    }

    private Booking checkCreateBooking(Long idBooking) {
        return bookingRepository.findById(idBooking)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено с id: " + idBooking));
    }
}
