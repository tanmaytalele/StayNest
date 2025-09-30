package com.tnt.StayNest.controller;

import com.tnt.StayNest.dto.LoginRequest;
import com.tnt.StayNest.dto.RegisterRequest;
import com.tnt.StayNest.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/{role}")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request,@PathVariable String role) {
        authService.register(request,role);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
