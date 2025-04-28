package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.router.controller.dto.AuthenticationRequest;
import com.github.simaodiazz.schola.backbone.server.router.event.AuthenticationRegisterRouteInvokeEvent;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import com.github.simaodiazz.schola.backbone.server.security.data.model.builder.UserBuilder;
import com.github.simaodiazz.schola.backbone.server.security.service.UserDataService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {

    private final @NotNull ApplicationEventPublisher publisher;
    private final @NotNull UserDataService userDataService;
    private final @NotNull AuthenticationProvider authenticationProvider;

    public AuthenticationController(@NotNull ApplicationEventPublisher publisher, @NotNull UserDataService userDataService, @NotNull AuthenticationProvider authenticationProvider) {
        this.publisher = publisher;
        this.userDataService = userDataService;
        this.authenticationProvider = authenticationProvider;
    }

    /**
     * Delegating login route to {@link jakarta.servlet.http.HttpServlet}
     * Because he already uses the same logic and persist the session and cookie
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @NotNull AuthenticationRequest authRequest) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    UsernamePasswordAuthenticationToken.unauthenticated(authRequest.username(), authRequest.password());

            Authentication authentication = authenticationProvider.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return ResponseEntity.ok("Login successful.");
        } catch (Exception exception) {
            final String message = exception.getMessage();
            return ResponseEntity.badRequest().body(message);
        }
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(final @RequestBody AuthenticationRequest request) {
        try {
            User user = UserBuilder.based(request).build();
            userDataService.save(user);

            publisher.publishEvent(
                    new AuthenticationRegisterRouteInvokeEvent(user));

            return ResponseEntity.ok("Account created, please login to continue.");
        } catch (Exception e) {
            final String message = e.getMessage();
            return ResponseEntity.badRequest().body(message);
        }
    }

    @GetMapping("/hasAuthority/admin/{username}")
    public ResponseEntity<?> isAdmin(final @PathVariable(name = "username") String username) {
        try {
            final boolean isAdmin = userDataService.username(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User with that id is not founded"))
                    .getAuthorities()
                    .stream()
                    .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));
            return isAdmin ? ResponseEntity.ok(true) : ResponseEntity.status(401).body(false);
        } catch (Exception exception) {
            final String message = exception.getMessage();
            return ResponseEntity.badRequest().body(message);
        }
    }
}
