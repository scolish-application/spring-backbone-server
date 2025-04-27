package com.github.simaodiazz.schola.backbone.server.security.data.model.builder;

import com.github.simaodiazz.schola.backbone.server.router.controller.dto.AuthenticationRequest;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class UserBuilder {

    private long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities = List.of();

    private UserBuilder() {}

    private UserBuilder(final @NotNull String username, final @NotNull String password) {
        this.username = username;
        this.password = password;
    }

    private UserBuilder(final @NotNull User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = user.getAuthorities();
    }

    public static UserBuilder empty() {
        return new UserBuilder();
    }

    public static UserBuilder from(final @NotNull User user) {
        return new UserBuilder(user);
    }

    public static UserBuilder based(final @NotNull AuthenticationRequest request) {
        return new UserBuilder(
                request.username(),
                request.password());
    }

    public @Flow(source = "id", target = "this") UserBuilder id(final long id) {
        this.id = id;
        return this;
    }

    public @Flow(source = "username", target = "this") UserBuilder username(final @NotNull String username) {
        this.username = username;
        return this;
    }

    public @Flow(source = "password", target = "this") UserBuilder password(final @NotNull String password) {
        this.password = password;
        return this;
    }

    public @Flow(source = "authorities", target = "this") UserBuilder authorities(final @NotNull @Unmodifiable Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
        return this;
    }

    public @NotNull @Unmodifiable User build() {
        Objects.requireNonNull(username, "Username must not be null");
        Objects.requireNonNull(password, "Password must not be null");
        return new User(this.id, this.username, this.password, this.authorities);
    }
}
