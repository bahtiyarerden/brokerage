package com.berden.brokerage.dto.external.response;

import com.berden.brokerage.dto.internal.UserDTO;

public record UserResponse(Long id, String username) {
    public static UserResponse fromDto(UserDTO userDto) {
        return new UserResponse(userDto.id(), userDto.username());
    }
}
