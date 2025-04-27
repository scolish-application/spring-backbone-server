package com.github.simaodiazz.schola.backbone.server.canteen.data.repository;

import com.github.simaodiazz.schola.backbone.server.canteen.data.model.Reservation;
import com.github.simaodiazz.schola.backbone.server.canteen.data.model.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findByUserId(Long userId, Pageable pageable);

    Page<Reservation> findByFarinaId(Long mealId, Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.farina.date >= :date AND r.user.id = :userId ORDER BY r.farina.date ASC")
    Page<Reservation> findUpcomingReservationsForUser(Long userId, LocalDate date, Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.farina.id = :mealId")
    Optional<Reservation> findByUserIdAndMealId(Long userId, Long mealId);

    List<Reservation> findByFarinaIdAndStatus(Long mealId, ReservationStatus status);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.farina.id = :mealId AND r.status != 'CANCELED'")
    int countActiveReservationsForMeal(Long mealId);

    @Query("SELECT r FROM Reservation r WHERE r.farina.date = :date AND r.user.id = :userId")
    List<Reservation> findByDateAndUserId(LocalDate date, Long userId);
}