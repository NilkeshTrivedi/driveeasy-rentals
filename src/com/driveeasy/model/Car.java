package com.driveeasy.model;

import com.driveeasy.model.enums.CarCategory;

public class Car {

    private final long id;
    private String model;
    private CarCategory category;
    private double dailyRate;
    private boolean underMaintenance;

    public Car(long id, String model, CarCategory category,
               double dailyRate, boolean underMaintenance) {
        this.id = id;
        this.model = model;
        this.category = category;
        this.dailyRate = dailyRate;
        this.underMaintenance = underMaintenance;
    }

    public long getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public CarCategory getCategory() {
        return category;
    }

    public void setCategory(CarCategory category) {
        this.category = category;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public boolean isUnderMaintenance() {
        return underMaintenance;
    }

    public void setUnderMaintenance(boolean underMaintenance) {
        this.underMaintenance = underMaintenance;
    }
}
