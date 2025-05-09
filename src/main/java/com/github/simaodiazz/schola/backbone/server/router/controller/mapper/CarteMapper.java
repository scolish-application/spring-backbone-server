package com.github.simaodiazz.schola.backbone.server.router.controller.mapper;

import com.github.simaodiazz.schola.backbone.server.registry.data.model.Carte;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.CarteCreateRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.CarteRequest;
import com.github.simaodiazz.schola.backbone.server.security.service.UserDataService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class CarteMapper {

    private final UserDataService userDataService;

    public CarteMapper(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    public CarteRequest toRequest(Carte carte) {
        if (carte == null) {
            return null;
        }

        return CarteRequest.builder()
                .id(carte.getId())
                .code(carte.getCode())
                .color(carte.getColor())
                .userId(carte.getUser().getId())
                .build();
    }

    public Carte toEntity(CarteRequest carteRequest) {
        if (carteRequest == null) {
            return null;
        }

        Carte carte = new Carte();
        carte.setId(carteRequest.getId());
        carte.setCode(carteRequest.getCode());
        carte.setColor(carteRequest.getColor());
        userDataService.id(carteRequest.getId()).ifPresent(carte::setUser);

        return carte;
    }

    public List<CarteRequest> toRequests(@NotNull Iterable<Carte> carts) {
        return StreamSupport.stream(carts.spliterator(), false)
                .map(this::toRequest)
                .collect(Collectors.toList());
    }
}