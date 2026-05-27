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

    @Transactional(readOnly = true)
    public FareBreakdown previewFare(Long carId, double estimatedDistanceKm,
                                     double estimatedDurationHours) {
        Car car = carService.findById(carId);
        validateUsageInputs(estimatedDistanceKm, estimatedDurationHours);
        return fareCalculator.calculate(car, estimatedDistanceKm, estimatedDurationHours);
    }

    public Reservation bookCar(Long carId, Long customerId,
                               LocalDate startDate, LocalDate endDate,
                               double estimatedDistanceKm, double estimatedDurationHours) {

        validateDates(startDate, endDate);
        validateUsageInputs(estimatedDistanceKm, estimatedDurationHours);

        Car car         = carService.findById(carId);
        Customer customer = customerService.findById(customerId);

        if (car.isUnderMaintenance())
            throw new ValidationException("Car " + carId + " is currently under maintenance");

        if (!reservationRepository.findConflictingReservations(carId, startDate, endDate).isEmpty())
            throw new BookingConflictException("Car " + carId + " is already booked for the selected dates");

        FareBreakdown fare = fareCalculator.calculate(car, estimatedDistanceKm, estimatedDurationHours);

        Reservation reservation = new Reservation(car, customer, startDate, endDate,
                estimatedDistanceKm, estimatedDurationHours);
        reservation.setBaseFareCharged(fare.getBaseFare());
        reservation.setDistanceFare(fare.getDistanceFare());
        reservation.setDurationFare(fare.getDurationFare());
        reservation.setCategorySurcharge(fare.getCategorySurcharge());
        reservation.setTotalFare(fare.getTotalFare());

        return reservationRepository.save(reservation);
    }

    /**
     * FIX: Changed cancellation guard from !startDate.isAfter(now) to
     * startDate.isBefore(now).  The original blocked same-day cancellations
     * (where startDate == today).  The corrected guard only blocks cancellations
     * after the rental has already begun.
     */
    public Reservation cancelReservation(Long reservationId, String cancellationReason) {
        Reservation reservation = findById(reservationId);

        if (reservation.getStatus() == ReservationStatus.CANCELLED)
            throw new ValidationException("Reservation " + reservationId + " is already cancelled");
        if (reservation.getStatus() == ReservationStatus.COMPLETED)
            throw new ValidationException("Cannot cancel a completed reservation");
        if (reservation.getStartDate().isBefore(LocalDate.now()))
            throw new ValidationException("Cannot cancel a reservation that has already started");

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservation.setCancellationReason(cancellationReason);
        return reservationRepository.save(reservation);
    }

    public Reservation completeReservation(Long reservationId) {
        Reservation reservation = findById(reservationId);
        if (reservation.getStatus() != ReservationStatus.ACTIVE)
            throw new ValidationException("Only ACTIVE reservations can be marked complete");
        reservation.setStatus(ReservationStatus.COMPLETED);
        return reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public Reservation findById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found with id: " + reservationId));
    }

    // FIX: uses corrected underscore-notation repository method names
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
        // FIX: getTotalRevenue() returns Double (boxed); null-safe unwrap
        Double revenue = reservationRepository.getTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null)
            throw new ValidationException("Start date and end date are required");
        if (startDate.isBefore(LocalDate.now()))
            throw new ValidationException("Booking start date cannot be in the past");
        if (!endDate.isAfter(startDate))
            throw new ValidationException("End date must be after start date");
    }

    private void validateUsageInputs(double distanceKm, double durationHours) {
        if (distanceKm <= 0)
            throw new ValidationException("Estimated distance must be greater than zero");
        if (durationHours <= 0)
            throw new ValidationException("Estimated duration must be greater than zero");
    }
}