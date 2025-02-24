package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(Long idUser, ItemCreateDto itemCreateDto);

    ItemDto findById(Long idItem, Long idUser);

    List<ItemDto> getAllItems(Long idUser);

    void deleteById(Long id, Long idUser);

    ItemDto updateItem(Long idItem, Long idUser, ItemUpdateDto itemUpdateDto);

    List<ItemDto> searchListItems(Long idUser, String text);
}
