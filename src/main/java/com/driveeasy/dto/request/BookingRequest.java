package com.driveeasy.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public class BookingRequest {

    @NotNull(message = "Car ID is required")
    private Long carId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Positive(message = "Estimated distance must be positive")
    private double estimatedDistanceKm;

    @Positive(message = "Estimated duration must be positive")
    private double estimatedDurationHours;

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public double getEstimatedDistanceKm() { return estimatedDistanceKm; }
    public void setEstimatedDistanceKm(double v) { this.estimatedDistanceKm = v; }

    public double getEstimatedDurationHours() { return estimatedDurationHours; }
    public void setEstimatedDurationHours(double v) { this.estimatedDurationHours = v; }
}