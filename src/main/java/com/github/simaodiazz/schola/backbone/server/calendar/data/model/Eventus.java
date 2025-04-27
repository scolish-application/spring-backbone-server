package com.github.simaodiazz.schola.backbone.server.calendar.data.model;

import com.github.simaodiazz.schola.backbone.server.database.entity.EntitySuperclass;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@RedisHash
@NoArgsConstructor
@AllArgsConstructor
@Data
public final class Eventus extends EntitySuperclass {

    @Column
    @NotNull(message = "O nome do evento não pode ser nulo")
    @Size(min = 1, max = 255, message = "O nome do evento deve ter entre 1 e 255 caracteres")
    private String name;

    @Column
    @Size(max = 2048, message = "A descrição do evento pode ter no máximo 500 caracteres")
    private String description;

    @Column
    @NotNull(message = "A data de início não pode ser nula")
    @FutureOrPresent(message = "A data de início deve ser no futuro ou no presente")
    private LocalDateTime start;

    @Column
    @NotNull(message = "A data de término não pode ser nula")
    @FutureOrPresent(message = "A data de término deve ser no futuro ou no presente")
    private LocalDateTime end;

    @OneToOne(cascade = CascadeType.ALL)
    @NotNull(message = "O utilizador precisa ser específicado")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Eventus(long id, LocalDateTime created, LocalDateTime updated, String name, String description, LocalDateTime start, LocalDateTime end, User user) {
        super(id, created, updated);
        this.name = name;
        this.description = description;
        this.start = start;
        this.end = end;
        this.user = user;
    }
}