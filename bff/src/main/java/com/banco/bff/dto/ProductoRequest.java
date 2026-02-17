package com.banco.bff.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductoRequest {
    @NotBlank(message = "El código único del cliente es obligatorio")
    private String codigoUnicoCliente;  
    
    private String traceId;
}