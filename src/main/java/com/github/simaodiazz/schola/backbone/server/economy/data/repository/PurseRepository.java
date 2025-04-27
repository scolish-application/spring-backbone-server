package com.github.simaodiazz.schola.backbone.server.economy.data.repository;

import com.github.simaodiazz.schola.backbone.server.economy.data.model.Purse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurseRepository extends JpaRepository<Purse, Long> {

    Optional<Purse> findByUserId(final long id);

}
