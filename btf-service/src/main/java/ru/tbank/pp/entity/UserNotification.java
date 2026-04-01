package ru.tbank.pp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_notification")
@Getter
@Setter
public class UserNotification {

    @EmbeddedId
    private UserNotificationId id;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @Column(nullable = false)
    private String externalId;
}
