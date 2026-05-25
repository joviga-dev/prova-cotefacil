package br.com.apipedidos.controller;

import br.com.apipedidos.dto.order.*;
import br.com.apipedidos.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Pedidos", description = "Operações de pedidos e itens de pedidos")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "Listar pedidos com paginação")
    public ResponseEntity<Page<OrderDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(orderService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID")
    public ResponseEntity<OrderDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findOrFail(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo pedido")
    public ResponseEntity<OrderDto> create(@RequestBody CreateOrderDto dto) {
        return ResponseEntity.ok(orderService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pedido")
    public ResponseEntity<OrderDto> update(
            @PathVariable Long id,
            @RequestBody UpdateOrderDto dto
    ) {
        return ResponseEntity.ok(orderService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar pedido")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "Listar itens de um pedido")
    public ResponseEntity<List<OrderItemDto>> findItemsByOrderId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(orderService.findItemsByOrderId(id));
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Adicionar item ao pedido")
    public ResponseEntity<OrderItemDto> addItem(
            @PathVariable Long id,
            @RequestBody CreateOrderItemDto dto
    ) {
        return ResponseEntity.ok(orderService.addItem(id, dto));
    }
}