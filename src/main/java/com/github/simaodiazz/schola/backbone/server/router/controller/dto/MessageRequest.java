package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import java.util.List;

public record MessageRequest(String content, List<Long> recipients) { }
