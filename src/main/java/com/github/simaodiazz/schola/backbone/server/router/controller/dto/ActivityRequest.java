package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import java.time.LocalDateTime;

public record ActivityRequest(
        Long id,
        String name,
        String description,
        LocalDateTime dueDate,
        Long disciplineId,
        Long semesterId,
        LocalDateTime createdAt) {
}