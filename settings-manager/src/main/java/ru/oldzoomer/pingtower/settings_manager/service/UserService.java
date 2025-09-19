package ru.oldzoomer.pingtower.settings_manager.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oldzoomer.pingtower.settings_manager.dto.User;
import ru.oldzoomer.pingtower.settings_manager.entity.UserEntity;
import ru.oldzoomer.pingtower.settings_manager.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable("users")
    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "users", key = "#id")
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id).map(this::convertToDto);
    }

    @Cacheable(value = "users", key = "#username")
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(this::convertToDto);
    }

    @Cacheable(value = "users", key = "#email")
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertToDto);
    }

    @CacheEvict("users")
    @Transactional
    public User createUser(User user) {
        // Проверим, что пользователя с таким именем или email еще нет
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("User with username " + user.getUsername() + " already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }

        UserEntity entity = convertToEntity(user);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        UserEntity savedEntity = userRepository.save(entity);
        return convertToDto(savedEntity);
    }

    @CacheEvict(value = "users", key = "#id")
    @Transactional
    public User updateUser(UUID id, User user) {
        Optional<UserEntity> existingEntity = userRepository.findById(id);
        if (existingEntity.isPresent()) {
            UserEntity entity = existingEntity.get();
            
            // Проверим, что нового username или email нет у других пользователей
            if (!entity.getUsername().equals(user.getUsername()) && 
                userRepository.existsByUsername(user.getUsername())) {
                throw new RuntimeException("User with username " + user.getUsername() + " already exists");
            }
            if (!entity.getEmail().equals(user.getEmail()) && 
                userRepository.existsByEmail(user.getEmail())) {
                throw new RuntimeException("User with email " + user.getEmail() + " already exists");
            }

            entity.setUsername(user.getUsername());
            entity.setEmail(user.getEmail());
            entity.setFirstName(user.getFirstName());
            entity.setLastName(user.getLastName());
            entity.setUpdatedAt(LocalDateTime.now());

            UserEntity updatedEntity = userRepository.save(entity);
            return convertToDto(updatedEntity);
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    @CacheEvict(value = "users", key = "#id")
    @Transactional
    public void deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    private User convertToDto(UserEntity entity) {
        User dto = new User();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private UserEntity convertToEntity(User dto) {
        UserEntity entity = new UserEntity();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }
}