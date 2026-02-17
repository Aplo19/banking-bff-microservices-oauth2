package com.banco.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "Auth Public", description = "Endpoints públicos del Auth Server")
public class AuthController {
    
    @GetMapping("/health")
    @Operation(summary = "Health check del Auth Server")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "auth-server",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
    
    @GetMapping("/info")
    @Operation(summary = "Información del Auth Server")
    public ResponseEntity<Map<String, String>> info() {
        return ResponseEntity.ok(Map.of(
            "issuer", "http://auth-server:9000",
            "token_endpoint", "http://auth-server:9000/oauth2/token",
            "jwks_endpoint", "http://auth-server:9000/oauth2/jwks",
            "authorization_endpoint", "http://auth-server:9000/oauth2/authorize"
        ));
    }
}