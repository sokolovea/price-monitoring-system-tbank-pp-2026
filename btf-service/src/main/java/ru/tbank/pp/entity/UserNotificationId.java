package ru.tbank.pp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.tbank.pp.model.ServiceConnectionService;

import java.io.Serializable;

@Data
@Embeddable
public class UserNotificationId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_service")
    private ServiceConnectionService notificationService;
}
