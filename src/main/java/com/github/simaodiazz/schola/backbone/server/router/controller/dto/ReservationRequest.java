package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import com.github.simaodiazz.schola.backbone.server.canteen.data.model.FarinaTemporal;
import com.github.simaodiazz.schola.backbone.server.canteen.data.model.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationRequest {

    private Long id;
    private Long farinaId;
    private String farinaName;
    private LocalDate farinaDate;
    private FarinaTemporal farinaTemporal;
    private Long userId;
    private String userName;
    private LocalDateTime reservationTime;
    private ReservationStatus status;
    private String specialRequirements;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFarinaId() {
        return farinaId;
    }

    public void setFarinaId(Long farinaId) {
        this.farinaId = farinaId;
    }

    public String getFarinaName() {
        return farinaName;
    }

    public void setFarinaName(String farinaName) {
        this.farinaName = farinaName;
    }

    public LocalDate getFarinaDate() {
        return farinaDate;
    }

    public void setFarinaDate(LocalDate farinaDate) {
        this.farinaDate = farinaDate;
    }

    public FarinaTemporal getFarinaTemporal() {
        return farinaTemporal;
    }

    public void setFarinaTemporal(FarinaTemporal farinaTemporal) {
        this.farinaTemporal = farinaTemporal;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getSpecialRequirements() {
        return specialRequirements;
    }

    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }
}