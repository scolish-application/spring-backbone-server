package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.economy.data.model.Purse;
import com.github.simaodiazz.schola.backbone.server.economy.data.model.Transaction;
import com.github.simaodiazz.schola.backbone.server.economy.data.service.PurseService;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.PurseCreateRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.PurseRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.TransactionCreateRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.mapper.PurseMapper;
import com.github.simaodiazz.schola.backbone.server.router.controller.mapper.TransactionMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/purses")
@Validated
public class PurseController {

    private final PurseService purseService;
    private final PurseMapper purseMapper;
    private final TransactionMapper transactionMapper;

    @Autowired
    public PurseController(PurseService purseService, PurseMapper purseMapper, TransactionMapper transactionMapper) {
        this.purseService = purseService;
        this.purseMapper = purseMapper;
        this.transactionMapper = transactionMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurseRequest> getPurseById(@PathVariable Long id) {
        return purseService.getPurseById(id)
                .map(purseMapper::request)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purse not found with ID: " + id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PurseRequest> getPurseByUserId(@PathVariable Long userId) {
        return purseService.getPurseByUserId(userId)
                .map(purseMapper::request)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purse not found for user ID: " + userId));
    }

    @PostMapping
    public ResponseEntity<PurseRequest> createPurse(@Valid @RequestBody PurseCreateRequest purseCreateDTO) {
        try {
            Purse purseEntity = purseMapper.createRequest(purseCreateDTO);
            Purse savedPurse = purseService.savePurse(purseEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body(purseMapper.request(savedPurse));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurseRequest> updatePurse(@PathVariable Long id, @Valid @RequestBody PurseCreateRequest purseCreateDTO) {
        Purse existingPurse = purseService.getPurseById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purse not found with ID: " + id));

        try {
            purseMapper.updateEntityFromDTO(purseCreateDTO, existingPurse);
            Purse updatedPurse = purseService.savePurse(existingPurse);
            return ResponseEntity.ok(purseMapper.request(updatedPurse));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{purseId}/transactions")
    public ResponseEntity<PurseRequest> addTransaction(@PathVariable Long purseId, @Valid @RequestBody TransactionCreateRequest transactionCreateDTO) {
        Purse purse = purseService.getPurseById(purseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purse not found with ID: " + purseId));

        Transaction transaction = transactionMapper.createRequest(transactionCreateDTO);
        purse.getTransactions().add(transaction);

        if (transaction.getMovement() == com.github.simaodiazz.schola.backbone.server.economy.data.model.TransactionMovement.IN) {
            purse.setAmount(purse.getAmount() + transaction.getAmount());
        } else {
            if (purse.getAmount() < transaction.getAmount()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
            }
            purse.setAmount(purse.getAmount() - transaction.getAmount());
        }

        Purse updatedPurse = purseService.savePurse(purse);
        return ResponseEntity.ok(purseMapper.request(updatedPurse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurse(@PathVariable Long id) {
        if (!purseService.getPurseById(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Purse not found with ID: " + id);
        }

        purseService.deletePurse(id);
        return ResponseEntity.noContent().build();
    }
}