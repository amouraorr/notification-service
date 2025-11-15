package com.fiap.notificationservice.infrastructure.persistence;

import com.fiap.notificationservice.domain.model.Notification;
import com.fiap.notificationservice.domain.port.NotificationRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter que implementa a porta NotificationRepository usando JPA.
 */
@Component
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final SpringDataNotificationRepository repo;
    private final NotificationEntityMapper mapper;

    public NotificationRepositoryAdapter(SpringDataNotificationRepository repo, NotificationEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = mapper.toEntity(notification);
        NotificationEntity saved = repo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Notification findById(UUID id) {
        Optional<NotificationEntity> opt = repo.findById(id);
        return opt.map(mapper::toDomain).orElse(null);
    }
}