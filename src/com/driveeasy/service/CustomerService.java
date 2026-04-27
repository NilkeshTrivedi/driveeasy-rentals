package com.driveeasy.service;

import com.driveeasy.exception.ResourceNotFoundException;
import com.driveeasy.exception.ValidationException;
import com.driveeasy.model.Customer;
import com.driveeasy.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Registers a new customer after checking for email/phone uniqueness.
     * Validation annotations on Customer handle format checks via @Valid at controller level;
     * business-rule uniqueness checks are enforced here.
     */
    public Customer registerCustomer(String name, String email, String phone,
                                     String drivingLicenseNumber) {
        if (customerRepository.existsByEmail(email)) {
            throw new ValidationException("A customer with email '" + email + "' already exists");
        }
        if (customerRepository.existsByPhone(phone)) {
            throw new ValidationException("A customer with phone '" + phone + "' already exists");
        }
        Customer customer = new Customer(name, email, phone, drivingLicenseNumber);
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long customerId, String name, String phone,
                                   String drivingLicenseNumber) {
        Customer customer = findById(customerId);

        // Allow phone update only if the new number doesn't belong to another customer
        if (!customer.getPhone().equals(phone) && customerRepository.existsByPhone(phone)) {
            throw new ValidationException("Phone number '" + phone + "' is already in use");
        }

        customer.setName(name);
        customer.setPhone(phone);
        customer.setDrivingLicenseNumber(drivingLicenseNumber);
        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public Customer findById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
    }

    @Transactional(readOnly = true)
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
