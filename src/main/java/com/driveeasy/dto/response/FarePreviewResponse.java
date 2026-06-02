package com.driveeasy.dto.response;

import com.driveeasy.model.dto.FareBreakdown;

public class FarePreviewResponse {

    private double baseFare;
    private double distanceFare;
    private double durationFare;
    private double categorySurcharge;
    private double totalFare;

    public static FarePreviewResponse from(FareBreakdown fb) {
        FarePreviewResponse r = new FarePreviewResponse();
        r.baseFare = fb.getBaseFare();
        r.distanceFare = fb.getDistanceFare();
        r.durationFare = fb.getDurationFare();
        r.categorySurcharge = fb.getCategorySurcharge();
        r.totalFare = fb.getTotalFare();
        return r;
    }

    public double getBaseFare() { return baseFare; }
    public double getDistanceFare() { return distanceFare; }
    public double getDurationFare() { return durationFare; }
    public double getCategorySurcharge() { return categorySurcharge; }
    public double getTotalFare() { return totalFare; }
}