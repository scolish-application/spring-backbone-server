package com.github.simaodiazz.schola.backbone.server.canteen.data.model;

public enum ReservationStatus {

    PENDING("Pendente"),
    CONFIRMED("Confirmada"),
    CANCELED("Cancelada"),
    COMPLETED("Concluída");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
