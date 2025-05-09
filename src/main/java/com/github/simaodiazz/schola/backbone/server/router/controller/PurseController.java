package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.economy.data.model.Purse;
import com.github.simaodiazz.schola.backbone.server.economy.data.model.Transaction;
import com.github.simaodiazz.schola.backbone.server.economy.data.model.TransactionDirection;
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

import java.util.List;

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
    public ResponseEntity<PurseRequest> getPurseByUserId(@PathVariable long userId) {
        return purseService.getPurseByUserId(userId)
                .map(purseMapper::request)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purse not found with ID: " + userId));
    }

    @GetMapping("/user/transactions/{id}")
    public ResponseEntity<List<Transaction>> getTransitionsByUserId(@PathVariable(name = "id") long id) {
        return purseService.getPurseByUserId(id)
                .map(purse -> ResponseEntity.ok(purse.getTransactions()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purse not found for user ID: " + id));
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

        if (transaction.getDirection() == TransactionDirection.IN) {
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
        if (purseService.getPurseById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Purse not found with ID: " + id);
        }

        purseService.deletePurse(id);
        return ResponseEntity.noContent().build();
    }
}