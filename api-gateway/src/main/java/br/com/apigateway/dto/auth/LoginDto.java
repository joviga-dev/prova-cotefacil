package br.com.apigateway.dto.auth;

import jakarta.validation.constraints.NotNull;

public record LoginDto(@NotNull String username,
                       @NotNull String password) {

}
