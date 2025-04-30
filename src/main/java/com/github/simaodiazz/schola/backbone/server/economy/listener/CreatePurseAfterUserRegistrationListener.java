package com.github.simaodiazz.schola.backbone.server.economy.listener;

import com.github.simaodiazz.schola.backbone.server.economy.data.model.Purse;
import com.github.simaodiazz.schola.backbone.server.economy.data.service.PurseService;
import com.github.simaodiazz.schola.backbone.server.router.event.AuthenticationRegisterRouteInvokeEvent;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CreatePurseAfterUserRegistrationListener {

    private final PurseService service;

    public CreatePurseAfterUserRegistrationListener(PurseService service) {
        this.service = service;
    }

    @EventListener
    @Transactional
    public void onRegister(final @NotNull AuthenticationRegisterRouteInvokeEvent event) {
        final User user = event.user();
        final Purse purse = new Purse(user);
        service.savePurse(purse);
    }
}