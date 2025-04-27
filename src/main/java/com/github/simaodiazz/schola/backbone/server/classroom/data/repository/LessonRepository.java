package com.github.simaodiazz.schola.backbone.server.classroom.data.repository;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Classroom;
import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Lesson;
import com.github.simaodiazz.schola.backbone.server.course.data.model.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByDiscipline(Discipline discipline);

    List<Lesson> findByClassroom(Classroom classroom);

    List<Lesson> findByActiveTrue();

    List<Lesson> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT l FROM Lesson l WHERE l.classroom.id = :classroomId AND l.startTime >= :startDate AND l.endTime <= :endDate")
    List<Lesson> findLessonsForClassroomInTimeRange(
            @Param("classroomId") Long classroomId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT l FROM Lesson l WHERE l.title LIKE %:keyword% OR l.description LIKE %:keyword%")
    List<Lesson> searchLessonsByKeyword(@Param("keyword") String keyword);
}