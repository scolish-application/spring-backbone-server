package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.calendar.data.model.Eventus;
import com.github.simaodiazz.schola.backbone.server.calendar.data.service.CalendarService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/events")
public class CalendarController {

    private final CalendarService calendarService;

    @Autowired
    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping("/eventus")
    public ResponseEntity<Eventus> createEvent(@Valid @RequestBody Eventus eventus) {
        Eventus savedEvent = calendarService.saveEvent(eventus);
        return ResponseEntity.ok(savedEvent);
    }

    @PutMapping("/eventus/{id}")
    public ResponseEntity<Eventus> updateEvent(@PathVariable Long id, @Valid @RequestBody Eventus eventus) {
        eventus.setId(id);
        Eventus updatedEvent = calendarService.updateEvent(eventus);
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping("/eventus/{id}")
    public ResponseEntity<Eventus> getEvent(@PathVariable Long id) {
        Optional<Eventus> eventus = calendarService.getEvent(id);
        return eventus.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/eventus/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        calendarService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/eventus/user/{id}")
    public ResponseEntity<Page<Eventus>> getEventsByUserId(
            @PathVariable long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        final @NotNull Page<Eventus> events = calendarService.getEventsByUserId(id, page, size);
        return ResponseEntity.ok(events);
    }
}
