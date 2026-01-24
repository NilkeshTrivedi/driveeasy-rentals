package com.driveeasy.service;

import com.driveeasy.dao.CarDao;
import com.driveeasy.dao.impl.CarDaoImpl;
import com.driveeasy.exception.ResourceNotFoundException;
import com.driveeasy.model.Car;
import com.driveeasy.model.enums.CarCategory;

import java.time.LocalDate;
import java.util.List;

public class CarService {

    private final CarDao carDao = new CarDaoImpl();

    public void addCar(Car car) {
        carDao.addCar(car);
    }

    public void updateCarPrice(long carId, double newPrice) {
        Car car = carDao.getCarById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        Car updatedCar = new Car(
                car.getId(),
                car.getModel(),
                car.getCategory(),
                newPrice,
                car.isUnderMaintenance()
        );

        carDao.updateCar(updatedCar);
    }

    public void markUnderMaintenance(long carId) {
        carDao.setCarMaintenance(carId, true);
    }

    public List<Car> searchByCategory(CarCategory category) {
        return carDao.getCarsByCategory(category);
    }

    public List<Car> searchByPriceRange(double min, double max) {
        return carDao.getCarsByPriceRange(min, max);
    }

    public List<Car> searchAvailableCars(LocalDate start, LocalDate end) {
        return carDao.getAvailableCars(start, end);
    }
}
