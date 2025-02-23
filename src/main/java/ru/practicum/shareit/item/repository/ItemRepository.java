package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> getAllItems(Long idUser);

    Optional<Item> findById(Long id);

    Item saveItem(Item item);

    void deleteItemId(Long id);

    List<Item> searchListItems(Long idUser, String text);
}
