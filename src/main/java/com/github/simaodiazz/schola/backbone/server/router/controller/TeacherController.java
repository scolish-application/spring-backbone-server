package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.entity.data.model.Teacher;
import com.github.simaodiazz.schola.backbone.server.entity.data.service.TeacherService;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.TeacherRequest;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public ResponseEntity<List<TeacherRequest>> getAllTeachers() {
        List<Teacher> teachers = teacherService.getAllTeachers();
        if (teachers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<TeacherRequest> responses = teachers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherRequest> getTeacherById(@PathVariable Long id) {
        return teacherService.getTeacherById(id)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nif")
    public ResponseEntity<TeacherRequest> getTeacherByNif(@RequestParam String nif) {
        return teacherService.getTeacherByNif(nif)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<TeacherRequest> getTeacherByUserId(@PathVariable Long userId) {
        return teacherService.getTeacherByUserId(userId)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name")
    public ResponseEntity<List<TeacherRequest>> searchTeachersByName(@RequestParam String name) {
        List<Teacher> teachers = teacherService.getTeacherByName(name);
        if (teachers.isEmpty())
            return ResponseEntity.noContent().build();

        List<TeacherRequest> responses = teachers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<TeacherRequest> createTeacher(@RequestBody TeacherRequest request) {
        if (!teacherService.isNifUnique(request.getNif())) {
            return ResponseEntity.badRequest().build();
        }

        Teacher teacher = mapToEntity(request);
        Teacher savedTeacher = teacherService.saveTeacher(teacher);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(savedTeacher));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherRequest> updateTeacher(@PathVariable Long id, @RequestBody TeacherRequest request) {
        Optional<Teacher> existingOptional = teacherService.getTeacherById(id);
        if (existingOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Teacher existing = existingOptional.get();

        // Check if NIF is changed and if it's unique
        if (!existing.getNif().equals(request.getNif()) && !teacherService.isNifUnique(request.getNif())) {
            return ResponseEntity.badRequest().build();
        }

        Teacher teacher = mapToEntity(request);
        teacher.setId(id);
        Teacher updatedTeacher = teacherService.saveTeacher(teacher);
        return ResponseEntity.ok(mapToResponse(updatedTeacher));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        Optional<Teacher> existing = teacherService.getTeacherById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-nif")
    public ResponseEntity<Boolean> checkNifUniqueness(@RequestParam String nif) {
        return ResponseEntity.ok(teacherService.isNifUnique(nif));
    }

    private @NotNull TeacherRequest mapToResponse(@NotNull Teacher teacher) {
        TeacherRequest response = new TeacherRequest();
        response.setName(teacher.getName());
        response.setNif(teacher.getNif());
        response.setUserId(teacher.getUser() != null ? teacher.getUser().getId() : null);

        return response;
    }

    private @NotNull Teacher mapToEntity(@NotNull TeacherRequest request) {
        Teacher teacher = new Teacher();
        teacher.setName(request.getName());
        teacher.setNif(request.getNif());

        // Set user if userId is provided
        if (request.getUserId() != null) {
            User user = new User();
            user.setId(request.getUserId());
            teacher.setUser(user);
        }

        return teacher;
    }
}