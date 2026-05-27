package com.driveeasy.repository;

import com.driveeasy.model.Car;
import com.driveeasy.model.enums.CarCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByCategory(CarCategory category);

    List<Car> findByBaseFareBetween(double minBaseFare, double maxBaseFare);

    List<Car> findByUnderMaintenance(boolean underMaintenance);

    /**
     * Returns cars that are:
     *  - not under maintenance, AND
     *  - have no ACTIVE reservation that overlaps the requested date range
     *
     * Overlap condition: reservation overlaps if NOT (end <= start OR start >= end)
     */
    @Query("""
            SELECT c FROM Car c
            WHERE c.underMaintenance = false
              AND c.id NOT IN (
                  SELECT r.car.id FROM Reservation r
                  WHERE r.status = com.driveeasy.model.enums.ReservationStatus.ACTIVE
                    AND NOT (r.endDate <= :startDate OR r.startDate >= :endDate)
              )
            """)
    List<Car> findAvailableCars(@Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);
}
