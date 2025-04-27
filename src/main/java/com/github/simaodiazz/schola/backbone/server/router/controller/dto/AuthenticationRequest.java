package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import org.jetbrains.annotations.NotNull;

public record AuthenticationRequest(@NotNull String username, @NotNull String password) {
}
