package com.driveeasy.dao.impl;

import com.driveeasy.dao.ReservationDao;
import com.driveeasy.model.Reservation;
import com.driveeasy.model.enums.ReservationStatus;
import com.driveeasy.util.DbConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationDaoImpl implements ReservationDao {

    @Override
    public void addReservation(Reservation reservation) {
        String sql = "INSERT INTO reservation (id, car_id, customer_id, start_date, end_date, total_distance_km, duration_hours, calculated_fare, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, reservation.getId());
            ps.setLong(2, reservation.getCarId());
            ps.setLong(3, reservation.getCustomerId());
            ps.setDate(4, Date.valueOf(reservation.getStartDate()));
            ps.setDate(5, Date.valueOf(reservation.getEndDate()));
            ps.setDouble(6, reservation.getTotalDistanceKm());
            ps.setDouble(7, reservation.getDurationHours());
            ps.setDouble(8, reservation.getCalculatedFare());
            ps.setString(9, reservation.getStatus().name());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateReservation(Reservation reservation) {
        String sql = "UPDATE reservation SET car_id=?, customer_id=?, start_date=?, end_date=?, total_distance_km=?, duration_hours=?, calculated_fare=?, status=? WHERE id=?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, reservation.getCarId());
            ps.setLong(2, reservation.getCustomerId());
            ps.setDate(3, Date.valueOf(reservation.getStartDate()));
            ps.setDate(4, Date.valueOf(reservation.getEndDate()));
            ps.setDouble(5, reservation.getTotalDistanceKm());
            ps.setDouble(6, reservation.getDurationHours());
            ps.setDouble(7, reservation.getCalculatedFare());
            ps.setString(8, reservation.getStatus().name());
            ps.setLong(9, reservation.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelReservation(long reservationId) {
        String sql = "UPDATE reservation SET status='CANCELLED' WHERE id=?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, reservationId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Reservation> getReservationById(long id) {
        String sql = "SELECT * FROM reservation WHERE id=?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapResultSetToReservation(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    @Override
    public List<Reservation> getReservationsByCustomer(long customerId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE customer_id=?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    @Override
    public List<Reservation> getReservationsByCar(long carId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE car_id=?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, carId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    @Override
    public List<Reservation> getActiveReservationsForCar(long carId, LocalDate startDate, LocalDate endDate) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE car_id=? AND status='ACTIVE' AND NOT (end_date <= ? OR start_date >= ?)";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, carId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    @Override
    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE status=?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        return new Reservation(
                rs.getLong("id"),
                rs.getLong("car_id"),
                rs.getLong("customer_id"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getDouble("total_distance_km"),
                rs.getDouble("duration_hours"),
                rs.getDouble("calculated_fare"),
                ReservationStatus.valueOf(rs.getString("status"))
        );
    }
}
