package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> findByOwnerId(Long ownerId);

    List<Item> findByNameContainingIgnoreCaseAndAvailableIsTrue(String text);
}
