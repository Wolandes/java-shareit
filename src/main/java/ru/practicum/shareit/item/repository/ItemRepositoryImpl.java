package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item saveItem(Item item) {
        if (item.getId() == 0) {
            item.setId(getNextId());
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getAllItems(Long idUser) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (Objects.equals(idUser, item.getOwnerId())) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public void deleteItemId(Long id) {
        items.remove(id);
        log.info("Удален предмет с id: " + id);
    }

    @Override
    public List<Item> searchListItems(Long idUser, String text) {
        List<Item> items = getAllItems(idUser);
        List<Item> result = new ArrayList<>();
        String lowerText = text.toLowerCase();
        for (Item item : items) {
            if (item.getAvailable() && item.getName().toLowerCase().contains(lowerText) || item.getDescription().toLowerCase().contains(lowerText)) {
                result.add(item);
            }
        }
        return result;
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
