package com.driveeasy.model;

import com.driveeasy.model.enums.CarCategory;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private CarCategory category;

    /**
     * Fixed base charge applied to every rental regardless of distance/time.
     * Think of this as a booking/rental initiation fee.
     */
    @Column(name = "base_fare", nullable = false)
    private double baseFare;

    /**
     * Variable rate charged per kilometre driven.
     */
    @Column(name = "per_km_rate", nullable = false)
    private double perKmRate;

    /**
     * Variable rate charged per hour of rental duration.
     */
    @Column(name = "per_hour_rate", nullable = false)
    private double perHourRate;

    @Column(name = "under_maintenance", nullable = false)
    private boolean underMaintenance;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    protected Car() {
        // JPA only
    }

    public Car(String model,
               CarCategory category,
               double baseFare,
               double perKmRate,
               double perHourRate) {
        this.model = model;
        this.category = category;
        this.baseFare = baseFare;
        this.perKmRate = perKmRate;
        this.perHourRate = perHourRate;
        this.underMaintenance = false;
    }

    // Getters
    public Long getId() { return id; }
    public String getModel() { return model; }
    public CarCategory getCategory() { return category; }
    public double getBaseFare() { return baseFare; }
    public double getPerKmRate() { return perKmRate; }
    public double getPerHourRate() { return perHourRate; }
    public boolean isUnderMaintenance() { return underMaintenance; }
    public List<Reservation> getReservations() { return reservations; }

    // Setters
    public void setModel(String model) { this.model = model; }
    public void setCategory(CarCategory category) { this.category = category; }
    public void setBaseFare(double baseFare) { this.baseFare = baseFare; }
    public void setPerKmRate(double perKmRate) { this.perKmRate = perKmRate; }
    public void setPerHourRate(double perHourRate) { this.perHourRate = perHourRate; }
    public void setUnderMaintenance(boolean underMaintenance) { this.underMaintenance = underMaintenance; }
}
