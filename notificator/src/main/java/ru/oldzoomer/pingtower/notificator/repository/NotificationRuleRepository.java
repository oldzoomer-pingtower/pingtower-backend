package ru.oldzoomer.pingtower.notificator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.oldzoomer.pingtower.notificator.entity.NotificationRuleEntity;

import java.util.Optional;

@Repository
public interface NotificationRuleRepository extends JpaRepository<NotificationRuleEntity, String> {
    Optional<NotificationRuleEntity> findById(String id);
}