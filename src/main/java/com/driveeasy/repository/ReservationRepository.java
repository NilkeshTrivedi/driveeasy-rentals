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

    List<Reservation> findByCustomer_IdOrderByCreatedAtDesc(Long customerId);

    List<Reservation> findByCar_IdOrderByCreatedAtDesc(Long carId);

    List<Reservation> findByStatus(ReservationStatus status);

    @Query("""
            SELECT r FROM Reservation r
            WHERE r.car.id = :carId
              AND r.status = com.driveeasy.model.enums.ReservationStatus.ACTIVE
              AND NOT (r.endDate <= :startDate OR r.startDate >= :endDate)
            """)
    List<Reservation> findConflictingReservations(@Param("carId") Long carId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT COALESCE(SUM(r.totalFare), 0.0) FROM Reservation r
            WHERE r.status IN (
                com.driveeasy.model.enums.ReservationStatus.ACTIVE,
                com.driveeasy.model.enums.ReservationStatus.COMPLETED
            )
            """)
    Double getTotalRevenue();

    @Query("""
        SELECT r FROM Reservation r
        JOIN FETCH r.car
        JOIN FETCH r.customer
        WHERE r.status = :status
        """)
    List<Reservation> findByStatusWithDetails(@Param("status") ReservationStatus status);

}