package com.github.simaodiazz.schola.backbone.server.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class EntitySuperclass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    protected LocalDateTime created;

    @Column(nullable = false)
    @UpdateTimestamp
    protected LocalDateTime updated;

    public EntitySuperclass(final long id) {
        this.id = id;
    }
}