package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.registry.data.model.Carte;
import com.github.simaodiazz.schola.backbone.server.registry.data.model.Registration;
import com.github.simaodiazz.schola.backbone.server.registry.data.service.RegistrationService;
import com.github.simaodiazz.schola.backbone.server.registry.data.service.CarteService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    private final @NotNull RegistrationService registrationService;
    private final @NotNull CarteService carteService;

    public RegistrationController(RegistrationService registrationService, CarteService carteService) {
        this.registrationService = registrationService;
        this.carteService = carteService;
    }

    @PostMapping("/create")
    public ResponseEntity<Registration> createRegistration(@RequestBody Registration registration) {
        Registration savedRegistration = registrationService.saveRegistration(registration);
        return ResponseEntity.ok(savedRegistration);
    }

    @PostMapping("/carte/create")
    public ResponseEntity<Carte> createCarte(@RequestBody Carte carte) {
        Carte savedCarte = carteService.saveCarte(carte);
        return ResponseEntity.ok(savedCarte);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Registration> getRegistration(@PathVariable Long id) {
        return registrationService.getRegistration(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/carte/{code}")
    public ResponseEntity<Carte> getCarte(@PathVariable String code) {
        return carteService.getCarte(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{registrationId}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable Long registrationId) {
        registrationService.deleteRegistration(registrationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/carte/{carteCode}")
    public ResponseEntity<Void> deleteCarte(@PathVariable String carteCode) {
        carteService.deleteCarte(carteCode);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{registrationId}/carte/{carteCode}")
    public ResponseEntity<Registration> associateCarteToRegistration(
            @PathVariable Long registrationId,
            @PathVariable String carteCode) {

        Registration registration = registrationService.getRegistration(registrationId)
                .orElse(null);
        if (registration == null) {
            return ResponseEntity.notFound().build();
        }

        Carte carte = carteService.getCarte(carteCode)
                .orElse(null);
        if (carte == null) {
            return ResponseEntity.notFound().build();
        }

        registration.setUser(carte.getUser());
        Registration updatedRegistration = registrationService.saveRegistration(registration);
        return ResponseEntity.ok(updatedRegistration);
    }
}
