package com.github.simaodiazz.schola.backbone.server.course.data.model;

import com.github.simaodiazz.schola.backbone.server.database.entity.EntitySuperclass;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@EqualsAndHashCode(callSuper = true)
@Entity
@RedisHash
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Category extends EntitySuperclass {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, length = 2048)
    private String description;

}
