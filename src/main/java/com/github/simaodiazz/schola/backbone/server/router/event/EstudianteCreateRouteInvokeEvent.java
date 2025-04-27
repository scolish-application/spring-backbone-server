package com.github.simaodiazz.schola.backbone.server.router.event;

import com.github.simaodiazz.schola.backbone.server.entity.data.model.Estudiante;
import jakarta.validation.constraints.NotNull;

public record EstudianteCreateRouteInvokeEvent(@NotNull Estudiante estudiante) {
}
