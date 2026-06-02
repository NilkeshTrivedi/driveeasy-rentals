package com.driveeasy.api.v1;

import com.driveeasy.dto.response.CarResponse;
import com.driveeasy.model.enums.CarCategory;
import com.driveeasy.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Cars", description = "Fleet management and availability")
@SecurityRequirement(name = "bearerAuth")
public class CarApiController {

    private final CarService carService;

    public CarApiController(CarService carService) {
        this.carService = carService;
    }

    /** GET /api/v1/cars — all cars (staff + admin) */
    @GetMapping("/staff/cars")
    @Operation(summary = "List all cars")
    public List<CarResponse> getAllCars() {
        return carService.getAllCars().stream()
                .map(CarResponse::from)
                .toList();
    }

    /** GET /api/v1/cars/available?startDate=&endDate= */
    @GetMapping("/staff/cars/available")
    @Operation(summary = "Find available cars for date range")
    public List<CarResponse> getAvailableCars(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return carService.findAvailableCars(startDate, endDate).stream()
                .map(CarResponse::from)
                .toList();
    }

    /** GET /api/v1/cars/{id} */
    @GetMapping("/staff/cars/{id}")
    @Operation(summary = "Get car by ID")
    public CarResponse getCarById(@PathVariable Long id) {
        return CarResponse.from(carService.findById(id));
    }

    /** POST /api/v1/admin/cars — admin only */
    @PostMapping("/admin/cars")
    @Operation(summary = "Add a new car (admin only)")
    public ResponseEntity<CarResponse> addCar(
            @RequestParam String model,
            @RequestParam CarCategory category,
            @RequestParam double baseFare,
            @RequestParam double perKmRate,
            @RequestParam double perHourRate) {
        var car = carService.addCar(model, category, baseFare, perKmRate, perHourRate);
        return ResponseEntity.status(HttpStatus.CREATED).body(CarResponse.from(car));
    }

    /** PUT /api/v1/admin/cars/{id}/pricing — admin only */
    @PutMapping("/admin/cars/{id}/pricing")
    @Operation(summary = "Update car pricing (admin only)")
    public CarResponse updatePricing(
            @PathVariable Long id,
            @RequestParam double baseFare,
            @RequestParam double perKmRate,
            @RequestParam double perHourRate) {
        return CarResponse.from(carService.updateCarPricing(id, baseFare, perKmRate, perHourRate));
    }

    /** PATCH /api/v1/admin/cars/{id}/maintenance — admin only */
    @PatchMapping("/admin/cars/{id}/maintenance")
    @Operation(summary = "Toggle maintenance status (admin only)")
    public ResponseEntity<Map<String, String>> toggleMaintenance(
            @PathVariable Long id,
            @RequestParam boolean underMaintenance) {
        if (underMaintenance) {
            carService.markUnderMaintenance(id);
        } else {
            carService.markAvailable(id);
        }
        return ResponseEntity.ok(Map.of(
                "message", "Car " + id + " maintenance status updated",
                "underMaintenance", String.valueOf(underMaintenance)
        ));
    }
}