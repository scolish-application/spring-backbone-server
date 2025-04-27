package com.github.simaodiazz.schola.backbone.server.classroom.data.service;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Classroom;
import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Lesson;
import com.github.simaodiazz.schola.backbone.server.classroom.data.repository.LessonRepository;
import com.github.simaodiazz.schola.backbone.server.course.data.model.Discipline;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;

    @Autowired
    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Transactional(readOnly = true)
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Lesson getLessonById(final Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with id: " + id));
    }

    @Transactional
    public Lesson createLesson(Lesson lesson) {
        validateLesson(lesson);
        return lessonRepository.save(lesson);
    }

    @Transactional
    public Lesson updateLesson(final Long id, Lesson updatedLesson) {
        Lesson existingLesson = getLessonById(id);

        existingLesson.setTitle(updatedLesson.getTitle());
        existingLesson.setDescription(updatedLesson.getDescription());
        existingLesson.setStartTime(updatedLesson.getStartTime());
        existingLesson.setEndTime(updatedLesson.getEndTime());
        existingLesson.setActive(updatedLesson.isActive());
        existingLesson.setLocation(updatedLesson.getLocation());
        existingLesson.setMaterials(updatedLesson.getMaterials());

        // Update relationships if provided
        if (updatedLesson.getDiscipline() != null) {
            existingLesson.setDiscipline(updatedLesson.getDiscipline());
        }

        if (updatedLesson.getClassroom() != null) {
            existingLesson.setClassroom(updatedLesson.getClassroom());
        }

        // Validate before saving
        validateLesson(existingLesson);
        return lessonRepository.save(existingLesson);
    }

    @Transactional
    public void deleteLesson(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new IllegalArgumentException("Lesson not found with id: " + id);
        }
        lessonRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Lesson> getLessonsByDiscipline(Discipline discipline) {
        return lessonRepository.findByDiscipline(discipline);
    }

    @Transactional(readOnly = true)
    public List<Lesson> getLessonsByClassroom(Classroom classroom) {
        return lessonRepository.findByClassroom(classroom);
    }

    @Transactional(readOnly = true)
    public List<Lesson> getActiveLessons() {
        return lessonRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Lesson> getLessonsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return lessonRepository.findByStartTimeBetween(start, end);
    }

    @Transactional(readOnly = true)
    public List<Lesson> searchLessons(String keyword) {
        return lessonRepository.searchLessonsByKeyword(keyword);
    }

    @Transactional(readOnly = true)
    public List<Lesson> getLessonsForClassroomInTimeRange(Long classroomId, LocalDateTime start, LocalDateTime end) {
        return lessonRepository.findLessonsForClassroomInTimeRange(classroomId, start, end);
    }

    private void validateLesson(@NotNull Lesson lesson) {
        if (lesson.getTitle() == null || lesson.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Lesson title cannot be empty");
        }

        if (lesson.getStartTime() == null || lesson.getEndTime() == null) {
            throw new IllegalArgumentException("Lesson start and end times must be provided");
        }

        if (lesson.getStartTime().isAfter(lesson.getEndTime())) {
            throw new IllegalArgumentException("Lesson start time must be before end time");
        }
    }
}