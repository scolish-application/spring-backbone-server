package com.github.simaodiazz.schola.backbone.server.canteen.data.model;

import com.github.simaodiazz.schola.backbone.server.database.entity.EntitySuperclass;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@RedisHash
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Farina extends EntitySuperclass {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2048)
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private FarinaTemporal temporal;

    @Column(nullable = false)
    private int maxReservations;

    @Column(nullable = false)
    private boolean vegetarian;

    @OneToMany(mappedBy = "farina", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Reservation> reservations = new HashSet<>();

    @Column(nullable = false)
    private LocalDate reservationDeadline;

    @Column(nullable = false)
    private double price;

    public Farina(long id, String name, String description, LocalDate date, FarinaTemporal temporal, int maxReservations, boolean vegetarian, LocalDate reservationDeadline, double price) {
        super(id);
        this.name = name;
        this.description = description;
        this.date = date;
        this.temporal = temporal;
        this.maxReservations = maxReservations;
        this.vegetarian = vegetarian;
        this.reservationDeadline = reservationDeadline;
        this.price = price;
    }

    public Farina(String name, String description, LocalDate date, FarinaTemporal temporal, int maxReservations, boolean vegetarian, LocalDate reservationDeadline) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.temporal = temporal;
        this.maxReservations = maxReservations;
        this.vegetarian = vegetarian;
        this.reservationDeadline = reservationDeadline;
    }

    public int getCurrentReservationCount() {
        return reservations.size();
    }

    public boolean isAvailable() {
        LocalDate current = LocalDate.now();
        return reservationDeadline.isAfter(current);
    }
}