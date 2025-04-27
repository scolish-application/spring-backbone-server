package com.github.simaodiazz.schola.backbone.server.classroom.data.model;

import com.github.simaodiazz.schola.backbone.server.database.entity.EntitySuperclass;
import com.github.simaodiazz.schola.backbone.server.course.data.model.Discipline;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@RedisHash
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity extends EntitySuperclass {

    @NotNull
    @Column(nullable = false)
    private String title;

    @Column(length = 2048)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discipline_id", nullable = false)
    private Discipline discipline;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dueDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "submissions",
            joinColumns = @JoinColumn(name = "submission_id", referencedColumnName = "id"))
    private List<Submission> submissions = new ArrayList<>();

}