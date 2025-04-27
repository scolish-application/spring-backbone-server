package com.github.simaodiazz.schola.backbone.server.canteen.data.repository;

import com.github.simaodiazz.schola.backbone.server.canteen.data.model.Farina;
import com.github.simaodiazz.schola.backbone.server.canteen.data.model.FarinaTemporal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FarinaRepository extends JpaRepository<Farina, Long> {

    List<Farina> findByDate(LocalDate date);

    Page<Farina> findByDate(LocalDate date, Pageable pageable);

    List<Farina> findByDateBetween(LocalDate startDate, LocalDate endDate);

    Page<Farina> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<Farina> findByDateAndTemporal(LocalDate date, FarinaTemporal temporal);

    @Query("SELECT f FROM Farina f WHERE f.date >= :startDate ORDER BY f.date ASC")
    Page<Farina> findUpcomingMeals(LocalDate startDate, Pageable pageable);

    @Query("SELECT f FROM Farina f WHERE f.date >= :today AND f.reservationDeadline >= :today ORDER BY f.date ASC")
    Page<Farina> findAvailableForReservation(LocalDate today, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.farina.id = :mealId")
    int countReservationsForMeal(Long mealId);
}