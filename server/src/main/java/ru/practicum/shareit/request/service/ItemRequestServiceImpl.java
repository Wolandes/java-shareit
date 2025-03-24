package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestCreateDto requestDto) {
        User requester = checkCreateUser(userId);
        ItemRequest request = itemRequestMapper.toItemRequest(requestDto);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        request = itemRequestRepository.save(request);
        log.info("Запрос добавлен с id: " + request.getId());
        return itemRequestMapper.toItemRequestDto(request);
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(Long userId) {
        checkCreateUser(userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        List<Long> requestIds = new ArrayList<>();
        for (ItemRequest request : requests) {
            requestIds.add(request.getId());
        }
        List<Item> items = itemRepository.findByRequestIds(requestIds);
        Map<Long, List<ItemDto>> itemsByRequestId = new HashMap<>();
        for (Item item : items) {
            Long reqId = item.getRequest().getId();
            if (!itemsByRequestId.containsKey(reqId)) {
                itemsByRequestId.put(reqId, new ArrayList<ItemDto>());
            }
            itemsByRequestId.get(reqId).add(itemMapper.toItemDto(item));
        }
        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest request : requests) {
            ItemRequestDto dto = itemRequestMapper.toItemRequestDto(request);
            List<ItemDto> itemDtos = itemsByRequestId.get(request.getId());
            if (itemDtos == null) {
                itemDtos = new ArrayList<ItemDto>();
            }
            dto.setItems(itemDtos);
            result.add(dto);
        }
        log.info("Получение списка пользователем с id: " + userId);
        return result;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        checkCreateUser(userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId);
        List<Long> requestIds = new ArrayList<>();
        for (ItemRequest request : requests) {
            requestIds.add(request.getId());
        }
        List<Item> items = itemRepository.findByRequestIds(requestIds);
        Map<Long, List<ItemDto>> itemsByRequestId = new HashMap<>();
        for (Item item : items) {
            Long reqId = item.getRequest().getId();
            if (!itemsByRequestId.containsKey(reqId)) {
                itemsByRequestId.put(reqId, new ArrayList<ItemDto>());
            }
            itemsByRequestId.get(reqId).add(itemMapper.toItemDto(item));
        }
        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest request : requests) {
            ItemRequestDto dto = itemRequestMapper.toItemRequestDto(request);
            List<ItemDto> itemDtos = itemsByRequestId.get(request.getId());
            if (itemDtos == null) {
                itemDtos = new ArrayList<ItemDto>();
            }
            dto.setItems(itemDtos);
            result.add(dto);
        }
        log.info("Получение списка пользователем с id: " + userId);
        return result;
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        User user = checkCreateUser(userId);

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Не найден запрос с id: " + requestId));

        List<Item> itemsList = itemRepository.findByRequestId(requestId);
        List<ItemDto> items = new ArrayList<>();

        for (Item item : itemsList) {
            items.add(itemMapper.toItemDto(item));
        }

        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(request);
        itemRequestDto.setItems(items);

        return itemRequestDto;
    }

    private User checkCreateUser(Long idUser) {
        return userRepository.findById(idUser).orElseThrow(() -> new NotFoundException("Не найден пользователь c id: " + idUser));
    }
}
