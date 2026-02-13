package com.driveeasy.repository;

import com.driveeasy.model.Car;
import com.driveeasy.model.enums.CarCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByCategory(CarCategory category);

    List<Car> findByBaseFareBetween(double minBaseFare, double maxBaseFare);
}

