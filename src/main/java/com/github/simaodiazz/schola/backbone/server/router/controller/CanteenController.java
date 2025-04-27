package com.github.simaodiazz.schola.backbone.server.router.controller;

import com.github.simaodiazz.schola.backbone.server.canteen.data.model.Farina;
import com.github.simaodiazz.schola.backbone.server.canteen.data.model.FarinaTemporal;
import com.github.simaodiazz.schola.backbone.server.canteen.data.model.Reservation;
import com.github.simaodiazz.schola.backbone.server.canteen.data.model.ReservationStatus;
import com.github.simaodiazz.schola.backbone.server.canteen.data.service.FarinaService;
import com.github.simaodiazz.schola.backbone.server.canteen.data.service.ReservationService;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.FarinaRequest;
import com.github.simaodiazz.schola.backbone.server.router.controller.dto.ReservationRequest;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/canteen")
public class CanteenController {

    private final FarinaService farinaService;
    private final ReservationService reservationService;

    @Autowired
    public CanteenController(FarinaService farinaService, ReservationService reservationService) {
        this.farinaService = farinaService;
        this.reservationService = reservationService;
    }

    @GetMapping("/meals/{id}")
    public ResponseEntity<FarinaRequest> getMealById(@PathVariable Long id) {
        Farina meal = farinaService.getMealById(id);
        if (meal == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToFarinaDTO(meal));
    }

    @GetMapping("/meals")
    public ResponseEntity<Page<FarinaRequest>> getUpcomingMeals(Pageable pageable) {
        Page<Farina> meals = farinaService.getUpcomingMeals(pageable);
        return ResponseEntity.ok(meals.map(this::convertToFarinaDTO));
    }

    @GetMapping("/meals/available")
    public ResponseEntity<Page<FarinaRequest>> getAvailableMeals(Pageable pageable) {
        Page<Farina> meals = farinaService.getAvailableMealsForReservation(pageable);
        return ResponseEntity.ok(meals.map(this::convertToFarinaDTO));
    }

    @GetMapping("/meals/date/{date}")
    public ResponseEntity<List<FarinaRequest>> getMealsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Farina> meals = farinaService.getMealsByDate(date);
        List<FarinaRequest> mealDTOs = meals.stream()
                .map(this::convertToFarinaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mealDTOs);
    }

    @GetMapping("/meals/range")
    public ResponseEntity<List<FarinaRequest>> getMealsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Farina> meals = farinaService.getMealsByDateRange(startDate, endDate);
        List<FarinaRequest> mealDTOs = meals.stream()
                .map(this::convertToFarinaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mealDTOs);
    }

    @GetMapping("/meals/date/{date}/type/{type}")
    public ResponseEntity<List<FarinaRequest>> getMealsByDateAndType(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable FarinaTemporal type) {
        List<Farina> meals = farinaService.getMealsByDateAndTemporal(date, type);
        List<FarinaRequest> mealDTOs = meals.stream()
                .map(this::convertToFarinaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mealDTOs);
    }

    @PostMapping("/meals")
    public ResponseEntity<FarinaRequest> createMeal(@RequestBody FarinaRequest mealDTO) {
        try {
            Farina meal = convertToFarinaEntity(mealDTO);
            Farina createdMeal = farinaService.createMeal(meal);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToFarinaDTO(createdMeal));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/meals/{id}")
    public ResponseEntity<FarinaRequest> updateMeal(@PathVariable Long id, @RequestBody FarinaRequest mealDTO) {
        try {
            Farina meal = convertToFarinaEntity(mealDTO);
            Farina updatedMeal = farinaService.updateMeal(id, meal);
            return ResponseEntity.ok(convertToFarinaDTO(updatedMeal));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/meals/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        try {
            farinaService.deleteMeal(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Reservation Endpoints

    @GetMapping("/reservations/{id}")
    public ResponseEntity<ReservationRequest> getReservationById(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToReservationDTO(reservation));
    }

    @GetMapping("/reservations/user")
    public ResponseEntity<Page<ReservationRequest>> getUserReservations(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<Reservation> reservations = reservationService.getUserReservations(user.getId(), pageable);
        return ResponseEntity.ok(reservations.map(this::convertToReservationDTO));
    }

    @GetMapping("/reservations/user/upcoming")
    public ResponseEntity<Page<ReservationRequest>> getUpcomingUserReservations(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<Reservation> reservations = reservationService.getUpcomingUserReservations(user.getId(), pageable);
        return ResponseEntity.ok(reservations.map(this::convertToReservationDTO));
    }

    @GetMapping("/reservations/meal/{mealId}")
    public ResponseEntity<Page<ReservationRequest>> getMealReservations(
            @PathVariable Long mealId,
            Pageable pageable) {
        Page<Reservation> reservations = reservationService.getMealReservations(mealId, pageable);
        return ResponseEntity.ok(reservations.map(this::convertToReservationDTO));
    }

    @PostMapping("/reservations/meal/{mealId}")
    public ResponseEntity<?> createReservation(
            @PathVariable Long mealId,
            @RequestParam(required = false) String specialRequirements,
            @AuthenticationPrincipal User user) {
        try {
            Reservation reservation = reservationService.createReservation(user, mealId, specialRequirements);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToReservationDTO(reservation));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/reservations/{id}/status")
    public ResponseEntity<?> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status,
            @AuthenticationPrincipal User user) {
        try {
            Reservation reservation = reservationService.updateReservationStatus(id, status, user);
            return ResponseEntity.ok(convertToReservationDTO(reservation));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<?> deleteReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            reservationService.deleteReservation(id, user);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Helper methods for DTO conversion

    private FarinaRequest convertToFarinaDTO(Farina meal) {
        FarinaRequest dto = new FarinaRequest();
        dto.setId(meal.getId());
        dto.setName(meal.getName());
        dto.setDescription(meal.getDescription());
        dto.setDate(meal.getDate());
        dto.setTemporal(meal.getTemporal());
        dto.setMaxReservations(meal.getMaxReservations());
        dto.setVegetarian(meal.isVegetarian());
        dto.setReservationDeadline(meal.getReservationDeadline());
        dto.setCurrentReservations(meal.getCurrentReservationCount());
        dto.setAvailableForReservation(meal.isAvailable());
        return dto;
    }

    private Farina convertToFarinaEntity(FarinaRequest dto) {
        if (dto.getId() != null) {
            return new Farina(
                    dto.getId(),
                    dto.getName(),
                    dto.getDescription(),
                    dto.getDate(),
                    dto.getTemporal(),
                    dto.getMaxReservations(),
                    dto.isVegetarian(),
                    dto.getReservationDeadline(),
                    dto.getPrice()
            );
        } else {
            return new Farina(
                    dto.getName(),
                    dto.getDescription(),
                    dto.getDate(),
                    dto.getTemporal(),
                    dto.getMaxReservations(),
                    dto.isVegetarian(),
                    dto.getReservationDeadline()
            );
        }
    }

    private @NotNull ReservationRequest convertToReservationDTO(@NotNull Reservation reservation) {
        ReservationRequest dto = new ReservationRequest();
        dto.setId(reservation.getId());
        dto.setFarinaId(reservation.getFarina().getId());
        dto.setFarinaName(reservation.getFarina().getName());
        dto.setFarinaDate(reservation.getFarina().getDate());
        dto.setFarinaTemporal(reservation.getFarina().getTemporal());
        dto.setUserId(reservation.getUser().getId());
        dto.setUserName(reservation.getUser().getUsername());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setStatus(reservation.getStatus());
        dto.setSpecialRequirements(reservation.getSpecialRequirements());
        return dto;
    }
}