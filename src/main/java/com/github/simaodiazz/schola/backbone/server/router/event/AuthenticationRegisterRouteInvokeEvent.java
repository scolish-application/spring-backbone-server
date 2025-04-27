package com.github.simaodiazz.schola.backbone.server.router.event;

import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import org.jetbrains.annotations.NotNull;

public record AuthenticationRegisterRouteInvokeEvent(@NotNull User user) {
}
