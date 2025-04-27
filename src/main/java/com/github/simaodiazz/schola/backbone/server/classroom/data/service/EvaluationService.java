package com.github.simaodiazz.schola.backbone.server.classroom.data.service;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Evaluation;
import com.github.simaodiazz.schola.backbone.server.classroom.data.repository.EvaluationRepository;
import com.github.simaodiazz.schola.backbone.server.classroom.data.repository.ProventusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final ProventusRepository proventusRepository;

    @Caching(
            put = {@CachePut(value = "evaluations", key = "#result.id")},
            evict = {
                    @CacheEvict(value = "evaluations", key = "'all'"),
                    @CacheEvict(value = "evaluations", key = "'semester-' + #evaluation.semester.id"),
                    @CacheEvict(value = "evaluations", key = "'discipline-' + #evaluation.discipline.id"),
                    @CacheEvict(value = "evaluations", key = "'semester-discipline-' + #evaluation.semester.id + '-' + #evaluation.discipline.id")
            }
    )
    public Evaluation save(Evaluation evaluation) {
        return evaluationRepository.save(evaluation);
    }

    @Caching(evict = {
            @CacheEvict(value = "evaluations", key = "#id"),
            @CacheEvict(value = "evaluations", key = "'all'"),
            @CacheEvict(value = "evaluations", allEntries = true),
            @CacheEvict(value = "evaluationAverages", key = "#id")
    })
    public void delete(Long id) {
        evaluationRepository.deleteById(id);
    }

    @Cacheable(value = "evaluations", key = "#id")
    public Optional<Evaluation> findById(Long id) {
        return evaluationRepository.findById(id);
    }

    @Cacheable(value = "evaluations", key = "'all'")
    public List<Evaluation> findAll() {
        return evaluationRepository.findAll();
    }

    @Cacheable(value = "evaluations", key = "'semester-' + #semesterId")
    public List<Evaluation> findBySemesterId(Long semesterId) {
        return evaluationRepository.findBySemesterId(semesterId);
    }

    @Cacheable(value = "evaluations", key = "'discipline-' + #disciplineId")
    public List<Evaluation> findByDisciplineId(Long disciplineId) {
        return evaluationRepository.findByDisciplineId(disciplineId);
    }

    @Cacheable(value = "evaluations", key = "'semester-discipline-' + #semesterId + '-' + #disciplineId")
    public List<Evaluation> findBySemesterIdAndDisciplineId(Long semesterId, Long disciplineId) {
        return evaluationRepository.findBySemesterIdAndDisciplineId(semesterId, disciplineId);
    }

    public List<Evaluation> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return evaluationRepository.findByDateRange(startDate, endDate);
    }

    @Cacheable(value = "evaluationAverages", key = "#evaluationId")
    public Double calculateEvaluationAverage(Long evaluationId) {
        return proventusRepository.calculateEvaluationAverage(evaluationId);
    }
}