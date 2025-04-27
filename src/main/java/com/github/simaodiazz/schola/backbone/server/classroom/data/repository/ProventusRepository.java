package com.github.simaodiazz.schola.backbone.server.classroom.data.repository;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Proventus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProventusRepository extends JpaRepository<Proventus, Long> {
    List<Proventus> findByEvaluationId(Long evaluationId);
    List<Proventus> findByStudentId(Long studentId);
    Optional<Proventus> findByEvaluationIdAndStudentId(Long evaluationId, Long studentId);

    @Query("SELECT AVG(p.grade) FROM Proventus p WHERE p.student.id = :studentId AND p.evaluation.semester.id = :semesterId")
    Double calculateStudentSemesterAverage(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);

    @Query("SELECT AVG(p.grade) FROM Proventus p WHERE p.evaluation.id = :evaluationId")
    Double calculateEvaluationAverage(@Param("evaluationId") Long evaluationId);
}