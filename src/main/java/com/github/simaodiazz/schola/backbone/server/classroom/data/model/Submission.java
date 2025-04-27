package com.github.simaodiazz.schola.backbone.server.classroom.data.model;

import com.github.simaodiazz.schola.backbone.server.database.entity.EntitySuperclass;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Estudiante;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@RedisHash
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Submission extends EntitySuperclass {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Activity activity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Estudiante student;

    @NotNull
    @Column(nullable = false)
    private String submissionLink;

    @Column(length = 1024)
    private String comments;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime submissionDate;

    @Column(nullable = false)
    private Boolean late = false;

}