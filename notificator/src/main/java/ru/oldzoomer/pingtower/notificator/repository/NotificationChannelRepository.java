package ru.oldzoomer.pingtower.notificator.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.oldzoomer.pingtower.notificator.entity.NotificationChannelEntity;

import java.util.Optional;

@Repository
public interface NotificationChannelRepository extends JpaRepository<NotificationChannelEntity, String> {
    Page<NotificationChannelEntity> findByType(String type, Pageable pageable);
    Optional<NotificationChannelEntity> findById(String id);
}