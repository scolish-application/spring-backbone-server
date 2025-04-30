package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.router.controller.dto.UserRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.UserResponse;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import com.github.simaodiazz.schola.backbone.server.security.service.UserDataService;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final @NotNull UserDataService userDataService;
    private final @NotNull PasswordEncoder passwordEncoder;

    public UserController(@NotNull UserDataService userDataService, @NotNull PasswordEncoder passwordEncoder) {
        this.userDataService = userDataService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = StreamSupport
                .stream(userDataService.findAll().spliterator(), false)
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable long id) {
        return userDataService.id(id)
                .map(this::mapToUserResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@CookieValue(name = "SESSION", required = false) String session) {
        if (session == null)
            return ResponseEntity.badRequest().body("This cookie is invalid.");

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return ResponseEntity.badRequest().body("You are not authenticated.");

        final User user = (User) authentication.getPrincipal();
        if (user == null)
            return ResponseEntity.badRequest().body("User not found.");

        final String username = user.getUsername();
        return ResponseEntity.ok(username);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @NotNull UserRequest userRequest) {
        try {
            User user = new User();
            user.setUsername(userRequest.username());
            user.setPassword(passwordEncoder.encode(userRequest.password()));

            List<SimpleGrantedAuthority> authorities = userRequest.authorities().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            user.setAuthorities(authorities);

            User savedUser = userDataService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToUserResponse(savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable long id, @RequestBody @NotNull UserRequest userRequest) {
        try {
            Optional<User> optionalUser = userDataService.id(id);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = optionalUser.get();
            user.setUsername(userRequest.username());

            if (userRequest.password() != null && !userRequest.password().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userRequest.password()));
            }

            if (userRequest.authorities() != null && !userRequest.authorities().isEmpty()) {
                List<SimpleGrantedAuthority> authorities = userRequest.authorities().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                user.setAuthorities(authorities);
            }

            userDataService.save(user);
            return ResponseEntity.ok("User updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
        try {
            Optional<User> optionalUser = userDataService.id(id);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            userDataService.delete(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Contract("_ -> new")
    private @NotNull UserResponse mapToUserResponse(@NotNull User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
    }
}