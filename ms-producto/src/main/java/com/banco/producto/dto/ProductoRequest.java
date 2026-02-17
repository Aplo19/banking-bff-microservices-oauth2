package com.banco.producto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductoRequest {
    @NotBlank(message = "El código único del cliente es obligatorio")
    private String codigoUnicoCliente;
    
    private String traceId;
    
    public ProductoRequest() {
        System.out.println("ProductoRequest constructor llamado");
    }
    
    public ProductoRequest(String codigoUnicoCliente, String traceId) {
        this.codigoUnicoCliente = codigoUnicoCliente;
        this.traceId = traceId;
        System.out.println("ProductoRequest creado con: " + codigoUnicoCliente);
    }
}