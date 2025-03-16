package com.berden.brokerage.service;

import com.berden.brokerage.common.constants.AssetsConstant;
import com.berden.brokerage.common.enums.Role;
import com.berden.brokerage.dto.external.request.RegisterCustomerRequest;
import com.berden.brokerage.dto.external.response.CustomerResponse;
import com.berden.brokerage.entity.Asset;
import com.berden.brokerage.entity.Customer;
import com.berden.brokerage.exception.DuplicateResourceException;
import com.berden.brokerage.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AssetService assetService;
    private final AuthService authService;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository,
                               AssetService assetService,
                               AuthService authService) {
        this.customerRepository = customerRepository;
        this.assetService = assetService;
        this.authService = authService;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerResponse registerCustomer(RegisterCustomerRequest request) {
        Optional<Customer> maybeUser = customerRepository.findByEmail(request.email());
        if (maybeUser.isPresent()) {
            throw new DuplicateResourceException("customer already exist with email: " + request.email());
        }

        Customer customer = Customer.builder().email(request.email()).build();
        Customer savedCustomer = customerRepository.save(customer);

        Asset assetTRY = Asset.builder()
                .assetName(AssetsConstant.TRY_ASSET)
                .size(AssetsConstant.INITIAL_BALANCE)
                .usableSize(AssetsConstant.INITIAL_BALANCE)
                .customerId(savedCustomer.getId())
                .build();
        assetService.createAsset(assetTRY);

        authService.registerUser(request.username(), request.password(), Role.CUSTOMER, savedCustomer.getId());

        return new CustomerResponse(savedCustomer.getId(), request.username(), request.email());
    }

}
