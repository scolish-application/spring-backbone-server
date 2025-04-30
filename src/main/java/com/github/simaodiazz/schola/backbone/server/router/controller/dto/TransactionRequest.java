package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import com.github.simaodiazz.schola.backbone.server.economy.data.model.TransactionDirection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionRequest {
    private Long id;
    private String location;
    private String cause;
    private double amount;
    private TransactionDirection movement;
}