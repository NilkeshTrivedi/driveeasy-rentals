package com.driveeasy.service;

import com.driveeasy.dao.CustomerDao;
import com.driveeasy.dao.impl.CustomerDaoImpl;
import com.driveeasy.exception.ValidationException;
import com.driveeasy.model.Customer;
import com.driveeasy.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final CustomerRepository customerRepository;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[6-9][0-9]{9}$");

    /**
     * Default constructor for console Phase 1 usage (DAO-based).
     */
    public CustomerService() {
        this.customerDao = new CustomerDaoImpl();
        this.customerRepository = null;
    }

    /**
     * Spring-managed constructor using JPA repository.
     */
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.customerDao = null;
    }

    public void registerCustomer(Customer customer) {

        if (!EMAIL_PATTERN.matcher(customer.getEmail()).matches()) {
            throw new ValidationException("Invalid email format");
        }

        if (!PHONE_PATTERN.matcher(customer.getPhone()).matches()) {
            throw new ValidationException("Invalid phone number");
        }

        boolean emailExists = emailExists(customer.getEmail());
        boolean phoneExists = phoneExists(customer.getPhone());

        if (emailExists || phoneExists) {
            throw new ValidationException("Customer with same email or phone already exists");
        }

        saveCustomer(customer);
    }

    private boolean emailExists(String email) {
        if (customerRepository != null) {
            return customerRepository.findByEmail(email).isPresent();
        }
        return customerDao.getCustomerByEmail(email).isPresent();
    }

    private boolean phoneExists(String phone) {
        if (customerRepository != null) {
            return customerRepository.findByPhone(phone).isPresent();
        }
        return customerDao.getCustomerByPhone(phone).isPresent();
    }

    private void saveCustomer(Customer customer) {
        if (customerRepository != null) {
            customerRepository.save(customer);
        } else {
            customerDao.addCustomer(customer);
        }
    }
}
