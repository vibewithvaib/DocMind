package org.docmind.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.docmind.backend.dto.AuthResponse;
import org.docmind.backend.dto.LoginRequest;
import org.docmind.backend.dto.RegisterRequest;
import org.docmind.backend.exception.UserAlreadyExistsException;
import org.docmind.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid
            @RequestBody
            RegisterRequest request
    ) throws UserAlreadyExistsException {

        String response =
                authService.register(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid
            @RequestBody
            LoginRequest request
    ) {

        AuthResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }
}
