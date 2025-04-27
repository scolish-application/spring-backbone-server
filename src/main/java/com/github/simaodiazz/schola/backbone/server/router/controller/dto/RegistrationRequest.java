package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import com.github.simaodiazz.schola.backbone.server.registry.data.model.RegistrationDirection;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    private Long id;

    @NotNull(message = "Registration direction must be specified")
    private RegistrationDirection direction;

    private long userId;

    private LocalDateTime createdAt;
}