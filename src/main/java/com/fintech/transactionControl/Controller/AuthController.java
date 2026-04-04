package com.fintech.transactionControl.Controller;

import com.fintech.transactionControl.DTOs.LoginDTO;
import com.fintech.transactionControl.DTOs.UserRequestDTO;
import com.fintech.transactionControl.DTOs.UserResponseDTO;
import com.fintech.transactionControl.Services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fintech.transactionControl.DTOs.UserResponseDTO;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/finApk/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService=authService;
    }

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody @Validated UserRequestDTO userRequestDTOBody){
        return authService.Register(userRequestDTOBody);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> Login (@RequestBody @Validated LoginDTO userBody){
        return authService.Login(userBody);
    }
}
