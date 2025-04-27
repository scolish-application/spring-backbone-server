package com.github.simaodiazz.schola.backbone.server.mail.data.service;

import com.github.simaodiazz.schola.backbone.server.mail.data.model.Courier;
import com.github.simaodiazz.schola.backbone.server.mail.data.model.Message;
import com.github.simaodiazz.schola.backbone.server.mail.data.repository.CourierRepository;
import com.github.simaodiazz.schola.backbone.server.mail.data.repository.MessageRepository;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourierService {

    private final CourierRepository courierRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public CourierService(CourierRepository courierRepository, MessageRepository messageRepository) {
        this.courierRepository = courierRepository;
        this.messageRepository = messageRepository;
    }

    @Cacheable(value = "couriers", key = "#id")
    public @Nullable Courier courier(final long id) {
        return courierRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "couriers", key = "#userId")
    public @Nullable Courier user(final long id) {
        return courierRepository.findByUserId(id).orElse(null);
    }

    @Cacheable(value = "couriers", key = "#user.id")
    public @Nullable Courier user(final @NotNull User user) {
        final long id = user.getId();
        return user(id);
    }

    @CachePut(value = "couriers", key = "#user.id")
    @Transactional
    public void save(User user) {
        Courier courier = new Courier(user);
        courierRepository.save(courier);
    }

    @Cacheable(value = "allCouriers")
    public Page<Courier> couriers(Pageable pageable) {
        return courierRepository.findAll(pageable);
    }

    @CacheEvict(value = {"couriers", "allCouriers"}, key = "#id")
    @Transactional
    public void deleteCourier(Long id) {
        courierRepository.deleteById(id);
    }

    // Message methods
    @Cacheable(value = "messages", key = "#id")
    public Message message(Long id) {
        return messageRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "sentMessages", key = "#courierId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Message> sent(Long id, Pageable pageable) {
        return messageRepository.findByAuthorId(id, pageable);
    }

    @Cacheable(value = "receivedMessages", key = "#courierId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Message> received(Long id, Pageable pageable) {
        return messageRepository.findByCourierId(id, pageable);
    }

    @Cacheable(value = "messagesByKeyword", key = "#keyword + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Message> searchMessages(String keyword, Pageable pageable) {
        return messageRepository.findByContentContainingIgnoreCase(keyword, pageable);
    }

    @Caching(
            put = {@CachePut(value = "messages", key = "#result.id")},
            evict = {
                    @CacheEvict(value = "sentMessages", allEntries = true),
                    @CacheEvict(value = "receivedMessages", allEntries = true),
                    @CacheEvict(value = "messagesByKeyword", allEntries = true)
            }
    )
    @Transactional
    public Message sendMessage(String content, Courier author, List<Courier> recipients) {
        Message message = new Message(content, author, recipients);
        return messageRepository.save(message);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "messages", key = "#id"),
                    @CacheEvict(value = "sentMessages", allEntries = true),
                    @CacheEvict(value = "receivedMessages", allEntries = true),
                    @CacheEvict(value = "messagesByKeyword", allEntries = true)
            }
    )
    @Transactional
    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }
}