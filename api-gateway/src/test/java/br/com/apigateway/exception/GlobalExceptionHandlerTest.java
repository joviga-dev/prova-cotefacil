package br.com.apigateway.exception;

import br.com.apigateway.config.GlobalExceptionHandler;
import br.com.apigateway.dto.auth.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @Test
    void deveTratarAuthenticationException() throws Exception {

        when(request.getRequestURI()).thenReturn("/api/orders");

        ResponseEntity<ErrorResponseDto> response = handler.handleAuthenticationException(new AuthenticationException("Erro de autenticação"), request);

        assertEquals(401, response.getStatusCode().value());

        assertNotNull(response.getBody());

        assertEquals("Acesso negado", response.getBody().message());
    }

    @Test
    void deveTratarBadCredentialsException() {

        when(request.getRequestURI()).thenReturn("/auth/login");

        ResponseEntity<ErrorResponseDto> response = handler.handleBadCredentialsException(new BadCredentialsException("Dados incorretos"), request);

        assertEquals(401, response.getStatusCode().value());

        assertNotNull(response.getBody());

        assertEquals("Dados incorretos", response.getBody().message());
    }

    @Test
    void deveTratarAccessDeniedException() {

        when(request.getRequestURI()).thenReturn("/api/orders");

        ResponseEntity<ErrorResponseDto> response = handler.handleAccessDeniedException(new AccessDeniedException("Acesso negado"), request);

        assertEquals(403, response.getStatusCode().value());

        assertNotNull(response.getBody());

        assertEquals("Acesso negado", response.getBody().message());
    }

    @Test
    void deveTratarGenericException() {

        when(request.getRequestURI()).thenReturn("/api/orders");

        ResponseEntity<ErrorResponseDto> response = handler.handleGenericException(new Exception("Erro interno"), request);

        assertEquals(500, response.getStatusCode().value());

        assertNotNull(response.getBody());

        assertEquals("Erro interno do servidor", response.getBody().message());
    }

    @Test
    void deveTratarRuntimeException() {

        when(request.getRequestURI()).thenReturn("/api/orders");

        ResponseEntity<ErrorResponseDto> response = handler.handleRuntimeException(new RuntimeException("Erro runtime"), request);

        assertEquals(500, response.getStatusCode().value());

        assertNotNull(response.getBody());

        assertEquals("Erro runtime", response.getBody().message());
    }
}