package com.github.simaodiazz.schola.backbone.server.security.listener;

import com.github.simaodiazz.schola.backbone.server.router.event.AuthenticationRegisterRouteInvokeEvent;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import com.github.simaodiazz.schola.backbone.server.security.service.UserPasswrChangeService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EncodePasswrAfterUserRegistrationListener {

    private final UserPasswrChangeService service;

    public EncodePasswrAfterUserRegistrationListener(UserPasswrChangeService service) {
        this.service = service;
    }

    @EventListener
    public void onRegister(final @NotNull AuthenticationRegisterRouteInvokeEvent event) {
        final User user = event.user();
        final String password = user.getPassword();
        service.updatePassword(user, password);
    }
}
