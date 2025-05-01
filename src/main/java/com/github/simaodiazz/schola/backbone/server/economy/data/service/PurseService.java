package com.github.simaodiazz.schola.backbone.server.economy.data.service;

import com.github.simaodiazz.schola.backbone.server.economy.data.model.Purse;
import com.github.simaodiazz.schola.backbone.server.economy.data.repository.PurseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PurseService {

    private final PurseRepository purseRepository;

    public PurseService(PurseRepository purseRepository) {
        this.purseRepository = purseRepository;
    }

    public Optional<Purse> getPurseById(final long id) {
        return purseRepository.findById(id);
    }

    public Optional<Purse> getPurseByUserId(final long id) {
        return purseRepository.findByUserId(id);
    }

    public Purse savePurse(final Purse purse) {
        return purseRepository.save(purse);
    }

    public void deletePurse(final Long id) {
        purseRepository.deleteById(id);
    }
}
