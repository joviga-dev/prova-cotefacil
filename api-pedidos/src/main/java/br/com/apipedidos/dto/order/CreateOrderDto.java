package br.com.apipedidos.dto.order;

public record CreateOrderDto(
        String customerName,
        String customerEmail,
        String status
) {
}