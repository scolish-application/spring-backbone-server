package com.github.simaodiazz.schola.backbone.server.registry.data.service;

import com.github.simaodiazz.schola.backbone.server.registry.data.model.Carte;
import com.github.simaodiazz.schola.backbone.server.registry.data.model.CarteColor;
import com.github.simaodiazz.schola.backbone.server.registry.data.repository.CarteRepository;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class CarteService {

    private final CarteRepository carteRepository;

    @Autowired
    public CarteService(CarteRepository carteRepository) {
        this.carteRepository = carteRepository;
    }

    public Carte saveCarte(Carte carte) {
        if (carte.getCreated() == null) {
            carte.setCreated(java.time.LocalDateTime.now());
        }
        return carteRepository.save(carte);
    }

    public Optional<Carte> getCarte(String code) {
        return carteRepository.findByCode(code);
    }

    public Optional<Carte> getCarteById(Long id) {
        return carteRepository.findById(id);
    }

    public List<Carte> getAllCartes() {
        return StreamSupport.stream(carteRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<Carte> getCartesByColor(CarteColor color) {
        return carteRepository.findByColor(color);
    }

    public Optional<Carte> getCarteByUser(User user) {
        return carteRepository.findByUser(user);
    }

    public void deleteCarte(String code) {
        Optional<Carte> carte = carteRepository.findByCode(code);
        carte.ifPresent(c -> carteRepository.deleteById(c.getId()));
    }

    public void deleteCarteById(Long id) {
        carteRepository.deleteById(id);
    }

    public boolean existsByCode(String code) {
        return carteRepository.existsByCode(code);
    }
}