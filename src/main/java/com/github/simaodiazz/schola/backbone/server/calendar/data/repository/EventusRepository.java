package com.github.simaodiazz.schola.backbone.server.calendar.data.repository;

import com.github.simaodiazz.schola.backbone.server.calendar.data.model.Eventus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventusRepository extends JpaRepository<Eventus, Long> {

    Page<Eventus> findAllByUserId(final long id, final PageRequest request);

}
