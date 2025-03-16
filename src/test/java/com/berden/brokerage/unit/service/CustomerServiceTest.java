package com.berden.brokerage.unit.service;

import com.berden.brokerage.common.enums.Role;
import com.berden.brokerage.dto.external.request.RegisterCustomerRequest;
import com.berden.brokerage.dto.external.response.CustomerResponse;
import com.berden.brokerage.entity.Asset;
import com.berden.brokerage.entity.Customer;
import com.berden.brokerage.exception.DuplicateResourceException;
import com.berden.brokerage.helpers.CustomerTestHelper;
import com.berden.brokerage.repository.CustomerRepository;
import com.berden.brokerage.service.AssetService;
import com.berden.brokerage.service.AuthService;
import com.berden.brokerage.service.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AssetService assetService;

    @Mock
    private AuthService authService;


    @Test
    public void registerCustomer_WhenEmailIsUnique_ShouldRegisterSuccessfully() {
        RegisterCustomerRequest request = CustomerTestHelper.registerCustomerReq;
        Customer customer = CustomerTestHelper.createCustomerWithId();

        when(customerRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(customerRepository.save(any())).thenReturn(customer);
        when(assetService.createAsset(any())).thenReturn(null);
        when(authService.registerUser(anyString(), anyString(), any(Role.class), anyLong())).thenReturn(null);

        CustomerResponse response = customerService.registerCustomer(request);

        assertNotNull(response);
        assertEquals(customer.getId(), response.id());
        assertEquals(request.username(), response.username());
        assertEquals(request.email(), response.email());

        verify(customerRepository).findByEmail(request.email());
        verify(customerRepository).save(any());
        verify(assetService).createAsset(any(Asset.class));
        verify(authService).registerUser(anyString(), anyString(), any(Role.class), anyLong());
    }

    @Test
    public void registerCustomer_WhenEmailAlreadyExists_ShouldThrowDuplicateResourceException() {
        RegisterCustomerRequest request = CustomerTestHelper.registerCustomerReq;
        Customer customer = CustomerTestHelper.createCustomerWithId();

        when(customerRepository.findByEmail(request.email())).thenReturn(Optional.of(customer));

        assertThrows(DuplicateResourceException.class, () ->
                customerService.registerCustomer(request)
        );

        verify(customerRepository).findByEmail(request.email());
        verify(customerRepository, never()).save(any());
        verify(assetService, never()).createAsset(any(Asset.class));
        verify(authService, never()).registerUser(anyString(), anyString(), any(Role.class), anyLong());
    }

}
