package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<ItemRequest> createRequest(@RequestBody ItemRequest request) {
        return ResponseEntity.ok(requestService.createRequest(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemRequest> getRequest(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.findById(id));
    }
}
