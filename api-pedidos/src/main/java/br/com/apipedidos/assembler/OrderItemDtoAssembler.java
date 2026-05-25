package br.com.apipedidos.assembler;

import br.com.apipedidos.dto.order.OrderItemDto;
import br.com.apipedidos.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemDtoAssembler {

    public OrderItemDto toDto(OrderItem item) {
        return new OrderItemDto(
                item.getId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}