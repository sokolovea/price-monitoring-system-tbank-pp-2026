package ru.tbank.pp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tbank.pp.dto.RegisterUserRequest;
import ru.tbank.pp.entity.User;
import ru.tbank.pp.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterUserRequest request) {
        Optional<User> existing = userRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        return userRepository.save(user);
    }
}