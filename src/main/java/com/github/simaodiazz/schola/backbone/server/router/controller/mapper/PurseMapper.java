package com.github.simaodiazz.schola.backbone.server.router.controller.mapper;

import com.github.simaodiazz.schola.backbone.server.economy.data.model.Purse;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.PurseCreateRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.PurseRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.TransactionRequest;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import com.github.simaodiazz.schola.backbone.server.security.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PurseMapper {

    private final UserDataService userService;
    private final TransactionMapper transactionMapper;

    @Autowired
    public PurseMapper(UserDataService userService, TransactionMapper transactionMapper) {
        this.userService = userService;
        this.transactionMapper = transactionMapper;
    }

    public PurseRequest request(Purse purse) {
        if (purse == null) {
            return null;
        }

        List<TransactionRequest> transactionDTOs = purse.getTransactions() != null
                ? purse.getTransactions().stream()
                .map(transactionMapper::request)
                .collect(Collectors.toList())
                : new ArrayList<>();

        return new PurseRequest(
                purse.getId(),
                purse.getAmount(),
                purse.getUser().getId(),
                transactionDTOs
        );
    }

    public Purse createRequest(PurseCreateRequest purseDTO) {
        if (purseDTO == null) {
            return null;
        }

        User user = userService.id(purseDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + purseDTO.getUserId()));

        Purse purse = new Purse();
        purse.setAmount(purseDTO.getPurse());
        purse.setUser(user);
        purse.setTransactions(new ArrayList<>());

        return purse;
    }

    public void updateEntityFromDTO(PurseCreateRequest purseDTO, Purse purse) {
        if (purseDTO == null || purse == null) {
            return;
        }

        purse.setAmount(purseDTO.getPurse());

        if (!(purse.getUser().getId() == purseDTO.getUserId())) {
            User user = userService.id(purseDTO.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + purseDTO.getUserId()));
            purse.setUser(user);
        }
    }
}