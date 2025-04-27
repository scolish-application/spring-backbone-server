package com.github.simaodiazz.schola.backbone.server.registry.data.repository;

import com.github.simaodiazz.schola.backbone.server.registry.data.model.Carte;
import com.github.simaodiazz.schola.backbone.server.registry.data.model.CarteColor;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarteRepository extends CrudRepository<Carte, Long> {

    Optional<Carte> findByCode(String code);

    List<Carte> findByColor(CarteColor color);

    Optional<Carte> findByUser(User user);

    boolean existsByCode(String code);
}