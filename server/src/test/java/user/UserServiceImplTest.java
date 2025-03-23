package user;


import ru.practicum.shareit.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DoubleException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");
    }

    @Test
    void saveUser_shouldSaveUserAndReturnUserDto() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setName("John Doe");
        createDto.setEmail("john@example.com");

        // Маппинг из DTO в сущность
        when(userMapper.toUserFromUserCreateDto(createDto)).thenReturn(user);
        // Проверяем, что дублирования email нет
        when(userRepository.findByEmail("john@example.com")).thenReturn(Collections.emptySet());
        // Сохраняем пользователя
        when(userRepository.save(user)).thenReturn(user);
        // Маппинг обратно в DTO
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.saveUser(createDto);
        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        verify(userRepository).save(user);
    }

    @Test
    void saveUser_shouldThrowDoubleExceptionWhenDuplicateEmail() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setName("John Doe");
        createDto.setEmail("john@example.com");

        when(userMapper.toUserFromUserCreateDto(createDto)).thenReturn(user);
        // Симуляция наличия пользователя с таким же email
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("john@example.com");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Set.of(anotherUser));

        assertThrows(DoubleException.class, () -> userService.saveUser(createDto));
    }

    @Test
    void findById_shouldReturnUserDtoWhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.findById(1L);
        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
    }

    @Test
    void findById_shouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.findById(1L));
        assertTrue(exception.getMessage().contains("Не найден пользователь c id: 1"));
    }

    @Test
    void findAll_shouldReturnListOfUserDtos() {
        List<User> users = List.of(user);
        List<UserDto> dtos = List.of(userDto);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toListUserDto(users)).thenReturn(dtos);

        List<UserDto> result = userService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void deleteById_shouldCallRepositoryDelete() {
        userService.deleteById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void updateUser_shouldUpdateUserAndReturnUpdatedUserDto_whenEmailNotChanged() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail(user.getEmail());

        when(userMapper.toUserFromUserUpdateDto(updateDto)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        when(userMapper.toUserDto(any(User.class))).thenAnswer(invocation -> {
            User arg = invocation.getArgument(0);
            UserDto dto = new UserDto();
            dto.setId(arg.getId());
            dto.setName(arg.getName());
            dto.setEmail(arg.getEmail());
            return dto;
        });

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository).findById(1L);
        verify(userMapper).toUserFromUserUpdateDto(updateDto);
        verify(userRepository).save(updatedUser);
        verify(userMapper, times(1)).toUserDto(updatedUser);
    }

    @Test
    void updateUser_shouldUpdateUserAndReturnUpdatedUserDto_whenEmailChanged() {
        // Тестируем обновление с изменением имени и email
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("newemail@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("newemail@example.com");
        when(userMapper.toUserFromUserUpdateDto(updateDto)).thenReturn(updatedUser);
        when(userRepository.findByEmail("newemail@example.com")).thenReturn(Collections.emptySet());
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        UserDto updatedDto = new UserDto();
        updatedDto.setId(1L);
        updatedDto.setName("Updated Name");
        updatedDto.setEmail("newemail@example.com");
        when(userMapper.toUserDto(updatedUser)).thenReturn(updatedDto);

        UserDto result = userService.updateUser(1L, updateDto);
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("newemail@example.com", result.getEmail());
    }

    @Test
    void updateUser_shouldThrowDoubleExceptionWhenNewEmailIsDuplicate() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("duplicate@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("duplicate@example.com");
        when(userMapper.toUserFromUserUpdateDto(updateDto)).thenReturn(updatedUser);

        // Симулируем, что email уже занят другим пользователем
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("duplicate@example.com");
        when(userRepository.findByEmail("duplicate@example.com")).thenReturn(Set.of(anotherUser));

        assertThrows(DoubleException.class, () -> userService.updateUser(1L, updateDto));
    }
}
