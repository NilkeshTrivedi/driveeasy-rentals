package com.driveeasy.api.v1;

import com.driveeasy.dto.request.BookingRequest;
import com.driveeasy.dto.response.FarePreviewResponse;
import com.driveeasy.dto.response.ReservationResponse;
import com.driveeasy.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/staff/reservations")
@Tag(name = "Reservations", description = "Booking lifecycle management")
@SecurityRequirement(name = "bearerAuth")
public class ReservationApiController {

    private final ReservationService reservationService;

    public ReservationApiController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    @Operation(summary = "List all reservations")
    public List<ReservationResponse> getAllReservations() {
        return reservationService.getAllReservations().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by ID")
    public ReservationResponse getReservation(@PathVariable Long id) {
        return ReservationResponse.from(reservationService.findById(id));
    }

    @GetMapping("/fare-preview")
    @Operation(summary = "Preview fare before booking")
    public FarePreviewResponse previewFare(
            @RequestParam Long carId,
            @RequestParam double estimatedDistanceKm,
            @RequestParam double estimatedDurationHours) {
        return FarePreviewResponse.from(
                reservationService.previewFare(carId, estimatedDistanceKm, estimatedDurationHours));
    }

    @PostMapping
    @Operation(summary = "Create a new booking")
    public ResponseEntity<ReservationResponse> bookCar(
            @Valid @RequestBody BookingRequest request) {
        var reservation = reservationService.bookCar(
                request.getCarId(),
                request.getCustomerId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getEstimatedDistanceKm(),
                request.getEstimatedDurationHours()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReservationResponse.from(reservation));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a reservation")
    public ReservationResponse cancelReservation(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        return ReservationResponse.from(reservationService.cancelReservation(id, reason));
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark reservation as completed")
    public ReservationResponse completeReservation(@PathVariable Long id) {
        return ReservationResponse.from(reservationService.completeReservation(id));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get reservations by customer")
    public List<ReservationResponse> getByCustomer(@PathVariable Long customerId) {
        return reservationService.getReservationsByCustomer(customerId).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get total revenue")
    public Map<String, Double> getTotalRevenue() {
        return Map.of("totalRevenue", reservationService.getTotalRevenue());
    }
}