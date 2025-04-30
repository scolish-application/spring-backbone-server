package com.github.simaodiazz.schola.backbone.server.classroom.data.model;

import com.github.simaodiazz.schola.backbone.server.course.data.model.Course;
import com.github.simaodiazz.schola.backbone.server.course.data.model.Discipline;
import com.github.simaodiazz.schola.backbone.server.database.entity.EntitySuperclass;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Estudiante;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@RedisHash
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Classroom extends EntitySuperclass {

    @NotNull
    @Column(nullable = false)
    private String name;

    @Column(length = 1024)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(nullable = false)
    private Boolean active = true;

    @Column
    private String code;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "classroom_students",
            joinColumns = @JoinColumn(name = "classroom_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private Set<Estudiante> students = new HashSet<>();

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "classroom_activities",
            joinColumns = @JoinColumn(name = "classroom_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id"))
    private List<Activity> activities = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "classroom_evaluations",
            joinColumns = @JoinColumn(name = "classroom_id"),
            inverseJoinColumns = @JoinColumn(name = "evaluation_id"))
    private List<Evaluation> evaluations = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "course_id", referencedColumnName = "id")
    private Course course;

    @Column
    private String syllabus;

    @Column
    private Integer capacity;

    @Column(nullable = false)
    private String type;

}
