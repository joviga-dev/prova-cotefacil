package br.com.apigateway.service.auth;

import br.com.apigateway.entity.User;
import br.com.apigateway.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static br.com.apigateway.factory.TestFactory.criarUsuarioValido;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAuthenticationFilterTest {

    @InjectMocks
    private UserAuthenticationFilter filter;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveAutenticarUsuarioComTokenValido() throws ServletException, IOException {

        User user = criarUsuarioValido();

        when(request.getRequestURI()).thenReturn("/api/orders");

        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");

        when(jwtTokenService.getSubjectFromToken("token-valido")).thenReturn("usuario");

        when(userRepository.findByUsername("usuario")).thenReturn(Optional.of(user));

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        assertEquals("usuario", SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void deveLancarExcecaoQuandoTokenNaoForInformado() throws ServletException, IOException {

        when(request.getRequestURI()).thenReturn("/api/orders");

        when(request.getHeader("Authorization")).thenReturn(null);

        assertThrows(BadCredentialsException.class, () -> filter.doFilterInternal(request, response, filterChain));

        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void deveIgnorarEndpointPublico() throws ServletException, IOException {

        when(request.getRequestURI()).thenReturn("/auth/login");

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request, response);

        verifyNoInteractions(jwtTokenService);
    }

    @Test
    void deveLancarExcecaoQuandoTokenForInvalido() {

        when(request.getRequestURI()).thenReturn("/api/orders");

        when(request.getHeader("Authorization")).thenReturn("Bearer token-invalido");

        when(jwtTokenService.getSubjectFromToken("token-invalido")).thenThrow(new BadCredentialsException("Token inválido"));

        assertThrows(BadCredentialsException.class, () -> filter.doFilterInternal(request, response, filterChain));
    }

}