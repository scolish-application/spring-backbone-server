package com.github.simaodiazz.schola.backbone.server.entity.data.repository;

import com.github.simaodiazz.schola.backbone.server.entity.data.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByNif(String nif);

    List<Estudiante> findBySchoolClass(String schoolClass);

    @Query("SELECT s FROM Estudiante s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Estudiante> searchByName(@Param("name") String name);

    @Query("SELECT s FROM Estudiante s JOIN s.guardians g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :guardianName, '%'))")
    List<Estudiante> findByGuardianName(@Param("guardianName") String guardianName);

    @Query("SELECT s FROM Estudiante s JOIN s.guardians g WHERE g.nif = :guardianNif")
    List<Estudiante> findByGuardianNif(@Param("guardianNif") String guardianNif);

    boolean existsByNif(String nif);

    Optional<Estudiante> findByUserId(long id);

}