package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PurseCreateRequest {

    @Min(value = 0, message = "Purse value must be positive")
    private double purse;

    @NotNull(message = "User ID is required")
    private Long userId;

}