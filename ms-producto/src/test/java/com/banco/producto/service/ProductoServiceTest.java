package com.banco.producto.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.banco.producto.dto.ProductoDTO;
import com.banco.producto.dto.ProductoRequest;
import com.banco.producto.mapper.ProductoMapper;
import com.banco.producto.model.ProductoFinanciero;
import com.banco.producto.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void obtenerProductosPorCliente_filtraInvalidos_yAsignaTraceId() {
        ProductoRequest request = new ProductoRequest();
        request.setCodigoUnicoCliente("CLI001");
        request.setTraceId("TRACE-1");

        ProductoFinanciero valido = ProductoFinanciero.builder()
            .codigoUnicoCliente("CLI001")
            .tipoProducto("CUENTA_AHORROS")
            .nombreProducto("Ahorro Plus")
            .saldo(BigDecimal.valueOf(1000))
            .build();

        ProductoFinanciero invalido = ProductoFinanciero.builder()
            .codigoUnicoCliente("")
            .build();

        when(productoRepository.buscarPorCodigoEncriptado("CLI001"))
            .thenReturn(List.of(valido, invalido));

        ProductoDTO dto = new ProductoDTO();
        when(productoMapper.toDTO(valido)).thenReturn(dto);

        List<ProductoDTO> resultado = productoService.obtenerProductosPorCliente(request);

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getTraceId().equals("TRACE-1"));
    }
}
