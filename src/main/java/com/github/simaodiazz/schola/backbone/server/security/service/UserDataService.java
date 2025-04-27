package com.github.simaodiazz.schola.backbone.server.security.service;

import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import com.github.simaodiazz.schola.backbone.server.security.data.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDataService {

    private final UserRepository repository;

    public UserDataService(UserRepository repository) {
        this.repository = repository;
    }

    public void save(@NotNull User user) {
        repository.save(user);
    }

    public @NotNull Optional<User> username(final @NotNull String username) {
        return repository.findByUsername(username);
    }
}