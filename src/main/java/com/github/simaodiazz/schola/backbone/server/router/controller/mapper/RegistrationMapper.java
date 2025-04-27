package com.github.simaodiazz.schola.backbone.server.router.controller.mapper;

import com.github.simaodiazz.schola.backbone.server.registry.data.model.Registration;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.RegistrationRequest;
import com.github.simaodiazz.schola.backbone.server.security.service.UserDataService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RegistrationMapper {

    private final UserDataService userDataService;

    @Autowired
    public RegistrationMapper(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    public RegistrationRequest toRequest(Registration registration) {
        if (registration == null) {
            return null;
        }

        return RegistrationRequest.builder()
                .id(registration.getId())
                .direction(registration.getDirection())
                .userId(registration.getUser().getId())
                .createdAt(registration.getCreated())
                .build();
    }

    public Registration toEntity(RegistrationRequest registrationDTO) {
        if (registrationDTO == null) {
            return null;
        }

        Registration registration = new Registration();
        registration.setId(registrationDTO.getId());
        registration.setDirection(registrationDTO.getDirection());

        userDataService.id(registrationDTO.getId()).ifPresent(registration::setUser);

        return registration;
    }

    public List<RegistrationRequest> toDTOList(@NotNull List<Registration> registrations) {
        return registrations.stream()
                .map(this::toRequest)
                .collect(Collectors.toList());
    }
}