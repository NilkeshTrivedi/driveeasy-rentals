package com.driveeasy.ui;

import com.driveeasy.model.Car;
import com.driveeasy.model.enums.CarCategory;
import com.driveeasy.service.CarService;
import com.driveeasy.service.CustomerService;
import com.driveeasy.service.ReservationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Optional console UI for quick manual testing.
 *
 * BUG FIX #5: The original ConsoleApp instantiated Spring-managed services with
 * "new CarService()" etc., which bypasses the Spring container entirely — repositories
 * are never injected so every call throws NullPointerException.
 * Fixed by making ConsoleApp a Spring @Component that implements CommandLineRunner.
 * Spring injects the fully-wired service beans through the constructor.
 *
 * BUG FIX #6/#7/#8/#9/#10: All service method calls have been updated to match the
 * Phase-2 API signatures:
 *   - carService.addCar(model, category, baseFare, perKmRate, perHourRate)
 *   - carService.findAvailableCars(start, end)   [was searchAvailableCars]
 *   - customerService.registerCustomer(name, email, phone, licenseNumber)
 *   - reservationService.bookCar(carId, customerId, start, end, distKm, hrs)
 *   - reservationService.cancelReservation(id, reason)  [two-arg overload]
 *
 * BUG FIX #1/#2/#3: Removed all Phase-1 constructor calls that accepted raw IDs.
 * Phase-2 JPA entities use auto-generated IDs and object associations.
 *
 * Activate only with the "console" Spring profile to keep it out of production:
 *   --spring.profiles.active=console
 */
@Component
@Profile("console")
public class ConsoleApp implements CommandLineRunner {

