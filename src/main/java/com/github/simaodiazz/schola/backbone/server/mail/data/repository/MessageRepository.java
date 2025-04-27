package com.github.simaodiazz.schola.backbone.server.mail.data.repository;

import com.github.simaodiazz.schola.backbone.server.mail.data.model.Courier;
import com.github.simaodiazz.schola.backbone.server.mail.data.model.Message;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<@NotNull Message> findByAuthor(Courier author);

    Page<@NotNull Message> findByAuthorId(Long authorId, Pageable pageable);

    @Query("SELECT m FROM Message m JOIN m.couriers c WHERE c.id = :courierId")
    List<@NotNull Message> findByCourierId(Long courierId);

    @Query("SELECT m FROM Message m JOIN m.couriers c WHERE c.id = :courierId")
    Page<@NotNull Message> findByCourierId(Long courierId, Pageable pageable);

    List<@NotNull Message> findByContentContainingIgnoreCase(String keyword);

    Page<@NotNull Message> findByContentContainingIgnoreCase(String keyword, Pageable pageable);

}