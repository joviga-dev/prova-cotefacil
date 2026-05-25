package br.com.apipedidos.dto.order;

import java.math.BigDecimal;

public record CreateOrderItemDto(
        String productName,
        Integer quantity,
        BigDecimal unitPrice
) {
}