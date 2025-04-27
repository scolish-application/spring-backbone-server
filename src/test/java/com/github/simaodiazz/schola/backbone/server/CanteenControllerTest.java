package com.github.simaodiazz.schola.backbone.server;

import com.github.simaodiazz.schola.backbone.server.canteen.data.model.Farina;
import com.github.simaodiazz.schola.backbone.server.canteen.data.model.FarinaTemporal;
import com.github.simaodiazz.schola.backbone.server.canteen.data.model.Reservation;
import com.github.simaodiazz.schola.backbone.server.canteen.data.model.ReservationStatus;
import com.github.simaodiazz.schola.backbone.server.canteen.data.service.FarinaService;
import com.github.simaodiazz.schola.backbone.server.canteen.data.service.ReservationService;
import com.github.simaodiazz.schola.backbone.server.router.controller.CanteenController;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.FarinaRequest;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CanteenControllerTest {

    @Mock
    private FarinaService farinaService;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private CanteenController canteenController;

    private Farina testFarina;
    private FarinaRequest testFarinaRequest;
    private Reservation testReservation;
    private User testUser;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // Setup test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testFarina = new Farina(
                1L,
                "Test Meal",
                "Description",
                LocalDate.now(),
                FarinaTemporal.LUNCH,
                50,
                true,
                LocalDate.from(LocalDateTime.now().plusDays(1)),
                10.0
        );

        testFarinaRequest = new FarinaRequest();
        testFarinaRequest.setId(1L);
        testFarinaRequest.setName("Test Meal");
        testFarinaRequest.setDescription("Description");
        testFarinaRequest.setDate(LocalDate.now());
        testFarinaRequest.setTemporal(FarinaTemporal.LUNCH);
        testFarinaRequest.setMaxReservations(50);
        testFarinaRequest.setVegetarian(true);
        testFarinaRequest.setReservationDeadline(LocalDate.from(LocalDateTime.now().plusDays(1)));
        testFarinaRequest.setPrice(10.0);

        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setFarina(testFarina);
        testReservation.setUser(testUser);
        testReservation.setReservationTime(LocalDateTime.now());
        testReservation.setStatus(ReservationStatus.CONFIRMED);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getMealById_ShouldReturnMeal_WhenMealExists() {
        // Arrange
        when(farinaService.getMealById(anyLong())).thenReturn(testFarina);

        // Act
        ResponseEntity<FarinaRequest> response = canteenController.getMealById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testFarina.getId(), response.getBody().getId());
        assertEquals(testFarina.getName(), response.getBody().getName());
    }

    @Test
    void getMealById_ShouldReturnNotFound_WhenMealDoesNotExist() {
        // Arrange
        when(farinaService.getMealById(anyLong())).thenReturn(null);

        // Act
        ResponseEntity<FarinaRequest> response = canteenController.getMealById(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getUpcomingMeals_ShouldReturnMeals() {
        // Arrange
        Page<Farina> mealPage = new PageImpl<>(List.of(testFarina));
        when(farinaService.getUpcomingMeals(any(Pageable.class))).thenReturn(mealPage);

        // Act
        ResponseEntity<Page<FarinaRequest>> response = canteenController.getUpcomingMeals(pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void createMeal_ShouldReturnCreated_WhenMealIsValid() {
        // Arrange
        when(farinaService.createMeal(any(Farina.class))).thenReturn(testFarina);

        // Act
        ResponseEntity<FarinaRequest> response = canteenController.createMeal(testFarinaRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testFarina.getName(), response.getBody().getName());
    }

    @Test
    void createMeal_ShouldReturnBadRequest_WhenInvalidData() {
        // Arrange
        when(farinaService.createMeal(any(Farina.class))).thenThrow(new IllegalArgumentException("Invalid data"));

        // Act
        ResponseEntity<FarinaRequest> response = canteenController.createMeal(testFarinaRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getMealsByDate_ShouldReturnMeals() {
        // Arrange
        LocalDate testDate = LocalDate.now();
        when(farinaService.getMealsByDate(any(LocalDate.class))).thenReturn(List.of(testFarina));

        // Act
        ResponseEntity<List<FarinaRequest>> response = canteenController.getMealsByDate(testDate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void createReservation_ShouldReturnCreated_WhenReservationIsValid() {
        // Arrange
        when(reservationService.createReservation(eq(testUser), eq(1L), anyString()))
                .thenReturn(testReservation);

        // Act
        ResponseEntity<?> response = canteenController.createReservation(1L, "No nuts", testUser);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reservationService).createReservation(testUser, 1L, "No nuts");
    }

    @Test
    void createReservation_ShouldReturnBadRequest_WhenInvalidData() {
        // Arrange
        String errorMessage = "Cannot reserve meal";
        // Use doThrow() para evitar problemas de correspondência de argumentos
        doThrow(new IllegalArgumentException(errorMessage))
                .when(reservationService).createReservation(eq(testUser), eq(1L), isNull());

        // Act
        ResponseEntity<?> response = canteenController.createReservation(1L, null, testUser);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void updateReservationStatus_ShouldReturnOk_WhenStatusUpdateIsValid() {
        // Arrange
        when(reservationService.updateReservationStatus(eq(1L), eq(ReservationStatus.CANCELED), eq(testUser)))
                .thenReturn(testReservation);

        // Act
        ResponseEntity<?> response = canteenController.updateReservationStatus(1L, ReservationStatus.CANCELED, testUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reservationService).updateReservationStatus(1L, ReservationStatus.CANCELED, testUser);
    }

    @Test
    void deleteReservation_ShouldReturnNoContent_WhenDeleteIsSuccessful() {
        // Arrange
        doNothing().when(reservationService).deleteReservation(eq(1L), eq(testUser));

        // Act
        ResponseEntity<?> response = canteenController.deleteReservation(1L, testUser);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reservationService).deleteReservation(1L, testUser);
    }
}