package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.*;

import java.util.List;

public record PerformanceResponse(
        Long studentId,
        Long semesterId,
        Double semesterAverage,
        List<Proventus> evaluationResults) {}
