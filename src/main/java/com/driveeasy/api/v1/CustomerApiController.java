package com.driveeasy.api.v1;

import com.driveeasy.dto.response.CustomerResponse;
import com.driveeasy.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/staff/customers")
@Tag(name = "Customers", description = "Customer management")
@SecurityRequirement(name = "bearerAuth")
public class CustomerApiController {

    private final CustomerService customerService;

    public CustomerApiController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @Operation(summary = "List all customers")
    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers().stream()
                .map(CustomerResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public CustomerResponse getCustomer(@PathVariable Long id) {
        return CustomerResponse.from(customerService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Register a new customer")
    public ResponseEntity<CustomerResponse> registerCustomer(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam(required = false) String drivingLicenseNumber) {
        var customer = customerService.registerCustomer(name, email, phone, drivingLicenseNumber);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerResponse.from(customer));
    }
}