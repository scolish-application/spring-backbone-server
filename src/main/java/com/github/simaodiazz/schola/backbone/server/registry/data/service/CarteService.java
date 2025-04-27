package com.github.simaodiazz.schola.backbone.server.registry.data.service;

import com.github.simaodiazz.schola.backbone.server.registry.data.model.Carte;
import com.github.simaodiazz.schola.backbone.server.registry.data.repository.CarteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CarteService {

    private final CarteRepository carteRepository;

    @Autowired
    public CarteService(CarteRepository carteRepository) {
        this.carteRepository = carteRepository;
    }

    public Carte saveCarte(Carte carte) {
        return carteRepository.save(carte);
    }

    public Optional<Carte> getCarte(String code) {
        return carteRepository.findById(code);
    }

    public void deleteCarte(String code) {
        carteRepository.deleteById(code);
    }
}
