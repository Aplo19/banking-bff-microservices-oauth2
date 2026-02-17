package com.banco.producto.repository;

import com.banco.producto.model.ProductoFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoFinanciero, Long> {
    
    List<ProductoFinanciero> findByCodigoUnicoCliente(String codigoUnicoCliente);
    
    Optional<List<ProductoFinanciero>> findByClienteId(Long clienteId);
    
    default List<ProductoFinanciero> buscarPorCodigoEncriptado(String codigoEncriptado) {
        String codigoDesencriptado = desencriptarCodigo(codigoEncriptado);
        return findByCodigoUnicoCliente(codigoDesencriptado);
    }
    
    private String desencriptarCodigo(String codigoEncriptado) {
       
        return codigoEncriptado;
    }
}