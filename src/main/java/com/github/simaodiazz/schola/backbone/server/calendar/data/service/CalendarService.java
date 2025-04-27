package com.github.simaodiazz.schola.backbone.server.calendar.data.service;

import com.github.simaodiazz.schola.backbone.server.calendar.data.model.Eventus;
import com.github.simaodiazz.schola.backbone.server.calendar.data.repository.EventusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CalendarService {

    private final EventusRepository eventusRepository;

    @Autowired
    public CalendarService(EventusRepository eventusRepository) {
        this.eventusRepository = eventusRepository;
    }

    @CachePut(value = "eventus", key = "#eventus.id")
    @CacheEvict(value = {"all_eventus", "eventus_by_user"}, allEntries = true)
    public Eventus saveEvent(Eventus eventus) {
        return eventusRepository.save(eventus);
    }

    @Cacheable(value = "eventus", key = "#id")
    public Optional<Eventus> getEvent(Long id) {
        return eventusRepository.findById(id);
    }

    @CachePut(value = "eventus", key = "#eventus.id")
    @CacheEvict(value = {"all_eventus", "eventus_by_user"}, allEntries = true)
    public Eventus updateEvent(Eventus eventus) {
        return eventusRepository.save(eventus);
    }

    @CacheEvict(value = {"eventus", "all_eventus", "eventus_by_user"}, allEntries = true)
    public void deleteEvent(Long id) {
        eventusRepository.deleteById(id);
    }

    @Cacheable(value = "eventus_by_user", key = "#userId + '-' + #page + '-' + #size", unless = "#result == null")
    public Page<Eventus> getEventsByUserId(long userId, int page, int size) {
        return eventusRepository.findAllByUserId(userId, PageRequest.of(page, size));
    }

    @Cacheable(value = "all_eventus", unless = "#result == null || #result.isEmpty()")
    public List<Eventus> getAllEvents() {
        return eventusRepository.findAll();
    }
}