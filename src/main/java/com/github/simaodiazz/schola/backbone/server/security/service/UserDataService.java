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

    public User save(@NotNull User user) {
        return repository.save(user);
    }

    public Optional<User> id(final long id) {
        return repository.findById(id);
    }

    public @NotNull Optional<User> username(final @NotNull String username) {
        return repository.findByUsername(username);
    }
}