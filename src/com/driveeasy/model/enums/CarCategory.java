package com.driveeasy.model.enums;

/**
 * Car categories with a category-based surcharge percentage.
 *
 * In the fare formula, the surcharge applies only to the VARIABLE portion
 * (distance + duration fare), not to the flat base fare.
 * This is the industry-standard approach used by major rental companies.
 *
 *   totalFare = baseFare + (variableFare × categoryMultiplier)
 *
 * where variableFare = (distanceKm × perKmRate) + (durationHours × perHourRate)
 */
public enum CarCategory {

    ECONOMY(1.0, "Economy"),
    SEDAN(1.2, "Sedan"),
    SUV(1.5, "SUV"),
    LUXURY(2.0, "Luxury");

    private final double fareMultiplier;
    private final String displayName;

    CarCategory(double fareMultiplier, String displayName) {
        this.fareMultiplier = fareMultiplier;
        this.displayName = displayName;
    }

    public double getFareMultiplier() {
        return fareMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }
}
