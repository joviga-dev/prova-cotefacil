package br.com.apipedidos.dto.order;

import java.math.BigDecimal;

public record OrderItemDto(
        Long id,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}