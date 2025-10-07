package com.gestortarefas.config;

import com.gestortarefas.model.*;
import com.gestortarefas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Inicializador COMPLETO dos dados da empresa
 * Cria: 30 utilizadores, 8 equipas e 27 tarefas
 */
@Configuration
@Order(1)
public class RealUsersInitializer {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    CommandLineRunner initCompleteData() {
        return args -> {
            // PASSO 1: Criar utilizadores
            if (userRepository.count() > 0) {
                System.out.println("=== Dados já existem, saltando inicialização ===");
                return;
            }

            System.out.println("=== Criando 30 utilizadores reais da empresa ===");

            // ====== ADMINISTRADORES (3) ======
            createUser("martim.sottomayor", "martim.sottomayor123", "Martim Sottomayor", 
                      "martim.sottomayor@gestordetarefas.pt", UserRole.ADMINISTRADOR);
            createUser("catarina.balsemao", "catarina.balsemao123", "Catarina Balsemão", 
                      "catarina.balsemao@gestordetarefas.pt", UserRole.ADMINISTRADOR);
            createUser("admin.correia", "admin.correia123", "Admin Carlos Correia", 
                      "admin.correia@gestordetarefas.pt", UserRole.ADMINISTRADOR);

            // ====== GERENTES (7) ======
            createUser("lucile.almeida", "lucile.almeida123", "Lucile Almeida", 
                      "lucilealmeida@gestordetarefas.pt", UserRole.GERENTE);
            createUser("bessa.ribeiro", "bessa.ribeiro123", "Bessa Ribeiro", 
                      "bessaribeiro@gestordetarefas.pt", UserRole.GERENTE);
            createUser("diana.brochado", "diana.brochado123", "Diana Brochado", 
                      "dianabrochado@gestordetarefas.pt", UserRole.GERENTE);
            createUser("paulo.bessa", "paulo.bessa123", "Paulo Bessa", 
                      "paulobessa@gestordetarefas.pt", UserRole.GERENTE);
            createUser("pedro.lopes", "pedro.lopes123", "Pedro Lopes", 
                      "pedrolopes@gestordetarefas.pt", UserRole.GERENTE);
            createUser("antonio.nolasco", "antonio.nolasco123", "António Nolasco", 
                      "antonionolasco@gestordetarefas.pt", UserRole.GERENTE);
            createUser("rui.goncalves", "rui.goncalves123", "Rui Gonçalves", 
                      "ruigoncalves@gestordetarefas.pt", UserRole.GERENTE);

            // ====== FUNCIONÁRIOS (20) ======
            createUser("rita.almeida", "rita.almeida123", "Rita Almeida", 
                      "ritaalmeida@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("sandra.rocha", "sandra.rocha123", "Sandra Rocha", 
                      "sandrarocha@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("ricardo.leal", "ricardo.leal123", "Ricardo Leal", 
                      "ricardoleal@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("carla.silva", "carla.silva123", "Carla Silva", 
                      "carlasilva@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("melinda.szekely", "melinda.szekely123", "Melinda Szekely", 
                      "melindaszekely@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("tatiana.albuquerque", "tatiana.albuquerque123", "Tatiana Albuquerque", 
                      "tatianaalbuquerque@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("rita.oliveira", "rita.oliveira123", "Rita Oliveira", 
                      "ritaoliveira@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("ana.reis", "ana.reis123", "Ana Reis", 
                      "anareis@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("joao.couto", "joao.couto123", "João Couto", 
                      "joaocouto@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("ines.rodrigues", "ines.rodrigues123", "Inês Rodrigues", 
                      "inesrodrigues@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("teresa.correia", "teresa.correia123", "Teresa Correia", 
                      "teresacorreia@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("vania.lourenco", "vania.lourenco123", "Vânia Lourenço", 
                      "vanialourenco@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("anca.tusa", "anca.tusa123", "Anca Tusa", 
                      "ancatusa@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("rogerio.silva", "rogerio.silva123", "Rogério Silva", 
                      "rogeriosilva@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("tiago.rodrigues", "tiago.rodrigues123", "Tiago Rodrigues", 
                      "tiagorodrigues@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("mohammad.aldossari", "mohammad.aldossari123", "Mohammad Al-Dossari", 
                      "mohammadaldossari@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("vijay.kumar", "vijay.kumar123", "Vijay Kumar", 
                      "vijaykumar@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("sanita.rahman", "sanita.rahman123", "Sanita Rahman", 
                      "sanitarahman@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("monica.lewinsky", "monica.lewinsky123", "Mónica Lewinsky", 
                      "monicalewinsky@gestordetarefas.pt", UserRole.FUNCIONARIO);
            createUser("cristiana.oliveira", "cristiana.oliveira123", "Cristiana Oliveira", 
                      "cristianaoliveira@gestordetarefas.pt", UserRole.FUNCIONARIO);

            System.out.println("=== 30 utilizadores criados com sucesso! ===");
            System.out.println("Password padrão: username123");
            System.out.println("Exemplos:");
            System.out.println("  martim.sottomayor / martim.sottomayor123");
            System.out.println("  lucile.almeida / lucile.almeida123");
            System.out.println("  rita.almeida / rita.almeida123");
            
            // ============================================================
            // PASSO 2: CRIAR 8 EQUIPAS COM ATRIBUIÇÃO DE MEMBROS
            // ============================================================
            System.out.println("\n=== CRIANDO 8 EQUIPAS ===");
            List<User> allUsers = userRepository.findAll();
            List<Team> teams = createTestTeams(allUsers);
            System.out.println("✓ " + teams.size() + " equipas criadas com sucesso!");
            
            // ============================================================
            // PASSO 3: CRIAR 27 TAREFAS
            // ============================================================
            System.out.println("\n=== CRIANDO 27 TAREFAS ===");
            createTestTasks(allUsers, teams);
            System.out.println("✓ Tarefas criadas com sucesso!");
            
            System.out.println("\n========================================");
            System.out.println("✓✓✓ INICIALIZAÇÃO COMPLETA ✓✓✓");
            System.out.println("  - 30 utilizadores");
            System.out.println("  - 8 equipas com membros");
            System.out.println("  - 27 tarefas atribuídas");
            System.out.println("========================================");
        };
    }

