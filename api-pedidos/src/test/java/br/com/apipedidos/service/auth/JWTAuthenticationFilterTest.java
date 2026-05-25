package br.com.apipedidos.service.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTAuthenticationFilterTest {

    @InjectMocks
    private JWTAuthenticationFilter filter;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveAutenticarComTokenValido() throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();

        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer token-valido");

        when(jwtTokenService.getSubjectFromToken("token-valido")).thenReturn("usuario");

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        assertEquals("usuario", SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        verify(filterChain).doFilter(request, response);

        verify(jwtTokenService).getSubjectFromToken("token-valido");
    }

    @Test
    void deveLancarExcecaoQuandoTokenAusente() throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();

        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(BadCredentialsException.class, () -> filter.doFilterInternal(request, response, filterChain));

        verify(filterChain, never()).doFilter(any(), any());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void devePropagarErroQuandoTokenInvalido() throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();

        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer token-invalido");

        when(jwtTokenService.getSubjectFromToken("token-invalido")).thenThrow(new RuntimeException("Token inválido"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> filter.doFilterInternal(request, response, filterChain));

        assertEquals("Token inválido", exception.getMessage());

        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void deveAceitarHeaderSemBearer() throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();

        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "token-direto");

        when(jwtTokenService.getSubjectFromToken("token-direto")).thenReturn("usuario");

        filter.doFilterInternal(request, response, filterChain);

        assertEquals("usuario", SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        verify(filterChain).doFilter(request, response);
    }
}