    private final CarService carService;
    private final CustomerService customerService;
    private final ReservationService reservationService;

    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(CarService carService,
                      CustomerService customerService,
                      ReservationService reservationService) {
        this.carService = carService;
        this.customerService = customerService;
        this.reservationService = reservationService;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Welcome to DriveEasy Rentals ===");

        while (true) {
            try {
                System.out.println("\n1. Admin");
                System.out.println("2. Staff");
                System.out.println("0. Exit");
                System.out.print("Choose option: ");

                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1 -> adminMenu();
                    case 2 -> staffMenu();
                    case 0 -> {
                        System.out.println("Thank you. Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // ── Admin ─────────────────────────────────────────────────────────────────

    private void adminMenu() {
        System.out.println("\n--- Admin Menu ---");
        System.out.println("1. Add Car");
        System.out.println("2. Update Car Price");
        System.out.println("3. Mark Car Under Maintenance");
        System.out.println("4. Mark Car Available");
        System.out.print("Choice: ");

        int choice = Integer.parseInt(scanner.nextLine().trim());
        try {
            switch (choice) {
                case 1 -> addCar();
                case 2 -> updateCarPrice();
                case 3 -> markMaintenance();
                case 4 -> markAvailable();
                default -> System.out.println("Invalid option");
            }
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void addCar() {
        System.out.print("Model: ");
        String model = scanner.nextLine();

        System.out.print("Category (ECONOMY/SEDAN/SUV/LUXURY): ");
        CarCategory category = CarCategory.valueOf(scanner.nextLine().toUpperCase());

        System.out.print("Base Fare: ");
        double baseFare = Double.parseDouble(scanner.nextLine());

        System.out.print("Per Km Rate: ");
        double perKmRate = Double.parseDouble(scanner.nextLine());

        System.out.print("Per Hour Rate: ");
        double perHourRate = Double.parseDouble(scanner.nextLine());

        // BUG FIX #1/#6: Phase-2 service auto-generates the ID; no id parameter needed.
        carService.addCar(model, category, baseFare, perKmRate, perHourRate);
        System.out.println("Car added successfully");
    }

    private void updateCarPrice() {
        System.out.print("Car ID: ");
        long id = Long.parseLong(scanner.nextLine().trim());

        System.out.print("New Base Fare: ");
        double baseFare = Double.parseDouble(scanner.nextLine());

        System.out.print("New Per Km Rate: ");
        double perKmRate = Double.parseDouble(scanner.nextLine());

        System.out.print("New Per Hour Rate: ");
        double perHourRate = Double.parseDouble(scanner.nextLine());

        carService.updateCarPricing(id, baseFare, perKmRate, perHourRate);
        System.out.println("Car pricing updated");
    }

    private void markMaintenance() {
        System.out.print("Car ID: ");
        long id = Long.parseLong(scanner.nextLine().trim());
        carService.markUnderMaintenance(id);
        System.out.println("Car marked under maintenance");
    }

    private void markAvailable() {
        System.out.print("Car ID: ");
        long id = Long.parseLong(scanner.nextLine().trim());
        carService.markAvailable(id);
        System.out.println("Car marked as available");
    }

    // ── Staff ─────────────────────────────────────────────────────────────────

    private void staffMenu() {
        System.out.println("\n--- Staff Menu ---");
        System.out.println("1. Register Customer");
        System.out.println("2. Book Car");
        System.out.println("3. Cancel Reservation");
        System.out.println("4. Search Available Cars");
        System.out.print("Choice: ");

        int choice = Integer.parseInt(scanner.nextLine().trim());
        try {
            switch (choice) {
                case 1 -> registerCustomer();
                case 2 -> bookCar();
                case 3 -> cancelReservation();
                case 4 -> searchAvailableCars();
                default -> System.out.println("Invalid option");
            }
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registerCustomer() {
        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Driving License Number (optional, press Enter to skip): ");
        String license = scanner.nextLine().trim();
        if (license.isEmpty()) license = null;

        // BUG FIX #2/#10: Phase-2 service accepts (name, email, phone, licenseNumber).
        // The Phase-1 approach of passing a pre-built Customer object with a manual ID
        // is incompatible with auto-generated JPA IDs.
        customerService.registerCustomer(name, email, phone, license);
        System.out.println("Customer registered successfully");
    }

    private void bookCar() {
        System.out.print("Car ID: ");
        long carId = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Customer ID: ");
        long custId = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Start Date (YYYY-MM-DD): ");
        LocalDate start = LocalDate.parse(scanner.nextLine().trim());

        System.out.print("End Date (YYYY-MM-DD): ");
        LocalDate end = LocalDate.parse(scanner.nextLine().trim());

        System.out.print("Estimated Distance (km): ");
        double distanceKm = Double.parseDouble(scanner.nextLine());

        System.out.print("Estimated Duration (hours): ");
        double durationHours = Double.parseDouble(scanner.nextLine());

        // BUG FIX #3/#8: Phase-2 bookCar takes (carId, customerId, start, end, km, hrs).
        // The fare is auto-calculated by FareCalculator — no manual fare input.
        var reservation = reservationService.bookCar(carId, custId, start, end, distanceKm, durationHours);
        System.out.printf("Car booked successfully. Reservation #%d — Total fare: ₹%.2f%n",
                reservation.getId(), reservation.getTotalFare());
    }

    private void cancelReservation() {
        System.out.print("Reservation ID: ");
        long id = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Cancellation reason (optional, press Enter to skip): ");
        String reason = scanner.nextLine().trim();
        if (reason.isEmpty()) reason = null;

        // BUG FIX #9: Phase-2 cancelReservation requires a reason parameter.
        reservationService.cancelReservation(id, reason);
        System.out.println("Reservation cancelled");
    }

    private void searchAvailableCars() {
        System.out.print("Start Date (YYYY-MM-DD): ");
        LocalDate start = LocalDate.parse(scanner.nextLine().trim());

        System.out.print("End Date (YYYY-MM-DD): ");
        LocalDate end = LocalDate.parse(scanner.nextLine().trim());

        // BUG FIX #7: method was renamed from searchAvailableCars to findAvailableCars
        List<Car> cars = carService.findAvailableCars(start, end);

        if (cars.isEmpty()) {
            System.out.println("No cars available");
        } else {
            cars.forEach(c -> System.out.printf(
                    "ID: %d | %s | %s | Base: ₹%.0f | Per Km: ₹%.0f | Per Hr: ₹%.0f%n",
                    c.getId(), c.getModel(), c.getCategory().getDisplayName(),
                    c.getBaseFare(), c.getPerKmRate(), c.getPerHourRate()));
        }
    }
}