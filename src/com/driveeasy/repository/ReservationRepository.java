package com.driveeasy.repository;

import com.driveeasy.model.Reservation;
import com.driveeasy.model.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByCustomerId(long customerId);

    List<Reservation> findByCarId(long carId);

    List<Reservation> findByStatus(ReservationStatus status);

    @Query("""
            SELECT r FROM Reservation r
            WHERE r.carId = :carId
              AND r.status = com.driveeasy.model.enums.ReservationStatus.ACTIVE
              AND NOT (r.endDate <= :startDate OR r.startDate >= :endDate)
            """)
    List<Reservation> findActiveReservationsForCarInRange(@Param("carId") long carId,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);
}

