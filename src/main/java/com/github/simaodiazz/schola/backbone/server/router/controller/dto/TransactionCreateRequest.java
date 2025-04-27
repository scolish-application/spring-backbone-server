package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import com.github.simaodiazz.schola.backbone.server.economy.data.model.TransactionMovement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionCreateRequest {

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Cause is required")
    private String cause;

    @Min(value = 0, message = "Amount must be positive")
    private double amount;

    @NotNull(message = "Movement type is required")
    private TransactionMovement movement;

}