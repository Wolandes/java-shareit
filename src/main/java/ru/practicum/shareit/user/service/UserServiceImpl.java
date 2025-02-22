package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DoubleException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto saveUser(UserCreateDto userCreateDto) {
        User user = userMapper.toUserFromUserCreateDto(userCreateDto);
        checkDoubleEmail(user);
        user = userRepository.saveUser(user);
        UserDto userDto = userMapper.toUserDto(user);
        log.info("Пользователь сохранен с id" + userDto.getId());
        return userDto;
    }

    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Не найден пользователь"));
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        List<User> allUsers = userRepository.findAll();
        if (allUsers.isEmpty()) {
            log.info("Список пользователей нет");
        }
        List<UserDto> allUsersDto = userMapper.toListUserDto(allUsers);
        return allUsersDto;
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto updateUser(Long id, UserUpdateDto updateUser) {
        UserDto UserDto = findById(id);
        User user = userMapper.toUserFromUserUpdateDto(updateUser);
        if (updateUser.getName() == null) {
            user.setName((updateUser.getName()));
        }
        if (updateUser.getEmail() == null) {
            user.setEmail(updateUser.getEmail());
        } else {
            checkDoubleEmail(user);
        }
        user.setId(id);
        userRepository.saveUser(user);
        UserDto = userMapper.toUserDto(user);
        log.info("Пользователь обновлен");
        return UserDto;
    }

    private void checkDoubleEmail(User user) {
        Set<User> users = userRepository.findSetAllUsers();
        for (User user1 : users) {
            if (user.getEmail().equals(user1.getEmail())) {
                log.info("В мапе не может хранится одинаковые email");
                throw new DoubleException("В мапе не может хранится одинаковые email");
            }
        }
    }
}
