package com.banco.producto.controller;

import com.banco.producto.dto.ProductoDTO;
import com.banco.producto.dto.ProductoRequest;
import com.banco.producto.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos Financieros", description = "API para gestión de productos financieros")
public class ProductoController {
    
    private final ProductoService productoService;
    
    @PostMapping("/buscar")
    @Operation(summary = "Buscar productos por código único de cliente")
    @ApiResponse(responseCode = "200", description = "Productos encontrados")
    @ApiResponse(responseCode = "401", description = "No autorizado")
    public ResponseEntity<List<ProductoDTO>> buscarProductos(
            @Valid @RequestBody ProductoRequest request,
            @RequestHeader(value = "X-Trace-Id", required = false)
            @Parameter(description = "ID para tracking de la solicitud") String traceId) {
        
        if (traceId != null) {
            request.setTraceId(traceId);
        }
        
        List<ProductoDTO> productos = productoService.obtenerProductosPorCliente(request);
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Buscar productos por tipo")
    public ResponseEntity<List<ProductoDTO>> buscarPorTipo(
            @PathVariable String tipo,
            @RequestParam String codigoUnicoCliente,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {
        
        List<ProductoDTO> productos = productoService.obtenerProductosPorTipo(
                codigoUnicoCliente, tipo, traceId != null ? traceId : "NO_TRACE");
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/saldo-total")
    @Operation(summary = "Calcular saldo total de productos")
    public ResponseEntity<BigDecimal> calcularSaldoTotal(
            @RequestParam String codigoUnicoCliente) {
        
        return productoService.calcularSaldoTotal(codigoUnicoCliente)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(BigDecimal.ZERO));
    }
}