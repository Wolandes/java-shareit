package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto saveItem(Long idUser, ItemCreateDto itemCreateDto) {
        User user = userRepository.findById(idUser).orElseThrow(() -> new NotFoundException("Не найден пользователь"));
        Item item = itemMapper.toItemFromItemCreateDto(itemCreateDto);
        item.setOwnerId(user.getId());
        item = itemRepository.saveItem(item);
        ItemDto itemDto = itemMapper.toItemDto(item);
        log.info("Предмет добавлен с id" + itemDto.getId());
        return itemDto;
    }

    @Override
    public ItemDto findById(Long idItem, Long idUser) {
        Item item = itemRepository.findById(idItem).orElseThrow(() -> new NotFoundException("Не найден предмет"));
        if (item.getOwnerId() != idUser) {
            throw new NotFoundException("Владелец  с id " + item.getId() + " не совпадает с пользователем c id" + idUser);
        }
        ItemDto itemDto = itemMapper.toItemDto(item);
        log.info("Найден предмет с id " + idItem);
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItems(Long idUser) {
        List<Item> items = itemRepository.getAllItems();
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            if (idUser == item.getOwnerId()) {
                itemDtos.add(itemMapper.toItemDto(item));
            }
        }
        return itemDtos;
    }

    @Override
    public ItemDto updateItem(Long idItem, Long idUser, ItemUpdateDto itemUpdateDto) {
        ItemDto itemDto = findById(idItem, idUser);
        Item item = itemMapper.toItemFromItemUpdateDto(itemUpdateDto);
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
    public void deleteById(Long id) {
        itemRepository.deleteItemId(id);
    }

    @Override
    public List<ItemDto> searchListItems(Long idUser, String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        User user = userRepository.findById(idUser).orElseThrow(() -> new NotFoundException("Не найден пользователь"));
        List<Item> items = itemRepository.getAllItems();
        List<Item> result = new ArrayList<>();
        String lowerText = text.toLowerCase();
        for (Item item : items) {
            if (item.getOwnerId() == idUser) {
                if (item.getAvailable() && item.getName().toLowerCase().contains(lowerText) || item.getDescription().toLowerCase().contains(lowerText)) {
                    result.add(item);
                }
            }
        }
        log.info("Получение значении с текстом: " + text);
        return itemMapper.toListItemDto(result);
    }
}
