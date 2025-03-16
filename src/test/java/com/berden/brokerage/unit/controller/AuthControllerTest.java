package com.berden.brokerage.unit.controller;

import com.berden.brokerage.controller.AuthController;
import com.berden.brokerage.dto.external.request.LoginRequest;
import com.berden.brokerage.dto.external.response.LoginResponse;
import com.berden.brokerage.exception.GlobalExceptionHandler;
import com.berden.brokerage.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    private final String URL_UNDER_THE_TEST = "/api/auth";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    private LoginRequest validLoginRequest;
    private LoginResponse validLoginResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        validLoginRequest = new LoginRequest("testUser", "password123");
        validLoginResponse = new LoginResponse("dummyJwtToken", "ROLE_ADMIN");
    }

    @Test
    void login_WhenCredentialsAreValid_ThenShouldReturnToken() throws Exception {
        when(authService.login(validLoginRequest)).thenReturn(validLoginResponse);

        mockMvc.perform(post(URL_UNDER_THE_TEST + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(validLoginResponse.token()))
                .andExpect(jsonPath("$.role").value(validLoginResponse.role()));

        verify(authService).login(validLoginRequest);
    }

    @Test
    void login_WhenCredentialsAreInvalid_ThenShouldReturnBadRequest() throws Exception {
        LoginRequest invalidLoginRequest = new LoginRequest("testUser", "wrongPassword");
        when(authService.login(invalidLoginRequest)).thenThrow(new BadCredentialsException("invalid credentials"));

        mockMvc.perform(post(URL_UNDER_THE_TEST + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("invalid credentials"));

    }

}
