package com.banco.producto.mapper;

import com.banco.producto.model.ProductoFinanciero;
import com.banco.producto.dto.ProductoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    ProductoMapper INSTANCE = Mappers.getMapper(ProductoMapper.class);
    
    @Mapping(source = "tipoProducto", target = "tipoProducto")
    @Mapping(source = "nombreProducto", target = "nombreProducto")
    @Mapping(source = "saldo", target = "saldo")
    ProductoDTO toDTO(ProductoFinanciero producto);
    
    ProductoFinanciero toEntity(ProductoDTO productoDTO);
}