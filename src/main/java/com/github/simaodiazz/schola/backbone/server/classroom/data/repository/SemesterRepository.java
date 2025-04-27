package com.github.simaodiazz.schola.backbone.server.classroom.data.repository;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    Optional<Semester> findByActiveTrue();
    List<Semester> findByAcademicYear(Integer academicYear);
    Optional<Semester> findByAcademicYearAndSemesterNumber(Integer academicYear, Integer semesterNumber);
}
