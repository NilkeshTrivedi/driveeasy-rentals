package com.driveeasy.controller;

import com.driveeasy.service.CustomerService;
import com.driveeasy.service.ReservationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final ReservationService reservationService;

    public CustomerController(CustomerService customerService,
                              ReservationService reservationService) {
        this.customerService = customerService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        return "staff/customers/list";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "staff/customers/register";
    }

    @PostMapping("/register")
    public String registerCustomer(@RequestParam String name,
                                   @RequestParam String email,
                                   @RequestParam String phone,
                                   @RequestParam(required = false) String drivingLicenseNumber,
                                   RedirectAttributes redirectAttributes) {
        var customer = customerService.registerCustomer(name, email, phone, drivingLicenseNumber);
        redirectAttributes.addFlashAttribute("successMessage",
                "Customer '" + customer.getName() + "' registered (ID: " + customer.getId() + ")");
        return "redirect:/staff/customers";
    }

    @GetMapping("/{id}")
    public String viewCustomer(@PathVariable Long id, Model model) {
        model.addAttribute("customer", customerService.findById(id));
        model.addAttribute("reservations", reservationService.getReservationsByCustomer(id));
        return "staff/customers/view";
    }
}
