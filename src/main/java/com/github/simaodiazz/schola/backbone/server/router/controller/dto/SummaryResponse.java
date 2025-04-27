package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Activity;
import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Classroom;
import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Evaluation;
import com.github.simaodiazz.schola.backbone.server.classroom.data.model.Lesson;

import java.util.List;

public record SummaryResponse(
        Classroom classroom,
        List<Activity> activities,
        List<Lesson> lessons,
        List<Evaluation> evaluations) {}
