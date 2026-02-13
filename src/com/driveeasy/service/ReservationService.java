package com.driveeasy.service;

import com.driveeasy.dao.CarDao;
import com.driveeasy.dao.ReservationDao;
import com.driveeasy.dao.impl.CarDaoImpl;
import com.driveeasy.dao.impl.ReservationDaoImpl;
import com.driveeasy.exception.BookingConflictException;
import com.driveeasy.exception.ResourceNotFoundException;
import com.driveeasy.exception.ValidationException;
import com.driveeasy.model.Car;
import com.driveeasy.model.Reservation;
import com.driveeasy.model.enums.ReservationStatus;
import com.driveeasy.repository.CarRepository;
import com.driveeasy.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationDao reservationDao;
    private final CarDao carDao;
    private final FareCalculator fareCalculator;

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;

    /**
     * Default constructor for console-based Phase 1 usage.
     * Uses concrete DAO implementations and a default FareCalculator.
     */
    public ReservationService() {
        this.reservationDao = new ReservationDaoImpl();
        this.carDao = new CarDaoImpl();
        this.fareCalculator = new FareCalculator();
        this.reservationRepository = null;
        this.carRepository = null;
    }

    /**
     * Spring-managed constructor using JPA repositories.
     */
    public ReservationService(ReservationRepository reservationRepository,
                              CarRepository carRepository,
                              FareCalculator fareCalculator) {
        this.reservationRepository = reservationRepository;
        this.carRepository = carRepository;
        this.fareCalculator = fareCalculator;
        this.reservationDao = null;
        this.carDao = null;
    }

    public void bookCar(Reservation reservation) {

        validateReservationRequest(reservation);

        Car car = getCarById(reservation.getCarId());

        if (car.isUnderMaintenance()) {
            throw new ValidationException("Car is under maintenance");
        }

        List<Reservation> conflicts = getActiveReservationsForCar(
                reservation.getCarId(),
                reservation.getStartDate(),
                reservation.getEndDate()
        );

        if (!conflicts.isEmpty()) {
            throw new BookingConflictException("Car already booked for selected dates");
        }

        double fare = fareCalculator.calculateFare(
                car,
                reservation.getTotalDistanceKm(),
                reservation.getDurationHours()
        );

        reservation.setCalculatedFare(fare);
        reservation.setStatus(ReservationStatus.ACTIVE);

        saveReservation(reservation);
    }

    private void validateReservationRequest(Reservation reservation) {
        if (reservation == null) {
            throw new ValidationException("Reservation must not be null");
        }

        if (reservation.getCarId() <= 0) {
            throw new ValidationException("Car ID must be positive");
        }

        if (reservation.getCustomerId() <= 0) {
            throw new ValidationException("Customer ID must be positive");
        }

        if (reservation.getStartDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Booking cannot start in the past");
        }

        if (!reservation.getEndDate().isAfter(reservation.getStartDate())) {
            throw new ValidationException("End date must be after start date");
        }

        if (reservation.getTotalDistanceKm() <= 0) {
            throw new ValidationException("Total distance (km) must be greater than zero");
        }

        if (reservation.getDurationHours() <= 0) {
            throw new ValidationException("Duration (hours) must be greater than zero");
        }
    }

    public void cancelReservation(long reservationId) {

        Reservation reservation = getReservationById(reservationId);

        if (!reservation.getStartDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Cannot cancel after booking has started");
        }

        if (reservationRepository != null) {
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
        } else {
            reservationDao.cancelReservation(reservationId);
        }
    }

    private Car getCarById(long carId) {
        if (carRepository != null) {
            return carRepository.findById(carId)
                    .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        }
        return carDao.getCarById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
    }

    private List<Reservation> getActiveReservationsForCar(long carId,
                                                          LocalDate startDate,
                                                          LocalDate endDate) {
        if (reservationRepository != null) {
            return reservationRepository.findActiveReservationsForCarInRange(carId, startDate, endDate);
        }
        return reservationDao.getActiveReservationsForCar(carId, startDate, endDate);
    }

    private void saveReservation(Reservation reservation) {
        if (reservationRepository != null) {
            reservationRepository.save(reservation);
        } else {
            reservationDao.addReservation(reservation);
        }
    }

    private Reservation getReservationById(long reservationId) {
        if (reservationRepository != null) {
            return reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        }
        return reservationDao.getReservationById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
    }
}
