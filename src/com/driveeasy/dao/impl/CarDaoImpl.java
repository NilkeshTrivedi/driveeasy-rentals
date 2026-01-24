package com.driveeasy.dao.impl;

import com.driveeasy.dao.CarDao;
import com.driveeasy.model.Car;
import com.driveeasy.model.enums.CarCategory;
import com.driveeasy.util.DbConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarDaoImpl implements CarDao {

    @Override
    public void addCar(Car car) {
        String sql = "INSERT INTO car (id, model, category, daily_rate, under_maintenance) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, car.getId());
            ps.setString(2, car.getModel());
            ps.setString(3, car.getCategory().name());
            ps.setDouble(4, car.getDailyRate());
            ps.setBoolean(5, car.isUnderMaintenance());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCar(Car car) {
        String sql = "UPDATE car SET model=?, category=?, daily_rate=?, under_maintenance=? WHERE id=?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, car.getModel());
            ps.setString(2, car.getCategory().name());
            ps.setDouble(3, car.getDailyRate());
            ps.setBoolean(4, car.isUnderMaintenance());
            ps.setLong(5, car.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCarMaintenance(long carId, boolean underMaintenance) {
        String sql = "UPDATE car SET under_maintenance=? WHERE id=?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, underMaintenance);
            ps.setLong(2, carId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Car> getCarById(long id) {
        String sql = "SELECT * FROM car WHERE id=?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Car car = mapResultSetToCar(rs);
                return Optional.of(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM car";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

    @Override
    public List<Car> getCarsByCategory(CarCategory category) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM car WHERE category=?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

    @Override
    public List<Car> getCarsByPriceRange(double minPrice, double maxPrice) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM car WHERE daily_rate BETWEEN ? AND ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

    @Override
    public List<Car> getAvailableCars(LocalDate startDate, LocalDate endDate) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM car c WHERE c.under_maintenance = FALSE AND c.id NOT IN (" +
                "SELECT r.car_id FROM reservation r WHERE r.status='ACTIVE' AND NOT (r.end_date <= ? OR r.start_date >= ?))";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

    private Car mapResultSetToCar(ResultSet rs) throws SQLException {
        return new Car(
                rs.getLong("id"),
                rs.getString("model"),
                CarCategory.valueOf(rs.getString("category")),
                rs.getDouble("daily_rate"),
                rs.getBoolean("under_maintenance")
        );
    }
}
