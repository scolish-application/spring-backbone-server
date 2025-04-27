package com.github.simaodiazz.schola.backbone.server.registry.data.repository;

import com.github.simaodiazz.schola.backbone.server.registry.data.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

}
