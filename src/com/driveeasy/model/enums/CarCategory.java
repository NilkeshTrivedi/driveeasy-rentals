package com.driveeasy.model.enums;

/**
 * Represents car categories along with a configurable fare multiplier.
 *
 * The multiplier is applied on top of the calculated base fare so that
 * premium segments (e.g. SUV, LUXURY) naturally cost more than ECONOMY.
 */
public enum CarCategory {

    ECONOMY(1.0),
    SEDAN(1.2),
    SUV(1.5),
    LUXURY(2.0);

    private final double fareMultiplier;

    CarCategory(double fareMultiplier) {
        this.fareMultiplier = fareMultiplier;
    }

    public double getFareMultiplier() {
        return fareMultiplier;
    }
}
