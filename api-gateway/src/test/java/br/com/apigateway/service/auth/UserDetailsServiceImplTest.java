package br.com.apigateway.service.auth;

import br.com.apigateway.entity.Role;
import br.com.apigateway.entity.User;
import br.com.apigateway.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static br.com.apigateway.factory.TestFactory.criarUsuarioValido;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl service;

    @Mock
    private UserRepository userRepository;

    @Test
    void deveCarregarUsuarioPorUsername() {

        User user = criarUsuarioValido();

        when(userRepository.findByUsername("usuario"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                service.loadUserByUsername("usuario");

        assertNotNull(userDetails);

        assertEquals(
                "usuario",
                userDetails.getUsername()
        );
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {

        when(userRepository.findByUsername("usuario"))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> service.loadUserByUsername("usuario")
        );
    }
}