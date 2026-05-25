package br.com.apipedidos.service;

import br.com.apipedidos.assembler.OrderDtoAssembler;
import br.com.apipedidos.assembler.OrderItemDtoAssembler;
import br.com.apipedidos.dto.order.*;
import br.com.apipedidos.entity.Order;
import br.com.apipedidos.entity.OrderItem;
import br.com.apipedidos.enums.OrderStatus;
import br.com.apipedidos.repository.OrderItemRepository;
import br.com.apipedidos.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderDtoAssembler  orderDtoAssembler;

    @Autowired
    private OrderItemDtoAssembler orderItemDtoAssembler;

    public Page<OrderDto> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(item -> this.orderDtoAssembler.toDto(item));
    }

    public OrderDto findOrFail(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Pedido não encontrado."));
        return this.orderDtoAssembler.toDto(order);
    }

    @Transactional
    public OrderDto create(CreateOrderDto dto) {

        Order order = new Order();

        order.setCustomerName(dto.customerName());
        order.setCustomerEmail(dto.customerEmail());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.valueOf(dto.status()));
        order.setTotalAmount(BigDecimal.ZERO);

        return this.orderDtoAssembler.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto update(Long id, UpdateOrderDto dto) {

        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Pedido não encontrado."));

        order.setCustomerName(dto.customerName());
        order.setCustomerEmail(dto.customerEmail());
        order.setStatus(OrderStatus.valueOf(dto.status()));

        return this.orderDtoAssembler.toDto(orderRepository.save(order));
    }

    @Transactional
    public void delete(Long id) {

        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Pedido não encontrado.");
        }

        orderRepository.deleteById(id);
    }

    public List<OrderItemDto> findItemsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Pedido não encontrado."));
        return order.getItems().stream().map(item -> this.orderItemDtoAssembler.toDto(item)).toList();
    }

    @Transactional
    public OrderItemDto addItem(Long orderId, CreateOrderItemDto dto) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Pedido não encontrado."));

        OrderItem item = new OrderItem();

        item.setOrder(order);
        item.setProductName(dto.productName());
        item.setQuantity(dto.quantity());
        item.setUnitPrice(dto.unitPrice());

        BigDecimal subtotal = dto.unitPrice().multiply(BigDecimal.valueOf(dto.quantity()));

        item.setSubtotal(subtotal);

        order.setTotalAmount(order.getTotalAmount().add(subtotal));

        orderRepository.save(order);

        return this.orderItemDtoAssembler.toDto(orderItemRepository.save(item));
    }

}