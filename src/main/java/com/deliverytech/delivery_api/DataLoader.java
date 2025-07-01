package com.deliverytech.delivery_api;

import com.deliverytech.delivery_api.entity.*;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.projection.ClienteResumoProjection;
import com.deliverytech.delivery_api.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component // Anotação para que o Spring gerencie esta classe
public class DataLoader implements CommandLineRunner {

    // Injeção de todos os repositórios necessários
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;

    public DataLoader(ClienteRepository clienteRepository, RestauranteRepository restauranteRepository,
                      ProdutoRepository produtoRepository, PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("----------------------------------------------------");
        System.out.println("🚀 INICIANDO DATALOADER: INSERINDO DADOS DE TESTE...");
        System.out.println("----------------------------------------------------");

        carregarDados();
        testarConsultas();

        System.out.println("----------------------------------------------------");
        System.out.println("✅ DATALOADER FINALIZADO.");
        System.out.println("----------------------------------------------------");
    }

    private void carregarDados() {
        System.out.println("\n--- 💾 2.1 Inserindo dados de teste ---");

        // 1. Criando 3 Clientes
        Cliente cli1 = new Cliente(null, "Ana Carolina", "ana.c@email.com", "11988887777", "Rua das Flores, 123", LocalDateTime.now(), true);
        Cliente cli2 = new Cliente(null, "Bruno Alves", "bruno.a@email.com", "11977776666", "Av. Paulista, 1000", LocalDateTime.now(), true);
        Cliente cli3 = new Cliente(null, "Carla Dias", "carla.d@email.com", "11966665555", "Rua Augusta, 500", LocalDateTime.now(), false); // Cliente inativo
        clienteRepository.saveAll(Arrays.asList(cli1, cli2, cli3));
        System.out.println("✓ 3 Clientes inseridos.");

        // 2. Criando 2 Restaurantes
        Restaurante rest1 = new Restaurante(null, "Pizzaria do Zé", "Pizza", "Rua da Mooca, 456", "1122223333", new BigDecimal("5.00"), new BigDecimal("4.5"), true);
        Restaurante rest2 = new Restaurante(null, "Sushi House", "Japonesa", "Rua da Liberdade, 789", "1133334444", new BigDecimal("12.50"), new BigDecimal("4.8"), true);
        restauranteRepository.saveAll(Arrays.asList(rest1, rest2));
        System.out.println("✓ 2 Restaurantes inseridos.");

        // 3. Criando 5 Produtos (associados aos restaurantes)
        Produto p1 = new Produto(null, "Pizza de Calabresa", "Mussarela, calabresa e cebola", new BigDecimal("45.90"), "Pizza Salgada", true, rest1.getId());
        Produto p2 = new Produto(null, "Pizza de Mussarela", "Mussarela e orégano", new BigDecimal("42.00"), "Pizza Salgada", true, rest1.getId());
        Produto p3 = new Produto(null, "Combinado Salmão (20 peças)", "Sashimi, niguiri e uramaki", new BigDecimal("89.90"), "Combinado", true, rest2.getId());
        Produto p4 = new Produto(null, "Temaki Filadélfia", "Salmão, cream cheese e cebolinha", new BigDecimal("28.50"), "Temaki", true, rest2.getId());
        Produto p5 = new Produto(null, "Coca-Cola 2L", "Refrigerante", new BigDecimal("10.00"), "Bebidas", false, rest1.getId()); // Produto indisponível
        produtoRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));
        System.out.println("✓ 5 Produtos inseridos.");

        // 4. Criando 2 Pedidos
        // ⚠️ ATENÇÃO: Seus pedidos não têm uma lista de itens (produtos).
        // Estou criando o pedido com os dados que a entidade permite.
        Pedido ped1 = new Pedido(null, "PED-001", LocalDateTime.now().minusHours(1), StatusPedido.ENTREGUE, new BigDecimal("55.90"), "Entregar na portaria", cli1.getId(), rest1.getId());
        Pedido ped2 = new Pedido(null, "PED-002", LocalDateTime.now(), StatusPedido.SAIU_PARA_ENTREGA, new BigDecimal("118.40"), "Caprichar no wasabi!", cli2.getId(), rest2.getId());
        pedidoRepository.saveAll(Arrays.asList(ped1, ped2));
        System.out.println("✓ 2 Pedidos inseridos.");
    }

    private void testarConsultas() {
        System.out.println("\n--- 🧪 2.2 Validando as consultas ---");

        // Testes do ClienteRepository
        System.out.println("\n[CLIENTE REPOSITORY]");
        System.out.println("findByEmail('bruno.a@email.com'): " + clienteRepository.findByEmail("bruno.a@email.com").orElse(null));
        System.out.println("findByAtivoTrue(): " + clienteRepository.findByAtivoTrue().size() + " clientes ativos encontrados.");
        System.out.println("findByNomeContainingIgnoreCase('carol'): " + clienteRepository.findByNomeContainingIgnoreCase("carol"));
        System.out.println("existsByEmail('ana.c@email.com'): " + clienteRepository.existsByEmail("ana.c@email.com"));
        System.out.println("existsByEmail('nao.existe@email.com'): " + clienteRepository.existsByEmail("nao.existe@email.com"));

        // Testes do RestauranteRepository
        System.out.println("\n[RESTAURANTE REPOSITORY]");
        System.out.println("findByCategoria('Pizza'): " + restauranteRepository.findByCategoria("Pizza"));
        System.out.println("findByAtivoTrue(): " + restauranteRepository.findByAtivoTrue().size() + " restaurantes ativos encontrados.");
        System.out.println("findByTaxaEntregaLessThanEqual(10.00): " + restauranteRepository.findByTaxaEntregaLessThanEqual(new BigDecimal("10.00")));
        System.out.println("findTop5ByOrderByNomeAsc(): " + restauranteRepository.findTop5ByOrderByNomeAsc());

        // Testes do ProdutoRepository
        System.out.println("\n[PRODUTO REPOSITORY]");
        Long primeiroRestauranteId = restauranteRepository.findTop5ByOrderByNomeAsc().get(0).getId();
        System.out.println("findByRestauranteId(" + primeiroRestauranteId + "): " + produtoRepository.findByRestauranteId(primeiroRestauranteId).size() + " produtos encontrados.");
        System.out.println("findByDisponivelTrue(): " + produtoRepository.findByDisponivelTrue().size() + " produtos disponíveis.");
        System.out.println("findByCategoria('Temaki'): " + produtoRepository.findByCategoria("Temaki"));
        System.out.println("findByPrecoLessThanEqual(30.00): " + produtoRepository.findByPrecoLessThanEqual(new BigDecimal("30.00")));

        // Testes do PedidoRepository
        System.out.println("\n[PEDIDO REPOSITORY]");
        Long primeiroClienteId = clienteRepository.findByAtivoTrue().get(0).getId();
        System.out.println("findByClienteId(" + primeiroClienteId + "): " + pedidoRepository.findByClienteId(primeiroClienteId));
        System.out.println("findByStatus(A_CAMINHO): " + pedidoRepository.findByStatus(StatusPedido.SAIU_PARA_ENTREGA));
        System.out.println("findTop10ByOrderByDataPedidoDesc(): " + pedidoRepository.findTop10ByOrderByDataPedidoDesc());
        System.out.println("findByDataPedidoBetween(ontem, amanhã): " + pedidoRepository.findByDataPedidoBetween(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)));
        
        // Testes de consultas com @Query
        System.out.println("\n--- 🧪 3.1 Testando consultas com @Query ---");

        System.out.println("\n[PEDIDO REPOSITORY - @Query]");

        // Teste 1: Total de vendas por restaurante
        System.out.println("findTotalVendasPorRestaurante(): " + pedidoRepository.findTotalVendasPorRestaurante());

        // Teste 2: Pedidos com valor acima de 100
        System.out.println("findPedidosComValorAcimaDe(100.00): " + pedidoRepository.findPedidosComValorAcimaDe(new BigDecimal("100.00")));

        // Teste 3: Relatório de pedidos entregues hoje
        System.out.println("findRelatorioPorPeriodoEStatus(hoje, ENTREGUE): " +
                pedidoRepository.findRelatorioPorPeriodoEStatus(
                        LocalDateTime.now().withHour(0).withMinute(0), // Início do dia de hoje
                        LocalDateTime.now().withHour(23).withMinute(59), // Fim do dia de hoje
                        StatusPedido.ENTREGUE
                )
        );

        //projection
        System.out.println("\n--- 🧩 3.3 Testando consultas com Projeções ---");
        System.out.println("\n[CLIENTE REPOSITORY - Projections]");
        
        List<ClienteResumoProjection> resumoClientes = clienteRepository.findResumoDeClientesAtivos();


    }
    
}