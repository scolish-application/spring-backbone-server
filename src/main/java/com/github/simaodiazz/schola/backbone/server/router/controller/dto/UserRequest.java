package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import java.util.List;

public record UserRequest(
        String username,
        String password,
        List<String> authorities
) {}