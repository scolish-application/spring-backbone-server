package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.calendar.data.model.Eventus;
import com.github.simaodiazz.schola.backbone.server.calendar.data.service.CalendarService;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.EventusRequest;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import com.github.simaodiazz.schola.backbone.server.security.service.UserDataService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class CalendarController {

    private final CalendarService calendarService;
    private final UserDataService userDataService;

    @Autowired
    public CalendarController(CalendarService calendarService, UserDataService userDataService) {
        this.calendarService = calendarService;
        this.userDataService = userDataService;
    }

    @PostMapping("/eventus")
    public ResponseEntity<EventusRequest> createEvent(@Valid @RequestBody EventusRequest eventusDTO) {
        try {
            Eventus eventus = convertToEntity(eventusDTO);
            Eventus savedEvent = calendarService.saveEvent(eventus);
            return ResponseEntity.ok(convertToDTO(savedEvent));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/eventus/{id}")
    public ResponseEntity<EventusRequest> updateEvent(@PathVariable Long id, @Valid @RequestBody EventusRequest eventusDTO) {
        try {
            eventusDTO.setId(id);
            Eventus eventus = convertToEntity(eventusDTO);
            Eventus updatedEvent = calendarService.updateEvent(eventus);
            return ResponseEntity.ok(convertToDTO(updatedEvent));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/eventus/{id}")
    public ResponseEntity<EventusRequest> getEvent(@PathVariable Long id) {
        Optional<Eventus> eventusOpt = calendarService.getEvent(id);
        return eventusOpt
                .map(eventus -> ResponseEntity.ok(convertToDTO(eventus)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/eventus/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        try {
            calendarService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/eventus/user/{id}")
    public ResponseEntity<Page<EventusRequest>> getEventsByUserId(
            @PathVariable long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            final @NotNull Page<Eventus> eventsPage = calendarService.getEventsByUserId(id, page, size);

            List<EventusRequest> eventDTOs = eventsPage.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            Page<EventusRequest> dtoPage = new PageImpl<>(
                    eventDTOs,
                    eventsPage.getPageable(),
                    eventsPage.getTotalElements()
            );

            return ResponseEntity.ok(dtoPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper methods for DTO conversion
    private EventusRequest convertToDTO(Eventus eventus) {
        EventusRequest dto = new EventusRequest();
        dto.setId(eventus.getId());
        dto.setName(eventus.getName());
        dto.setDescription(eventus.getDescription());
        dto.setStart(eventus.getStart());
        dto.setEnd(eventus.getEnd());
        dto.setUserId(eventus.getUser().getId());
        dto.setUserName(eventus.getUser().getUsername());
        dto.setCreated(eventus.getCreated());
        dto.setUpdated(eventus.getUpdated());
        return dto;
    }

    private Eventus convertToEntity(EventusRequest dto) {
        User user = userDataService.id(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + dto.getUserId()));

        Eventus eventus = new Eventus();
        if (dto.getId() != null) {
            eventus.setId(dto.getId());
        }
        eventus.setName(dto.getName());
        eventus.setDescription(dto.getDescription());
        eventus.setStart(dto.getStart());
        eventus.setEnd(dto.getEnd());
        eventus.setUser(user);
        return eventus;
    }
}