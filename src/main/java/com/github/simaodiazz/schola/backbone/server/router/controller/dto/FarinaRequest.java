package com.github.simaodiazz.schola.backbone.server.router.controller.dto;

import com.github.simaodiazz.schola.backbone.server.canteen.data.model.FarinaTemporal;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class FarinaRequest {

    private Long id;
    private String name;
    private String description;
    private LocalDate date;
    private FarinaTemporal temporal;
    private int maxReservations;
    private boolean vegetarian;
    private LocalDate reservationDeadline;
    private int currentReservations;
    private boolean availableForReservation;
    private double price;

}