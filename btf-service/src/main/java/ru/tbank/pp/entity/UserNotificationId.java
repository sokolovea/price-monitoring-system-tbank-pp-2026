package ru.tbank.pp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import ru.tbank.pp.model.ServiceConnectionService;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class UserNotificationId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "notification_service")
    private ServiceConnectionService notificationService;
}
