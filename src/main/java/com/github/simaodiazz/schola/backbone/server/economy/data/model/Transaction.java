package com.github.simaodiazz.schola.backbone.server.economy.data.model;

import com.github.simaodiazz.schola.backbone.server.database.entity.EntitySuperclass;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@EqualsAndHashCode(callSuper = true)
@Entity
@RedisHash
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Transaction extends EntitySuperclass {

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String cause;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    private TransactionMovement movement;

}
