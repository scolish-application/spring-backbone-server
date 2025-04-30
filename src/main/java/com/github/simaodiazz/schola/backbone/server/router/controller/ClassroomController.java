package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.classroom.data.facade.ClassroomFacade;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.PerformanceResponse;
import com.github.simaodiazz.schola.backbone.server.classroom.data.model.*;
import com.github.simaodiazz.schola.backbone.server.course.data.model.Discipline;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Estudiante;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/classroom")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomFacade classroomFacade;

    // Semester endpoints
    @GetMapping("/semesters/current")
    public ResponseEntity<Semester> getCurrentSemester() {
        return classroomFacade.getCurrentSemester()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/semesters/{id}")
    public ResponseEntity<Semester> getSemester(@PathVariable long id) {
        return classroomFacade.getSemester(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/semesters")
    public ResponseEntity<List<Semester>> getAllSemesters() {
        return ResponseEntity.ok(classroomFacade.getAllSemesters());
    }

    @PostMapping("/semesters")
    public ResponseEntity<Semester> createSemester(@RequestBody Semester semester) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classroomFacade.saveSemester(semester));
    }

    @PutMapping("/semesters/{id}")
    public ResponseEntity<Semester> updateSemester(@PathVariable long id, @RequestBody Semester semester) {
        semester.setId(id);
        return ResponseEntity.ok(classroomFacade.saveSemester(semester));
    }

    @PostMapping("/semesters/{id}/activate")
    public ResponseEntity<Semester> activateSemester(@PathVariable long id) {
        return ResponseEntity.ok(classroomFacade.activateSemester(id));
    }

    // Classroom endpoints
    @GetMapping("/classrooms")
    public ResponseEntity<List<Classroom>> getAllClassrooms() {
        return ResponseEntity.ok(classroomFacade.getAllClassrooms());
    }

    @GetMapping("/classrooms/{id}")
    public ResponseEntity<Classroom> getClassroom(@PathVariable long id) {
        try {
            return ResponseEntity.ok(classroomFacade.getClassroom(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/classrooms")
    public ResponseEntity<Classroom> createClassroom(@RequestBody Classroom classroom) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classroomFacade.createClassroom(classroom));
    }

    @PutMapping("/classrooms/{id}")
    public ResponseEntity<Classroom> updateClassroom(@PathVariable long id, @RequestBody Classroom classroom) {
        return ResponseEntity.ok(classroomFacade.updateClassroom(id, classroom));
    }

    @GetMapping("/classrooms/semester/{semesterId}")
    public ResponseEntity<List<Classroom>> getClassroomsBySemester(@PathVariable long semesterId) {
        return classroomFacade.getSemester(semesterId)
                .map(semester -> ResponseEntity.ok(classroomFacade.getClassroomsBySemester(semester)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/classrooms/{id}/students")
    public ResponseEntity<Classroom> addStudentToClassroom(@PathVariable long id, @RequestBody Estudiante student) {
        return ResponseEntity.ok(classroomFacade.addStudentToClassroom(id, student));
    }

    @DeleteMapping("/classrooms/{id}/students/{studentId}")
    public ResponseEntity<Classroom> removeStudentFromClassroom(@PathVariable long id, @PathVariable long studentId) {
        Estudiante student = new Estudiante();
        student.setId(studentId);
        return ResponseEntity.ok(classroomFacade.removeStudentFromClassroom(id, student));
    }

    @PutMapping("/classrooms/{id}/students")
    public ResponseEntity<Classroom> updateClassroomStudents(@PathVariable long id, @RequestBody Set<Estudiante> students) {
        return ResponseEntity.ok(classroomFacade.updateClassroomStudents(id, students));
    }

    // Activity endpoints
    @GetMapping("/activities/{id}")
    public ResponseEntity<Activity> getActivity(@PathVariable long id) {
        return classroomFacade.getActivity(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/activities")
    public ResponseEntity<List<Activity>> getAllActivities() {
        return ResponseEntity.ok(classroomFacade.getAllActivities());
    }

    @GetMapping("/activities/discipline/{disciplineId}")
    public ResponseEntity<List<Activity>> getActivitiesByDiscipline(@PathVariable long disciplineId) {
        Discipline discipline = new Discipline();
        discipline.setId(disciplineId);
        return ResponseEntity.ok(classroomFacade.getActivitiesByDiscipline(discipline));
    }

    @GetMapping("/activities/semester/{semesterId}")
    public ResponseEntity<List<Activity>> getActivitiesBySemester(@PathVariable long semesterId) {
        return classroomFacade.getSemester(semesterId)
                .map(semester -> ResponseEntity.ok(classroomFacade.getActivitiesBySemester(semester)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/activities")
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classroomFacade.saveActivity(activity));
    }

    @PutMapping("/activities/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable long id, @RequestBody Activity activity) {
        activity.setId(id);
        return ResponseEntity.ok(classroomFacade.saveActivity(activity));
    }

    @DeleteMapping("/activities/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable long id) {
        classroomFacade.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/activities/due-before")
    public ResponseEntity<List<Activity>> getActivitiesDueBefore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(classroomFacade.getActivitiesDueBefore(date));
    }

    @GetMapping("/activities/due-after")
    public ResponseEntity<List<Activity>> getActivitiesDueAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(classroomFacade.getActivitiesDueAfter(date));
    }

    // Lesson endpoints
    @GetMapping("/lessons")
    public ResponseEntity<List<Lesson>> getAllLessons() {
        return ResponseEntity.ok(classroomFacade.getAllLessons());
    }

    @GetMapping("/lessons/{id}")
    public ResponseEntity<Lesson> getLesson(@PathVariable long id) {
        try {
            return ResponseEntity.ok(classroomFacade.getLesson(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/lessons")
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classroomFacade.createLesson(lesson));
    }

    @PutMapping("/lessons/{id}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable long id, @RequestBody Lesson lesson) {
        return ResponseEntity.ok(classroomFacade.updateLesson(id, lesson));
    }

    @GetMapping("/lessons/discipline/{disciplineId}")
    public ResponseEntity<List<Lesson>> getLessonsByDiscipline(@PathVariable long disciplineId) {
        Discipline discipline = new Discipline();
        discipline.setId(disciplineId);
        return ResponseEntity.ok(classroomFacade.getLessonsByDiscipline(discipline));
    }

    @GetMapping("/lessons/classroom/{classroomId}")
    public ResponseEntity<List<Lesson>> getLessonsByClassroom(@PathVariable long classroomId) {
        try {
            Classroom classroom = classroomFacade.getClassroom(classroomId);
            return ResponseEntity.ok(classroomFacade.getLessonsByClassroom(classroom));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/lessons/between-dates")
    public ResponseEntity<List<Lesson>> getLessonsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(classroomFacade.getLessonsBetweenDates(start, end));
    }

    // Evaluation endpoints
    @GetMapping("/evaluations/{id}")
    public ResponseEntity<Evaluation> getEvaluation(@PathVariable long id) {
        return classroomFacade.getEvaluation(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/evaluations")
    public ResponseEntity<List<Evaluation>> getAllEvaluations() {
        return ResponseEntity.ok(classroomFacade.getAllEvaluations());
    }

    @GetMapping("/evaluations/semester/{semesterId}")
    public ResponseEntity<List<Evaluation>> getEvaluationsBySemester(@PathVariable long semesterId) {
        return ResponseEntity.ok(classroomFacade.getEvaluationsBySemester(semesterId));
    }

    @GetMapping("/evaluations/discipline/{disciplineId}")
    public ResponseEntity<List<Evaluation>> getEvaluationsByDiscipline(@PathVariable long disciplineId) {
        return ResponseEntity.ok(classroomFacade.getEvaluationsByDiscipline(disciplineId));
    }

    @PostMapping("/evaluations")
    public ResponseEntity<Evaluation> createEvaluation(@RequestBody Evaluation evaluation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classroomFacade.saveEvaluation(evaluation));
    }

    @PutMapping("/evaluations/{id}")
    public ResponseEntity<Evaluation> updateEvaluation(@PathVariable long id, @RequestBody Evaluation evaluation) {
        evaluation.setId(id);
        return ResponseEntity.ok(classroomFacade.saveEvaluation(evaluation));
    }

    @GetMapping("/evaluations/{id}/average")
    public ResponseEntity<Double> calculateEvaluationAverage(@PathVariable long id) {
        return ResponseEntity.ok(classroomFacade.calculateEvaluationAverage(id));
    }

    // Submission endpoints
    @GetMapping("/submissions/{id}")
    public ResponseEntity<Submission> getSubmission(@PathVariable long id) {
        return classroomFacade.getSubmission(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/submissions")
    public ResponseEntity<List<Submission>> getAllSubmissions() {
        return ResponseEntity.ok(classroomFacade.getAllSubmissions());
    }

    @GetMapping("/submissions/semester/{semesterId}")
    public ResponseEntity<List<Submission>> getSubmissionsBySemester(@PathVariable long semesterId) {
        return ResponseEntity.ok(classroomFacade.getSubmissionsBySemester(semesterId));
    }

    @GetMapping("/submissions/discipline/{disciplineId}")
    public ResponseEntity<List<Submission>> getSubmissionsByDiscipline(@PathVariable long disciplineId) {
        return ResponseEntity.ok(classroomFacade.getSubmissionsByDiscipline(disciplineId));
    }

    @PostMapping("/submissions")
    public ResponseEntity<Submission> createSubmission(@RequestBody Submission submission) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classroomFacade.saveSubmission(submission));
    }

    @PutMapping("/submissions/{id}")
    public ResponseEntity<Submission> updateSubmission(@PathVariable long id, @RequestBody Submission submission) {
        submission.setId(id);
        return ResponseEntity.ok(classroomFacade.saveSubmission(submission));
    }

    @GetMapping("/submissions/past")
    public ResponseEntity<Page<Submission>> getPastSubmissions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime now,
            Pageable pageable) {
        if (now == null) {
            now = LocalDateTime.now();
        }
        return ResponseEntity.ok(classroomFacade.getPastSubmissions(now, pageable));
    }

    @GetMapping("/submissions/upcoming")
    public ResponseEntity<Page<Submission>> getUpcomingSubmissions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime now,
            Pageable pageable) {
        if (now == null) {
            now = LocalDateTime.now();
        }
        return ResponseEntity.ok(classroomFacade.getUpcomingSubmissions(now, pageable));
    }

    // Proventus endpoints
    @GetMapping("/proventus/{id}")
    public ResponseEntity<Proventus> getProventus(@PathVariable long id) {
        return classroomFacade.getProventus(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/proventus")
    public ResponseEntity<List<Proventus>> getAllProventus() {
        return ResponseEntity.ok(classroomFacade.getAllProventus());
    }

    @GetMapping("/proventus/evaluation/{evaluationId}")
    public ResponseEntity<List<Proventus>> getProventusByEvaluation(@PathVariable long evaluationId) {
        return ResponseEntity.ok(classroomFacade.getProventusByEvaluation(evaluationId));
    }

    @GetMapping("/proventus/student/{studentId}")
    public ResponseEntity<List<Proventus>> getProventusByStudent(@PathVariable long studentId) {
        return ResponseEntity.ok(classroomFacade.getProventusByStudent(studentId));
    }

    @PostMapping("/proventus")
    public ResponseEntity<Proventus> createProventus(@RequestBody Proventus proventus) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classroomFacade.saveProventus(proventus));
    }

    @PutMapping("/proventus/{id}")
    public ResponseEntity<Proventus> updateProventus(@PathVariable long id, @RequestBody Proventus proventus) {
        proventus.setId(id);
        return ResponseEntity.ok(classroomFacade.saveProventus(proventus));
    }

    @GetMapping("/students/{studentId}/semesters/{semesterId}/average")
    public ResponseEntity<Double> calculateStudentSemesterAverage(
            @PathVariable long studentId,
            @PathVariable long semesterId) {
        return ResponseEntity.ok(classroomFacade.calculateStudentSemesterAverage(studentId, semesterId));
    }

    @GetMapping("/students/{studentId}/semesters/{semesterId}/performance")
    public ResponseEntity<PerformanceResponse> getStudentPerformanceSummary(
            @PathVariable long studentId,
            @PathVariable long semesterId) {
        return ResponseEntity.ok(classroomFacade.getStudentPerformanceSummary(studentId, semesterId));
    }
}