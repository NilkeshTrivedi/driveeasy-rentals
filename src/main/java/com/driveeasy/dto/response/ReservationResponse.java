package com.driveeasy.dto.response;

import com.driveeasy.model.Reservation;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationResponse {

    private Long id;
    private Long carId;
    private String carModel;
    private Long customerId;
    private String customerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private double estimatedDistanceKm;
    private double estimatedDurationHours;
    private double baseFareCharged;
    private double distanceFare;
    private double durationFare;
    private double categorySurcharge;
    private double totalFare;
    private String status;
    private LocalDateTime createdAt;
    private String cancellationReason;

    public static ReservationResponse from(Reservation r) {
        ReservationResponse dto = new ReservationResponse();
        dto.id = r.getId();
        dto.carId = r.getCar().getId();
        dto.carModel = r.getCar().getModel();
        dto.customerId = r.getCustomer().getId();
        dto.customerName = r.getCustomer().getName();
        dto.startDate = r.getStartDate();
        dto.endDate = r.getEndDate();
        dto.estimatedDistanceKm = r.getEstimatedDistanceKm();
        dto.estimatedDurationHours = r.getEstimatedDurationHours();
        dto.baseFareCharged = r.getBaseFareCharged();
        dto.distanceFare = r.getDistanceFare();
        dto.durationFare = r.getDurationFare();
        dto.categorySurcharge = r.getCategorySurcharge();
        dto.totalFare = r.getTotalFare();
        dto.status = r.getStatus().name();
        dto.createdAt = r.getCreatedAt();
        dto.cancellationReason = r.getCancellationReason();
        return dto;
    }

    public Long getId() { return id; }
    public Long getCarId() { return carId; }
    public String getCarModel() { return carModel; }
    public Long getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getEstimatedDistanceKm() { return estimatedDistanceKm; }
    public double getEstimatedDurationHours() { return estimatedDurationHours; }
    public double getBaseFareCharged() { return baseFareCharged; }
    public double getDistanceFare() { return distanceFare; }
    public double getDurationFare() { return durationFare; }
    public double getCategorySurcharge() { return categorySurcharge; }
    public double getTotalFare() { return totalFare; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getCancellationReason() { return cancellationReason; }
}