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

    @Cacheable(value = "teachersByNif", key = "#nif")
    public Optional<Teacher> getTeacherByNif(String nif) {
        return teacherRepository.findByNif(nif);
    }

    @Cacheable(value = "teachersByUserId", key = "#userId")
    public Optional<Teacher> getTeacherByUserId(Long userId) {
        return teacherRepository.findByUserId(userId);
    }

    @Cacheable(value = "searchTeachers", key = "#name")
    public List<Teacher> getTeacherByName(String name) {
        return teacherRepository.findByName(name);
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
