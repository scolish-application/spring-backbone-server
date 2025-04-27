package com.github.simaodiazz.schola.backbone.server.router.event;

import com.github.simaodiazz.schola.backbone.server.entity.data.model.Guardian;
import jakarta.validation.constraints.NotNull;

public record GuardianCreateRouteInvokeEvent(@NotNull Guardian guardian) {
}
