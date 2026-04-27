package com.driveeasy.controller;

import com.driveeasy.model.dto.FareBreakdown;
import com.driveeasy.model.enums.CarCategory;
import com.driveeasy.service.CarService;
import com.driveeasy.service.CustomerService;
import com.driveeasy.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/staff/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final CarService carService;
    private final CustomerService customerService;

    public ReservationController(ReservationService reservationService,
                                 CarService carService,
                                 CustomerService customerService) {
        this.reservationService = reservationService;
        this.carService = carService;
        this.customerService = customerService;
    }

    @GetMapping
    public String listReservations(Model model) {
        model.addAttribute("reservations", reservationService.getAllReservations());
        return "staff/reservations/list";
    }

    /**
     * Step 1: pick dates and see available cars.
     */
    @GetMapping("/book")
    public String showBookingStep1(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        model.addAttribute("today", LocalDate.now());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        if (startDate != null && endDate != null) {
            model.addAttribute("availableCars", carService.findAvailableCars(startDate, endDate));
            model.addAttribute("categories", CarCategory.values());
        }
        return "staff/reservations/book-step1";
    }

    /**
     * Step 2: enter customer + usage estimate → see full fare breakdown before confirming.
     */
    @GetMapping("/book/fare-preview")
    public String showFarePreview(
            @RequestParam Long carId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam double estimatedDistanceKm,
            @RequestParam double estimatedDurationHours,
            Model model) {

        FareBreakdown fareBreakdown = reservationService.previewFare(
                carId, estimatedDistanceKm, estimatedDurationHours);

        model.addAttribute("car", carService.findById(carId));
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("estimatedDistanceKm", estimatedDistanceKm);
        model.addAttribute("estimatedDurationHours", estimatedDurationHours);
        model.addAttribute("fareBreakdown", fareBreakdown);
        model.addAttribute("customers", customerService.getAllCustomers());
        return "staff/reservations/book-step2-preview";
    }

    /**
     * Final confirmation — persists the reservation with auto-calculated fare.
     */
    @PostMapping("/book/confirm")
    public String confirmBooking(
            @RequestParam Long carId,
            @RequestParam Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam double estimatedDistanceKm,
            @RequestParam double estimatedDurationHours,
            RedirectAttributes redirectAttributes) {

        var reservation = reservationService.bookCar(
                carId, customerId, startDate, endDate,
                estimatedDistanceKm, estimatedDurationHours);

        redirectAttributes.addFlashAttribute("successMessage",
                "Booking confirmed! Reservation #" + reservation.getId() +
                        " — Total fare: ₹" + String.format("%.2f", reservation.getTotalFare()));
        return "redirect:/staff/reservations";
    }

    @GetMapping("/{id}")
    public String viewReservation(@PathVariable Long id, Model model) {
        model.addAttribute("reservation", reservationService.findById(id));
        return "staff/reservations/view";
    }

    @PostMapping("/{id}/cancel")
    public String cancelReservation(@PathVariable Long id,
                                    @RequestParam(required = false) String reason,
                                    RedirectAttributes redirectAttributes) {
        reservationService.cancelReservation(id, reason);
        redirectAttributes.addFlashAttribute("successMessage", "Reservation #" + id + " cancelled");
        return "redirect:/staff/reservations";
    }

    @PostMapping("/{id}/complete")
    public String completeReservation(@PathVariable Long id,
                                      RedirectAttributes redirectAttributes) {
        reservationService.completeReservation(id);
        redirectAttributes.addFlashAttribute("successMessage", "Reservation #" + id + " marked as completed");
        return "redirect:/staff/reservations";
    }
}
