package com.banco.cliente.controller;

import com.banco.cliente.dto.ClienteDTO;
import com.banco.cliente.dto.ClienteRequest;
import com.banco.cliente.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/clientes") 
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "API para gestión de clientes")
public class ClienteController {
    
    private final ClienteService clienteService;
    
    @GetMapping("/test")
    public String test() {
        return "ms-cliente funciona! " + LocalDateTime.now();
    }
    
    @PostMapping("/buscar")
    @Operation(summary = "Buscar cliente por código único encriptado")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public ResponseEntity<ClienteDTO> buscarCliente(
            @Valid @RequestBody ClienteRequest request,
            @RequestHeader(value = "X-Trace-Id", required = false)
            @Parameter(description = "ID para tracking de la solicitud") String traceId) {
        
        if (traceId != null) {
            request.setTraceId(traceId);
        }
        
        return clienteService.obtenerClientePorCodigo(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}