    private void createUser(String username, String password, String fullName, String email, UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(role);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        System.out.println("✓ Criado: " + username + " (" + role + ")");
    }
    
    // ================================================================
    // MÉTODOS DE CRIAÇÃO DE EQUIPAS
    // ================================================================
    
    private List<Team> createTestTeams(List<User> users) {
        System.out.println("Criando 8 equipas da empresa de tubos de aço...");

        // Buscar utilizadores específicos por username
        User martim = findUserByUsername(users, "martim.sottomayor");
        User catarina = findUserByUsername(users, "catarina.balsemao");
        User lucile = findUserByUsername(users, "lucile.almeida");
        User bessa = findUserByUsername(users, "bessa.ribeiro");
        User diana = findUserByUsername(users, "diana.brochado");
        User paulo = findUserByUsername(users, "paulo.bessa");
        User pedro = findUserByUsername(users, "pedro.lopes");
        User antonio = findUserByUsername(users, "antonio.nolasco");
        User rui = findUserByUsername(users, "rui.goncalves");

        // Funcionários
        User rita = findUserByUsername(users, "rita.almeida");
        User sandra = findUserByUsername(users, "sandra.rocha");
        User ricardo = findUserByUsername(users, "ricardo.leal");
        User carla = findUserByUsername(users, "carla.silva");
        User melinda = findUserByUsername(users, "melinda.szekely");
        User tatiana = findUserByUsername(users, "tatiana.albuquerque");
        User ritaO = findUserByUsername(users, "rita.oliveira");
        User ana = findUserByUsername(users, "ana.reis");
        User joao = findUserByUsername(users, "joao.couto");
        User ines = findUserByUsername(users, "ines.rodrigues");
        User teresa = findUserByUsername(users, "teresa.correia");
        User vania = findUserByUsername(users, "vania.lourenco");
        User anca = findUserByUsername(users, "anca.tusa");
        User rogerio = findUserByUsername(users, "rogerio.silva");
        User tiago = findUserByUsername(users, "tiago.rodrigues");
        User mohammad = findUserByUsername(users, "mohammad.aldossari");
        User vijay = findUserByUsername(users, "vijay.kumar");
        User sanita = findUserByUsername(users, "sanita.rahman");
        User monica = findUserByUsername(users, "monica.lewinsky");
        User cristiana = findUserByUsername(users, "cristiana.oliveira");

        // Equipa 1 - Direção
        Team teamDirecao = createTeam("Direção", 
                                     "Equipa de direção executiva da empresa",
                                     martim);
        addMembersToTeam(teamDirecao, Arrays.asList(martim, catarina));

        // Equipa 2 - Gestão Administrativa
        Team teamGestao = createTeam("Gestão Administrativa", 
                                    "Gestão de recursos humanos e administração",
                                    lucile);
        addMembersToTeam(teamGestao, Arrays.asList(lucile, rita, sandra, ricardo));

        // Equipa 3 - Comercial
        Team teamComercial = createTeam("Comercial", 
                                       "Equipa comercial e vendas",
                                       bessa);
        addMembersToTeam(teamComercial, Arrays.asList(bessa, carla, melinda));

        // Equipa 4 - Compras
        Team teamCompras = createTeam("Compras", 
                                     "Gestão de compras e fornecedores",
                                     diana);
        addMembersToTeam(teamCompras, Arrays.asList(diana, tatiana, ritaO));

        // Equipa 5 - Logística
        Team teamLogistica = createTeam("Logística", 
                                       "Logística e distribuição",
                                       paulo);
        addMembersToTeam(teamLogistica, Arrays.asList(paulo, ana, joao));

        // Equipa 6 - Produção
        Team teamProducao = createTeam("Produção", 
                                      "Produção e operações fabris",
                                      pedro);
        addMembersToTeam(teamProducao, Arrays.asList(pedro, ines, teresa, vania));

        // Equipa 7 - Apoio ao Cliente
        Team teamApoio = createTeam("Apoio ao Cliente", 
                                   "Suporte técnico e atendimento ao cliente",
                                   antonio);
        addMembersToTeam(teamApoio, Arrays.asList(antonio, anca, rogerio));

        // Equipa 8 - Financeiro
        Team teamFinanceiro = createTeam("Financeiro",
                                        "Gestão financeira e contabilidade",
                                        rui);
        addMembersToTeam(teamFinanceiro, Arrays.asList(rui, tiago, mohammad, vijay, sanita, monica, cristiana));

        return Arrays.asList(teamDirecao, teamGestao, teamComercial, teamCompras, 
                           teamLogistica, teamProducao, teamApoio, teamFinanceiro);
    }
    
