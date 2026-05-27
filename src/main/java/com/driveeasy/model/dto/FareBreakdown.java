package com.driveeasy.model.dto;

/**
 * Immutable value object holding a fully itemised fare breakdown.
 *
 * Every field is a concrete rupee amount, not a rate or multiplier.
 * This is returned by FareCalculator and then:
 *  - Shown to staff as a preview before confirming a booking
 *  - Stored field-by-field on the Reservation for billing transparency
 *  - Used to generate the invoice
 */
public final class FareBreakdown {

    private final double baseFare;
    private final double distanceFare;
    private final double durationFare;
    private final double categorySurcharge;
    private final double totalFare;

    public FareBreakdown(double baseFare,
                         double distanceFare,
                         double durationFare,
                         double categorySurcharge,
                         double totalFare) {
        this.baseFare          = baseFare;
        this.distanceFare      = distanceFare;
        this.durationFare      = durationFare;
        this.categorySurcharge = categorySurcharge;
        this.totalFare         = totalFare;
    }

    public double getBaseFare()          { return baseFare; }
    public double getDistanceFare()      { return distanceFare; }
    public double getDurationFare()      { return durationFare; }
    public double getCategorySurcharge() { return categorySurcharge; }
    public double getTotalFare()         { return totalFare; }

    @Override
    public String toString() {
        return String.format(
                "Base fare:          ₹%.2f%n" +
                        "Distance fare:      ₹%.2f%n" +
                        "Duration fare:      ₹%.2f%n" +
                        "Category surcharge: ₹%.2f%n" +
                        "─────────────────────────%n" +
                        "Total:              ₹%.2f",
                baseFare, distanceFare, durationFare, categorySurcharge, totalFare
        );
    }
}
