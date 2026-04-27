package com.driveeasy.controller;

import com.driveeasy.model.Car;
import com.driveeasy.model.enums.CarCategory;
import com.driveeasy.service.CarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public String listCars(Model model) {
        model.addAttribute("cars", carService.getAllCars());
        model.addAttribute("categories", CarCategory.values());
        return "admin/cars/list";
    }

    @GetMapping("/add")
    public String showAddCarForm(Model model) {
        model.addAttribute("categories", CarCategory.values());
        return "admin/cars/add";
    }

    /**
     * BUG FIX #13: The original code used @RequestParam String model_ which would
     * look for a request parameter literally named "model_". HTML forms submit the
     * field as "model". Added explicit @RequestParam("model") to bind correctly.
     * Renamed the local variable to carModel to avoid shadowing the Model parameter.
     */
    @PostMapping("/add")
    public String addCar(@RequestParam("model") String carModel,
                         @RequestParam CarCategory category,
                         @RequestParam double baseFare,
                         @RequestParam double perKmRate,
                         @RequestParam double perHourRate,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        Car car = carService.addCar(carModel, category, baseFare, perKmRate, perHourRate);
        redirectAttributes.addFlashAttribute("successMessage",
                "Car '" + car.getModel() + "' added successfully (ID: " + car.getId() + ")");
        return "redirect:/admin/cars";
    }

    @GetMapping("/{id}/pricing")
    public String showUpdatePricingForm(@PathVariable Long id, Model model) {
        model.addAttribute("car", carService.findById(id));
        return "admin/cars/pricing";
    }

    @PostMapping("/{id}/pricing")
    public String updatePricing(@PathVariable Long id,
                                @RequestParam double baseFare,
                                @RequestParam double perKmRate,
                                @RequestParam double perHourRate,
                                RedirectAttributes redirectAttributes) {
        carService.updateCarPricing(id, baseFare, perKmRate, perHourRate);
        redirectAttributes.addFlashAttribute("successMessage", "Pricing updated for car " + id);
        return "redirect:/admin/cars";
    }

    @PostMapping("/{id}/maintenance/on")
    public String markMaintenance(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        carService.markUnderMaintenance(id);
        redirectAttributes.addFlashAttribute("successMessage", "Car " + id + " marked as under maintenance");
        return "redirect:/admin/cars";
    }

    @PostMapping("/{id}/maintenance/off")
    public String markAvailable(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        carService.markAvailable(id);
        redirectAttributes.addFlashAttribute("successMessage", "Car " + id + " is now available");
        return "redirect:/admin/cars";
    }
}