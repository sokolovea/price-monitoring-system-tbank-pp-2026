package ru.tbank.pp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.pp.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
