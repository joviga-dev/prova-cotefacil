package br.com.apipedidos.assembler;

import br.com.apipedidos.dto.order.OrderDto;
import br.com.apipedidos.dto.order.OrderItemDto;
import br.com.apipedidos.entity.Order;
import br.com.apipedidos.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderDtoAssembler {

    private final OrderItemDtoAssembler orderItemDtoAssembler;

    public OrderDtoAssembler(OrderItemDtoAssembler orderItemDtoAssembler) {
        this.orderItemDtoAssembler = orderItemDtoAssembler;
    }

    public OrderDto toDto(Order order) {

        List<OrderItemDto> itens = null;

        if (order.getItems() != null) {
            itens = order.getItems().isEmpty() ? new ArrayList<>() :
                    order.getItems()
                    .stream()
                    .map(orderItemDtoAssembler::toDto)
                    .toList();
        }

        return new OrderDto(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getOrderDate(),
                order.getStatus().name(),
                order.getTotalAmount(),
                itens
        );
    }
}