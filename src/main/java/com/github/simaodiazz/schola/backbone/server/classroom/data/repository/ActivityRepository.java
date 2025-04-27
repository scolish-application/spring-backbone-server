package com.github.simaodiazz.schola.backbone.server.classroom.data.repository;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Activity;
import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Semester;
import com.github.simaodiazz.schola.backbone.server.course.data.model.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByDiscipline(Discipline discipline);

    List<Activity> findBySemester(Semester semester);

    List<Activity> findByDisciplineAndSemester(Discipline discipline, Semester semester);

    List<Activity> findByDueDateBefore(LocalDateTime date);

    List<Activity> findByDueDateAfter(LocalDateTime date);

    List<Activity> findByDueDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Activity> findByTitleContainingIgnoreCase(String titlePart);

    List<Activity> findByTitleAndDiscipline(String title, Discipline discipline);

    @Query("SELECT a FROM Activity a WHERE a.submissions IS EMPTY")
    List<Activity> findActivitiesWithNoSubmissions();

    @Query("SELECT a FROM Activity a WHERE a.discipline.id = :disciplineId")
    List<Activity> findByDisciplineId(@Param("disciplineId") Long disciplineId);

    @Query("SELECT a FROM Activity a WHERE a.semester.id = :semesterId")
    List<Activity> findBySemesterId(@Param("semesterId") Long semesterId);

    long countByDiscipline(Discipline discipline);

    long countBySemester(Semester semester);

    boolean existsByTitleAndDiscipline(String title, Discipline discipline);
}