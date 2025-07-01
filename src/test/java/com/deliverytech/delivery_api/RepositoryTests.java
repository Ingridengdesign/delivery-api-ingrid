package com.deliverytech.delivery_api;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
public class RepositoryTests {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Test
    @DisplayName("Cenário 1: Deve buscar um cliente por seu email")
    void deveBuscarClientePorEmail() {
        
        Cliente novoCliente = new Cliente(null, "João Silva", "joao@email.com", "999999999", "Rua Teste", LocalDateTime.now(), true);
        clienteRepository.save(novoCliente);


        Optional<Cliente> clienteEncontrado = clienteRepository.findByEmail("joao@email.com");

        assertThat(clienteEncontrado).isPresent(); 
        assertThat(clienteEncontrado.get().getNome()).isEqualTo("João Silva"); 
    }

    @Test
    @DisplayName("Cenário 2: Deve buscar a lista de produtos de um restaurante específico")
    void deveBuscarProdutosPorRestaurante() {

        Restaurante restaurante = restauranteRepository.save(new Restaurante(null, "Restaurante Teste", "Brasileira", "Endereço", "123", BigDecimal.TEN, BigDecimal.ONE, true));
        produtoRepository.save(new Produto(null, "Produto A", "Desc A", BigDecimal.ONE, "Cat A", true, restaurante.getId()));
        produtoRepository.save(new Produto(null, "Produto B", "Desc B", BigDecimal.TEN, "Cat B", true, restaurante.getId()));

        List<Produto> produtos = produtoRepository.findByRestauranteId(restaurante.getId());

        assertThat(produtos).isNotNull();
        assertThat(produtos).hasSize(2); 

        assertThat(produtos.get(0).getNome()).isEqualTo("Produto A");
    }

    @Test
    @DisplayName("Cenário 3: Deve buscar os 10 pedidos mais recentes")
    void deveBuscarPedidosRecentes() {
       
        Cliente cliente = clienteRepository.save(new Cliente(null, "Cliente Pedido", "cli@ped.com", "123", "Rua", LocalDateTime.now(), true));
        Restaurante restaurante = restauranteRepository.save(new Restaurante(null, "Restaurante Pedido", "Cat", "End", "123", BigDecimal.ONE, BigDecimal.ONE, true));


        pedidoRepository.save(new Pedido(null, "PED-001", LocalDateTime.now().minusDays(1), StatusPedido.ENTREGUE, BigDecimal.ONE, "", cliente.getId(), restaurante.getId()));
        Pedido pedidoRecente = pedidoRepository.save(new Pedido(null, "PED-002", LocalDateTime.now(), StatusPedido.PENDENTE, BigDecimal.TEN, "", cliente.getId(), restaurante.getId()));
        pedidoRepository.save(new Pedido(null, "PED-003", LocalDateTime.now().minusHours(5), StatusPedido.ENTREGUE, BigDecimal.TEN, "", cliente.getId(), restaurante.getId()));


        List<Pedido> pedidos = pedidoRepository.findTop10ByOrderByDataPedidoDesc();


        assertThat(pedidos).hasSize(3); // Temos 3 pedidos no total
        assertThat(pedidos.get(0).getNumeroPedido()).isEqualTo(pedidoRecente.getNumeroPedido()); 
    }

    @Test
    @DisplayName("Cenário 4: Deve buscar restaurantes com taxa de entrega até R$ 5,00")
    void deveBuscarRestaurantesPorTaxaDeEntrega() {
  
        restauranteRepository.save(new Restaurante(null, "Taxa Baixa", "Pizza", "Rua A", "1", new BigDecimal("4.99"), BigDecimal.ONE, true));
        restauranteRepository.save(new Restaurante(null, "Taxa OK", "Japa", "Rua B", "2", new BigDecimal("5.00"), BigDecimal.ONE, true));
        restauranteRepository.save(new Restaurante(null, "Taxa Alta", "Lanche", "Rua C", "3", new BigDecimal("5.01"), BigDecimal.ONE, true));


        List<Restaurante> restaurantes = restauranteRepository.findByTaxaEntregaLessThanEqual(new BigDecimal("5.00"));

        assertThat(restaurantes).hasSize(2); 
        assertThat(restaurantes).noneMatch(restaurante -> restaurante.getNome().equals("Taxa Alta"));
    }
}