package com.github.simaodiazz.schola.backbone.server.entity.data.service;

import com.github.simaodiazz.schola.backbone.server.entity.data.model.Teacher;
import com.github.simaodiazz.schola.backbone.server.entity.data.repository.TeacherRepository;
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
public class TeacherService {

    private final TeacherRepository teacherRepository;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Cacheable(value = "teachers", key = "#id")
    public Optional<Teacher> getTeacherById(Long id) {
        return teacherRepository.findById(id);
    }

    @Cacheable(value = "teachersByEmail", key = "#email")
    public Optional<Teacher> getTeacherByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }

    @Cacheable(value = "teachersByNif", key = "#nif")
    public Optional<Teacher> getTeacherByNif(String nif) {
        return teacherRepository.findByNif(nif);
    }

    @Cacheable(value = "teachersByUserId", key = "#userId")
    public Optional<Teacher> getTeacherByUserId(Long userId) {
        return teacherRepository.findByUserId(userId);
    }

    @Cacheable(value = "teachersByLastName", key = "#lastName")
    public List<Teacher> getTeachersByLastName(String lastName) {
        return teacherRepository.findByLastName(lastName);
    }

    @Cacheable(value = "searchTeachers", key = "#name")
    public List<Teacher> searchTeachersByName(String name) {
        return teacherRepository.searchByName(name);
    }

    @CachePut(value = "teachers", key = "#result.id")
    @CacheEvict(value = {"teachersByEmail", "teachersByNif", "teachersByUserId", "teachersByLastName", "searchTeachers"}, allEntries = true)
    public Teacher saveTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @CacheEvict(value = {"teachers", "teachersByEmail", "teachersByNif", "teachersByUserId", "teachersByLastName", "searchTeachers"}, allEntries = true)
    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }

    public boolean isEmailUnique(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return !teacherRepository.existsByEmail(email);
    }

    public boolean isNifUnique(String nif) {
        if (nif == null || nif.isEmpty()) {
            return false;
        }
        return !teacherRepository.existsByNif(nif);
    }

    @Cacheable(value = "allTeachers")
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }
}
