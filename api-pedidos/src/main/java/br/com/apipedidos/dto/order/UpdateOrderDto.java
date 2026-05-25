package br.com.apipedidos.dto.order;

public record UpdateOrderDto(
        String customerName,
        String customerEmail,
        String status
) {
}