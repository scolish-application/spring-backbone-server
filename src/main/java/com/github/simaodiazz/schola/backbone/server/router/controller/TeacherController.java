package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.entity.data.model.Teacher;
import com.github.simaodiazz.schola.backbone.server.entity.data.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        return teacherService.getTeacherById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email")
    public ResponseEntity<Teacher> getTeacherByEmail(@RequestParam String email) {
        return teacherService.getTeacherByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nif")
    public ResponseEntity<Teacher> getTeacherByNif(@RequestParam String nif) {
        return teacherService.getTeacherByNif(nif)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Teacher> getTeacherByUserId(@PathVariable Long userId) {
        return teacherService.getTeacherByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/lastname")
    public ResponseEntity<List<Teacher>> getTeachersByLastName(@RequestParam String lastName) {
        List<Teacher> teachers = teacherService.getTeachersByLastName(lastName);
        if (teachers.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Teacher>> searchTeachersByName(@RequestParam String name) {
        List<Teacher> teachers = teacherService.searchTeachersByName(name);
        if (teachers.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(teachers);
    }

    @PostMapping
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher) {
        Teacher savedTeacher = teacherService.saveTeacher(teacher);
        return ResponseEntity.ok(savedTeacher);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id, @RequestBody Teacher request) {
        Optional<Teacher> existing = teacherService.getTeacherById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final Teacher teacher = teacherService.saveTeacher(request);
        return ResponseEntity.ok(teacher);
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
}
