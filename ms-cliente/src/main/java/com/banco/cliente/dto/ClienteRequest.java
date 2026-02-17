package com.banco.cliente.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClienteRequest {
    @NotBlank(message = "El código único es obligatorio")
    private String codigoUnico;
    
    private String traceId;
}