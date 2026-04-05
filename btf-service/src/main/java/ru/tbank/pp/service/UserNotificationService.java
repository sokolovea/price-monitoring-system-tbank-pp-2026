package ru.tbank.pp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.pp.entity.UserNotification;
import ru.tbank.pp.entity.UserNotificationId;
import ru.tbank.pp.exception.UserNotFoundException;
import ru.tbank.pp.model.ServiceConnectionConnectRequest;
import ru.tbank.pp.model.ServiceConnectionStatusCheckRequest;
import ru.tbank.pp.repository.UserNotificationRepository;
import ru.tbank.pp.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserNotificationService {
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;

    private final UserService userService;

    public boolean checkIfUserNotificationExists(ServiceConnectionStatusCheckRequest serviceConnectionStatusCheckRequest) {
        var userNotificationId = new UserNotificationId();
        userNotificationId.setUserId(serviceConnectionStatusCheckRequest.getId());
        userNotificationId.setNotificationService(serviceConnectionStatusCheckRequest.getService());

        var userNotification = userNotificationRepository.findById(userNotificationId);

        return userNotification.isPresent();
    }

    public void connectService(ServiceConnectionConnectRequest serviceConnectionConnectRequest) {
        var userNotificationId = new UserNotificationId();
        userNotificationId.setUserId(serviceConnectionConnectRequest.getId());
        userNotificationId.setNotificationService(serviceConnectionConnectRequest.getService());

        var userOptional = userRepository.findById(serviceConnectionConnectRequest.getId());
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(String.format("User with id '%s' not found", serviceConnectionConnectRequest.getId()));
        }
        var user = userOptional.get();

        var userNotification = new UserNotification();
        userNotification.setUser(user);
        userNotification.setId(userNotificationId);
        userNotification.setInternalId(serviceConnectionConnectRequest.getInternalId());

        userNotificationRepository.save(userNotification);
    }


}
