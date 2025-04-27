package com.github.simaodiazz.schola.backbone.server.classroom.data.model;

import com.github.simaodiazz.schola.backbone.server.database.entity.EntitySuperclass;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@RedisHash
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Semester extends EntitySuperclass {

    @NotNull
    @Column(nullable = false, unique = true)
    private String name;

    @NotNull
    @Min(1)
    @Max(3)
    @Column(nullable = false)
    private Integer semesterNumber;

    @NotNull
    @Min(2000)
    @Max(2100)
    @Column(nullable = false)
    private Integer academicYear;

    @NotNull
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate endDate;

    @Column
    private String description;

    @Column(nullable = false)
    private Boolean active = false;
}