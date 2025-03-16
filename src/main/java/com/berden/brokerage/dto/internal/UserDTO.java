package com.berden.brokerage.dto.internal;

import com.berden.brokerage.common.enums.Role;
import com.berden.brokerage.entity.User;

public record UserDTO(Long id, String username, Role role, Long customerId) {
    public static UserDTO fromEntity(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getRole(), user.getCustomerId());
    }
}
