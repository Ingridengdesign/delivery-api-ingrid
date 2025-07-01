package com.deliverytech.delivery_api.dto;

import java.math.BigDecimal;

// Esta classe não é uma entidade, é apenas um "transportador" de dados.
// Usamos um 'record' para uma sintaxe mais enxuta, mas uma classe normal com construtor, getters e setters também funciona.
public record VendasDTO(
    String nomeRestaurante,
    BigDecimal totalVendas
) {
}