package com.driveeasy.dao;

import com.driveeasy.model.Car;
import com.driveeasy.model.enums.CarCategory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CarDao {

    // Add a new car
    void addCar(Car car);

    // Update existing car pricing or details
    void updateCar(Car car);

    // Mark a car under maintenance
    void setCarMaintenance(long carId, boolean underMaintenance);

    // Get car by ID
    Optional<Car> getCarById(long id);

    // Get all cars
    List<Car> getAllCars();

    // Search cars by category
    List<Car> getCarsByCategory(CarCategory category);

    // Search cars by price range
    List<Car> getCarsByPriceRange(double minPrice, double maxPrice);

    // Search cars available for a date range (to be implemented in DAO Impl)
    List<Car> getAvailableCars(LocalDate startDate, LocalDate endDate);
}
