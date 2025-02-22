package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public Item toItemFromItemCreateDto(ItemCreateDto itemCreateDto) {
        Item item = new Item();
        item.setName(itemCreateDto.getName());
        item.setDescription(itemCreateDto.getDescription());
        item.setAvailable(itemCreateDto.getAvailable());
        return item;
    }

    public Item toItemFromItemUpdateDto(ItemUpdateDto itemUpdateDto) {
        Item item = new Item();
        item.setName(itemUpdateDto.getName());
        item.setDescription(itemUpdateDto.getDescription());
        item.setAvailable(itemUpdateDto.getAvailable());
        return item;
    }

    public List<ItemDto> toListItemDto(List<Item> items) {
        List<ItemDto> usersDto = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = toItemDto(item);
            usersDto.add(itemDto);
        }
        return usersDto;
    }
}
