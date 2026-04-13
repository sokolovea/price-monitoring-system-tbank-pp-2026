package ru.tbank.pp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.pp.dto.RegisterUserRequest;
import ru.tbank.pp.entity.User;
import ru.tbank.pp.exception.AuthException;
import ru.tbank.pp.exception.UserNotFoundException;
import ru.tbank.pp.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Регистрация нового пользователя
     */
    @Transactional
    public User register(RegisterUserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        // Можно добавить дополнительные поля, если они появятся позже
        // user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Поиск пользователя по email (используется в AuthenticationProvider / UserDetailsService)
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Проверка существования пользователя по email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    public Long getIdFromCridentials() {
        return getUserFromCridentials().getId();
    }

    public User getUserFromCridentials() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AuthException("Authentication object is null");
        }
        var user = (User) auth.getPrincipal();
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user;
    }

}