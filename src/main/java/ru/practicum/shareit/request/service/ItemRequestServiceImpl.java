package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements  ItemRequestService {
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemRequest createRequest(ItemRequest request) {
        return requestRepository.save(request);
    }

    @Override
    public ItemRequest findById(Long id) {
        return requestRepository.findById(id).orElseThrow(() -> new NotFoundException("Запрос не найден с id: " + id));
    }
}
