package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import java.util.List;
import java.util.Set;

public record MessageRequest(String content, long author, Set<Long> targets) { }
