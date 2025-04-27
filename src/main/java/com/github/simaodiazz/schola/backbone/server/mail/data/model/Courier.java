package com.github.simaodiazz.schola.backbone.server.mail.data.model;

import com.github.simaodiazz.schola.backbone.server.database.entity.EntitySuperclass;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

@Entity
@RedisHash
public final class Courier extends EntitySuperclass {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "author")
    private List<Message> sent;

    @ManyToMany
    @JoinTable(
            name = "courier_received_messages",
            joinColumns = @JoinColumn(name = "courier_id"),
            inverseJoinColumns = @JoinColumn(name = "message_id"))
    private List<Message> received;

    public Courier() {
        this.sent = new ArrayList<>();
        this.received = new ArrayList<>();
    }

    public Courier(final @NotNull User user) {
        this.user = user;
        this.sent = new ArrayList<>();
        this.received = new ArrayList<>();
    }

    public Courier(final @NotNull User user, final @NotNull List<Message> sent) {
        this.user = user;
        this.sent = sent;
        this.received = new ArrayList<>();
    }

    public Courier(final long id, final @NotNull User user, final @NotNull List<Message> sent) {
        super(id);
        this.user = user;
        this.sent = sent;
        this.received = new ArrayList<>();
    }

    public @NotNull User getUser() {
        return user;
    }

    public @NotNull List<Message> getSent() {
        return sent;
    }

    public @NotNull List<Message> getReceived() {
        return received;
    }
}