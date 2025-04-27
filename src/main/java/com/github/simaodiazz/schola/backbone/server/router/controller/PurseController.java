package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.economy.data.model.Purse;
import com.github.simaodiazz.schola.backbone.server.economy.data.service.PurseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/purses")
public class PurseController {

    private final PurseService purseService;

    @Autowired
    public PurseController(PurseService purseService) {
        this.purseService = purseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Purse> getPurseById(@PathVariable Long id) {
        return purseService.getPurseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Purse> getPurseByUserId(@PathVariable Long userId) {
        return purseService.getPurseByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Purse> createPurse(@RequestBody Purse purse) {
        Purse savedPurse = purseService.savePurse(purse);
        return ResponseEntity.ok(savedPurse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Purse> updatePurse(@PathVariable Long id, @RequestBody Purse purse) {
        Optional<Purse> existingPurse = purseService.getPurseById(id);
        if (existingPurse.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Purse updatedPurse = purseService.savePurse(purse);
        return ResponseEntity.ok(updatedPurse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurse(@PathVariable Long id) {
        Optional<Purse> existingPurse = purseService.getPurseById(id);
        if (existingPurse.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        purseService.deletePurse(id);
        return ResponseEntity.noContent().build();
    }
}
