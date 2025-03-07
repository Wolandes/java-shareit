package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto saveItem(Long idUser, ItemCreateDto itemCreateDto) {
        User user = checkCreateUser(idUser);
        Item item = itemMapper.toItemFromItemCreateDto(itemCreateDto);
        item.setOwner(user);
        item = itemRepository.save(item);
        log.info("Предмет добавлен с id: " + item.getId());
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto findById(Long idItem, Long idUser) {
        checkCreateUser(idUser);
        Item item = checkCreateItem(idItem);
        log.info("Найден предмет с id: " + idItem);
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItems(Long idUser) {
        checkCreateUser(idUser);
        return itemMapper.toListItemDto(itemRepository.findAllByOwnerId(idUser));
    }

    @Override
    public ItemDto updateItem(Long idItem, Long idUser, ItemUpdateDto itemUpdateDto) {
        Item itemCheckId = checkCreateItem(idItem);
        ItemDto itemDto = findById(idItem, idUser);
        Item item = itemMapper.toItemFromItemUpdateDto(itemUpdateDto);
        if (!Objects.equals(itemCheckId.getOwnerId(), idUser)) {
            throw new NotFoundException("Пользователь с id: " + idUser + ".Не совпадает с владельцем предмета с id: " + itemCheckId.getOwnerId());
        }
        if (item.getName() == null) {
            item.setName(itemDto.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(itemDto.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(itemDto.getAvailable());
        }
        item.setId(idItem);
        item.setOwnerId(idUser);
        itemRepository.saveItem(item);
        itemDto = itemMapper.toItemDto(item);
        log.info("Предмет обновлен с id: " + idItem);
        return itemDto;
    }

    @Override
    public void deleteById(Long id, Long idUser) {
        checkCreateUser(idUser);
        Item item = checkCreateItem(id);
        if (Objects.equals(item.getOwnerId(), idUser)) {
            throw new ValidationException("Владелец предмета с id: " + item.getOwnerId() + ". Не совпадает пользователем с id: " + idUser);
        }
        itemRepository.deleteItemId(id);
    }

    @Override
    public List<ItemDto> searchListItems(Long idUser, String text) {
        checkCreateUser(idUser);
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.searchListItems(idUser, text);
        log.info("Получение значении с текстом: " + text);
        return itemMapper.toListItemDto(items);
    }

    private User checkCreateUser(Long idUser) {
        return userRepository.findById(idUser).orElseThrow(() -> new NotFoundException("Не найден пользователь c id: " + idUser));
    }

    private Item checkCreateItem(Long idItem) {
        return itemRepository.findById(idItem).orElseThrow(() -> new NotFoundException("Не найден предмет c id: " + idItem));
    }
}
