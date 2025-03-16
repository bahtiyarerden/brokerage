package com.berden.brokerage.service;

import com.berden.brokerage.dto.external.request.RegisterCustomerRequest;
import com.berden.brokerage.dto.external.response.CustomerResponse;

public interface CustomerService {
    CustomerResponse registerCustomer(RegisterCustomerRequest request);
}
