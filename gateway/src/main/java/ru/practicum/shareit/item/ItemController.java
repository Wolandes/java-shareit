package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

/**
 * Контроллер для работы с вещами.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Добавление вещи пользователем id: {}", userId);
        return itemClient.createItem(userId, itemCreateDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody CommentCreateDto commentCreateDto) {
        log.info("Добавление комментария к вещи id: {} пользователем id: {}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentCreateDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение вещи id: {} пользователем id: {}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех вещей пользователя id: {}", userId);
        return itemClient.getItems(userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Обновление вещи id: {} пользователем id: {}", itemId, userId);
        return itemClient.updateItem(itemId, userId, itemUpdateDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam String text) {
        log.info("Поиск вещей с текстом '{}' пользователем id: {}", text, userId);
        return itemClient.searchItems(userId, text);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Удаление вещи id: {} пользователем id: {}", itemId, userId);
        return itemClient.deleteItem(itemId, userId);
    }
}
