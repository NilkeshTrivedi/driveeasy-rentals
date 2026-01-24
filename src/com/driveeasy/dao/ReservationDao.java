package com.driveeasy.dao;

import com.driveeasy.model.Reservation;
import com.driveeasy.model.enums.ReservationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationDao {

    // Add a new reservation
    void addReservation(Reservation reservation);

    // Update reservation details (status, dates)
    void updateReservation(Reservation reservation);

    // Cancel reservation
    void cancelReservation(long reservationId);

    // Get reservation by ID
    Optional<Reservation> getReservationById(long id);

    // Get all reservations
    List<Reservation> getAllReservations();

    // Get reservations by customer
    List<Reservation> getReservationsByCustomer(long customerId);

    // Get reservations by car
    List<Reservation> getReservationsByCar(long carId);

    // Check for overlapping reservations for a car
    List<Reservation> getActiveReservationsForCar(long carId, LocalDate startDate, LocalDate endDate);

    // Get reservations by status
    List<Reservation> getReservationsByStatus(ReservationStatus status);
}
