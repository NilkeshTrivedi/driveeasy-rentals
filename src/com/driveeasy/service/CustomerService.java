package com.driveeasy.service;

import com.driveeasy.dao.CustomerDao;
import com.driveeasy.dao.impl.CustomerDaoImpl;
import com.driveeasy.exception.ValidationException;
import com.driveeasy.model.Customer;

import java.util.regex.Pattern;

public class CustomerService {

    private final CustomerDao customerDao = new CustomerDaoImpl();

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[6-9][0-9]{9}$");

    public void registerCustomer(Customer customer) {

        if (!EMAIL_PATTERN.matcher(customer.getEmail()).matches()) {
            throw new ValidationException("Invalid email format");
        }

        if (!PHONE_PATTERN.matcher(customer.getPhone()).matches()) {
            throw new ValidationException("Invalid phone number");
        }

        boolean emailExists = customerDao.getCustomerByEmail(customer.getEmail()).isPresent();
        boolean phoneExists = customerDao.getCustomerByPhone(customer.getPhone()).isPresent();

        if (emailExists || phoneExists) {
            throw new ValidationException("Customer with same email or phone already exists");
        }

        customerDao.addCustomer(customer);
    }
}
