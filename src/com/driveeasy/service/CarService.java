package com.driveeasy.service;

import com.driveeasy.dao.CarDao;
import com.driveeasy.dao.impl.CarDaoImpl;
import com.driveeasy.exception.ResourceNotFoundException;
import com.driveeasy.exception.ValidationException;
import com.driveeasy.model.Car;
import com.driveeasy.model.enums.CarCategory;
import com.driveeasy.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CarService {

    private final CarDao carDao;
    private final CarRepository carRepository;

    /**
     * Default constructor used by the legacy console UI (DAO-based).
     */
    public CarService() {
        this.carDao = new CarDaoImpl();
        this.carRepository = null;
    }

    /**
     * Spring-managed constructor using JPA repositories.
     */
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
        this.carDao = null;
    }

    public void addCar(Car car) {
        if (carRepository != null) {
            carRepository.save(car);
        } else {
            carDao.addCar(car);
        }
    }

    /**
     * Update all pricing attributes of a car in a single, consistent operation.
     */
    public void updateCarPricing(long carId,
                                 double newBaseFare,
                                 double newPerKmRate,
                                 double newPerHourRate) {

        if (newBaseFare < 0 || newPerKmRate < 0 || newPerHourRate < 0) {
            throw new ValidationException("Pricing values must not be negative");
        }

        Car car = getCarById(carId);

        Car updatedCar = new Car(
                car.getId(),
                car.getModel(),
                car.getCategory(),
                newBaseFare,
                newPerKmRate,
                newPerHourRate,
                car.isUnderMaintenance());

        if (carRepository != null) {
            carRepository.save(updatedCar);
        } else {
            carDao.updateCar(updatedCar);
        }
    }

    public void markUnderMaintenance(long carId) {
        if (carRepository != null) {
            Car car = getCarById(carId);
            car.setUnderMaintenance(true);
            carRepository.save(car);
        } else {
            carDao.setCarMaintenance(carId, true);
        }
    }

    public void markAvailable(long carId){
        Car car = getCarById(carId);

        if (!car.isUnderMaintenance()) {
            throw new ValidationException("Car is already available");
        }

        car.setUnderMaintenance(false);

        if (carRepository != null) {
            carRepository.save(car);
        } else {
            carDao.setCarMaintenance(carId, false);
        }
    }

    public List<Car> searchByCategory(CarCategory category) {
        if (carRepository != null) {
            return carRepository.findByCategory(category);
        }
        return carDao.getCarsByCategory(category);
    }

    public List<Car> searchByPriceRange(double min, double max) {
        if (carRepository != null) {
            return carRepository.findByBaseFareBetween(min, max);
        }
        return carDao.getCarsByPriceRange(min, max);
    }

    public List<Car> searchAvailableCars(LocalDate start, LocalDate end) {
        // For now, reuse DAO-based availability query even in Spring context.
        if (carDao != null) {
            return carDao.getAvailableCars(start, end);
        }
        // Fallback: if no DAO, return all cars (UI can still filter by dates later if needed).
        return carRepository.findAll();
    }

    private Car getCarById(long carId) {
        if (carRepository != null) {
            return carRepository.findById(carId)
                    .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        }
        return carDao.getCarById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
    }
}
