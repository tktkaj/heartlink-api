package com.ss.heartlinkapi.notification.repository;

import com.ss.heartlinkapi.notification.entity.NotificationEntity;
import com.ss.heartlinkapi.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByUserId(Long userId);
}
