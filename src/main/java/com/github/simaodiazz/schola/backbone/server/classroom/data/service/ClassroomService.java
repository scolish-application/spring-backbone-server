package com.github.simaodiazz.schola.backbone.server.classroom.data.service;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Classroom;
import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Semester;
import com.github.simaodiazz.schola.backbone.server.classroom.data.repository.ClassroomRepository;
import com.github.simaodiazz.schola.backbone.server.course.data.model.Discipline;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Estudiante;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    @Autowired
    public ClassroomService(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    @Transactional(readOnly = true)
    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Classroom getClassroomById(final long id) {
        return classroomRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Classroom not found with id: " + id));
    }

    @Transactional
    public Classroom createClassroom(Classroom classroom) {
        // Generate a unique access code if not provided
        if (classroom.getCode() == null || classroom.getCode().trim().isEmpty()) {
            classroom.setCode(generateUniqueAccessCode());
        }

        validateClassroom(classroom);
        return classroomRepository.save(classroom);
    }

    @Transactional
    public Classroom updateClassroom(final long id, Classroom updatedClassroom) {
        Classroom existingClassroom = getClassroomById(id);

        // Update basic fields
        existingClassroom.setName(updatedClassroom.getName());
        existingClassroom.setDescription(updatedClassroom.getDescription());
        existingClassroom.setActive(updatedClassroom.getActive());
        existingClassroom.setSyllabus(updatedClassroom.getSyllabus());
        existingClassroom.setCapacity(updatedClassroom.getCapacity());
        existingClassroom.setType(updatedClassroom.getType());

        if (updatedClassroom.getDisciplines() != null) {
            existingClassroom.setDisciplines(updatedClassroom.getDisciplines());
        }

        if (updatedClassroom.getSemester() != null) {
            existingClassroom.setSemester(updatedClassroom.getSemester());
        }

        validateClassroom(existingClassroom);
        return classroomRepository.save(existingClassroom);
    }

    @Transactional
    public void deleteClassroom(final long id) {
        if (!classroomRepository.existsById(id)) {
            throw new IllegalArgumentException("Classroom not found with id: " + id);
        }
        classroomRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Classroom> getClassroomsByDiscipline(Discipline discipline) {
        return classroomRepository.findByDisciplineId(
                discipline.getId());
    }

    @Transactional(readOnly = true)
    public List<Classroom> getClassroomsByDiscipline(final long id) {
        return classroomRepository.findByDisciplineId(id);
    }

    @Transactional(readOnly = true)
    public List<Classroom> getClassroomsBySemester(Semester semester) {
        return classroomRepository.findBySemester(semester);
    }

    @Transactional(readOnly = true)
    public List<Classroom> getActiveClassrooms() {
        return classroomRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Classroom getClassroomByCode(String code) {
        return classroomRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Classroom not found with code: " + code));
    }

    @Transactional(readOnly = true)
    public List<Classroom> getClassroomsByStudentId(String studentId) {
        return classroomRepository.findClassroomsByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<Classroom> searchClassrooms(String keyword) {
        return classroomRepository.searchClassroomsByKeyword(keyword);
    }

    @Transactional(readOnly = true)
    public Integer countStudentsInClassroom(final long classroomId) {
        return classroomRepository.countStudentsInClassroom(classroomId);
    }

    @Transactional
    public Classroom addStudentToClassroom(final long classroomId, Estudiante student) {
        Classroom classroom = getClassroomById(classroomId);
        classroom.getStudents().add(student);
        return classroomRepository.save(classroom);
    }

    @Transactional
    public Classroom removeStudentFromClassroom(final long classroomId, Estudiante student) {
        Classroom classroom = getClassroomById(classroomId);
        classroom.getStudents().remove(student);
        return classroomRepository.save(classroom);
    }

    @Transactional
    public Classroom updateStudents(final long classroomId, Set<Estudiante> students) {
        Classroom classroom = getClassroomById(classroomId);
        classroom.setStudents(students);
        return classroomRepository.save(classroom);
    }

    @Transactional
    public List<Classroom> getClassroomsByDisciplineAndSemester(Discipline discipline, Semester semester) {
        return classroomRepository.findByDisciplineAndSemester(discipline, semester);
    }

    private void validateClassroom(@NotNull Classroom classroom) {
        if (classroom.getName() == null || classroom.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Classroom name cannot be empty");
        }

        if (classroom.getDisciplines() == null || classroom.getDisciplines().isEmpty()) {
            throw new IllegalArgumentException("Classroom must be associated with a discipline");
        }

        if (classroom.getSemester() == null) {
            throw new IllegalArgumentException("Classroom must be associated with a semester");
        }
    }

    private String generateUniqueAccessCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            codeBuilder.append(chars.charAt(random.nextInt(chars.length())));
        }

        String code = codeBuilder.toString();

        // Check if code already exists and regenerate if needed
        if (classroomRepository.findByCode(code).isPresent()) {
            return generateUniqueAccessCode();
        }

        return code;
    }
}