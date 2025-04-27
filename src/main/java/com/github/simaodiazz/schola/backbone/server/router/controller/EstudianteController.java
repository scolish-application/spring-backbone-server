package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.entity.data.model.Address;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Estudiante;
import com.github.simaodiazz.schola.backbone.server.entity.data.model.Guardian;
import com.github.simaodiazz.schola.backbone.server.entity.data.service.EstudianteService;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.*;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;

    @Autowired
    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @GetMapping
    public ResponseEntity<List<EstudianteResponse>> getAllStudents() {
        List<Estudiante> estudiantes = estudianteService.getAllStudents();
        List<EstudianteResponse> response = estudiantes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponse> getStudentById(@PathVariable Long id) {
        return estudianteService.getStudentById(id)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante não encontrado com o ID: " + id));
    }

    @GetMapping("/nif/{nif}")
    public ResponseEntity<EstudianteResponse> getStudentByNif(@PathVariable String nif) {
        return estudianteService.getStudentByNif(nif)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante não encontrado com o NIF: " + nif));
    }

    @GetMapping("/class/{schoolClass}")
    public ResponseEntity<List<EstudianteResponse>> getStudentsByClass(@PathVariable String schoolClass) {
        List<Estudiante> estudiantes = estudianteService.getStudentsBySchoolClass(schoolClass);
        List<EstudianteResponse> response = estudiantes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EstudianteResponse>> searchStudentsByName(@RequestParam String name) {
        List<Estudiante> estudiantes = estudianteService.searchStudentsByName(name);
        List<EstudianteResponse> response = estudiantes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/guardian/name")
    public ResponseEntity<List<EstudianteResponse>> getStudentsByGuardianName(@RequestParam String name) {
        List<Estudiante> estudiantes = estudianteService.getStudentsByGuardianName(name);
        List<EstudianteResponse> response = estudiantes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/guardian/nif")
    public ResponseEntity<List<EstudianteResponse>> getStudentsByGuardianNif(@RequestParam String nif) {
        List<Estudiante> estudiantes = estudianteService.getStudentsByGuardianNif(nif);
        List<EstudianteResponse> response = estudiantes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<EstudianteResponse> getStudentByUserId(@PathVariable Long userId) {
        return estudianteService.getStudentByUserId(userId)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante não encontrado com o User ID: " + userId));
    }

    @PostMapping
    public ResponseEntity<EstudianteResponse> createStudent(@Valid @RequestBody EstudianteRequest request) {
        // Verificar se o NIF já existe
        if (!estudianteService.isNifUnique(request.getNif())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NIF já cadastrado no sistema");
        }

        Estudiante estudiante = convertToEntity(request);
        Estudiante savedEstudiante = estudianteService.saveStudent(estudiante);
        return new ResponseEntity<>(convertToResponse(savedEstudiante), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponse> updateStudent(@PathVariable Long id, @Valid @RequestBody EstudianteRequest request) {
        return estudianteService.getStudentById(id)
                .map(existingStudent -> {
                    if (!existingStudent.getNif().equals(request.getNif()) && !estudianteService.isNifUnique(request.getNif())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NIF já cadastrado no sistema");
                    }

                    Estudiante updatedStudent = updateEntityFromRequest(existingStudent, request);
                    Estudiante savedEstudiante = estudianteService.saveStudent(updatedStudent);
                    return new ResponseEntity<>(convertToResponse(savedEstudiante), HttpStatus.OK);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante não encontrado com o ID: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (estudianteService.getStudentById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante não encontrado com o ID: " + id);
        }
        estudianteService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-nif")
    public ResponseEntity<Boolean> checkNifUniqueness(@RequestParam String nif) {
        return ResponseEntity.ok(estudianteService.isNifUnique(nif));
    }

    private @NotNull Estudiante convertToEntity(@NotNull EstudianteRequest request) {
        Estudiante estudiante = new Estudiante();
        estudiante.setName(request.getName());
        estudiante.setNif(request.getNif());
        estudiante.setDateOfBirth(request.getDateOfBirth());
        estudiante.setPhone(request.getPhone());
        estudiante.setGender(request.getGender());
        estudiante.setSchoolClass(request.getSchoolClass());
        estudiante.setEmergencyContactName(request.getEmergencyContactName());
        estudiante.setEmergencyContactPhone(request.getEmergencyContactPhone());
        estudiante.setSpecialNeeds(request.isSpecialNeeds());
        estudiante.setMedicalInformation(request.getMedicalInformation());

        if (request.getAddress() != null) {
            Address address = new Address();
            address.setStreet(request.getAddress().street());
            address.setCity(request.getAddress().city());
            address.setPostalCode(request.getAddress().postalCode());
            address.setCountry(request.getAddress().country());
            estudiante.setAddress(address);
        }

        if (request.getGuardians() != null && !request.getGuardians().isEmpty()) {
            List<Guardian> guardians = new ArrayList<>();
            for (GuardianRequest guardianRequest : request.getGuardians()) {
                Guardian guardian = getGuardian(guardianRequest);

                guardians.add(guardian);
            }
            estudiante.setGuardians(guardians);
        }

        return estudiante;
    }

    private static @NotNull Guardian getGuardian(@NotNull GuardianRequest guardianRequest) {
        Guardian guardian = new Guardian();
        guardian.setName(guardianRequest.getName());
        guardian.setRelationship(guardianRequest.getRelationship());
        guardian.setNif(guardianRequest.getNif());
        guardian.setPhone(guardianRequest.getPhone());
        guardian.setOccupation(guardianRequest.getOccupation());
        guardian.setWorkPhone(guardianRequest.getWorkPhone());
        guardian.setPrimaryGuardian(guardianRequest.isPrimaryGuardian());

        if (guardianRequest.getAddress() != null) {
            Address guardianAddress = new Address();
            guardianAddress.setStreet(guardianRequest.getAddress().street());
            guardianAddress.setCity(guardianRequest.getAddress().city());
            guardianAddress.setPostalCode(guardianRequest.getAddress().postalCode());
            guardianAddress.setCountry(guardianRequest.getAddress().country());
            guardian.setAddress(guardianAddress);
        }
        return guardian;
    }

    private Estudiante updateEntityFromRequest(Estudiante estudiante, EstudianteRequest request) {
        estudiante.setName(request.getName());
        estudiante.setNif(request.getNif());
        estudiante.setDateOfBirth(request.getDateOfBirth());
        estudiante.setPhone(request.getPhone());
        estudiante.setGender(request.getGender());
        estudiante.setSchoolClass(request.getSchoolClass());
        estudiante.setEmergencyContactName(request.getEmergencyContactName());
        estudiante.setEmergencyContactPhone(request.getEmergencyContactPhone());
        estudiante.setSpecialNeeds(request.isSpecialNeeds());
        estudiante.setMedicalInformation(request.getMedicalInformation());

        if (request.getAddress() != null) {
            Address address = estudiante.getAddress();
            if (address == null) {
                address = new Address();
                estudiante.setAddress(address);
            }
            address.setStreet(request.getAddress().street());
            address.setCity(request.getAddress().city());
            address.setPostalCode(request.getAddress().postalCode());
            address.setCountry(request.getAddress().country());
        }

        if (request.getGuardians() != null) {
            estudiante.getGuardians().clear();

            for (GuardianRequest guardianRequest : request.getGuardians()) {
                Guardian guardian = getGuardian(guardianRequest);

                estudiante.getGuardians().add(guardian);
            }
        }

        return estudiante;
    }

    private EstudianteResponse convertToResponse(Estudiante estudiante) {
        EstudianteResponse response = new EstudianteResponse();
        response.setId(estudiante.getId());
        response.setName(estudiante.getName());
        response.setNif(estudiante.getNif());
        response.setDateOfBirth(estudiante.getDateOfBirth());
        response.setPhone(estudiante.getPhone());
        response.setGender(estudiante.getGender());
        response.setSchoolClass(estudiante.getSchoolClass());
        response.setEmergencyContactName(estudiante.getEmergencyContactName());
        response.setEmergencyContactPhone(estudiante.getEmergencyContactPhone());
        response.setSpecialNeeds(estudiante.isSpecialNeeds());
        response.setMedicalInformation(estudiante.getMedicalInformation());

        if (estudiante.getAddress() != null) {
            Address address = estudiante.getAddress();
            AddressResponse addressResponse = new AddressResponse(
                    address.getId(),
                    address.getStreet(),
                    address.getCity(),
                    address.getPostalCode(),
                    address.getCountry()
            );
            response.setAddress(addressResponse);
        }

        if (estudiante.getGuardians() != null && !estudiante.getGuardians().isEmpty()) {
            List<GuardianResponse> guardianResponses = getGuardianResponses(estudiante);
            response.setGuardians(guardianResponses);
        }

        if (estudiante.getUser() != null) {
            response.setUserId(estudiante.getUser().getId());
        }

        return response;
    }

    private static @NotNull List<GuardianResponse> getGuardianResponses(@NotNull Estudiante estudiante) {
        List<GuardianResponse> guardianResponses = new ArrayList<>();
        for (Guardian guardian : estudiante.getGuardians()) {
            GuardianResponse guardianResponse = new GuardianResponse();
            guardianResponse.setId(guardian.getId());
            guardianResponse.setName(guardian.getName());
            guardianResponse.setRelationship(guardian.getRelationship());
            guardianResponse.setNif(guardian.getNif());
            guardianResponse.setPhone(guardian.getPhone());
            guardianResponse.setOccupation(guardian.getOccupation());
            guardianResponse.setWorkPhone(guardian.getWorkPhone());
            guardianResponse.setPrimaryGuardian(guardian.isPrimaryGuardian());

            if (guardian.getAddress() != null) {
                Address guardianAddress = guardian.getAddress();
                AddressResponse guardianAddressResponse = new AddressResponse(
                        guardianAddress.getId(),
                        guardianAddress.getStreet(),
                        guardianAddress.getCity(),
                        guardianAddress.getPostalCode(),
                        guardianAddress.getCountry()
                );
                guardianResponse.setAddress(guardianAddressResponse);
            }

            if (guardian.getUser() != null) {
                guardianResponse.setUserId(guardian.getUser().getId());
            }

            guardianResponses.add(guardianResponse);
        }
        return guardianResponses;
    }
}