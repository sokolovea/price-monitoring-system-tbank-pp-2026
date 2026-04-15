package ru.tbank.pp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.tbank.pp.entity.User;
import ru.tbank.pp.entity.UserNotification;
import ru.tbank.pp.entity.UserNotificationId;
import ru.tbank.pp.exception.UserNotFoundException;
import ru.tbank.pp.model.ServiceConnectionConnectRequest;
import ru.tbank.pp.model.ServiceConnectionStatusCheckRequest;
import ru.tbank.pp.model.ServiceConnectionService;
import ru.tbank.pp.repository.UserNotificationRepository;
import ru.tbank.pp.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserNotificationServiceTest {

    @Mock
    private UserNotificationRepository userNotificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserNotificationService userNotificationService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setPassword("password");
        testUser.setRole("USER");
    }

    @Test
    void checkConnectionStatus_UserNotFound_ReturnsFalse() {
        ServiceConnectionStatusCheckRequest request = new ServiceConnectionStatusCheckRequest();
        request.setId(1L);
        request.setService(ServiceConnectionService.TELEGRAM);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = userNotificationService.checkConnectionStatus(request);

        assertThat(result).isFalse();
    }

    @Test
    void checkConnectionStatus_UserNotificationExists_ReturnsFalse() {
        ServiceConnectionStatusCheckRequest request = new ServiceConnectionStatusCheckRequest();
        request.setId(1L);
        request.setService(ServiceConnectionService.TELEGRAM);

        UserNotification userNotification = new UserNotification();
        userNotification.setInternalId(12345L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userNotificationRepository.findById(any(UserNotificationId.class)))
                .thenReturn(Optional.of(userNotification));

        boolean result = userNotificationService.checkConnectionStatus(request);

        assertThat(result).isFalse();
    }

    @Test
    void checkConnectionStatus_UserNotificationNotExists_ReturnsTrue() {
        ServiceConnectionStatusCheckRequest request = new ServiceConnectionStatusCheckRequest();
        request.setId(1L);
        request.setService(ServiceConnectionService.TELEGRAM);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userNotificationRepository.findById(any(UserNotificationId.class)))
                .thenReturn(Optional.empty());

        boolean result = userNotificationService.checkConnectionStatus(request);

        assertThat(result).isTrue();
    }

    @Test
    void connectService_Success() {
        ServiceConnectionConnectRequest request = new ServiceConnectionConnectRequest();
        request.setId(1L);
        request.setService(ServiceConnectionService.TELEGRAM);
        request.setInternalId(12345L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userNotificationRepository.save(any(UserNotification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userNotificationService.connectService(request);

        verify(userRepository).findById(1L);
        verify(userNotificationRepository).save(any(UserNotification.class));
    }

    @Test
    void connectService_UserNotFound_ThrowsException() {
        ServiceConnectionConnectRequest request = new ServiceConnectionConnectRequest();
        request.setId(99L);
        request.setService(ServiceConnectionService.TELEGRAM);
        request.setInternalId(12345L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userNotificationService.connectService(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with id '99' not found");

        verify(userNotificationRepository, never()).save(any());
    }
}
