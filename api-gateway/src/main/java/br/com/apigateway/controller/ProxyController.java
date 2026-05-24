package br.com.apigateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/orders")
public class ProxyController {

    private final WebClient webClient;

    /**
     * Configura a URL da API de pedidos no webclient a partir do application.properties
     * @param webClientBuilder
     * @param ordersApiUrl
     */
    public ProxyController(WebClient.Builder webClientBuilder,
                           @Value("${api.pedidos.url}") String ordersApiUrl) {

        this.webClient = webClientBuilder.baseUrl(ordersApiUrl).build();
    }


    @PostMapping("/hello")
    public String hello(HttpServletRequest request) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        System.out.println("Token recebido: " + authorizationHeader);

        return "Hello World";
    }
}
