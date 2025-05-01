package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.registry.data.model.Carte;
import com.github.simaodiazz.schola.backbone.server.registry.data.model.Registration;
import com.github.simaodiazz.schola.backbone.server.registry.data.service.CarteService;
import com.github.simaodiazz.schola.backbone.server.registry.data.service.RegistrationService;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.CarteRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.RegistrationRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.mapper.CarteMapper;
import com.github.simaodiazz.schola.backbone.server.router.controller.mapper.RegistrationMapper;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@Validated
public class RegistrationController {

    private final RegistrationService registrationService;
    private final CarteService carteService;
    private final RegistrationMapper registrationMapper;
    private final CarteMapper carteMapper;

    @Autowired
    public RegistrationController(
            RegistrationService registrationService,
            CarteService carteService,
            RegistrationMapper registrationMapper,
            CarteMapper carteMapper) {
        this.registrationService = registrationService;
        this.carteService = carteService;
        this.registrationMapper = registrationMapper;
        this.carteMapper = carteMapper;
    }

    @PostMapping
    public ResponseEntity<RegistrationRequest> createRegistration(@Valid @RequestBody RegistrationRequest registrationDTO) {
        Registration registration = registrationMapper.toEntity(registrationDTO);
        Registration savedRegistration = registrationService.saveRegistration(registration);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrationMapper.toRequest(savedRegistration));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationRequest> getRegistration(@PathVariable Long id) {
        return registrationService.getRegistration(id)
                .map(registration -> ResponseEntity.ok(registrationMapper.toRequest(registration)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration not found with id: " + id));
    }

    @GetMapping
    public ResponseEntity<Page<RegistrationRequest>> getAllRegistrations(Pageable pageable) {
        Page<Registration> registrations = registrationService.getAllRegistrations(pageable);
        Page<RegistrationRequest> registrationDTOs = registrations.map(registrationMapper::toRequest);
        return ResponseEntity.ok(registrationDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistrationRequest> updateRegistration(@PathVariable Long id, @Valid @RequestBody RegistrationRequest registrationDTO) {
        if (!registrationService.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration not found with id: " + id);
        }

        Registration registration = registrationMapper.toEntity(registrationDTO);
        registration.setId(id);
        Registration updatedRegistration = registrationService.saveRegistration(registration);
        return ResponseEntity.ok(registrationMapper.toRequest(updatedRegistration));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable Long id) {
        if (!registrationService.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration not found with id: " + id);
        }

        registrationService.deleteRegistration(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/carte")
    public ResponseEntity<CarteRequest> createCarte(@Valid @RequestBody @NotNull CarteRequest carteDTO) {
        if (carteService.existsByCode(carteDTO.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Carte already exists with code: " + carteDTO.getCode());
        }

        Carte carte = carteMapper.toEntity(carteDTO);
        Carte savedCarte = carteService.saveCarte(carte);
        return ResponseEntity.status(HttpStatus.CREATED).body(carteMapper.toRequest(savedCarte));
    }

    @GetMapping("/carte/{code}")
    public ResponseEntity<CarteRequest> getCarte(@PathVariable String code) {
        return carteService.getCarte(code)
                .map(carte -> ResponseEntity.ok(carteMapper.toRequest(carte)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carte not found with code: " + code));
    }

    @GetMapping("/carte")
    public ResponseEntity<List<CarteRequest>> getAllCartes() {
        List<Carte> carts = carteService.getAllCartes();
        return ResponseEntity.ok(carteMapper.toRequests(carts));
    }

    @PutMapping("/carte/{code}")
    public ResponseEntity<CarteRequest> updateCarte(@PathVariable String code, @Valid @RequestBody CarteRequest carteDTO) {
        if (!carteService.existsByCode(code)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Carte not found with code: " + code);
        }

        Carte carte = carteMapper.toEntity(carteDTO);
        carte.setCode(code);
        Carte updatedCarte = carteService.saveCarte(carte);
        return ResponseEntity.ok(carteMapper.toRequest(updatedCarte));
    }

    @DeleteMapping("/carte/{code}")
    public ResponseEntity<Void> deleteCarte(@PathVariable String code) {
        if (!carteService.existsByCode(code)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Carte not found with code: " + code);
        }

        carteService.deleteCarte(code);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{registrationId}/carte/{code}")
    public ResponseEntity<RegistrationRequest> associateCarteToRegistration(
            @PathVariable Long registrationId,
            @PathVariable String code) {

        Registration registration = registrationService.getRegistration(registrationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Registration not found with id: " + registrationId));

        Carte carte = carteService.getCarte(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Carte not found with code: " + code));

        registration.setUser(carte.getUser());
        Registration updatedRegistration = registrationService.saveRegistration(registration);
        return ResponseEntity.ok(registrationMapper.toRequest(updatedRegistration));
    }
}