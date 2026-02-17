package com.banco.producto.service;

import com.banco.producto.dto.ProductoDTO;
import com.banco.producto.dto.ProductoRequest;
import com.banco.producto.mapper.ProductoMapper;
import com.banco.producto.model.ProductoFinanciero;
import com.banco.producto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {
    
    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    
    @FunctionalInterface
    public interface ProductoTransformer extends Function<ProductoFinanciero, ProductoDTO> {
    }
    
    // Predicate para validar productos
    private final Predicate<ProductoFinanciero> productoValidoPredicate = producto ->
            producto != null && 
            producto.getCodigoUnicoCliente() != null && 
            !producto.getCodigoUnicoCliente().isEmpty();
    
    public List<ProductoDTO> obtenerProductosPorCliente(ProductoRequest request) {
        log.info("Buscando productos para cliente - traceId: {}", request.getTraceId());
        
        // Crear transformer usando lambda
        ProductoTransformer transformer = producto -> {
            ProductoDTO dto = productoMapper.toDTO(producto);
            dto.setTraceId(request.getTraceId());
            return dto;
        };
        
        return productoRepository.buscarPorCodigoEncriptado(request.getCodigoUnicoCliente())
                .stream()
                .filter(productoValidoPredicate)
                .map(transformer)  
                .collect(Collectors.toList());
    }
    
    
    public List<ProductoDTO> obtenerProductosPorTipo(String codigoUnicoCliente, String tipoProducto, String traceId) {
        log.info("Buscando productos por tipo: {} para cliente: {}", tipoProducto, codigoUnicoCliente);
        
        // Predicate para filtrar por tipo de producto
        Predicate<ProductoFinanciero> tipoProductoPredicate = 
                producto -> tipoProducto.equalsIgnoreCase(producto.getTipoProducto());
        
        // Crear transformer
        ProductoTransformer transformer = producto -> {
            ProductoDTO dto = productoMapper.toDTO(producto);
            dto.setTraceId(traceId);
            return dto;
        };
        
        return productoRepository.findByCodigoUnicoCliente(codigoUnicoCliente)
                .stream()
                .filter(productoValidoPredicate)
                .filter(tipoProductoPredicate)
                .map(transformer)
                .collect(Collectors.toList());
    }
    
    public Optional<BigDecimal> calcularSaldoTotal(String codigoUnicoCliente) {
        return Optional.ofNullable(codigoUnicoCliente)
                .filter(codigo -> !codigo.isEmpty())
                .map(productoRepository::findByCodigoUnicoCliente)
                .map(productos -> productos.stream()
                        .map(ProductoFinanciero::getSaldo)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .filter(saldoTotal -> saldoTotal.compareTo(BigDecimal.ZERO) > 0);
    }
    
    private String desencriptarCodigo(String codigoEncriptado) {
       
        log.debug("Desencriptando código: {}", codigoEncriptado);
        return codigoEncriptado;
    }
    
    public List<ProductoDTO> obtenerProductosConSaldoMayorA(
            String codigoUnicoCliente, BigDecimal saldoMinimo, String traceId) {
        
        Predicate<ProductoFinanciero> saldoPredicate = 
                producto -> producto.getSaldo().compareTo(saldoMinimo) > 0;
        
        ProductoTransformer transformer = producto -> {
            ProductoDTO dto = productoMapper.toDTO(producto);
            dto.setTraceId(traceId);
            return dto;
        };
        
        return productoRepository.findByCodigoUnicoCliente(codigoUnicoCliente)
                .stream()
                .filter(productoValidoPredicate)
                .filter(saldoPredicate)
                .map(transformer)
                .collect(Collectors.toList());
    }
}