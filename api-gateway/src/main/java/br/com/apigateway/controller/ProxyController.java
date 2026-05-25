package br.com.apigateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Pedidos", description = "Proxy responsável por encaminhar requisições para a API de pedidos")
public class ProxyController {

    private final WebClient webClient;

    /**
     * Configura a URL da API de pedidos no webclient a partir do application.properties
     */
    public ProxyController(WebClient.Builder webClientBuilder, @Value("${api.pedidos.url}") String ordersApiUrl) {
        this.webClient = webClientBuilder.baseUrl(ordersApiUrl).build();
    }

    @GetMapping
    @Operation(summary = "Listar pedidos")
    public Object findAllOrders(@Parameter(hidden = true) Pageable pageable,
                                @Parameter(hidden = true) HttpServletRequest request) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/api/orders").queryParam("page",
                        pageable.getPageNumber()).queryParam("size", pageable.getPageSize()).build())
                .header(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION))
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID")
    public Object findOrderById(@PathVariable Long id,
                                @Parameter(hidden = true) HttpServletRequest request) {
        return webClient.get().uri("/api/orders/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION))
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Criar pedido")
    public Object createOrder(@RequestBody Object body,
                              @Parameter(hidden = true) HttpServletRequest request) {
        return webClient.post().uri("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pedido")
    public Object updateOrder(@PathVariable Long id, @RequestBody Object body,
                              @Parameter(hidden = true) HttpServletRequest request) {
        return webClient.put().uri("/api/orders/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar pedido")
    public void deleteOrder(@PathVariable Long id,
                            @Parameter(hidden = true) HttpServletRequest request) {
        webClient.delete().uri("/api/orders/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "Listar itens do pedido")
    public Object findItems(@PathVariable Long id,
                            @Parameter(hidden = true) HttpServletRequest request) {
        return webClient.get().uri("/api/orders/{id}/items", id)
                .header(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION))
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Adicionar item ao pedido")
    public Object addItem(@PathVariable Long id, @RequestBody Object body,
                          @Parameter(hidden = true) HttpServletRequest request) {
        return webClient.post().uri("/api/orders/{id}/items", id)
                .header(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }
}