package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Set<User> findByEmail(String email);
}
