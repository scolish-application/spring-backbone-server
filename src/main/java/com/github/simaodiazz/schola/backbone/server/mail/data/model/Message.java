package com.github.simaodiazz.schola.backbone.server.mail.data.model;

import com.github.simaodiazz.schola.backbone.server.database.entity.EntitySuperclass;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@RedisHash
@NoArgsConstructor
@AllArgsConstructor
@Data
public final class Message extends EntitySuperclass {

    @Column(nullable = false)
    private String content;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "courier_author_id", referencedColumnName = "id")
    private Courier author;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "message_couriers",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "courier_id"))
    private List<Courier> couriers;

    public Message(final long id, final @NotNull String content, final @NotNull Courier author, final @NotNull @UnmodifiableView List<Courier> couriers) {
        super(id);
        this.content = content;
        this.author = author;
        this.couriers = couriers;
    }

    public String getContent() {
        return content;
    }

    public Courier getAuthor() {
        return author;
    }

    public List<Courier> getCouriers() {
        return couriers;
    }
}
