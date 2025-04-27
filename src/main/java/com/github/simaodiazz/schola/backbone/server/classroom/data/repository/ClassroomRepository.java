package com.github.simaodiazz.schola.backbone.server.classroom.data.repository;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Classroom;
import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Semester;
import com.github.simaodiazz.schola.backbone.server.course.data.model.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    List<Classroom> findByDiscipline(Discipline discipline);

    List<Classroom> findBySemester(Semester semester);

    List<Classroom> findByActiveTrue();

    Optional<Classroom> findByCode(String code);

    @Query("SELECT c FROM Classroom c JOIN c.students s WHERE s.id = :studentId")
    List<Classroom> findClassroomsByStudentId(@Param("studentId") String studentId);

    @Query("SELECT c FROM Classroom c JOIN FETCH c.discipline JOIN FETCH c.semester WHERE c.id = :id")
    Optional<Classroom> findByIdWithDetails(@Param("id") long id);

    @Query("SELECT c FROM Classroom c WHERE c.name LIKE %:keyword% OR c.description LIKE %:keyword%")
    List<Classroom> searchClassroomsByKeyword(@Param("keyword") String keyword);

    List<Classroom> findByDisciplineAndSemester(Discipline discipline, Semester semester);

    @Query("SELECT COUNT(s) FROM Classroom c JOIN c.students s WHERE c.id = :classroomId")
    Integer countStudentsInClassroom(@Param("classroomId") final long classroomId);

}