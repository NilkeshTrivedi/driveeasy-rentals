package com.driveeasy.service;

import com.driveeasy.exception.BookingConflictException;
import com.driveeasy.exception.ResourceNotFoundException;
import com.driveeasy.exception.ValidationException;
import com.driveeasy.model.Car;
import com.driveeasy.model.Customer;
import com.driveeasy.model.Reservation;
import com.driveeasy.model.dto.FareBreakdown;
import com.driveeasy.model.enums.ReservationStatus;
import com.driveeasy.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CarService carService;
    private final CustomerService customerService;
    private final FareCalculator fareCalculator;

    public ReservationService(ReservationRepository reservationRepository,
                              CarService carService,
                              CustomerService customerService,
                              FareCalculator fareCalculator) {
        this.reservationRepository = reservationRepository;
        this.carService = carService;
        this.customerService = customerService;
        this.fareCalculator = fareCalculator;
    }

    /**
     * Previews the fare for a potential booking without persisting anything.
     * Call this to show a breakdown to the user before they confirm.
     */
    @Transactional(readOnly = true)
    public FareBreakdown previewFare(Long carId,
                                     double estimatedDistanceKm,
                                     double estimatedDurationHours) {
        Car car = carService.findById(carId);
        validateUsageInputs(estimatedDistanceKm, estimatedDurationHours);
        return fareCalculator.calculate(car, estimatedDistanceKm, estimatedDurationHours);
    }

    /**
     * Confirms and persists a booking:
     *  1. Validates all inputs
     *  2. Checks car is not under maintenance
     *  3. Checks for date conflicts
     *  4. Calculates fare (auto — no manual entry)
     *  5. Stores itemised fare breakdown on the reservation
     */
    public Reservation bookCar(Long carId, Long customerId,
                               LocalDate startDate, LocalDate endDate,
                               double estimatedDistanceKm, double estimatedDurationHours) {

        validateDates(startDate, endDate);
        validateUsageInputs(estimatedDistanceKm, estimatedDurationHours);

        Car car = carService.findById(carId);
        Customer customer = customerService.findById(customerId);

        if (car.isUnderMaintenance()) {
            throw new ValidationException("Car " + carId + " is currently under maintenance");
        }

        List<Reservation> conflicts =
                reservationRepository.findConflictingReservations(carId, startDate, endDate);
        if (!conflicts.isEmpty()) {
            throw new BookingConflictException(
                    "Car " + carId + " is already booked for the selected dates");
        }

        // Fare is calculated automatically — never entered manually
        FareBreakdown fare = fareCalculator.calculate(car, estimatedDistanceKm, estimatedDurationHours);

        Reservation reservation = new Reservation(car, customer, startDate, endDate,
                estimatedDistanceKm, estimatedDurationHours);

        // Store all fare components for billing transparency
        reservation.setBaseFareCharged(fare.getBaseFare());
        reservation.setDistanceFare(fare.getDistanceFare());
        reservation.setDurationFare(fare.getDurationFare());
        reservation.setCategorySurcharge(fare.getCategorySurcharge());
        reservation.setTotalFare(fare.getTotalFare());

        return reservationRepository.save(reservation);
    }

    /**
     * Cancels a reservation. Allowed only if the booking has not yet started.
     * Reason is optional but recommended.
     *
     * BUG FIX #20: Changed from !startDate.isAfter(now) to startDate.isBefore(now)
     * so that same-day cancellations are permitted. The old guard blocked any
     * reservation starting today, which is needlessly restrictive.
     */
    public Reservation cancelReservation(Long reservationId, String cancellationReason) {
        Reservation reservation = findById(reservationId);

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ValidationException("Reservation " + reservationId + " is already cancelled");
        }
        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new ValidationException("Cannot cancel a completed reservation");
        }
        // BUG FIX #20: only block cancellation if the rental has already started in the past
        if (reservation.getStartDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Cannot cancel a reservation that has already started");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservation.setCancellationReason(cancellationReason);
        return reservationRepository.save(reservation);
    }

    /**
     * Marks a reservation as completed (e.g. car has been returned).
     */
    public Reservation completeReservation(Long reservationId) {
        Reservation reservation = findById(reservationId);
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new ValidationException("Only ACTIVE reservations can be marked complete");
        }
        reservation.setStatus(ReservationStatus.COMPLETED);
        return reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public Reservation findById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found with id: " + reservationId));
    }

    // BUG FIX #4: Updated to use corrected repository method name (underscore notation)
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByCustomer(Long customerId) {
        return reservationRepository.findByCustomer_IdOrderByCreatedAtDesc(customerId);
    }

    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByCar(Long carId) {
        return reservationRepository.findByCar_IdOrderByCreatedAtDesc(carId);
    }

    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public double getTotalRevenue() {
        // BUG FIX #12: getTotalRevenue() now returns Double; null-safe unwrap with default 0.0
        Double revenue = reservationRepository.getTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("Start date and end date are required");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new ValidationException("Booking start date cannot be in the past");
        }
        if (!endDate.isAfter(startDate)) {
            throw new ValidationException("End date must be after start date");
        }
    }

    private void validateUsageInputs(double distanceKm, double durationHours) {
        if (distanceKm <= 0) {
            throw new ValidationException("Estimated distance must be greater than zero");
        }
        if (durationHours <= 0) {
            throw new ValidationException("Estimated duration must be greater than zero");
        }
    }
}