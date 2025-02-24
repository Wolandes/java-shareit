package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long idUser,
                           @Validated @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Добавлется предмет от пользователя с id: " + idUser);
        return itemService.saveItem(idUser, itemCreateDto);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long idUser,
                           @PathVariable Long id) {
        log.info("Пошел процесс поиска предмета с id: " + id);
        return itemService.findById(id, idUser);
    }

    @GetMapping
    public List<ItemDto> getAllItemsWithUserId(@RequestHeader("X-Sharer-User-Id") Long idUser) {
        log.info("Получение предметов с пользователя с id: " + idUser);
        return itemService.getAllItems(idUser);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long idUser,
                              @PathVariable Long id,
                              @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Обновление предмета с id" + id);
        return itemService.updateItem(id, idUser, itemUpdateDto);
    }

    @GetMapping("search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long idUser,
                                     @RequestParam(required = false) String text) {
        log.info("Поиск предметов с текстом: " + text + " .У Пользователя с id: " + idUser);
        return itemService.searchListItems(idUser, text);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long idUser,
                           @PathVariable Long id) {
        log.info("Удаление предмета с id: " + id);
        itemService.deleteById(id, idUser);
    }
}
