package com.driveeasy.service;

import com.driveeasy.model.Car;

/**
 * Pure domain service responsible for computing rental fare.
 *
 * This class is intentionally free of any persistence or UI concerns so that
 * it can be reused unchanged when the project moves to Spring Boot / REST / JPA.
 */
public class FareCalculator {

    /**
     * Calculate the total fare for a rental.
     *
     * Formula:
     *   totalFare =
     *       (distanceKm × perKmRate)
     *     + (durationHours × perHourRate)
     *     + baseFare
     *     × categoryMultiplier
     *
     * where categoryMultiplier comes from {@link com.driveeasy.model.enums.CarCategory}.
     *
     * @param car           the rented car
     * @param distanceKm    total distance in kilometres
     * @param durationHours total duration in hours
     * @return calculated total fare (never negative)
     */
    public double calculateFare(Car car, double distanceKm, double durationHours) {

        if (car == null) {
            throw new IllegalArgumentException("Car must not be null");
        }
        if (distanceKm < 0) {
            throw new IllegalArgumentException("Distance (km) cannot be negative");
        }
        if (durationHours < 0) {
            throw new IllegalArgumentException("Duration (hours) cannot be negative");
        }

        double baseFare = car.getBaseFare();
        double perKmRate = car.getPerKmRate();
        double perHourRate = car.getPerHourRate();
        double categoryMultiplier = car.getCategory().getFareMultiplier();

        double rawFare =
                (distanceKm * perKmRate)
                        + (durationHours * perHourRate)
                        + baseFare;

        double totalFare = rawFare * categoryMultiplier;

        // Guard against rounding artefacts producing tiny negative numbers.
        return Math.max(0.0, totalFare);
    }
}

