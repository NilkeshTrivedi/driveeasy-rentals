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

    List<Reservation> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Reservation> findByCarIdOrderByCreatedAtDesc(Long carId);

    List<Reservation> findByStatus(ReservationStatus status);

    /**
     * Checks for ACTIVE reservations on a specific car that overlap
     * the requested [startDate, endDate) window.
     *
     * Used to enforce booking conflict prevention before confirming a reservation.
     */
    @Query("""
            SELECT r FROM Reservation r
            WHERE r.car.id = :carId
              AND r.status = com.driveeasy.model.enums.ReservationStatus.ACTIVE
              AND NOT (r.endDate <= :startDate OR r.startDate >= :endDate)
            """)
    List<Reservation> findConflictingReservations(@Param("carId") Long carId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    /**
     * Revenue report: sum of total fares for ACTIVE and COMPLETED reservations.
     */
    @Query("""
            SELECT COALESCE(SUM(r.totalFare), 0.0) FROM Reservation r
            WHERE r.status IN (
                com.driveeasy.model.enums.ReservationStatus.ACTIVE,
                com.driveeasy.model.enums.ReservationStatus.COMPLETED
            )
            """)
    double getTotalRevenue();
}
