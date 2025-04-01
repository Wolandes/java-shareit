package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Процесс получения списков всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        log.info("Поиск юзера с id: " + id);
        return userService.findById(id);
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Validated UserCreateDto userCreateDto) {
        log.info("Пошел процесс добавление пользователя");
        return userService.saveUser(userCreateDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody @Validated UserUpdateDto userUpdateDto) {
        log.info("Обновления пользователя с id: " + id);
        if (userUpdateDto.getName() == null && userUpdateDto.getEmail() == null) {
            throw new ValidationException("Должен быть указан одно из полей");
        }
        return userService.updateUser(id, userUpdateDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Удаление пользователя");
        userService.deleteById(id);
    }
}
