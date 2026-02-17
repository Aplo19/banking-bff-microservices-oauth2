package com.banco.bff.controller;

import com.banco.bff.dto.ClienteProductoResponse;
import com.banco.bff.dto.EncryptedRequest;
import com.banco.bff.service.ClienteProductoService;
import com.banco.bff.service.EncryptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/integracion")
@RequiredArgsConstructor
@Tag(name = "BFF Integracion", description = "API BFF para integracion de microservicios")
public class BffController {

    private final ClienteProductoService clienteProductoService;
    private final EncryptionService encryptionService;

    @PostMapping("/cliente-productos")
    @Operation(summary = "Obtener cliente y productos con codigo encriptado",
               description = "Recibe codigoUnico encriptado y devuelve cliente + productos")
    public Mono<ResponseEntity<ClienteProductoResponse>> obtenerClienteConProductos(
            @Valid @RequestBody EncryptedRequest request,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {

        log.info("BFF - Solicitud ENCRIPTADA recibida");

        if (!encryptionService.isEncrypted(request.getCodigoUnico())) {
            log.warn("Solicitud rechazada: codigoUnico no esta encriptado");
            return Mono.just(ResponseEntity.badRequest().build());
        }

        try {
            String codigoDesencriptado = encryptionService.decrypt(request.getCodigoUnico());
            log.info("Codigo desencriptado: {}", codigoDesencriptado);

            String traceIdFinal = request.getTraceId() != null ? request.getTraceId() :
                                 (traceId != null ? traceId : "NO_TRACE");

            return clienteProductoService.obtenerClienteConProductos(codigoDesencriptado, traceIdFinal)
                    .map(ResponseEntity::ok)
                    .onErrorResume(e -> {
                        log.error("Error en servicio: {}", e.getMessage());
                        return Mono.just(ResponseEntity.badRequest().build());
                    });

        } catch (Exception e) {
            log.error("Error desencriptando: {}", e.getMessage());
            return Mono.just(ResponseEntity.badRequest().build());
        }
    }

    @GetMapping("/debug-encryption")
    public ResponseEntity<Map<String, Object>> debugEncryption() {
        Map<String, Object> response = new HashMap<>();

        try {
            response.put("service", "Encryption Debug");
            response.put("timestamp", java.time.LocalDateTime.now().toString());

            String testText = "CLI001";
            String encrypted = encryptionService.encrypt(testText);
            String decrypted = encryptionService.decrypt(encrypted);

            response.put("testText", testText);
            response.put("encrypted", encrypted);
            response.put("decrypted", decrypted);
            response.put("success", testText.equals(decrypted));
            response.put("isEncryptedTest", encryptionService.isEncrypted(encrypted));
            response.put("isEncryptedPlain", encryptionService.isEncrypted(testText));
            response.put("newKeyExample", EncryptionService.generateNewKey());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getName());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/encriptar")
    @Operation(summary = "Encriptar codigo unico (solo desarrollo)")
    public ResponseEntity<String> encriptarCodigo(@RequestParam String codigoUnico) {
        try {
            String encriptado = encryptionService.encrypt(codigoUnico);
            log.info("Encriptado: '{}' -> '{}'", codigoUnico, encriptado);
            return ResponseEntity.ok(encriptado);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/desencriptar")
    @Operation(summary = "Desencriptar codigo (solo desarrollo)")
    public ResponseEntity<String> desencriptarCodigo(@RequestParam String textoEncriptado) {
        try {
            String desencriptado = encryptionService.decrypt(textoEncriptado);
            log.info("Desencriptado: '{}' -> '{}'", textoEncriptado, desencriptado);
            return ResponseEntity.ok(desencriptado);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("BFF esta funcionando correctamente");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("BFF API Test - " + java.time.LocalDateTime.now());
    }

    @GetMapping("/debug/token")
    public ResponseEntity<?> debugToken(org.springframework.security.core.Authentication authentication) {
        Map<String, Object> debug = new HashMap<>();
        debug.put("authorities", authentication.getAuthorities());
        debug.put("principal", authentication.getPrincipal());
        debug.put("details", authentication.getDetails());
        return ResponseEntity.ok(debug);
    }
}
