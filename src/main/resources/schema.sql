DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS car;
DROP TABLE IF EXISTS users;

CREATE TABLE car (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    model VARCHAR(100) NOT NULL,
    category VARCHAR(20) NOT NULL,
    base_fare DOUBLE NOT NULL,
    per_km_rate DOUBLE NOT NULL,
    per_hour_rate DOUBLE NOT NULL,
    under_maintenance BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(15) NOT NULL UNIQUE,
    driving_license_number VARCHAR(20)
);

CREATE TABLE reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    car_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    estimated_distance_km DOUBLE NOT NULL,
    estimated_duration_hours DOUBLE NOT NULL,
    base_fare_charged DOUBLE NOT NULL,
    distance_fare DOUBLE NOT NULL,
    duration_fare DOUBLE NOT NULL,
    category_surcharge DOUBLE NOT NULL,
    total_fare DOUBLE NOT NULL,
    status VARCHAR(15) NOT NULL,
    created_at DATETIME NOT NULL,
    cancelled_at DATETIME,
    cancellation_reason VARCHAR(255),
    FOREIGN KEY (car_id) REFERENCES car(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(10) NOT NULL,
    full_name VARCHAR(100),
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);