package com.banco.cliente.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.banco.cliente.dto.ClienteDTO;
import com.banco.cliente.dto.ClienteRequest;
import com.banco.cliente.mapper.ClienteMapper;
import com.banco.cliente.model.Cliente;
import com.banco.cliente.repository.ClienteRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void obtenerClientePorCodigo_asignaTraceId_yFiltraValidos() {
        ClienteRequest request = new ClienteRequest();
        request.setCodigoUnico("CLI001");
        request.setTraceId("TRACE-1");

        Cliente cliente = Cliente.builder()
            .codigoUnico("CLI001")
            .nombres("Juan")
            .apellidos("Perez")
            .tipoDocumento("DNI")
            .numeroDocumento("12345678")
            .build();

        ClienteDTO dto = new ClienteDTO();

        when(clienteRepository.buscarPorCodigoEncriptado("CLI001"))
            .thenReturn(Optional.of(cliente));
        when(clienteMapper.toDTO(cliente)).thenReturn(dto);

        Optional<ClienteDTO> result = clienteService.obtenerClientePorCodigo(request);

        assertTrue(result.isPresent());
        assertEquals("TRACE-1", result.get().getTraceId());
    }
}
