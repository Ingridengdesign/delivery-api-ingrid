package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.projection.ClienteResumoProjection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);
    
    Optional<Cliente> findByTelefone(String telefone);

    List<Cliente> findByAtivoTrue();

    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    boolean existsByEmail(String email);

    //projection

    @Query("SELECT c FROM Cliente c WHERE c.ativo = true")
    List<ClienteResumoProjection> findResumoDeClientesAtivos();

}