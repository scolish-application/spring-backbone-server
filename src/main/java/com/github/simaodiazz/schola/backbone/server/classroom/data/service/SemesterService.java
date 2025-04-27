package com.github.simaodiazz.schola.backbone.server.classroom.data.service;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Semester;
import com.github.simaodiazz.schola.backbone.server.classroom.data.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SemesterService  {

    private final SemesterRepository semesterRepository;

    @Caching(
            put = {@CachePut(value = "semesters", key = "#result.id")},
            evict = {
                    @CacheEvict(value = "semesters", key = "'all'"),
                    @CacheEvict(value = "semesters", key = "'active'"),
                    @CacheEvict(value = "semesters", key = "'academic-year-' + #semester.academicYear"),
                    @CacheEvict(value = "semesters", key = "'academic-year-semester-' + #semester.academicYear + '-' + #semester.semesterNumber")})
    public Semester save(Semester semester) {
        return semesterRepository.save(semester);
    }

    @Caching(evict = {
            @CacheEvict(value = "semesters", key = "#id"),
            @CacheEvict(value = "semesters", key = "'all'"),
            @CacheEvict(value = "semesters", key = "'active'"),
            @CacheEvict(value = "semesters", allEntries = true)})
    public void delete(Long id) {
        semesterRepository.deleteById(id);
    }

    @Cacheable(value = "semesters", key = "#id")
    public Optional<Semester> findById(Long id) {
        return semesterRepository.findById(id);
    }

    @Cacheable(value = "semesters", key = "'all'")
    public List<Semester> findAll() {
        return semesterRepository.findAll();
    }

    @Cacheable(value = "semesters", key = "'active'")
    public Optional<Semester> findActiveSemester() {
        return semesterRepository.findByActiveTrue();
    }

    @Cacheable(value = "semesters", key = "'academic-year-' + #academicYear")
    public List<Semester> findByAcademicYear(Integer academicYear) {
        return semesterRepository.findByAcademicYear(academicYear);
    }

    @Cacheable(value = "semesters", key = "'academic-year-semester-' + #academicYear + '-' + #semesterNumber")
    public Optional<Semester> findByAcademicYearAndSemesterNumber(Integer academicYear, Integer semesterNumber) {
        return semesterRepository.findByAcademicYearAndSemesterNumber(academicYear, semesterNumber);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "semesters", key = "'active'"),
            @CacheEvict(value = "semesters", allEntries = true)})
    public Semester activateSemester(Long id) {
        Optional<Semester> currentActive = semesterRepository.findByActiveTrue();
        currentActive.ifPresent(semester -> {
            semester.setActive(false);
            semesterRepository.save(semester);
        });

        Optional<Semester> newActive = semesterRepository.findById(id);
        if (newActive.isPresent()) {
            Semester semester = newActive.get();
            semester.setActive(true);
            return semesterRepository.save(semester);
        } else {
            throw new IllegalArgumentException("Semester not found with id: " + id);
        }
    }
}