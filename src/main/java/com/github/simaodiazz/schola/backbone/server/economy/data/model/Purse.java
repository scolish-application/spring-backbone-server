package com.github.simaodiazz.schola.backbone.server.economy.data.model;

import com.github.simaodiazz.schola.backbone.server.database.entity.EntitySuperclass;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@RedisHash
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Purse extends EntitySuperclass {

    @Column(nullable = false)
    private double amount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "purse_transactions",
            joinColumns = @JoinColumn(name = "purse_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "transaction_id", referencedColumnName = "id"))
    private List<Transaction> transactions;

    public Purse(final @NotNull User user) {
        this.user = user;
        this.amount = 0;
        this.transactions = new ArrayList<>();
    }
}
