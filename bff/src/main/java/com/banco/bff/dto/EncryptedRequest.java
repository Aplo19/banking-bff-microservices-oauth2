package com.banco.bff.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedRequest {
    @NotBlank(message = "El codigo unico encriptado es obligatorio")
    private String codigoUnico;

    private String traceId;
}
