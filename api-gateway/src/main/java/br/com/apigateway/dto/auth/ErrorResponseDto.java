package br.com.apigateway.dto.auth;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String message,
        String path
) {
}