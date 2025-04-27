package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String nif;

    @NotNull
    private Long userId;

}