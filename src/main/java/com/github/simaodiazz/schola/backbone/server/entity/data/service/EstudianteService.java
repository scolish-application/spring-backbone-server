package com.github.simaodiazz.schola.backbone.server.entity.data.service;

import com.github.simaodiazz.schola.backbone.server.entity.data.model.Estudiante;
import com.github.simaodiazz.schola.backbone.server.entity.data.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;

    @Autowired
    public EstudianteService(EstudianteRepository estudianteRepository) {
        this.estudianteRepository = estudianteRepository;
    }

    @Cacheable(value = "students")
    public List<Estudiante> getAllStudents() {
        return estudianteRepository.findAll();
    }

    @Cacheable(value = "studentById", key = "#id")
    public Optional<Estudiante> getStudentById(Long id) {
        return estudianteRepository.findById(id);
    }

    @Cacheable(value = "studentByNif", key = "#nif")
    public Optional<Estudiante> getStudentByNif(String nif) {
        return estudianteRepository.findByNif(nif);
    }

    @Cacheable(value = "studentsByClass", key = "#schoolClass")
    public List<Estudiante> getStudentsBySchoolClass(final long classroomId) {
        return estudianteRepository.findByClassroomId(classroomId);
    }

    @Cacheable(value = "studentsByName", key = "#name")
    public List<Estudiante> searchStudentsByName(String name) {
        return estudianteRepository.searchByName(name);
    }

    @Cacheable(value = "studentsByGuardianName", key = "#guardianName")
    public List<Estudiante> getStudentsByGuardianName(String guardianName) {
        return estudianteRepository.findByGuardianName(guardianName);
    }

    @Cacheable(value = "studentsByGuardianNif", key = "#guardianNif")
    public List<Estudiante> getStudentsByGuardianNif(String guardianNif) {
        return estudianteRepository.findByGuardianNif(guardianNif);
    }

    @CachePut(value = "studentById", key = "#result.id")
    @CacheEvict(value = {
            "students", "studentsByClass", "studentsByName",
            "studentsByGuardianName", "studentsByGuardianNif",
            "studentByNif"
    }, allEntries = true)
    public Estudiante saveStudent(Estudiante student) {
        return estudianteRepository.save(student);
    }

    @CacheEvict(value = {
            "students", "studentById", "studentsByClass", "studentsByName",
            "studentsByGuardianName", "studentsByGuardianNif",
            "studentByNif"
    }, allEntries = true)
    public void deleteStudent(Long id) {
        estudianteRepository.deleteById(id);
    }

    public boolean isNifUnique(String nif) {
        if (nif == null || nif.isEmpty()) {
            return false;
        }
        return !estudianteRepository.existsByNif(nif);
    }

    @Cacheable(value = "studentByUserId", key = "#userId")
    public Optional<Estudiante> getStudentByUserId(Long userId) {
        return estudianteRepository.findByUserId(userId);
    }
}
