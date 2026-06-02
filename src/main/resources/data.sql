-- Seed Cars if table is empty
INSERT INTO car (id, model, category, base_fare, per_km_rate, per_hour_rate, under_maintenance)
SELECT 1, 'Hyundai i10', 'ECONOMY', 500.0, 10.0, 50.0, 0
WHERE NOT EXISTS (SELECT 1 FROM car WHERE id = 1);

INSERT INTO car (id, model, category, base_fare, per_km_rate, per_hour_rate, under_maintenance)
SELECT 2, 'Honda City', 'SEDAN', 800.0, 12.0, 70.0, 0
WHERE NOT EXISTS (SELECT 1 FROM car WHERE id = 2);

INSERT INTO car (id, model, category, base_fare, per_km_rate, per_hour_rate, under_maintenance)
SELECT 3, 'Mahindra XUV700', 'SUV', 1200.0, 15.0, 100.0, 0
WHERE NOT EXISTS (SELECT 1 FROM car WHERE id = 3);

INSERT INTO car (id, model, category, base_fare, per_km_rate, per_hour_rate, under_maintenance)
SELECT 4, 'BMW 5 Series', 'LUXURY', 3000.0, 25.0, 250.0, 0
WHERE NOT EXISTS (SELECT 1 FROM car WHERE id = 4);

INSERT INTO car (id, model, category, base_fare, per_km_rate, per_hour_rate, under_maintenance)
SELECT 5, 'Suzuki Swift', 'ECONOMY', 450.0, 9.0, 45.0, 1
WHERE NOT EXISTS (SELECT 1 FROM car WHERE id = 5);

-- Seed Customers if table is empty
INSERT INTO customer (id, name, email, phone, driving_license_number)
SELECT 1, 'Rahul Sharma', 'rahul@example.com', '9876543210', 'DL-142011006789'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE id = 1);

INSERT INTO customer (id, name, email, phone, driving_license_number)
SELECT 2, 'Priya Patel', 'priya@example.com', '8765432109', 'DL-202012005432'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE id = 2);

-- Seed an active reservation if table is empty
INSERT INTO reservation (id, car_id, customer_id, start_date, end_date, estimated_distance_km, estimated_duration_hours, base_fare_charged, distance_fare, duration_fare, category_surcharge, total_fare, status, created_at)
SELECT 1, 2, 1, '2026-05-26', '2026-05-28', 150.0, 48.0, 800.0, 1800.0, 3360.0, 1032.0, 6992.0, 'ACTIVE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM reservation WHERE id = 1);

-- Seed Users
-- admin123 and staff123 hashed with BCrypt strength 12
INSERT INTO users (username, password, role, full_name, enabled)
SELECT 'admin',
       '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
       'ADMIN', 'System Administrator', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO users (username, password, role, full_name, enabled)
SELECT 'staff',
       '$2a$12$tT3HAHb9JhfZ5JkbzFGqNeIp0wTWTnYpPBnSSSPKFCFbk/0ZLG.fS',
       'STAFF', 'Staff Member', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'staff');