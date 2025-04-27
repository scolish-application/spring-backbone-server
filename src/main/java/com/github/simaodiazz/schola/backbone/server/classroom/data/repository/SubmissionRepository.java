package com.github.simaodiazz.schola.backbone.server.classroom.data.repository;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("SELECT s FROM Submission s WHERE s.activity.semester.id = :semesterId")
    List<Submission> findBySemesterId(@Param("semesterId") Long semesterId);

    @Query("SELECT s FROM Submission s WHERE s.activity.discipline.id = :disciplineId")
    List<Submission> findByDisciplineId(@Param("disciplineId") Long disciplineId);

    @Query("SELECT s FROM Submission s WHERE s.activity.semester.id = :semesterId AND s.activity.discipline.id = :disciplineId")
    List<Submission> findBySemesterIdAndDisciplineId(@Param("semesterId") Long semesterId, @Param("disciplineId") Long disciplineId);

    @Query("SELECT s FROM Submission s WHERE s.activity.dueDate BETWEEN :startDate AND :endDate")
    List<Submission> findByDueDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Submission s WHERE s.activity.dueDate < :now ORDER BY s.activity.dueDate DESC")
    Page<Submission> findPastSubmissions(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT s FROM Submission s WHERE s.activity.dueDate >= :now ORDER BY s.activity.dueDate ASC")
    Page<Submission> findUpcomingSubmissions(@Param("now") LocalDateTime now, Pageable pageable);
}