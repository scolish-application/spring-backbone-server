package com.github.simaodiazz.schola.backbone.server.registry.data.repository;

import com.github.simaodiazz.schola.backbone.server.registry.data.model.Carte;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarteRepository extends CrudRepository<Carte, String> {

}
