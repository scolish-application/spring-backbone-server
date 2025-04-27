package com.github.simaodiazz.schola.backbone.server.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserChargeService implements UserDetailsService {

    private final UserDataService userDataService;

    public UserChargeService(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userDataService.username(username).orElseThrow(
                () -> new UsernameNotFoundException("User '" + username + "' not found"));
    }
}
