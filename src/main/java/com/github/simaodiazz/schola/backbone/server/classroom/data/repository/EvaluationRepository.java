package com.github.simaodiazz.schola.backbone.server.classroom.data.repository;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findBySemesterId(Long semesterId);
    List<Evaluation> findByDisciplineId(Long disciplineId);
    List<Evaluation> findBySemesterIdAndDisciplineId(Long semesterId, Long disciplineId);

    @Query("SELECT e FROM Evaluation e WHERE e.evaluationDate BETWEEN :startDate AND :endDate")
    List<Evaluation> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
