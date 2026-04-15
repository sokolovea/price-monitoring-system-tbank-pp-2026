package ru.tbank.pp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.pp.entity.UserNotification;
import ru.tbank.pp.entity.UserNotificationId;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, UserNotificationId> {

}
