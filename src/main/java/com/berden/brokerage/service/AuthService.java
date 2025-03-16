package com.berden.brokerage.service;

import com.berden.brokerage.common.enums.Role;
import com.berden.brokerage.dto.external.request.LoginRequest;
import com.berden.brokerage.dto.external.response.LoginResponse;
import com.berden.brokerage.dto.internal.UserDTO;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    UserDTO registerUser(String username, String password, Role role, Long customerId);
}
