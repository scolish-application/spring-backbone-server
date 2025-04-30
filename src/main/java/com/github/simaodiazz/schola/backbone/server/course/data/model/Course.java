package com.github.simaodiazz.schola.backbone.server.course.data.model;

import com.github.simaodiazz.schola.backbone.server.database.entity.EntitySuperclass;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Teacher;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@RedisHash
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Course extends EntitySuperclass {

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(nullable = false, length = 2048)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id")
    private Teacher director;

    @NotNull
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "course_disciplines",
            joinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "discipline_id", referencedColumnName = "id"))
    private List<Discipline> disciplines;

}
