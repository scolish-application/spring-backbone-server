package com.github.simaodiazz.schola.backbone.server.classroom.data.service;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Proventus;
import com.github.simaodiazz.schola.backbone.server.classroom.data.repository.ProventusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProventusService {

    private final ProventusRepository evaluationResultRepository;

    @Caching(
            put = {@CachePut(value = "evaluationResults", key = "#result.id")},
            evict = {
                    @CacheEvict(value = "evaluationResults", key = "'all'"),
                    @CacheEvict(value = "evaluationResults", key = "'evaluation-' + #evaluationResult.evaluation.id"),
                    @CacheEvict(value = "evaluationResults", key = "'student-' + #evaluationResult.student.id"),
                    @CacheEvict(value = "evaluationResults", key = "'evaluation-student-' + #evaluationResult.evaluation.id + '-' + #evaluationResult.student.id"),
                    @CacheEvict(value = "studentSemesterAverages", key = "#evaluationResult.student.id + '-' + #evaluationResult.evaluation.semester.id"),
                    @CacheEvict(value = "evaluationAverages", key = "#evaluationResult.evaluation.id")
            }
    )
    public Proventus save(Proventus evaluationResult) {
        return evaluationResultRepository.save(evaluationResult);
    }

    @Caching(evict = {
            @CacheEvict(value = "evaluationResults", key = "#id"),
            @CacheEvict(value = "evaluationResults", key = "'all'"),
            @CacheEvict(value = "evaluationResults", allEntries = true),
            @CacheEvict(value = "studentSemesterAverages", allEntries = true),
            @CacheEvict(value = "evaluationAverages", allEntries = true)
    })
    public void delete(Long id) {
        evaluationResultRepository.deleteById(id);
    }

    @Cacheable(value = "evaluationResults", key = "#id")
    public Optional<Proventus> findById(Long id) {
        return evaluationResultRepository.findById(id);
    }

    @Cacheable(value = "evaluationResults", key = "'all'")
    public List<Proventus> findAll() {
        return evaluationResultRepository.findAll();
    }

    @Cacheable(value = "evaluationResults", key = "'evaluation-' + #evaluationId")
    public List<Proventus> findByEvaluationId(Long evaluationId) {
        return evaluationResultRepository.findByEvaluationId(evaluationId);
    }

    @Cacheable(value = "evaluationResults", key = "'student-' + #studentId")
    public List<Proventus> findByStudentId(Long studentId) {
        return evaluationResultRepository.findByStudentId(studentId);
    }

    @Cacheable(value = "evaluationResults", key = "'evaluation-student-' + #evaluationId + '-' + #studentId")
    public Optional<Proventus> findByEvaluationIdAndStudentId(Long evaluationId, Long studentId) {
        return evaluationResultRepository.findByEvaluationIdAndStudentId(evaluationId, studentId);
    }

    @Cacheable(value = "studentSemesterAverages", key = "#studentId + '-' + #semesterId")
    public Double calculateStudentSemesterAverage(Long studentId, Long semesterId) {
        return evaluationResultRepository.calculateStudentSemesterAverage(studentId, semesterId);
    }
}