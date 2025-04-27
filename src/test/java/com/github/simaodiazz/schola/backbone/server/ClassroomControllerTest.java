package com.github.simaodiazz.schola.backbone.server;

import com.github.simaodiazz.schola.backbone.server.classroom.data.facade.ClassroomFacade;
import com.github.simaodiazz.schola.backbone.server.classroom.data.model.*;
import com.github.simaodiazz.schola.backbone.server.course.data.model.Discipline;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Estudiante;
import com.github.simaodiazz.schola.backbone.server.router.controller.ClassroomController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassroomControllerTest {

    @Mock
    private ClassroomFacade classroomFacade;

    @InjectMocks
    private ClassroomController classroomController;

    private Semester semester;
    private Classroom classroom;
    private Activity activity;
    private Lesson lesson;
    private Evaluation evaluation;
    private Submission submission;
    private Proventus proventus;
    private Estudiante student;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        semester = new Semester();
        semester.setId(1L);
        semester.setName("Spring 2025");

        Discipline discipline = new Discipline();
        discipline.setId(1L);
        discipline.setName("Mathematics");

        classroom = new Classroom();
        classroom.setId(1L);
        classroom.setName("Advanced Math");
        classroom.setDiscipline(discipline);
        classroom.setSemester(semester);

        student = new Estudiante();
        student.setId(1L);
        student.setName("John Doe");

        activity = new Activity();
        activity.setId(1L);
        activity.setTitle("Calculus Assignment");
        activity.setDiscipline(discipline);
        activity.setDueDate(now.plusDays(7));

        lesson = new Lesson();
        lesson.setId(1L);
        lesson.setTitle("Differential Equations");
        lesson.setClassroom(classroom);
        lesson.setStartTime(now);
        lesson.setEndTime(now.plusHours(2));

        evaluation = new Evaluation();
        evaluation.setId(1L);
        evaluation.setTitle("Midterm Exam");
        evaluation.setDiscipline(discipline);
        evaluation.setSemester(semester);

        submission = new Submission();
        submission.setId(1L);
        submission.setActivity(activity);
        submission.setStudent(student);
        submission.setSubmissionDate(now);

        proventus = new Proventus();
        proventus.setId(1L);
        proventus.setEvaluation(evaluation);
        proventus.setStudent(student);
        proventus.setGrade(85.5);
    }

    @Test
    void getCurrentSemester_ShouldReturnSemester_WhenExists() {
        // Given
        when(classroomFacade.getCurrentSemester()).thenReturn(Optional.of(semester));

        // When
        ResponseEntity<Semester> response = classroomController.getCurrentSemester();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(semester);
    }

    @Test
    void getCurrentSemester_ShouldReturnNotFound_WhenNotExists() {
        // Given
        when(classroomFacade.getCurrentSemester()).thenReturn(Optional.empty());

        // When
        ResponseEntity<Semester> response = classroomController.getCurrentSemester();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getSemester_ShouldReturnSemester_WhenExists() {
        // Given
        when(classroomFacade.getSemester(1L)).thenReturn(Optional.of(semester));

        // When
        ResponseEntity<Semester> response = classroomController.getSemester(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(semester);
    }

    @Test
    void createSemester_ShouldReturnCreatedSemester() {
        // Given
        when(classroomFacade.saveSemester(any(Semester.class))).thenReturn(semester);

        // When
        ResponseEntity<Semester> response = classroomController.createSemester(semester);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(semester);
    }

    @Test
    void activateSemester_ShouldReturnActivatedSemester() {
        // Given
        when(classroomFacade.activateSemester(1L)).thenReturn(semester);

        // When
        ResponseEntity<Semester> response = classroomController.activateSemester(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(semester);
    }

    @Test
    void getClassroom_ShouldReturnClassroom_WhenExists() {
        // Given
        when(classroomFacade.getClassroom(1L)).thenReturn(classroom);

        // When
        ResponseEntity<Classroom> response = classroomController.getClassroom(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(classroom);
    }

    @Test
    void createClassroom_ShouldReturnCreatedClassroom() {
        // Given
        when(classroomFacade.createClassroom(any(Classroom.class))).thenReturn(classroom);

        // When
        ResponseEntity<Classroom> response = classroomController.createClassroom(classroom);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(classroom);
    }

    @Test
    void addStudentToClassroom_ShouldReturnUpdatedClassroom() {
        // Given
        when(classroomFacade.addStudentToClassroom(eq(1L), any(Estudiante.class))).thenReturn(classroom);

        // When
        ResponseEntity<Classroom> response = classroomController.addStudentToClassroom(1L, student);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(classroom);
    }

    @Test
    void getActivity_ShouldReturnActivity_WhenExists() {
        // Given
        when(classroomFacade.getActivity(1L)).thenReturn(Optional.of(activity));

        // When
        ResponseEntity<Activity> response = classroomController.getActivity(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(activity);
    }

    @Test
    void createActivity_ShouldReturnCreatedActivity() {
        // Given
        when(classroomFacade.saveActivity(any(Activity.class))).thenReturn(activity);

        // When
        ResponseEntity<Activity> response = classroomController.createActivity(activity);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(activity);
    }

    @Test
    void deleteActivity_ShouldReturnNoContent() {
        // When
        ResponseEntity<Void> response = classroomController.deleteActivity(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(classroomFacade).deleteActivity(1L);
    }

    @Test
    void getLesson_ShouldReturnLesson_WhenExists() {
        // Given
        when(classroomFacade.getLesson(1L)).thenReturn(lesson);

        // When
        ResponseEntity<Lesson> response = classroomController.getLesson(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(lesson);
    }

    @Test
    void getLessonsBetweenDates_ShouldReturnLessons() {
        // Given
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(1);
        List<Lesson> lessons = List.of(lesson);

        when(classroomFacade.getLessonsBetweenDates(start, end)).thenReturn(lessons);

        // When
        ResponseEntity<List<Lesson>> response = classroomController.getLessonsBetweenDates(start, end);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(lessons);
    }

    @Test
    void getEvaluation_ShouldReturnEvaluation_WhenExists() {
        // Given
        when(classroomFacade.getEvaluation(1L)).thenReturn(Optional.of(evaluation));

        // When
        ResponseEntity<Evaluation> response = classroomController.getEvaluation(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(evaluation);
    }

    @Test
    void calculateEvaluationAverage_ShouldReturnAverage() {
        // Given
        Double average = 85.0;
        when(classroomFacade.calculateEvaluationAverage(1L)).thenReturn(average);

        // When
        ResponseEntity<Double> response = classroomController.calculateEvaluationAverage(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(average);
    }

    @Test
    void getSubmission_ShouldReturnSubmission_WhenExists() {
        // Given
        when(classroomFacade.getSubmission(1L)).thenReturn(Optional.of(submission));

        // When
        ResponseEntity<Submission> response = classroomController.getSubmission(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(submission);
    }

    @Test
    void getPastSubmissions_ShouldReturnPastSubmissions() {
        // Given
        List<Submission> submissions = List.of(submission);
        Page<Submission> page = new PageImpl<>(submissions);
        Pageable pageable = Pageable.unpaged();

        when(classroomFacade.getPastSubmissions(any(LocalDateTime.class), eq(pageable))).thenReturn(page);

        // When
        ResponseEntity<Page<Submission>> response = classroomController.getPastSubmissions(now, pageable);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(page);
    }

    @Test
    void getProventus_ShouldReturnProventus_WhenExists() {
        // Given
        when(classroomFacade.getProventus(1L)).thenReturn(Optional.of(proventus));

        // When
        ResponseEntity<Proventus> response = classroomController.getProventus(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(proventus);
    }

    @Test
    void calculateStudentSemesterAverage_ShouldReturnAverage() {
        // Given
        Double average = 87.5;
        when(classroomFacade.calculateStudentSemesterAverage(1L, 1L)).thenReturn(average);

        // When
        ResponseEntity<Double> response = classroomController.calculateStudentSemesterAverage(1L, 1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(average);
    }
}