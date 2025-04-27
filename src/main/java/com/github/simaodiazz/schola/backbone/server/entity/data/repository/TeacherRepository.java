package com.github.simaodiazz.schola.backbone.server.entity.data.repository;

import com.github.simaodiazz.schola.backbone.server.entity.data.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByEmail(String email);

    Optional<Teacher> findByNif(String nif);

    Optional<Teacher> findByUserId(Long userId);

    List<Teacher> findByLastName(String lastName);

    @Query("SELECT t FROM Teacher t WHERE LOWER(t.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(t.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Teacher> searchByName(@Param("name") String name);

    boolean existsByEmail(String email);

    boolean existsByNif(String nif);

}