    private User findUserByUsername(List<User> users, String username) {
        return users.stream()
            .filter(u -> u.getUsername().equals(username))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado: " + username));
    }

    private Team createTeam(String name, String description, User manager) {
        Team team = new Team();
        team.setName(name);
        team.setDescription(description);
        team.setManager(manager);
        team.setActive(true);
        team.setCreatedAt(LocalDateTime.now());

        team = teamRepository.save(team);
        System.out.println("Equipa criada: " + name + " (Gestor: " + manager.getFullName() + ")");
        return team;
    }

    private void addMembersToTeam(Team team, List<User> members) {
        System.out.println("DEBUG: Adicionando " + members.size() + " membros à equipa " + team.getName());
        
        // Recarregar a equipa do banco para garantir que está gerenciada
        Team managedTeam = teamRepository.findById(team.getId())
            .orElseThrow(() -> new RuntimeException("Equipa não encontrada: " + team.getId()));
        
        for (User member : members) {
            // Recarregar utilizador do banco
            User managedUser = userRepository.findById(member.getId())
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado: " + member.getId()));
            
            managedTeam.getMembers().add(managedUser);
            System.out.println("  - Adicionado: " + managedUser.getFullName());
        }
        
        teamRepository.save(managedTeam);
        System.out.println("✓ " + members.size() + " membros adicionados à equipa " + managedTeam.getName());
    }
    
    // ================================================================
    // MÉTODOS DE CRIAÇÃO DE TAREFAS
    // ================================================================
    
