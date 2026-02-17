package com.banco.cliente.service;

import com.banco.cliente.dto.ClienteDTO;
import com.banco.cliente.dto.ClienteRequest;
import com.banco.cliente.mapper.ClienteMapper;
import com.banco.cliente.model.Cliente;
import com.banco.cliente.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {
    
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    
    private final Predicate<Cliente> clienteValidoPredicate = cliente ->
            cliente != null && 
            cliente.getCodigoUnico() != null && 
            !cliente.getCodigoUnico().isEmpty();
    
    public Optional<ClienteDTO> obtenerClientePorCodigo(ClienteRequest request) {
        log.info("Buscando cliente - traceId: {}", request.getTraceId());
        
        return clienteRepository.buscarPorCodigoEncriptado(request.getCodigoUnico())
                .filter(clienteValidoPredicate)
                .map(cliente -> {
                    ClienteDTO dto = clienteMapper.toDTO(cliente);
                    dto.setTraceId(request.getTraceId());
                    return dto;
                });
    }
}