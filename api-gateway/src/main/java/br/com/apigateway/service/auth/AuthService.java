package br.com.apigateway.service.auth;

import br.com.apigateway.dto.auth.LoginDto;
import br.com.apigateway.dto.auth.RecoveryJwtTokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    /**
     * Realiza a autenticação do usuário e retorna um token JWT.
     *
     * @param loginUserDto DTO contendo username e password do usuário
     * @return token JWT gerado para o usuário autenticado
     */
    public RecoveryJwtTokenDto authenticateUser(LoginDto loginUserDto) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.username(),
                        loginUserDto.password()
                );

        Authentication authentication =
                authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        UserDetailsImpl userDetails =
                (UserDetailsImpl) authentication.getPrincipal();

        return new RecoveryJwtTokenDto(jwtTokenService.generateToken(userDetails));
    }

}