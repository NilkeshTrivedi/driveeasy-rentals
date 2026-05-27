package com.driveeasy.service;

import com.driveeasy.model.Car;
import com.driveeasy.model.dto.FareBreakdown;
import org.springframework.stereotype.Component;

/**
 * Pure domain service responsible for computing rental fare.
 *
 * Industry-standard formula used by major car rental companies (Hertz, Avis, Zoomcar):
 *
 *   variableFare   = (distanceKm × perKmRate) + (durationHours × perHourRate)
 *   categorySurcharge = variableFare × (categoryMultiplier - 1.0)
 *   totalFare      = baseFare + variableFare + categorySurcharge
 *
 * The base fare is a flat booking fee — it is NOT multiplied by the category.
 * The category surcharge applies only to the variable (usage-based) portion.
 *
 * This matches how real-world rental pricing works:
 *  - Economy: no surcharge on variable usage
 *  - Sedan:   +20% on variable usage
 *  - SUV:     +50% on variable usage
 *  - Luxury:  +100% on variable usage
 *
 * The breakdown is stored on the Reservation for full billing transparency.
 */
@Component
public class FareCalculator {

    /**
     * Calculates a fully itemised fare breakdown for a rental.
     *
     * @param car                 the car being rented
     * @param estimatedDistanceKm estimated distance in kilometres
     * @param estimatedHours      estimated rental duration in hours
     * @return an immutable {@link FareBreakdown} with all line items and the total
     */
    public FareBreakdown calculate(Car car, double estimatedDistanceKm, double estimatedHours) {
        if (car == null) {
            throw new IllegalArgumentException("Car must not be null");
        }
        if (estimatedDistanceKm < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        if (estimatedHours < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }

        double baseFare       = car.getBaseFare();
        double distanceFare   = estimatedDistanceKm * car.getPerKmRate();
        double durationFare   = estimatedHours * car.getPerHourRate();
        double variableFare   = distanceFare + durationFare;

        // Category surcharge applies ONLY to variable usage, not to the base fee
        double multiplier         = car.getCategory().getFareMultiplier();
        double categorySurcharge  = variableFare * (multiplier - 1.0);

        double totalFare = baseFare + variableFare + categorySurcharge;

        return new FareBreakdown(
                baseFare,
                distanceFare,
                durationFare,
                categorySurcharge,
                Math.max(0.0, totalFare)   // guard against floating-point artefacts
        );
    }
}
