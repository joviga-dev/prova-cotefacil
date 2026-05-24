package br.com.apigateway.service.auth;

import br.com.apigateway.entity.User;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.Test;
import static br.com.apigateway.factory.TestFactory.*;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceTest {

    private final JwtTokenService service = new JwtTokenService();

    @Test
    void deveGerarTokenComSucesso() {

        User user = criarUsuarioValido();

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        String token = service.generateToken(userDetails);

        assertNotNull(token);

        assertFalse(token.isBlank());
    }

    @Test
    void deveRetornarSubjectDoToken() {

        User user = criarUsuarioValido();

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        String token = service.generateToken(userDetails);

        String subject = service.getSubjectFromToken(token);

        assertEquals("usuario", subject);
    }

    @Test
    void deveLancarExcecaoParaTokenInvalido() {
        assertThrows(JWTVerificationException.class, () -> service.getSubjectFromToken("token-invalido"));
    }

    @Test
    void deveLancarExcecaoParaTokenVazio() {
        assertThrows(JWTVerificationException.class, () -> service.getSubjectFromToken(""));
    }

    @Test
    void deveLancarExcecaoParaTokenNulo() {
        assertThrows(JWTVerificationException.class, () -> service.getSubjectFromToken(null));
    }

}