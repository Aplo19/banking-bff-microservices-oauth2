package com.banco.bff.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación OAuth2", description = "API para validación de tokens OAuth2")
public class OAuth2Controller {
    
    @GetMapping("/validate")
    @Operation(summary = "Validar token actual", description = "Verifica si el token JWT OAuth2 es válido")
    public Map<String, Object> validateToken(@AuthenticationPrincipal Jwt jwt) {
        log.info("Token OAuth2 válido para usuario: {}", jwt.getSubject());
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("username", jwt.getSubject());
        response.put("issuer", jwt.getIssuer());
        response.put("issuedAt", jwt.getIssuedAt());
        response.put("expiresAt", jwt.getExpiresAt());
        response.put("scopes", jwt.getClaimAsString("scope"));
        response.put("tokenType", "Bearer");
        response.put("timestamp", LocalDateTime.now().toString());
        
        return response;
    }
    
    @GetMapping("/me")
    @Operation(summary = "Información del usuario autenticado", description = "Obtiene información del usuario desde el token JWT")
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        log.info("👤 Usuario autenticado: {}", jwt.getSubject());
        
        Map<String, Object> response = new HashMap<>();
        response.put("username", jwt.getSubject());
        response.put("userId", jwt.getClaimAsString("sub"));
        response.put("fullName", jwt.getClaimAsString("name"));
        response.put("email", jwt.getClaimAsString("email"));
        response.put("roles", jwt.getClaimAsStringList("roles"));
        response.put("scopes", jwt.getClaimAsString("scope"));
        response.put("allClaims", jwt.getClaims());
        
        return response;
    }
    
    @GetMapping("/token-info")
    @Operation(summary = "Información técnica del token", description = "Muestra información técnica del token JWT")
    public Map<String, Object> getTokenInfo(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
            "subject", jwt.getSubject(),
            "issuer", jwt.getIssuer(),
            "audience", jwt.getAudience(),
            "issuedAt", jwt.getIssuedAt(),
            "expiresAt", jwt.getExpiresAt(),
            "headers", jwt.getHeaders(),
            "algorithm", jwt.getHeaders().get("alg"),
            "tokenType", jwt.getHeaders().get("typ")
        );
    }
}