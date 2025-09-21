package ru.oldzoomer.pingtower.pinger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.oldzoomer.pingtower.pinger.entity.CheckConfigurationEntity;

@Repository
public interface CheckConfigurationRepository extends JpaRepository<CheckConfigurationEntity, String> {
}