package com.driveeasy.dao;

import com.driveeasy.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {

    // Add a new customer
    void addCustomer(Customer customer);

    // Update customer details
    void updateCustomer(Customer customer);

    // Get customer by ID
    Optional<Customer> getCustomerById(long id);

    // Get customer by email
    Optional<Customer> getCustomerByEmail(String email);

    // Get customer by phone
    Optional<Customer> getCustomerByPhone(String phone);

    // Get all customers
    List<Customer> getAllCustomers();
}
