package com.github.simaodiazz.schola.backbone.server.classroom.data.facade;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.*;
import com.github.simaodiazz.schola.backbone.server.classroom.data.service.*;
import com.github.simaodiazz.schola.backbone.server.course.data.model.Discipline;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Estudiante;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.PerformanceResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ClassroomFacade {

    private final ActivityService activityService;
    private final ClassroomService classroomService;
    private final EvaluationService evaluationService;
    private final LessonService lessonService;
    private final ProventusService proventusService;
    private final SemesterService semesterService;
    private final SubmissionService submissionService;

    public Optional<Semester> getCurrentSemester() {
        return semesterService.findActiveSemester();
    }

    public Optional<Semester> getSemester(final long id) {
        return semesterService.findById(id);
    }

    public List<Semester> getAllSemesters() {
        return semesterService.findAll();
    }

    public Semester saveSemester(final @NotNull Semester semester) {
        return semesterService.save(semester);
    }

    @Transactional
    public Semester activateSemester(final long semesterId) {
        return semesterService.activateSemester(semesterId);
    }

    public List<Classroom> getAllClassrooms() {
        return classroomService.getAllClassrooms();
    }

    public Classroom getClassroom(final long id) {
        return classroomService.getClassroomById(id);
    }

    public Classroom createClassroom(final @NotNull Classroom classroom) {
        return classroomService.createClassroom(classroom);
    }

    public Classroom updateClassroom(final long id, final @NotNull Classroom classroom) {
        return classroomService.updateClassroom(id, classroom);
    }

    public List<Classroom> getClassroomsBySemester(final @NotNull Semester semester) {
        return classroomService.getClassroomsBySemester(semester);
    }

    @Transactional
    public Classroom addStudentToClassroom(final long classroomId, final @NotNull Estudiante student) {
        return classroomService.addStudentToClassroom(classroomId, student);
    }

    @Transactional
    public Classroom removeStudentFromClassroom(final long classroomId, final @NotNull Estudiante student) {
        return classroomService.removeStudentFromClassroom(classroomId, student);
    }

    @Transactional
    public Classroom updateClassroomStudents(final long id, final @NotNull Set<Estudiante> students) {
        return classroomService.updateStudents(id, students);
    }

    public Optional<Activity> getActivity(final long id) {
        return activityService.findById(id);
    }

    public List<Activity> getAllActivities() {
        return activityService.findAll();
    }

    public List<Activity> getActivitiesByDiscipline(Discipline discipline) {
        return activityService.findByDiscipline(discipline);
    }

    public List<Activity> getActivitiesBySemester(Semester semester) {
        return activityService.findBySemester(semester);
    }

    public Activity saveActivity(Activity activity) {
        return activityService.save(activity);
    }

    public void deleteActivity(final long id) {
        activityService.delete(id);
    }

    public List<Activity> getActivitiesDueBefore(LocalDateTime date) {
        return activityService.findByDueDateBefore(date);
    }

    public List<Activity> getActivitiesDueAfter(LocalDateTime date) {
        return activityService.findByDueDateAfter(date);
    }

    public List<Lesson> getAllLessons() {
        return lessonService.getAllLessons();
    }

    public Lesson getLesson(final long id) {
        return lessonService.getLessonById(id);
    }

    public Lesson createLesson(Lesson lesson) {
        return lessonService.createLesson(lesson);
    }

    public Lesson updateLesson(final long id, Lesson lesson) {
        return lessonService.updateLesson(id, lesson);
    }

    public List<Lesson> getLessonsByDiscipline(Discipline discipline) {
        return lessonService.getLessonsByDiscipline(discipline);
    }

    public List<Lesson> getLessonsByClassroom(Classroom classroom) {
        return lessonService.getLessonsByClassroom(classroom);
    }

    public List<Lesson> getLessonsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return lessonService.getLessonsBetweenDates(start, end);
    }

    public Optional<Evaluation> getEvaluation(Long id) {
        return evaluationService.findById(id);
    }

    public List<Evaluation> getAllEvaluations() {
        return evaluationService.findAll();
    }

    public List<Evaluation> getEvaluationsBySemester(Long semesterId) {
        return evaluationService.findBySemesterId(semesterId);
    }

    public List<Evaluation> getEvaluationsByDiscipline(Long disciplineId) {
        return evaluationService.findByDisciplineId(disciplineId);
    }

    public Evaluation saveEvaluation(Evaluation evaluation) {
        return evaluationService.save(evaluation);
    }

    public Double calculateEvaluationAverage(Long evaluationId) {
        return evaluationService.calculateEvaluationAverage(evaluationId);
    }

    public Optional<Submission> getSubmission(Long id) {
        return submissionService.findById(id);
    }

    public List<Submission> getAllSubmissions() {
        return submissionService.findAll();
    }

    public List<Submission> getSubmissionsBySemester(Long semesterId) {
        return submissionService.findBySemesterId(semesterId);
    }

    public List<Submission> getSubmissionsByDiscipline(Long disciplineId) {
        return submissionService.findByDisciplineId(disciplineId);
    }

    public Submission saveSubmission(Submission submission) {
        return submissionService.save(submission);
    }

    public Page<Submission> getPastSubmissions(LocalDateTime now, Pageable pageable) {
        return submissionService.findPastSubmissions(now, pageable);
    }

    public Page<Submission> getUpcomingSubmissions(LocalDateTime now, Pageable pageable) {
        return submissionService.findUpcomingSubmissions(now, pageable);
    }

    public Optional<Proventus> getProventus(Long id) {
        return proventusService.findById(id);
    }

    public List<Proventus> getAllProventus() {
        return proventusService.findAll();
    }

    public List<Proventus> getProventusByEvaluation(Long evaluationId) {
        return proventusService.findByEvaluationId(evaluationId);
    }

    public List<Proventus> getProventusByStudent(Long studentId) {
        return proventusService.findByStudentId(studentId);
    }

    public Proventus saveProventus(Proventus proventus) {
        return proventusService.save(proventus);
    }

    public Double calculateStudentSemesterAverage(Long studentId, Long semesterId) {
        return proventusService.calculateStudentSemesterAverage(studentId, semesterId);
    }

    public PerformanceResponse getStudentPerformanceSummary(Long studentId, Long semesterId) {
        Double semesterAverage = proventusService.calculateStudentSemesterAverage(studentId, semesterId);
        List<Proventus> evaluationResults = proventusService.findByStudentId(studentId);

        // Filter evaluations for the specific semester
        List<Proventus> semesterResults = evaluationResults.stream()
                .filter(result -> result.getEvaluation().getSemester().getId() == semesterId)
                .toList();

        return new PerformanceResponse(studentId, semesterId, semesterAverage, semesterResults);
    }
}