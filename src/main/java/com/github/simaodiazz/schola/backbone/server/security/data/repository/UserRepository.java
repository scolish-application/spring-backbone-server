package com.github.simaodiazz.schola.backbone.server.security.data.repository;

import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(final String username);

}
