package com.driveeasy.model;

import com.driveeasy.model.enums.CarCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "car")
public class Car {

    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "model", nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private CarCategory category;
    /**
     * Fixed base fare charged for every rental, independent of distance/time.
     */
    @Column(name = "base_fare", nullable = false)
    private double baseFare;
    /**
     * Variable fare charged per kilometre driven.
     */
    @Column(name = "per_km_rate", nullable = false)
    private double perKmRate;
    /**
     * Variable fare charged per hour of usage.
     */
    @Column(name = "per_hour_rate", nullable = false)
    private double perHourRate;

    @Column(name = "under_maintenance", nullable = false)
    private boolean underMaintenance;

    protected Car() {
        // JPA only
    }

    public Car(long id,
               String model,
               CarCategory category,
               double baseFare,
               double perKmRate,
               double perHourRate,
               boolean underMaintenance) {
        this.id = id;
        this.model = model;
        this.category = category;
        this.baseFare = baseFare;
        this.perKmRate = perKmRate;
        this.perHourRate = perHourRate;
        this.underMaintenance = underMaintenance;
    }

    public long getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public CarCategory getCategory() {
        return category;
    }

    public void setCategory(CarCategory category) {
        this.category = category;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    public double getPerKmRate() {
        return perKmRate;
    }

    public void setPerKmRate(double perKmRate) {
        this.perKmRate = perKmRate;
    }

    public double getPerHourRate() {
        return perHourRate;
    }

    public void setPerHourRate(double perHourRate) {
        this.perHourRate = perHourRate;
    }

    public boolean isUnderMaintenance() {
        return underMaintenance;
    }

    public void setUnderMaintenance(boolean underMaintenance) {
        this.underMaintenance = underMaintenance;
    }
}
