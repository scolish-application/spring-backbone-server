package com.github.simaodiazz.schola.backbone.server;

import com.github.simaodiazz.schola.backbone.server.calendar.data.model.Eventus;
import com.github.simaodiazz.schola.backbone.server.calendar.data.service.CalendarService;
import com.github.simaodiazz.schola.backbone.server.router.controller.CalendarController;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.EventusRequest;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import com.github.simaodiazz.schola.backbone.server.security.service.UserDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalendarControllerTest {

    @Mock
    private CalendarService calendarService;

    @Mock
    private UserDataService userDataService;

    @InjectMocks
    private CalendarController calendarController;

    private Eventus testEvent;
    private EventusRequest testEventDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        // Setup test event
        testEvent = new Eventus();
        testEvent.setId(1L);
        testEvent.setName("Test Event");
        testEvent.setDescription("Test Description");
        testEvent.setStart(LocalDateTime.now().plusDays(1));
        testEvent.setEnd(LocalDateTime.now().plusDays(1).plusHours(2));
        testEvent.setUser(testUser);
        testEvent.setCreated(LocalDateTime.now());
        testEvent.setUpdated(LocalDateTime.now());

        // Setup test event DTO
        testEventDTO = new EventusRequest();
        testEventDTO.setId(1L);
        testEventDTO.setName("Test Event");
        testEventDTO.setDescription("Test Description");
        testEventDTO.setStart(LocalDateTime.now().plusDays(1));
        testEventDTO.setEnd(LocalDateTime.now().plusDays(1).plusHours(2));
        testEventDTO.setUserId(1L);
        testEventDTO.setUserName("testuser");
        testEventDTO.setCreated(LocalDateTime.now());
        testEventDTO.setUpdated(LocalDateTime.now());
    }

    @Test
    void createEvent_ShouldReturnCreatedEvent() {
        // Arrange
        when(userDataService.id(anyLong())).thenReturn(Optional.of(testUser));
        when(calendarService.saveEvent(any(Eventus.class))).thenReturn(testEvent);

        // Act
        ResponseEntity<EventusRequest> response = calendarController.createEvent(testEventDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testEvent.getId(), response.getBody().getId());
        assertEquals(testEvent.getName(), response.getBody().getName());
    }

    @Test
    void createEvent_ShouldReturnBadRequest_WhenUserNotFound() {
        // Arrange
        when(userDataService.id(anyLong())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<EventusRequest> response = calendarController.createEvent(testEventDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateEvent_ShouldReturnUpdatedEvent() {
        // Arrange
        when(userDataService.id(anyLong())).thenReturn(Optional.of(testUser));
        when(calendarService.updateEvent(any(Eventus.class))).thenReturn(testEvent);

        // Act
        ResponseEntity<EventusRequest> response = calendarController.updateEvent(1L, testEventDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(testEvent.getName(), response.getBody().getName());
    }

    @Test
    void getEvent_ShouldReturnEvent_WhenEventExists() {
        // Arrange
        when(calendarService.getEvent(anyLong())).thenReturn(Optional.of(testEvent));

        // Act
        ResponseEntity<EventusRequest> response = calendarController.getEvent(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testEvent.getId(), response.getBody().getId());
        assertEquals(testEvent.getName(), response.getBody().getName());
    }

    @Test
    void getEvent_ShouldReturnNotFound_WhenEventDoesNotExist() {
        // Arrange
        when(calendarService.getEvent(anyLong())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<EventusRequest> response = calendarController.getEvent(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteEvent_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(calendarService).deleteEvent(anyLong());

        // Act
        ResponseEntity<Void> response = calendarController.deleteEvent(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(calendarService).deleteEvent(1L);
    }

    @Test
    void getEventsByUserId_ShouldReturnEvents() {
        // Arrange
        Page<Eventus> eventPage = new PageImpl<>(List.of(testEvent));
        when(calendarService.getEventsByUserId(anyLong(), anyInt(), anyInt())).thenReturn(eventPage);

        // Act
        ResponseEntity<Page<EventusRequest>> response = calendarController.getEventsByUserId(1L, 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(calendarService).getEventsByUserId(1L, 0, 10);
    }
}