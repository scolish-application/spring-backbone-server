package com.github.simaodiazz.schola.backbone.server.mail.data.model;

import com.github.simaodiazz.schola.backbone.server.database.entity.EntitySuperclass;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@RedisHash
@NoArgsConstructor
@Data
public final class Message extends EntitySuperclass {

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "courier_author_id", referencedColumnName = "id")
    private Courier author;

    @ManyToMany(mappedBy = "received")
    private Set<Courier> couriers = new HashSet<>();

    public Message(String content, Courier author, Set<User> targets) {
        this.content = content;
        this.author = author;
        // The targets should be handled in the service layer
    }

    public Message(long id, LocalDateTime created, LocalDateTime updated, String content, Courier author) {
        super(id, created, updated);
        this.content = content;
        this.author = author;
    }

    // Needed getters and setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Courier getAuthor() {
        return author;
    }

    public void setAuthor(Courier author) {
        this.author = author;
    }

    public Set<Courier> getCouriers() {
        return couriers;
    }

    public void setCouriers(Set<Courier> couriers) {
        this.couriers = couriers;
    }
}