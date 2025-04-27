package com.github.simaodiazz.schola.backbone.server.classroom.data.service;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Submission;
import com.github.simaodiazz.schola.backbone.server.classroom.data.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;

    @Transactional
    @Caching(
            put = {@CachePut(value = "submissions", key = "#result.id")},
            evict = {
                    @CacheEvict(value = "submissions", key = "'all'"),
                    @CacheEvict(value = "submissions", key = "'semester-' + #submission.activity.semester.id", condition = "#submission.activity != null && #submission.activity.semester != null"),
                    @CacheEvict(value = "submissions", key = "'discipline-' + #submission.activity.discipline.id", condition = "#submission.activity != null && #submission.activity.discipline != null"),
                    @CacheEvict(value = "submissions", key = "'semester-discipline-' + #submission.activity.semester.id + '-' + #submission.activity.discipline.id",
                            condition = "#submission.activity != null && #submission.activity.semester != null && #submission.activity.discipline != null")})
    public Submission save(@NotNull Submission submission) {
        if (submission.getActivity() != null &&
                submission.getActivity().getDueDate() != null &&
                submission.getSubmissionDate() != null &&
                submission.getSubmissionDate().isAfter(submission.getActivity().getDueDate())) {
            submission.setLate(true);
        }
        return submissionRepository.save(submission);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "submissions", key = "#id"),
            @CacheEvict(value = "submissions", key = "'all'"),
            @CacheEvict(value = "submissions", allEntries = true)})
    public void delete(Long id) {
        submissionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "submissions", key = "#id")
    public Optional<Submission> findById(Long id) {
        return submissionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "submissions", key = "'all'")
    public List<Submission> findAll() {
        return submissionRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "submissions", key = "'semester-' + #semesterId")
    public List<Submission> findBySemesterId(Long semesterId) {
        return submissionRepository.findBySemesterId(semesterId);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "submissions", key = "'discipline-' + #disciplineId")
    public List<Submission> findByDisciplineId(Long disciplineId) {
        return submissionRepository.findByDisciplineId(disciplineId);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "submissions", key = "'semester-discipline-' + #semesterId + '-' + #disciplineId")
    public List<Submission> findBySemesterIdAndDisciplineId(Long semesterId, Long disciplineId) {
        return submissionRepository.findBySemesterIdAndDisciplineId(semesterId, disciplineId);
    }

    @Transactional(readOnly = true)
    public List<Submission> findByDueDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return submissionRepository.findByDueDateRange(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public Page<Submission> findPastSubmissions(LocalDateTime now, Pageable pageable) {
        return submissionRepository.findPastSubmissions(now, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Submission> findUpcomingSubmissions(LocalDateTime now, Pageable pageable) {
        return submissionRepository.findUpcomingSubmissions(now, pageable);
    }
}