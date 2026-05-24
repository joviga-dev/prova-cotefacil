package br.com.apigateway.service.auth;

import br.com.apigateway.dto.auth.RecoveryJwtTokenDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static br.com.apigateway.factory.TestFactory.criarUsuarioValido;
import static br.com.apigateway.factory.TestFactory.loginValido;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService service;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private Authentication authentication;

    @Test
    void deveAutenticarUsuarioComSucesso() {

        var loginDto = loginValido();

        var user = criarUsuarioValido();

        var userDetails = new UserDetailsImpl(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(jwtTokenService.generateToken(userDetails)).thenReturn("jwt-token");

        RecoveryJwtTokenDto response = service.authenticateUser(loginDto);

        assertNotNull(response);

        assertEquals("jwt-token", response.token());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(jwtTokenService).generateToken(userDetails);
    }

    @Test
    void deveLancarExcecaoParaCredenciaisInvalidas() {

        var loginDto = loginValido();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Dados incorretos"));

        assertThrows(BadCredentialsException.class, () -> service.authenticateUser(loginDto));

        verify(jwtTokenService, never()).generateToken(any());
    }



    @Test
    void deveGerarTokenJwt() {

        var user = criarUsuarioValido();

        var userDetails = new UserDetailsImpl(user);

        when(jwtTokenService.generateToken(userDetails)).thenReturn("jwt-token");

        String token = jwtTokenService.generateToken(userDetails);

        assertNotNull(token);

        assertEquals("jwt-token", token);
    }

    @Test
    void deveValidarTokenJwt() {

        String token = "jwt-token";

        when(jwtTokenService.getSubjectFromToken(token)).thenReturn("admin");

        String subject = jwtTokenService.getSubjectFromToken(token);

        assertEquals("admin", subject);
    }

}