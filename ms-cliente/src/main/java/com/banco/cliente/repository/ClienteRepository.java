package com.banco.cliente.repository;

import com.banco.cliente.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByCodigoUnico(String codigoUnico);
    
    default Optional<Cliente> buscarPorCodigoEncriptado(String codigoEncriptado) {
        String codigoDesencriptado = desencriptar(codigoEncriptado);
        return findByCodigoUnico(codigoDesencriptado);
    }
    
    private String desencriptar(String codigoEncriptado) {
     
        return codigoEncriptado;
    }
}