package com.github.simaodiazz.schola.backbone.server.mail.listener;

import com.github.simaodiazz.schola.backbone.server.mail.data.service.CourierService;
import com.github.simaodiazz.schola.backbone.server.router.event.AuthenticationRegisterRouteInvokeEvent;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateCourierAfterUserRegistrationListener {

    private final @NotNull CourierService courierService;

    public CreateCourierAfterUserRegistrationListener(final @NotNull CourierService courierService) {
        this.courierService = courierService;
    }

    @EventListener
    @Transactional
    public void handleAuthenticationRegisterEvent(final @NotNull AuthenticationRegisterRouteInvokeEvent event) {
        final User user = event.user();
        courierService.save(user);
    }
}