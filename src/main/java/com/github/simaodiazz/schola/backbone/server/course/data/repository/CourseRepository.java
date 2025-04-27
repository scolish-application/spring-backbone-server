package com.github.simaodiazz.schola.backbone.server.course.data.repository;

import com.github.simaodiazz.schola.backbone.server.course.data.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByNameContainingIgnoreCase(String name);

}
