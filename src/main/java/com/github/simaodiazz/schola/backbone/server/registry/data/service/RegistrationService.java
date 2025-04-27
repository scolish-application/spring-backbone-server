package com.github.simaodiazz.schola.backbone.server.registry.data.service;

import com.github.simaodiazz.schola.backbone.server.registry.data.model.Registration;
import com.github.simaodiazz.schola.backbone.server.registry.data.model.RegistrationDirection;
import com.github.simaodiazz.schola.backbone.server.registry.data.repository.RegistrationRepository;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RegistrationService {

    private final RegistrationRepository registrationRepository;

    @Autowired
    public RegistrationService(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    public Registration saveRegistration(Registration registration) {
        if (registration.getCreated() == null) {
            registration.setCreated(LocalDateTime.now());
        }
        return registrationRepository.save(registration);
    }

    public Optional<Registration> getRegistration(Long id) {
        return registrationRepository.findById(id);
    }

    public Page<Registration> getAllRegistrations(Pageable pageable) {
        return registrationRepository.findAll(pageable);
    }

    public List<Registration> findByUser(User user) {
        return registrationRepository.findByUser(user);
    }

    public List<Registration> findByDirection(RegistrationDirection direction) {
        return registrationRepository.findByDirection(direction);
    }

    public List<Registration> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return registrationRepository.findByCreatedAtBetween(start, end);
    }

    public void deleteRegistration(Long id) {
        registrationRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return registrationRepository.existsById(id);
    }
}