    private void createTestTasks(List<User> users, List<Team> teams) {
        System.out.println("Criando 27 tarefas da empresa de tubos de aço...");
        
        // Buscar equipas por nome
        Team teamDirecao = findTeamByName(teams, "Direção");
        Team teamGestao = findTeamByName(teams, "Gestão Administrativa");
        Team teamComercial = findTeamByName(teams, "Comercial");
        Team teamCompras = findTeamByName(teams, "Compras");
        Team teamLogistica = findTeamByName(teams, "Logística");
        Team teamProducao = findTeamByName(teams, "Produção");
        Team teamApoio = findTeamByName(teams, "Apoio ao Cliente");
        Team teamFinanceiro = findTeamByName(teams, "Financeiro");
        
        // Buscar utilizadores
        User martim = findUserByUsername(users, "martim.sottomayor");
        User catarina = findUserByUsername(users, "catarina.balsemao");
        User lucile = findUserByUsername(users, "lucile.almeida");
        User bessa = findUserByUsername(users, "bessa.ribeiro");
        User carla = findUserByUsername(users, "carla.silva");
        User diana = findUserByUsername(users, "diana.brochado");
        User paulo = findUserByUsername(users, "paulo.bessa");
        User ana = findUserByUsername(users, "ana.reis");
        User joao = findUserByUsername(users, "joao.couto");
        User pedro = findUserByUsername(users, "pedro.lopes");
        User ines = findUserByUsername(users, "ines.rodrigues");
        User teresa = findUserByUsername(users, "teresa.correia");
        User antonio = findUserByUsername(users, "antonio.nolasco");
        User anca = findUserByUsername(users, "anca.tusa");
        User rogerio = findUserByUsername(users, "rogerio.silva");
        User rui = findUserByUsername(users, "rui.goncalves");
        User tiago = findUserByUsername(users, "tiago.rodrigues");
        
        LocalDateTime now = LocalDateTime.now();
        
        // === TAREFAS DIREÇÃO (3) ===
        createTask("Planeamento Estratégico 2025", 
                  "Definir objetivos estratégicos da empresa para o próximo ano, incluindo metas de vendas e expansão de mercado",
                  Task.TaskPriority.ALTA, Task.TaskStatus.PENDENTE, martim, martim, teamDirecao, 
                  now.plusDays(15), "estrategia,planeamento", 40);
        
        createTask("Reunião com Investidores",
                  "Apresentar resultados do trimestre e discutir planos de investimento em novas linhas de produção",
                  Task.TaskPriority.ALTA, Task.TaskStatus.EM_ANDAMENTO, catarina, martim, teamDirecao,
                  now.plusDays(7), "investidores,financeiro", 8);
        
        createTask("Certificação ISO 9001",
                  "Coordenar processo de renovação da certificação ISO 9001 para garantir qualidade dos tubos de aço",
                  Task.TaskPriority.ALTA, Task.TaskStatus.PENDENTE, martim, martim, teamDirecao,
                  now.plusDays(45), "certificacao,qualidade", 60);
        
        // === TAREFAS GESTÃO ADMINISTRATIVA (3) ===
        createTask("Implementação Sistema ERP",
                  "Supervisionar implementação do novo sistema ERP para gestão integrada da empresa",
                  Task.TaskPriority.ALTA, Task.TaskStatus.EM_ANDAMENTO, lucile, lucile, teamGestao,
                  now.plusDays(60), "erp,sistema,gestao", 120);
        
        createTask("Auditoria Anual Contabilidade",
                  "Coordenar auditoria anual dos registos contabilísticos e fiscais da empresa",
                  Task.TaskPriority.NORMAL, Task.TaskStatus.PENDENTE, lucile, lucile, teamGestao,
                  now.plusDays(30), "auditoria,contabilidade", 24);
        
        createTask("Políticas de Recursos Humanos",
                  "Atualizar manual de políticas de RH incluindo procedimentos de segurança na produção",
                  Task.TaskPriority.NORMAL, Task.TaskStatus.PENDENTE, lucile, lucile, teamGestao,
                  now.plusDays(20), "rh,politicas,seguranca", 16);
        
        // === TAREFAS COMERCIAL (4) ===
        createTask("Prospecção Mercado Construção Civil",
                  "Identificar e contactar potenciais clientes no setor da construção civil para tubos estruturais",
                  Task.TaskPriority.ALTA, Task.TaskStatus.EM_ANDAMENTO, bessa, bessa, teamComercial,
                  now.plusDays(10), "prospeccao,construcao", 20);
        
        createTask("Catálogo Produtos 2025",
                  "Criar novo catálogo com especificações técnicas de todos os tipos de tubos de aço disponíveis",
                  Task.TaskPriority.NORMAL, Task.TaskStatus.PENDENTE, carla, bessa, teamComercial,
                  now.plusDays(25), "catalogo,marketing", 30);
        
        createTask("Proposta Cliente ABC Construções",
                  "Elaborar proposta comercial detalhada para fornecimento de 500 toneladas de tubos galvanizados",
                  Task.TaskPriority.ALTA, Task.TaskStatus.EM_ANDAMENTO, bessa, bessa, teamComercial,
                  now.plusDays(2), "proposta,cliente", 4);
        
        createTask("Feira Internacional do Aço",
                  "Organizar participação na feira internacional do aço em Madrid - stand e apresentações",
                  Task.TaskPriority.NORMAL, Task.TaskStatus.PENDENTE, carla, bessa, teamComercial,
                  now.plusDays(90), "feira,internacional", 50);
        
        // === TAREFAS COMPRAS (3) ===
        createTask("Negociação Fornecedores Aço",
                  "Renegociar contratos com fornecedores de aço bruto para reduzir custos em 15%",
                  Task.TaskPriority.ALTA, Task.TaskStatus.EM_ANDAMENTO, diana, diana, teamCompras,
                  now.plusDays(14), "fornecedores,negociacao", 25);
        
        createTask("Qualificação Novos Fornecedores",
                  "Avaliar e qualificar novos fornecedores de matéria-prima na Europa de Leste",
                  Task.TaskPriority.NORMAL, Task.TaskStatus.PENDENTE, diana, diana, teamCompras,
                  now.plusDays(40), "fornecedores,qualificacao", 35);
        
        createTask("Compra Equipamentos Galvanização",
                  "Processar compra de novos equipamentos para linha de galvanização dos tubos",
                  Task.TaskPriority.ALTA, Task.TaskStatus.EM_ANDAMENTO, diana, diana, teamCompras,
                  now.plusDays(5), "equipamentos,galvanizacao", 8);
        
        // === TAREFAS LOGÍSTICA (3) ===
        createTask("Otimização Rotas Distribuição",
                  "Analisar e otimizar rotas de distribuição para reduzir custos de transporte em 20%",
                  Task.TaskPriority.ALTA, Task.TaskStatus.PENDENTE, paulo, paulo, teamLogistica,
                  now.plusDays(21), "distribuicao,otimizacao", 28);
        
        createTask("Sistema Rastreamento Cargas",
                  "Implementar sistema de rastreamento GPS para todos os camiões de distribuição",
                  Task.TaskPriority.NORMAL, Task.TaskStatus.EM_ANDAMENTO, ana, paulo, teamLogistica,
                  now.plusDays(35), "rastreamento,tecnologia", 40);
        
        createTask("Ampliação Armazém Principal",
                  "Coordenar obras de ampliação do armazém para acomodar aumento de 40% no stock",
                  Task.TaskPriority.ALTA, Task.TaskStatus.PENDENTE, joao, paulo, teamLogistica,
                  now.plusDays(120), "armazem,ampliacao", 80);
        
        // === TAREFAS PRODUÇÃO (4) ===
        createTask("Manutenção Preventiva Máquinas",
                  "Executar manutenção preventiva em todas as máquinas de corte e soldadura",
                  Task.TaskPriority.ALTA, Task.TaskStatus.PENDENTE, pedro, pedro, teamProducao,
                  now.plusDays(3), "manutencao,maquinas", 16);
        
        createTask("Linha Produção Tubos Inox",
                  "Configurar nova linha de produção especializada em tubos de aço inoxidável",
                  Task.TaskPriority.ALTA, Task.TaskStatus.EM_ANDAMENTO, ines, pedro, teamProducao,
                  now.plusDays(45), "inox,linha-producao", 60);
        
        createTask("Controle Qualidade Soldaduras",
                  "Implementar novos procedimentos de controle de qualidade para soldaduras dos tubos",
                  Task.TaskPriority.ALTA, Task.TaskStatus.PENDENTE, teresa, pedro, teamProducao,
                  now.plusDays(18), "qualidade,soldadura", 24);
        
        createTask("Produção Lote Especial",
                  "Produzir lote especial de 200 tubos com especificações personalizadas para cliente VIP",
                  Task.TaskPriority.ALTA, Task.TaskStatus.EM_ANDAMENTO, ines, pedro, teamProducao,
                  now.plusDays(4), "lote-especial,vip", 12);
        
        // === TAREFAS APOIO AO CLIENTE (4) ===
        createTask("Manual Técnico Instalação",
                  "Criar manual técnico detalhado para instalação de tubos estruturais em construções",
                  Task.TaskPriority.NORMAL, Task.TaskStatus.PENDENTE, antonio, antonio, teamApoio,
                  now.plusDays(30), "manual,instalacao", 20);
        
        createTask("Formação Equipa Suporte",
                  "Formar equipa de suporte técnico sobre novas especificações de tubos galvanizados",
                  Task.TaskPriority.NORMAL, Task.TaskStatus.EM_ANDAMENTO, anca, antonio, teamApoio,
                  now.plusDays(14), "formacao,suporte", 16);
        
        createTask("Sistema Tickets Online",
                  "Implementar sistema online para gestão de tickets de suporte e reclamações",
                  Task.TaskPriority.ALTA, Task.TaskStatus.PENDENTE, rogerio, antonio, teamApoio,
                  now.plusDays(40), "sistema,tickets", 35);
        
        createTask("Resolução Reclamação Urgente",
                  "Resolver reclamação urgente sobre qualidade de tubos fornecidos à empresa XYZ",
                  Task.TaskPriority.ALTA, Task.TaskStatus.EM_ANDAMENTO, anca, antonio, teamApoio,
                  now.plusDays(1), "reclamacao,qualidade", 6);
        
        // === TAREFAS FINANCEIRO (3) ===
        createTask("Análise Fluxo de Caixa",
                  "Analisar projeção de fluxo de caixa para próximos 6 meses considerando novos investimentos",
                  Task.TaskPriority.ALTA, Task.TaskStatus.EM_ANDAMENTO, rui, rui, teamFinanceiro,
                  now.plusDays(12), "fluxo-caixa,analise", 18);
        
        createTask("Relatório Rentabilidade Produtos",
                  "Gerar relatório detalhado de rentabilidade por tipo de tubo produzido",
                  Task.TaskPriority.NORMAL, Task.TaskStatus.PENDENTE, tiago, rui, teamFinanceiro,
                  now.plusDays(20), "rentabilidade,relatorio", 12);
        
        createTask("Negociação Financiamento",
                  "Negociar condições de financiamento bancário para expansão da fábrica",
                  Task.TaskPriority.ALTA, Task.TaskStatus.EM_ANDAMENTO, rui, rui, teamFinanceiro,
                  now.plusDays(7), "financiamento,banco", 10);
        
        System.out.println("Criadas 27 tarefas da empresa de tubos de aço!");
    }
    
    private Team findTeamByName(List<Team> teams, String name) {
        return teams.stream()
            .filter(t -> t.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Equipa não encontrada: " + name));
    }
    
    private void createTask(String title, String description, Task.TaskPriority priority,
                           Task.TaskStatus status, User assignee, User creator, Team team,
                           LocalDateTime dueDate, String tags, int estimatedHours) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setStatus(status);
        task.setUser(assignee);
        task.setCreatedBy(creator);
        task.setAssignedTeam(team);
        task.setDueDate(dueDate);
        task.setTags(tags);
        task.setEstimatedHours(estimatedHours);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        
        taskRepository.save(task);
    }
}
