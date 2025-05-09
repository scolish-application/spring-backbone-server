package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.router.controller.dto.AuthenticationRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.UserResponse;
import com.github.simaodiazz.schola.backbone.server.router.event.AuthenticationRegisterRouteInvokeEvent;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import com.github.simaodiazz.schola.backbone.server.security.data.model.builder.UserBuilder;
import com.github.simaodiazz.schola.backbone.server.security.service.UserDataService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @NotNull AuthenticationRequest authRequest,
                                        HttpServletRequest request) {
        try {
            UsernamePasswordAuthenticationToken token =
                    UsernamePasswordAuthenticationToken.unauthenticated(authRequest.username(), authRequest.password());

            Authentication authentication = authenticationProvider.authenticate(token);

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            User user = userDataService.username(
                    authRequest.username()).orElse(null);

            if (user == null)
                return ResponseEntity.badRequest().build();

            return ResponseEntity.ok(new UserResponse(user.getId(), authRequest.username(), user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
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
