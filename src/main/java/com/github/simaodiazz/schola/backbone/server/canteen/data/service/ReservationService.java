package com.github.simaodiazz.schola.backbone.server.canteen.data.service;

import com.github.simaodiazz.schola.backbone.server.canteen.data.model.Farina;
import com.github.simaodiazz.schola.backbone.server.canteen.data.model.Reservation;
import com.github.simaodiazz.schola.backbone.server.canteen.data.model.ReservationStatus;
import com.github.simaodiazz.schola.backbone.server.canteen.data.repository.ReservationRepository;
import com.github.simaodiazz.schola.backbone.server.security.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final FarinaService farinaService;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, FarinaService farinaService) {
        this.reservationRepository = reservationRepository;
        this.farinaService = farinaService;
    }

    @Cacheable(value = "reservations", key = "#id")
    public Reservation getReservationById(final long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "userReservations", key = "#userId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Reservation> getUserReservations(Long userId, Pageable pageable) {
        return reservationRepository.findByUserId(userId, pageable);
    }

    @Cacheable(value = "upcomingUserReservations", key = "#userId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Reservation> getUpcomingUserReservations(Long userId, Pageable pageable) {
        return reservationRepository.findUpcomingReservationsForUser(userId, LocalDate.now(), pageable);
    }

    @Cacheable(value = "mealReservations", key = "#mealId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Reservation> getMealReservations(Long mealId, Pageable pageable) {
        return reservationRepository.findByFarinaId(mealId, pageable);
    }

    public boolean hasUserReservedMeal(Long userId, Long mealId) {
        return reservationRepository.findByUserIdAndMealId(userId, mealId).isPresent();
    }

    @CachePut(value = "reservations", key = "#result.id")
    @CacheEvict(value = {"userReservations", "upcomingUserReservations", "mealReservations"}, allEntries = true)
    @Transactional
    public Reservation createReservation(User user, Long mealId, String specialRequirements) {
        Farina meal = farinaService.getMealById(mealId);
        if (meal == null) {
            throw new IllegalArgumentException("Refeição não encontrada");
        }

        Optional<Reservation> existingReservation =
                reservationRepository.findByUserIdAndMealId(user.getId(), mealId);
        if (existingReservation.isPresent()) {
            throw new IllegalArgumentException("Usuário já possui uma reserva para esta refeição");
        }

        if (!farinaService.isMealAvailableForReservation(mealId)) {
            throw new IllegalArgumentException("Refeição não está disponível para reserva");
        }

        List<Reservation> sameDay = reservationRepository.findByDateAndUserId(meal.getDate(), user.getId());
        if (!sameDay.isEmpty()) {
            for (Reservation r : sameDay) {
                if (r.getFarina().getTemporal() == meal.getTemporal() &&
                        r.getStatus() != ReservationStatus.CANCELED) {
                    throw new IllegalArgumentException("Usuário já possui uma reserva para uma refeição do mesmo tipo neste dia");
                }
            }
        }

        Reservation reservation = new Reservation(
                meal,
                user,
                LocalDateTime.now(),
                ReservationStatus.CONFIRMED,
                specialRequirements
        );

        return reservationRepository.save(reservation);
    }

    @CachePut(value = "reservations", key = "#result.id")
    @CacheEvict(value = {"userReservations", "upcomingUserReservations", "mealReservations"}, allEntries = true)
    @Transactional
    public Reservation updateReservationStatus(Long reservationId, ReservationStatus status, User user) {
        Optional<Reservation> optReservation = reservationRepository.findById(reservationId);
        if (optReservation.isEmpty()) {
            throw new IllegalArgumentException("Reserva não encontrada");
        }

        Reservation reservation = optReservation.get();

        if (!(reservation.getUser().getId() == user.getId())) {
            throw new IllegalArgumentException("Usuário não tem permissão para modificar esta reserva");
        }

        if (status == ReservationStatus.CANCELED) {
            if (LocalDate.now().isAfter(reservation.getFarina().getReservationDeadline())) {
                throw new IllegalArgumentException("Não é possível cancelar após o prazo final");
            }
        }

        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }

    @CacheEvict(value = {"reservations", "userReservations", "upcomingUserReservations", "mealReservations"}, allEntries = true)
    @Transactional
    public void deleteReservation(Long reservationId, User user) {
        Optional<Reservation> optReservation = reservationRepository.findById(reservationId);
        if (optReservation.isEmpty()) {
            throw new IllegalArgumentException("Reserva não encontrada");
        }

        Reservation reservation = optReservation.get();

        if (!(reservation.getUser().getId() == user.getId())) {
            throw new IllegalArgumentException("Usuário não tem permissão para excluir esta reserva");
        }

        if (LocalDate.now().isAfter(reservation.getFarina().getReservationDeadline())) {
            throw new IllegalArgumentException("Não é possível excluir após o prazo final");
        }

        reservationRepository.deleteById(reservationId);
    }

    public int getActiveReservationCount(Long mealId) {
        return reservationRepository.countActiveReservationsForMeal(mealId);
    }
}