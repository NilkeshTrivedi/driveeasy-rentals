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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReservationService {

    private final ReservationDao reservationDao = new ReservationDaoImpl();
    private final CarDao carDao = new CarDaoImpl();

    public void bookCar(Reservation reservation) {

        if (reservation.getStartDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Booking cannot start in the past");
        }

        if (!reservation.getEndDate().isAfter(reservation.getStartDate())) {
            throw new ValidationException("End date must be after start date");
        }

        Car car = carDao.getCarById(reservation.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        if (car.isUnderMaintenance()) {
            throw new ValidationException("Car is under maintenance");
        }

        List<Reservation> conflicts =
                reservationDao.getActiveReservationsForCar(
                        reservation.getCarId(),
                        reservation.getStartDate(),
                        reservation.getEndDate()
                );

        if (!conflicts.isEmpty()) {
            throw new BookingConflictException("Car already booked for selected dates");
        }

        long days = ChronoUnit.DAYS.between(
                reservation.getStartDate(),
                reservation.getEndDate()
        );

        double totalPrice = days * car.getDailyRate();

        Reservation finalReservation = new Reservation(
                reservation.getId(),
                reservation.getCarId(),
                reservation.getCustomerId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                totalPrice,
                ReservationStatus.ACTIVE
        );

        reservationDao.addReservation(finalReservation);
    }

    public void cancelReservation(long reservationId) {

        Reservation reservation = reservationDao.getReservationById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (!reservation.getStartDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Cannot cancel after booking has started");
        }

        reservationDao.cancelReservation(reservationId);
    }
}
