package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        return ResponseEntity.ok(requestService.createRequest(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemRequest> getRequest(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.findById(id));
    }
}
