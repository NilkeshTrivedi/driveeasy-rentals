package com.driveeasy.ui;

import com.driveeasy.exception.*;
import com.driveeasy.model.Car;
import com.driveeasy.model.Customer;
import com.driveeasy.model.Reservation;
import com.driveeasy.model.enums.CarCategory;
import com.driveeasy.service.CarService;
import com.driveeasy.service.CustomerService;
import com.driveeasy.service.ReservationService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {

    private static final Scanner scanner = new Scanner(System.in);

    private static final CarService carService = new CarService();
    private static final CustomerService customerService = new CustomerService();
    private static final ReservationService reservationService = new ReservationService();

    public static void main(String[] args) {

        System.out.println("=== Welcome to DriveEasy Rentals ===");

        while (true) {
            try {
                System.out.println("\n1. Admin");
                System.out.println("2. Staff");
                System.out.println("0. Exit");
                System.out.print("Choose option: ");

                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> adminMenu();
                    case 2 -> staffMenu();
                    case 0 -> {
                        System.out.println("Thank you. Goodbye!");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // ================= ADMIN MENU =================

    private static void adminMenu() {
        System.out.println("\n--- Admin Menu ---");
        System.out.println("1. Add Car");
        System.out.println("2. Update Car Price");
        System.out.println("3. Mark Car Under Maintenance");
        System.out.println("4. Mark Car Available");
        System.out.print("Choice: ");

        int choice = Integer.parseInt(scanner.nextLine());

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

    private static void addCar() {
        System.out.print("Car ID: ");
        long id = Long.parseLong(scanner.nextLine());

        System.out.print("Model: ");
        String model = scanner.nextLine();

        System.out.print("Category (ECONOMY/SEDAN/SUV/LUXURY): ");
        CarCategory category = CarCategory.valueOf(scanner.nextLine().toUpperCase());

        System.out.print("Daily Rate: ");
        double rate = Double.parseDouble(scanner.nextLine());

        Car car = new Car(id, model, category, rate, false);
        carService.addCar(car);

        System.out.println("Car added successfully");
    }

    private static void updateCarPrice() {
        System.out.print("Car ID: ");
        long id = Long.parseLong(scanner.nextLine());

        System.out.print("New Price: ");
        double price = Double.parseDouble(scanner.nextLine());

        carService.updateCarPrice(id, price);
        System.out.println("Price updated");
    }

    private static void markMaintenance() {
        System.out.print("Car ID: ");
        long id = Long.parseLong(scanner.nextLine());

        carService.markUnderMaintenance(id);
        System.out.println("Car marked under maintenance");
    }

    private static void markAvailable() {
        System.out.print("Car ID: ");
        long id = Long.parseLong(scanner.nextLine());

        carService.markAvailable(id);
        System.out.println("Car marked as available");
    }


    // ================= STAFF MENU =================

    private static void staffMenu() {
        System.out.println("\n--- Staff Menu ---");
        System.out.println("1. Register Customer");
        System.out.println("2. Book Car");
        System.out.println("3. Cancel Reservation");
        System.out.println("4. Search Available Cars");
        System.out.print("Choice: ");

        int choice = Integer.parseInt(scanner.nextLine());

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

    private static void registerCustomer() {
        System.out.print("Customer ID: ");
        long id = Long.parseLong(scanner.nextLine());

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        Customer customer = new Customer(id, name, email, phone);
        customerService.registerCustomer(customer);

        System.out.println("Customer registered successfully");
    }

    private static void bookCar() {
        System.out.print("Reservation ID: ");
        long resId = Long.parseLong(scanner.nextLine());

        System.out.print("Car ID: ");
        long carId = Long.parseLong(scanner.nextLine());

        System.out.print("Customer ID: ");
        long custId = Long.parseLong(scanner.nextLine());

        System.out.print("Start Date (YYYY-MM-DD): ");
        LocalDate start = LocalDate.parse(scanner.nextLine());

        System.out.print("End Date (YYYY-MM-DD): ");
        LocalDate end = LocalDate.parse(scanner.nextLine());

        Reservation reservation = new Reservation(
                resId,
                carId,
                custId,
                start,
                end,
                0.0,
                null
        );

        reservationService.bookCar(reservation);
        System.out.println("Car booked successfully");
    }

    private static void cancelReservation() {
        System.out.print("Reservation ID: ");
        long id = Long.parseLong(scanner.nextLine());

        reservationService.cancelReservation(id);
        System.out.println("Reservation cancelled");
    }

    private static void searchAvailableCars() {
        System.out.print("Start Date (YYYY-MM-DD): ");
        LocalDate start = LocalDate.parse(scanner.nextLine());

        System.out.print("End Date (YYYY-MM-DD): ");
        LocalDate end = LocalDate.parse(scanner.nextLine());

        List<Car> cars = carService.searchAvailableCars(start, end);

        if (cars.isEmpty()) {
            System.out.println("No cars available");
        } else {
            cars.forEach(c ->
                    System.out.println(
                            c.getId() + " | " +
                                    c.getModel() + " | " +
                                    c.getCategory() + " | ₹" +
                                    c.getDailyRate()
                    )
            );
        }
    }
}

