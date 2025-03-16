package com.berden.brokerage.unit.controller;

import com.berden.brokerage.common.enums.Role;
import com.berden.brokerage.controller.AdminController;
import com.berden.brokerage.dto.external.request.RegisterCustomerRequest;
import com.berden.brokerage.dto.external.request.UserRegistrationRequest;
import com.berden.brokerage.dto.external.response.CustomerResponse;
import com.berden.brokerage.dto.external.response.OrderResponse;
import com.berden.brokerage.dto.external.response.UserResponse;
import com.berden.brokerage.dto.internal.UserDTO;
import com.berden.brokerage.entity.Order;
import com.berden.brokerage.exception.GlobalExceptionHandler;
import com.berden.brokerage.helpers.CustomerTestHelper;
import com.berden.brokerage.helpers.OrderTestHelper;
import com.berden.brokerage.helpers.UserTestHelper;
import com.berden.brokerage.service.AuthService;
import com.berden.brokerage.service.CustomerService;
import com.berden.brokerage.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {
    private final String URL_UNDER_THE_TEST = "/api/admin";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @InjectMocks
    private AdminController adminController;

    @Mock
    private CustomerService customerService;

    @Mock
    private OrderService orderService;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(adminController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerCustomer_WhenAdmin_ThenShouldCreateCustomer() throws Exception {
        RegisterCustomerRequest validCustomerRequest = CustomerTestHelper.registerCustomerReq;
        CustomerResponse validCustomerResponse = CustomerTestHelper.customerResponse;
        when(customerService.registerCustomer(any(RegisterCustomerRequest.class))).thenReturn(validCustomerResponse);

        mockMvc.perform(post(URL_UNDER_THE_TEST + "/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCustomerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(validCustomerResponse.id()))
                .andExpect(jsonPath("$.username").value(validCustomerResponse.username()))
                .andExpect(jsonPath("$.email").value(validCustomerResponse.email()));

        verify(customerService).registerCustomer(any(RegisterCustomerRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerAdmin_WhenAdmin_ThenShouldCreateAdminUser() throws Exception {
        UserRegistrationRequest validUserRequest = UserTestHelper.userRegistrationRequest;
        UserResponse validUserResponse = UserTestHelper.userResponse;
        UserDTO validUserDTO = UserTestHelper.adminDto;

        when(authService.registerUser(eq(validUserRequest.username()), eq(validUserRequest.password()), eq(Role.ADMIN), eq(null)))
                .thenReturn(validUserDTO);

        mockMvc.perform(post(URL_UNDER_THE_TEST + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(validUserResponse.id()))
                .andExpect(jsonPath("$.username").value(validUserResponse.username()));

        verify(authService).registerUser(validUserRequest.username(), validUserRequest.password(), Role.ADMIN, null);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void matchOrder_WhenAdmin_ThenShouldMatchOrder() throws Exception {
        Order validOrder = OrderTestHelper.createAppleBuyOrderWithId();
        OrderResponse validOrderResponse = OrderTestHelper.appMatchedResponse;
        when(orderService.matchOrder(anyLong())).thenReturn(validOrder);

        mockMvc.perform(put(URL_UNDER_THE_TEST + "/orders/1/match"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validOrderResponse.id()));

        verify(orderService).matchOrder(1L);
    }
}