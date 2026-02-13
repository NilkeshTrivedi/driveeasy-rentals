package com.driveeasy.model;

import com.driveeasy.model.enums.ReservationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "car_id", nullable = false)
    private long carId;

    @Column(name = "customer_id", nullable = false)
    private long customerId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    /**
     * Total distance (in kilometres) associated with this reservation.
     * In Phase 1 this is provided by staff as an estimate.
     */
    @Column(name = "total_distance_km", nullable = false)
    private double totalDistanceKm;
    /**
     * Total duration (in hours) for which the car is rented.
     * In Phase 1 this is provided by staff as an estimate.
     */
    @Column(name = "duration_hours", nullable = false)
    private double durationHours;
    /**
     * Final fare calculated using the FareCalculator and stored for reporting.
     */
    @Column(name = "calculated_fare", nullable = false)
    private double calculatedFare;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    protected Reservation() {
        // JPA only
    }

    public Reservation(long id,
                       long carId,
                       long customerId,
                       LocalDate startDate,
                       LocalDate endDate,
                       double totalDistanceKm,
                       double durationHours,
                       double calculatedFare,
                       ReservationStatus status) {
        this.id = id;
        this.carId = carId;
        this.customerId = customerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDistanceKm = totalDistanceKm;
        this.durationHours = durationHours;
        this.calculatedFare = calculatedFare;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public long getCarId() {
        return carId;
    }

    public void setCarId(long carId) {
        this.carId = carId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public double getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(double durationHours) {
        this.durationHours = durationHours;
    }

    public double getCalculatedFare() {
        return calculatedFare;
    }

    public void setCalculatedFare(double calculatedFare) {
        this.calculatedFare = calculatedFare;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
