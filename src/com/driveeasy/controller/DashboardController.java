package com.driveeasy.controller;

import com.driveeasy.model.enums.ReservationStatus;
import com.driveeasy.repository.CarRepository;
import com.driveeasy.repository.CustomerRepository;
import com.driveeasy.repository.ReservationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;

    public DashboardController(CarRepository carRepository,
                               CustomerRepository customerRepository,
                               ReservationRepository reservationRepository) {
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        long totalCars      = carRepository.count();
        long availableCars  = carRepository.findByUnderMaintenance(false).size();
        long totalCustomers = customerRepository.count();
        long activeBookings = reservationRepository.findByStatus(ReservationStatus.ACTIVE).size();

        // BUG FIX #12: getTotalRevenue() returns Double (boxed); guard against null
        // before assigning to a primitive double to prevent NullPointerException.
        Double revenueResult = reservationRepository.getTotalRevenue();
        double totalRevenue  = revenueResult != null ? revenueResult : 0.0;

        model.addAttribute("totalCars", totalCars);
        model.addAttribute("availableCars", availableCars);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("activeBookings", activeBookings);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("recentReservations",
                reservationRepository.findByStatus(ReservationStatus.ACTIVE));

        return "dashboard";
    }
}