package com.berden.brokerage.dto.external.request;

import jakarta.validation.constraints.NotEmpty;

public record RegisterCustomerRequest(@NotEmpty String email, @NotEmpty String username,
                                      @NotEmpty String password) {
}
