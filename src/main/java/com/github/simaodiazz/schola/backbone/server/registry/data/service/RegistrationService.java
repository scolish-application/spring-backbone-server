package com.github.simaodiazz.schola.backbone.server.registry.data.service;

import com.github.simaodiazz.schola.backbone.server.registry.data.model.Registration;
import com.github.simaodiazz.schola.backbone.server.registry.data.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;

    @Autowired
    public RegistrationService(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    public Registration saveRegistration(Registration registration) {
        return registrationRepository.save(registration);
    }

    public Optional<Registration> getRegistration(Long id) {
        return registrationRepository.findById(id);
    }

    public void deleteRegistration(Long id) {
        registrationRepository.deleteById(id);
    }
}
