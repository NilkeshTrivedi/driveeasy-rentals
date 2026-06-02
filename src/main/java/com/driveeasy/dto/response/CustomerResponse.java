package com.driveeasy.dto.response;

import com.driveeasy.model.Customer;

public class CustomerResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String drivingLicenseNumber;

    public static CustomerResponse from(Customer customer) {
        CustomerResponse r = new CustomerResponse();
        r.id = customer.getId();
        r.name = customer.getName();
        r.email = customer.getEmail();
        r.phone = customer.getPhone();
        r.drivingLicenseNumber = customer.getDrivingLicenseNumber();
        return r;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDrivingLicenseNumber() { return drivingLicenseNumber; }
}