package com.github.simaodiazz.schola.backbone.server.classroom.data.service;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Activity;
import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Semester;

import com.github.simaodiazz.schola.backbone.server.classroom.data.repository.ActivityRepository;
import com.github.simaodiazz.schola.backbone.server.course.data.model.Discipline;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    @Caching(
            put = {@CachePut(value = "activities", key = "#result.id")},
            evict = {
                    @CacheEvict(value = "activities", key = "'all'"),
                    @CacheEvict(value = "activities", key = "'discipline-' + #activity.discipline.id"),
                    @CacheEvict(value = "activities", key = "'semester-' + #activity.semester.id"),
                    @CacheEvict(value = "activities", key = "'discipline-semester-' + #activity.discipline.id + '-' + #activity.semester.id")
            }
    )
    public Activity save(Activity activity) {
        return activityRepository.save(activity);
    }

    @Caching(evict = {
            @CacheEvict(value = "activities", key = "#id"),
            @CacheEvict(value = "activities", key = "'all'"),
            @CacheEvict(value = "activities", allEntries = true)
    })
    public void delete(final long id) {
        activityRepository.deleteById(id);
    }

    @Cacheable(value = "activities", key = "#id")
    public Optional<Activity> findById(final long id) {
        return activityRepository.findById(id);
    }

    @Cacheable(value = "activities", key = "'all'")
    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

    @Cacheable(value = "activities", key = "'discipline-' + #discipline.id")
    public List<Activity> findByDiscipline(Discipline discipline) {
        return activityRepository.findByDiscipline(discipline);
    }

    @Cacheable(value = "activities", key = "'semester-' + #semester.id")
    public List<Activity> findBySemester(Semester semester) {
        return activityRepository.findBySemester(semester);
    }

    public List<Activity> findByDueDateBefore(LocalDateTime date) {
        return activityRepository.findByDueDateBefore(date);
    }

    public List<Activity> findByDueDateAfter(LocalDateTime date) {
        return activityRepository.findByDueDateAfter(date);
    }

    public List<Activity> findByDueDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return activityRepository.findByDueDateBetween(startDate, endDate);
    }

    public List<Activity> findByTitleContainingIgnoreCase(String titlePart) {
        return activityRepository.findByTitleContainingIgnoreCase(titlePart);
    }

    @Cacheable(value = "activities", key = "'no-submissions'")
    public List<Activity> findActivitiesWithNoSubmissions() {
        return activityRepository.findActivitiesWithNoSubmissions();
    }

    @Cacheable(value = "activityCounts", key = "'discipline-' + #discipline.id")
    public long countByDiscipline(Discipline discipline) {
        return activityRepository.countByDiscipline(discipline);
    }

    @Cacheable(value = "activityCounts", key = "'semester-' + #semester.id")
    public long countBySemester(Semester semester) {
        return activityRepository.countBySemester(semester);
    }

    public boolean existsByTitleAndDiscipline(String title, Discipline discipline) {
        return activityRepository.existsByTitleAndDiscipline(title, discipline);
    }
}