package ru.tbank.pp.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class UserNotificationId implements Serializable {

    private Long userId;

    private String notificationService;
}
