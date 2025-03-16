package com.berden.brokerage.helpers;

import com.berden.brokerage.dto.external.request.RegisterCustomerRequest;
import com.berden.brokerage.dto.external.response.CustomerResponse;
import com.berden.brokerage.entity.Customer;

public class CustomerTestHelper {
    public static RegisterCustomerRequest registerCustomerReq =
            new RegisterCustomerRequest("test@test.com", "customer", "strongcustomerpass");
    public static CustomerResponse customerResponse =
            new CustomerResponse(1L, "customer", "test@test.com");

    public static Customer createCustomer() {
        return Customer.builder()
                .email("test@test.com")
                .build();
    }

    public static Customer createCustomerWithId() {
        return Customer.builder()
                .id(1L)
                .email("test@test.com")
                .build();
    }
}
