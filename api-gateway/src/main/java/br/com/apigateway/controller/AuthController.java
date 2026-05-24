package br.com.apigateway.controller;

import br.com.apigateway.dto.auth.LoginDto;
import br.com.apigateway.dto.auth.RecoveryJwtTokenDto;
import br.com.apigateway.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Realiza login e retorna um JWT")
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso")
    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDto> authenticateUser(@RequestBody LoginDto loginUserDto) {
        RecoveryJwtTokenDto token = authService.authenticateUser(loginUserDto);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

}
