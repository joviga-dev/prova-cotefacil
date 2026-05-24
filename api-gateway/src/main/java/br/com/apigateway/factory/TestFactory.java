package br.com.apigateway.factory;

import br.com.apigateway.dto.auth.LoginDto;
import br.com.apigateway.entity.Role;
import br.com.apigateway.entity.User;
import br.com.apigateway.enums.RoleName;

import java.util.List;

public class TestFactory {

    public static LoginDto loginValido() {
        return new LoginDto(
                "usuario",
                "senha123"
        );
    }

    public static User criarUsuarioValido() {

        User user = new User();

        user.setId(1L);
        user.setUsername("usuario");
        user.setPassword(
                "$2a$10$4l4GaRfl/g9SzPTMMFyAv.3mc0/dgRkWvgwD.qw6tHOlBom1sKrXy"
        );
        user.setRoles(List.of(new Role(1L, RoleName.USER)));

        return user;
    }
}