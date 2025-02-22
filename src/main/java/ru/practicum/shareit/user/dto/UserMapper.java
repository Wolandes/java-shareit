package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public User toUserFromUserCreateDto(UserCreateDto userCreateDto) {
        User user = new User();
        user.setName(userCreateDto.getName());
        user.setEmail(userCreateDto.getEmail());
        return user;
    }

    public User toUserFromUserUpdateDto(UserUpdateDto userUpdateDto) {
        User user = new User();
        user.setName(userUpdateDto.getName());
        user.setEmail(userUpdateDto.getEmail());
        return user;
    }

    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public List<UserDto> toListUserDto(List<User> users) {
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            UserDto userDto = toUserDto(user);
            usersDto.add(userDto);
        }
        return usersDto;
    }
}
