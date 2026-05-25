package br.com.apigateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProxyControllerTest {

    private MockWebServer mockWebServer;
    private ProxyController controller;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        controller = new ProxyController(
                WebClient.builder(),
                mockWebServer.url("/").toString()
        );

        request = mock(HttpServletRequest.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer token-valido");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void deveRepassarAuthorizationHeaderParaApiPedidos() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "content": []
                        }
                        """));

        controller.findAllOrders(
                PageRequest.of(0, 10),
                request
        );

        RecordedRequest recordedRequest =
                mockWebServer.takeRequest();

        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/api/orders?page=0&size=10", recordedRequest.getPath());

        assertEquals(
                "Bearer token-valido",
                recordedRequest.getHeader(HttpHeaders.AUTHORIZATION)
        );
    }

    @Test
    void devePropagarErroDaApiPedidos() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "message": "Erro na API de pedidos"
                        }
                        """));

        assertThrows(
                WebClientResponseException.class,
                () -> controller.findAllOrders(
                        PageRequest.of(0, 10),
                        request
                )
        );
    }
}