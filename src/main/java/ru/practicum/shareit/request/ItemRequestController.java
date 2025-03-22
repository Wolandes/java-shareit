package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        log.info("Создание запроса пользователем с id: " + userId);
        return requestService.createRequest(userId, itemRequestCreateDto);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) {
        log.info("Получение запроса с id: " + requestId + ". Пользователем с id: " + requestId);
        return requestService.getRequestById(userId, requestId);
    }
}
