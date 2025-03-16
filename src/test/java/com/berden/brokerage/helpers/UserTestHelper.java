package com.berden.brokerage.helpers;

import com.berden.brokerage.common.enums.Role;
import com.berden.brokerage.dto.external.request.UserRegistrationRequest;
import com.berden.brokerage.dto.external.response.UserResponse;
import com.berden.brokerage.dto.internal.UserDTO;
import com.berden.brokerage.entity.User;

public class UserTestHelper {
    public static UserRegistrationRequest userRegistrationRequest =
            new UserRegistrationRequest("admin", "admin");
    public static UserDTO adminDto = new UserDTO(1L, "admin", Role.ADMIN, null);
    public static UserResponse userResponse = new UserResponse(1L, "admin");


    public static User createUserAdmin() {
        return User.builder()
                .username("user")
                .password("userstrongpass")
                .role(Role.ADMIN)
                .build();
    }

    public static User createUserCustomer() {
        return User.builder()
                .username("customer")
                .password("customerstrongpass")
                .role(Role.CUSTOMER)
                .customerId(1L)
                .build();
    }
}
