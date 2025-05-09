package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import com.github.simaodiazz.schola.backbone.server.registry.data.model.CarteColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CarteCreateRequest {

    @NotBlank(message = "Carte code cannot be blank")
    private String code;

    @NotNull(message = "Carte color must be specified")
    private CarteColor color;

    private long userId;

}
