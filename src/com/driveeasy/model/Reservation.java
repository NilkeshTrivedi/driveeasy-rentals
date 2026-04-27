package com.driveeasy.model;

import com.driveeasy.model.enums.ReservationStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Proper JPA association — eliminates raw carId foreign key.
     * Enables JOIN queries and referential integrity.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Estimated distance in km — used for fare calculation.
     * Updated to actual distance on return in a real deployment.
     */
    @Column(name = "estimated_distance_km", nullable = false)
    private double estimatedDistanceKm;

    /**
     * Estimated duration in hours — used for fare calculation.
     */
    @Column(name = "estimated_duration_hours", nullable = false)
    private double estimatedDurationHours;

    /**
     * Pre-calculated fare stored for billing and reporting.
     * Broken down into components for transparency.
     */
    @Column(name = "base_fare_charged", nullable = false)
    private double baseFareCharged;

    @Column(name = "distance_fare", nullable = false)
    private double distanceFare;

    @Column(name = "duration_fare", nullable = false)
    private double durationFare;

    @Column(name = "category_surcharge", nullable = false)
    private double categorySurcharge;

    @Column(name = "total_fare", nullable = false)
    private double totalFare;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private ReservationStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", length = 255)
    private String cancellationReason;

    protected Reservation() {
        // JPA only
    }

    public Reservation(Car car,
                       Customer customer,
                       LocalDate startDate,
                       LocalDate endDate,
                       double estimatedDistanceKm,
                       double estimatedDurationHours) {
        this.car = car;
        this.customer = customer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.estimatedDistanceKm = estimatedDistanceKm;
        this.estimatedDurationHours = estimatedDurationHours;
        this.status = ReservationStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Getters
    public Long getId() { return id; }
    public Car getCar() { return car; }
    public Customer getCustomer() { return customer; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getEstimatedDistanceKm() { return estimatedDistanceKm; }
    public double getEstimatedDurationHours() { return estimatedDurationHours; }
    public double getBaseFareCharged() { return baseFareCharged; }
    public double getDistanceFare() { return distanceFare; }
    public double getDurationFare() { return durationFare; }
    public double getCategorySurcharge() { return categorySurcharge; }
    public double getTotalFare() { return totalFare; }
    public ReservationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public String getCancellationReason() { return cancellationReason; }

    // Setters
    public void setCar(Car car) { this.car = car; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setEstimatedDistanceKm(double estimatedDistanceKm) { this.estimatedDistanceKm = estimatedDistanceKm; }
    public void setEstimatedDurationHours(double estimatedDurationHours) { this.estimatedDurationHours = estimatedDurationHours; }
    public void setBaseFareCharged(double baseFareCharged) { this.baseFareCharged = baseFareCharged; }
    public void setDistanceFare(double distanceFare) { this.distanceFare = distanceFare; }
    public void setDurationFare(double durationFare) { this.durationFare = durationFare; }
    public void setCategorySurcharge(double categorySurcharge) { this.categorySurcharge = categorySurcharge; }
    public void setTotalFare(double totalFare) { this.totalFare = totalFare; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
}
