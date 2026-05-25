package br.com.apipedidos.service;

import br.com.apipedidos.assembler.OrderDtoAssembler;
import br.com.apipedidos.assembler.OrderItemDtoAssembler;
import br.com.apipedidos.dto.order.*;
import br.com.apipedidos.entity.Order;
import br.com.apipedidos.entity.OrderItem;
import br.com.apipedidos.enums.OrderStatus;
import br.com.apipedidos.repository.OrderItemRepository;
import br.com.apipedidos.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService service;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderDtoAssembler orderDtoAssembler;

    @Mock
    private OrderItemDtoAssembler orderItemDtoAssembler;

    @Test
    void deveListarPedidosPaginados() {
        var order = pedidoValido();
        var dto = orderDtoValido();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findAll(pageable)).thenReturn(page);
        when(orderDtoAssembler.toDto(order)).thenReturn(dto);

        Page<OrderDto> result = service.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(dto, result.getContent().get(0));

        verify(orderRepository).findAll(pageable);
        verify(orderDtoAssembler).toDto(order);
    }

    @Test
    void deveBuscarPedidoPorId() {
        var order = pedidoValido();
        var dto = orderDtoValido();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderDtoAssembler.toDto(order)).thenReturn(dto);

        OrderDto result = service.findOrFail(1L);

        assertNotNull(result);
        assertEquals(dto, result);

        verify(orderRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoExistirAoBuscar() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findOrFail(1L));

        verify(orderDtoAssembler, never()).toDto(any());
    }

    @Test
    void deveCriarPedido() {
        var input = createOrderDtoValido();
        var orderSalvo = pedidoValido();
        var dto = orderDtoValido();

        when(orderRepository.save(any(Order.class))).thenReturn(orderSalvo);
        when(orderDtoAssembler.toDto(orderSalvo)).thenReturn(dto);

        OrderDto result = service.create(input);

        assertNotNull(result);
        assertEquals(dto, result);

        verify(orderRepository).save(argThat(order -> order.getCustomerName().equals("João Silva") && order.getCustomerEmail().equals("joao@email.com") && order.getStatus().equals(OrderStatus.PENDING) && order.getTotalAmount().compareTo(BigDecimal.ZERO) == 0 && order.getOrderDate() != null));
    }

    @Test
    void deveAtualizarPedido() {
        var order = pedidoValido();
        var input = updateOrderDtoValido();
        var dto = orderDtoAtualizado();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderDtoAssembler.toDto(order)).thenReturn(dto);

        OrderDto result = service.update(1L, input);

        assertNotNull(result);
        assertEquals(dto, result);

        assertEquals("Maria Oliveira", order.getCustomerName());
        assertEquals("maria@email.com", order.getCustomerEmail());
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());

        verify(orderRepository).save(order);
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoExistirAoAtualizar() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.update(1L, updateOrderDtoValido()));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void deveDeletarPedido() {
        when(orderRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(orderRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoExistirAoDeletar() {
        when(orderRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.delete(1L));

        verify(orderRepository, never()).deleteById(any());
    }

    @Test
    void deveListarItensDoPedido() {
        var order = pedidoValido();
        var item = itemValido(order);
        var itemDto = orderItemDtoValido();

        order.setItems(List.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemDtoAssembler.toDto(item)).thenReturn(itemDto);

        List<OrderItemDto> result = service.findItemsByOrderId(1L);

        assertEquals(1, result.size());
        assertEquals(itemDto, result.get(0));

        verify(orderRepository).findById(1L);
        verify(orderItemDtoAssembler).toDto(item);
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoExistirAoListarItens() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findItemsByOrderId(1L));

        verify(orderItemDtoAssembler, never()).toDto(any());
    }

    @Test
    void deveAdicionarItemAoPedido() {
        var order = pedidoValido();
        var input = createOrderItemDtoValido();
        var itemSalvo = itemValido(order);
        var itemDto = orderItemDtoValido();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(itemSalvo);
        when(orderItemDtoAssembler.toDto(itemSalvo)).thenReturn(itemDto);

        OrderItemDto result = service.addItem(1L, input);

        assertNotNull(result);
        assertEquals(itemDto, result);
        assertEquals(new BigDecimal("25.00"), order.getTotalAmount());

        verify(orderRepository).save(order);
        verify(orderItemRepository).save(argThat(item -> item.getOrder().equals(order) && item.getProductName().equals("Dipirona 1g") && item.getQuantity().equals(2) && item.getUnitPrice().compareTo(new BigDecimal("12.50")) == 0 && item.getSubtotal().compareTo(new BigDecimal("25.00")) == 0));
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoExistirAoAdicionarItem() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.addItem(1L, createOrderItemDtoValido()));

        verify(orderItemRepository, never()).save(any());
    }

    private CreateOrderDto createOrderDtoValido() {
        return new CreateOrderDto("João Silva", "joao@email.com", "PENDING");
    }

    private UpdateOrderDto updateOrderDtoValido() {
        return new UpdateOrderDto("Maria Oliveira", "maria@email.com", "CONFIRMED");
    }

    private CreateOrderItemDto createOrderItemDtoValido() {
        return new CreateOrderItemDto("Dipirona 1g", 2, new BigDecimal("12.50"));
    }

    private Order pedidoValido() {
        Order order = new Order();

        order.setId(1L);
        order.setCustomerName("João Silva");
        order.setCustomerEmail("joao@email.com");
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setItems(List.of());

        return order;
    }

    private OrderItem itemValido(Order order) {
        OrderItem item = new OrderItem();

        item.setId(1L);
        item.setOrder(order);
        item.setProductName("Dipirona 1g");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("12.50"));
        item.setSubtotal(new BigDecimal("25.00"));

        return item;
    }

    private OrderDto orderDtoValido() {
        return new OrderDto(1L, "João Silva", "joao@email.com", LocalDateTime.now(), "PENDING", BigDecimal.ZERO, List.of());
    }

    private OrderDto orderDtoAtualizado() {
        return new OrderDto(1L, "Maria Oliveira", "maria@email.com", LocalDateTime.now(), "CONFIRMED", BigDecimal.ZERO, List.of());
    }

    private OrderItemDto orderItemDtoValido() {
        return new OrderItemDto(1L, "Dipirona 1g", 2, new BigDecimal("12.50"), new BigDecimal("25.00"));
    }
}