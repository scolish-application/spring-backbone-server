package com.github.simaodiazz.schola.backbone.server.mail.data.repository;

import com.github.simaodiazz.schola.backbone.server.mail.data.model.Courier;
import com.github.simaodiazz.schola.backbone.server.mail.data.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByAuthor(Courier author);

    Page<Message> findByAuthorId(Long authorId, Pageable pageable);

    @Query("SELECT m FROM Message m JOIN m.couriers c WHERE c.id = :courierId")
    List<Message> findByCourierId(@Param("courierId") Long courierId);

    @Query("SELECT m FROM Message m JOIN m.couriers c WHERE c.id = :courierId")
    Page<Message> findByCourierId(@Param("courierId") Long courierId, Pageable pageable);

    List<Message> findByContentContainingIgnoreCase(String keyword);

    Page<Message> findByContentContainingIgnoreCase(String keyword, Pageable pageable);
}