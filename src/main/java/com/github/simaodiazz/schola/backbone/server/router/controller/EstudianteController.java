package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.entity.data.model.Estudiante;
import com.github.simaodiazz.schola.backbone.server.entity.data.service.EstudianteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class EstudianteController {

    private final EstudianteService estudianteService;

    @Autowired
    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @GetMapping
    public ResponseEntity<List<Estudiante>> getAllStudents() {
        List<Estudiante> students = estudianteService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estudiante> getStudentById(@PathVariable Long id) {
        return estudianteService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nif/{nif}")
    public ResponseEntity<Estudiante> getStudentByNif(@PathVariable String nif) {
        return estudianteService.getStudentByNif(nif)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/class/{schoolClass}")
    public ResponseEntity<List<Estudiante>> getStudentsByClass(@PathVariable String schoolClass) {
        List<Estudiante> students = estudianteService.getStudentsBySchoolClass(schoolClass);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Estudiante>> searchStudents(@RequestParam String name) {
        List<Estudiante> students = estudianteService.searchStudentsByName(name);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/guardian")
    public ResponseEntity<List<Estudiante>> getStudentsByGuardian(@RequestParam(required = false) String name,
                                                                  @RequestParam(required = false) String nif) {
        if (name != null && !name.isEmpty()) {
            return ResponseEntity.ok(estudianteService.getStudentsByGuardianName(name));
        } else if (nif != null && !nif.isEmpty()) {
            return ResponseEntity.ok(estudianteService.getStudentsByGuardianNif(nif));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@Valid @RequestBody Estudiante student) {
        if (estudianteService.isNifUnique(student.getNif())) {
            return ResponseEntity.badRequest().body("NIF já está registado no sistema");
        }

        if (student.getEmail() != null && !student.getEmail().isEmpty() && estudianteService.isEmailUnique(student.getEmail())) {
            return ResponseEntity.badRequest().body("Email já está registado no sistema");
        }

        Estudiante savedEstudiante = estudianteService.saveStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEstudiante);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @Valid @RequestBody Estudiante student) {
        return estudianteService.getStudentById(id)
                .map(existingEstudiante -> {
                    if (!existingEstudiante.getNif().equals(student.getNif()) && estudianteService.isNifUnique(student.getNif())) {
                        return ResponseEntity.badRequest().body("NIF já está registado no sistema");
                    }

                    if (student.getEmail() != null && !student.getEmail().isEmpty() &&
                            (existingEstudiante.getEmail() == null || !existingEstudiante.getEmail().equals(student.getEmail())) &&
                            estudianteService.isEmailUnique(student.getEmail())) {
                        return ResponseEntity.badRequest().body("Email já está registado no sistema");
                    }

                    student.setId(id);
                    Estudiante updatedStudent = estudianteService.saveStudent(student);
                    return ResponseEntity.ok(updatedStudent);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (estudianteService.getStudentById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        estudianteService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}