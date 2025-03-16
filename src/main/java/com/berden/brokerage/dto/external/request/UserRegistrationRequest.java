package com.berden.brokerage.dto.external.request;

import jakarta.validation.constraints.NotEmpty;

public record UserRegistrationRequest(@NotEmpty String username, @NotEmpty String password) {
}
