package com.github.simaodiazz.schola.backbone.server.registry.data.repository;

import com.github.simaodiazz.schola.backbone.server.registry.data.model.Registration;
import com.github.simaodiazz.schola.backbone.server.registry.data.model.RegistrationDirection;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findByUser(User user);

    List<Registration> findByDirection(RegistrationDirection direction);

}