package com.github.simaodiazz.schola.backbone.server.security.service;

import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import com.github.simaodiazz.schola.backbone.server.security.data.model.builder.UserBuilder;
import com.github.simaodiazz.schola.backbone.server.security.data.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserPasswrChangeService implements UserDetailsPasswordService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public UserPasswrChangeService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails updatePassword(final UserDetails details, final String password) {
        final String username = details.getUsername();
        User user = repository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User '" + username + "' not found"));
        final String encoded = encoder.encode(password);
        user = UserBuilder.from(user).password(encoded).build();
        return repository.save(user);
    }
}
