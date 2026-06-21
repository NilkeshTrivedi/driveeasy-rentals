-- ─────────────────────────────────────────────────────────
-- Cars
-- ─────────────────────────────────────────────────────────
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

-- ─────────────────────────────────────────────────────────
-- Customers
-- ─────────────────────────────────────────────────────────
INSERT INTO customer (id, name, email, phone, driving_license_number)
SELECT 1, 'Rahul Sharma', 'rahul@example.com', '9876543210', 'DL-142011006789'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE id = 1);

INSERT INTO customer (id, name, email, phone, driving_license_number)
SELECT 2, 'Priya Patel', 'priya@example.com', '8765432109', 'DL-202012005432'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE id = 2);

-- ─────────────────────────────────────────────────────────
-- Users  (BCrypt strength 10)
-- admin  → admin123
-- staff  → staff123
-- ─────────────────────────────────────────────────────────
INSERT INTO users (username, password, role, full_name, enabled)
SELECT 'admin',
       '$2a$10$BLBQRJucik.1nXieHvmQb.G3QH.0Tnc7/UvWnpmpm6PZA4mAya9BS',
       'ADMIN', 'System Administrator', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO users (username, password, role, full_name, enabled)
SELECT 'staff',
       '$2a$10$CZ2A8qfv.WISgcNw13JckeHxw0H1bi6csNJLYs51dsPsZMI7Exnf2',
       'STAFF', 'Staff Member', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'staff');