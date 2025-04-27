package com.github.simaodiazz.schola.backbone.server.course.data.service;

import com.github.simaodiazz.schola.backbone.server.course.data.model.Course;
import com.github.simaodiazz.schola.backbone.server.course.data.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Cacheable(value = "courses", key = "#id")
    public Optional<Course> getCourseById(final Long id) {
        return courseRepository.findById(id);
    }

    @Cacheable(value = "coursesByName", key = "#name")
    public List<Course> searchCoursesByName(final String name) {
        return courseRepository.findByNameContainingIgnoreCase(name);
    }

    @CachePut(value = "courses", key = "#result.id")
    public Course saveCourse(final Course course) {
        return courseRepository.save(course);
    }

    @CacheEvict(value = "courses", key = "#id")
    public void deleteCourse(final Long id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}
