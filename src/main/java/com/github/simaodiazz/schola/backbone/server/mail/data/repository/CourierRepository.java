package com.github.simaodiazz.schola.backbone.server.mail.data.repository;

import com.github.simaodiazz.schola.backbone.server.mail.data.model.Courier;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {

    Optional<Courier> findByUserId(Long userId);

    @Override
    @NotNull
    Page<Courier> findAll(@NotNull Pageable pageable);

}