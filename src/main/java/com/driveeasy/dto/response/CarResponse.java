package com.driveeasy.dto.response;

import com.driveeasy.model.Car;

public class CarResponse {

    private Long id;
    private String model;
    private String category;
    private double baseFare;
    private double perKmRate;
    private double perHourRate;
    private boolean underMaintenance;

    public static CarResponse from(Car car) {
        CarResponse r = new CarResponse();
        r.id = car.getId();
        r.model = car.getModel();
        r.category = car.getCategory().getDisplayName();
        r.baseFare = car.getBaseFare();
        r.perKmRate = car.getPerKmRate();
        r.perHourRate = car.getPerHourRate();
        r.underMaintenance = car.isUnderMaintenance();
        return r;
    }

    public Long getId() { return id; }
    public String getModel() { return model; }
    public String getCategory() { return category; }
    public double getBaseFare() { return baseFare; }
    public double getPerKmRate() { return perKmRate; }
    public double getPerHourRate() { return perHourRate; }
    public boolean isUnderMaintenance() { return underMaintenance; }
}