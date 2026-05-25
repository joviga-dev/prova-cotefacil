package br.com.apipedidos.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        String customerName,
        String customerEmail,
        LocalDateTime orderDate,
        String status,
        BigDecimal totalAmount,
        List<OrderItemDto> items
) {
}