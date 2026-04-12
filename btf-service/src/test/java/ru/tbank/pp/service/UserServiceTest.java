package ru.tbank.pp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.tbank.pp.dto.RegisterUserRequest;
import ru.tbank.pp.entity.User;
import ru.tbank.pp.exception.AuthException;
import ru.tbank.pp.exception.UserNotFoundException;
import ru.tbank.pp.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterUserRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole("USER");

        registerRequest = new RegisterUserRequest("test@test.com", "password123");
    }

    @Test
    void register_Success() {
        when(userRepository.existsByEmailIgnoreCase(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.register(registerRequest);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        assertThat(result.getRole()).isEqualTo("USER");
        verify(userRepository).existsByEmailIgnoreCase(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UserAlreadyExists_ThrowsException() {
        when(userRepository.existsByEmailIgnoreCase(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь с таким email уже существует");

        verify(userRepository, never()).save(any());
    }

    @Test
    void findByEmail_UserExists() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByEmail("test@test.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void findByEmail_UserNotExists() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("unknown@test.com");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByEmail_UserExists() {
        when(userRepository.existsByEmailIgnoreCase("test@test.com")).thenReturn(true);

        boolean result = userService.existsByEmail("test@test.com");

        assertThat(result).isTrue();
    }

    @Test
    void existsByEmail_UserNotExists() {
        when(userRepository.existsByEmailIgnoreCase("unknown@test.com")).thenReturn(false);

        boolean result = userService.existsByEmail("unknown@test.com");

        assertThat(result).isFalse();
    }

    @Test
    void getIdFromCridentials_Success() {
        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(testUser, null)
        );

        Long result = userService.getIdFromCridentials();

        assertThat(result).isEqualTo(1L);
        SecurityContextHolder.clearContext();
    }

    @Test
    void getUserFromCridentials_AuthNull_ThrowsException() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> userService.getUserFromCridentials())
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("Authentication object is null");
    }

    @Test
    void getUserFromCridentials_PrincipalNull_ThrowsException() {
        var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThatThrownBy(() -> userService.getUserFromCridentials())
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");

        SecurityContextHolder.clearContext();
    }

    @Test
    void getUserFromCridentials_Success() {
        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(testUser, null)
        );

        User result = userService.getUserFromCridentials();

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        SecurityContextHolder.clearContext();
    }
}
