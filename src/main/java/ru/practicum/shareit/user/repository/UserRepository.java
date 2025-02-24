package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    User saveUser(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    void deleteById(Long id);

    public Set<User> findSetAllUsers();
}
