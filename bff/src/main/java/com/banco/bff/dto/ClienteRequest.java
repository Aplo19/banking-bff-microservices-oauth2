package com.banco.bff.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClienteRequest {
    @NotBlank(message = "El código único es obligatorio")
    private String codigoUnico;
    
    private String traceId;
    
    
    public ClienteRequest() {}
    
    public ClienteRequest(String codigoUnico, String traceId) {
        this.codigoUnico = codigoUnico;
        this.traceId = traceId;
    }
}