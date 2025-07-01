package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.dto.VendasDTO;
import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.enums.StatusPedido;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByStatus(StatusPedido status);

    List<Pedido> findTop10ByOrderByDataPedidoDesc();

    List<Pedido> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);

    /*Query para obter o total de vendas por restaurante Utilizando DTO para retornar apenas os campos necessários */
    @Query("SELECT new com.deliverytech.delivery_api.dto.VendasDTO(r.nome, SUM(p.valorTotal)) " +
           "FROM Pedido p JOIN Restaurante r ON p.restauranteId = r.id " +
           "GROUP BY r.nome")
    List<VendasDTO> findTotalVendasPorRestaurante();

    @Query("SELECT p FROM Pedido p WHERE p.valorTotal > :valorMinimo")
    List<Pedido> findPedidosComValorAcimaDe(@Param("valorMinimo") BigDecimal valorMinimo);

    @Query("SELECT p FROM Pedido p WHERE p.dataPedido BETWEEN :inicio AND :fim AND p.status = :status")
    List<Pedido> findRelatorioPorPeriodoEStatus(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("status") StatusPedido status
    );

}