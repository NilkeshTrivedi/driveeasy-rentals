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

    /**
     * FIX: Spring Data derived queries on @ManyToOne associations require
     * underscore notation to traverse the join.  The original names
     * findByCustomerIdOrderByCreatedAtDesc / findByCarIdOrderByCreatedAtDesc
     * looked for fields literally named "customerId" / "carId" on Reservation,
     * which don't exist (the fields are "customer" and "car" objects).
     * Spring Data threw PropertyReferenceException at application startup.
     */
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

    /**
     * FIX: Return type changed from double (primitive) to Double (boxed).
     * COALESCE guarantees a non-null SQL result, but on some Hibernate/MySQL
     * dialect combinations the query can still return null before COALESCE
     * is applied, causing a NullPointerException during auto-unboxing.
     * Using Double lets callers null-check safely.
     */
    @Query("""
            SELECT COALESCE(SUM(r.totalFare), 0.0) FROM Reservation r
            WHERE r.status IN (
                com.driveeasy.model.enums.ReservationStatus.ACTIVE,
                com.driveeasy.model.enums.ReservationStatus.COMPLETED
            )
            """)
    Double getTotalRevenue();
}