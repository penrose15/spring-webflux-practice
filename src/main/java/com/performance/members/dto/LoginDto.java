package com.performance.members.dto;

import java.util.Objects;

public record LoginDto(String email, String password) {
    public LoginDto {
        Objects.requireNonNull(email);
        Objects.requireNonNull(password);
    }
}
