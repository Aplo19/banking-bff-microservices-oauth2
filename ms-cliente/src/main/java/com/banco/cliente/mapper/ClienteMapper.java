package com.banco.cliente.mapper;

import com.banco.cliente.model.Cliente;
import com.banco.cliente.dto.ClienteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);
    
    ClienteDTO toDTO(Cliente cliente);
    
    Cliente toEntity(ClienteDTO clienteDTO);
}