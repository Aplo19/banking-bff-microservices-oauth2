package com.banco.producto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos_financieros")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoFinanciero {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;
    
    @Column(name = "codigo_unico_cliente", nullable = false)
    private String codigoUnicoCliente;
    
    @Column(name = "tipo_producto", nullable = false)
    private String tipoProducto;
    
    @Column(name = "nombre_producto", nullable = false)
    private String nombreProducto;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal saldo;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}