package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import java.util.List;

public record UserResponse(
        long id,
        String username,
        List<String> authorities
) {}