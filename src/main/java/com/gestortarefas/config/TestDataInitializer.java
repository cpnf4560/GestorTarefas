package com.gestortarefas.config;

import com.gestortarefas.model.*;
import com.gestortarefas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Inicializa dados de teste na aplicação
 * ATIVADO - Para criar dados de teste iniciais
 */
@Component
public class TestDataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        // Criar utilizadores se não existirem
        List<User> users;
        if (userRepository.count() == 0) {
            System.out.println("=== Inicializando utilizadores de teste ===");
            users = createTestUsers();
        } else {
            users = userRepository.findAll();
        }
        
        // Criar equipas se não existirem
        List<Team> teams;
        if (teamRepository.count() == 0) {
            System.out.println("=== Inicializando equipas de teste ===");
            teams = createTestTeams(users);
        } else {
            teams = teamRepository.findAll();
        }
        
        // Criar tarefas se não existirem
        if (taskRepository.count() == 0) {
            System.out.println("=== Inicializando tarefas de teste ===");
            createTestTasks(users, teams);
            System.out.println("=== Tarefas de teste criadas com sucesso! ===");
        }
    }

    private List<User> createTestUsers() {
        System.out.println("Criando utilizadores de teste...");

        // Admin existente
        User admin = createUser("admin", "admin123", "Administrador Sistema", 
                               "admin@gestortarefas.com", UserRole.ADMINISTRADOR);

        // Gerentes
        User gerente1 = createUser("gerente", "gerente123", "Maria Silva", 
                                  "maria.silva@gestortarefas.com", UserRole.GERENTE);
        User gerente2 = createUser("gerente2", "gerente123", "João Santos", 
                                  "joao.santos@gestortarefas.com", UserRole.GERENTE);
        User gerente3 = createUser("gerente3", "gerente123", "Ana Costa", 
                                  "ana.costa@gestortarefas.com", UserRole.GERENTE);

        // Funcionários
        User funcionario1 = createUser("funcionario", "funcionario123", "Pedro Oliveira", 
                                      "pedro.oliveira@gestortarefas.com", UserRole.FUNCIONARIO);
        User funcionario2 = createUser("carlos.mendes", "funcionario123", "Carlos Mendes", 
                                      "carlos.mendes@gestortarefas.com", UserRole.FUNCIONARIO);
        User funcionario3 = createUser("sofia.rodrigues", "funcionario123", "Sofia Rodrigues", 
                                      "sofia.rodrigues@gestortarefas.com", UserRole.FUNCIONARIO);
        User funcionario4 = createUser("miguel.ferreira", "funcionario123", "Miguel Ferreira", 
                                      "miguel.ferreira@gestortarefas.com", UserRole.FUNCIONARIO);
        User funcionario5 = createUser("rita.pereira", "funcionario123", "Rita Pereira", 
                                      "rita.pereira@gestortarefas.com", UserRole.FUNCIONARIO);
        User funcionario6 = createUser("bruno.alves", "funcionario123", "Bruno Alves", 
                                      "bruno.alves@gestortarefas.com", UserRole.FUNCIONARIO);
        User funcionario7 = createUser("catarina.lima", "funcionario123", "Catarina Lima", 
                                      "catarina.lima@gestortarefas.com", UserRole.FUNCIONARIO);
        User funcionario8 = createUser("ricardo.silva", "funcionario123", "Ricardo Silva", 
                                      "ricardo.silva@gestortarefas.com", UserRole.FUNCIONARIO);

        return Arrays.asList(admin, gerente1, gerente2, gerente3, 
                           funcionario1, funcionario2, funcionario3, funcionario4,
                           funcionario5, funcionario6, funcionario7, funcionario8);
    }

    private User createUser(String username, String password, String fullName, String email, UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(role);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        // Criar perfil básico
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setJobTitle(getJobTitle(role));
        profile.setDepartment(getDepartment(role));
        profile.setLocation("Porto, Portugal");
        profile.setCreatedAt(LocalDateTime.now());
        profile.setEmailNotifications(true);
        
        userProfileRepository.save(profile);

        System.out.println("Utilizador criado: " + username + " (" + fullName + ") - " + role);
        return user;
    }

    private String getJobTitle(UserRole role) {
        switch (role) {
            case ADMINISTRADOR: return "Administrador de Sistema";
            case GERENTE: return "Gestor de Projetos";
            case FUNCIONARIO: return "Desenvolvedor";
            default: return "Funcionário";
        }
    }

    private String getDepartment(UserRole role) {
        switch (role) {
            case ADMINISTRADOR: return "TI";
            case GERENTE: return "Gestão de Projetos";
            case FUNCIONARIO: return random.nextBoolean() ? "Desenvolvimento" : "Qualidade";
            default: return "Geral";
        }
    }

    private List<Team> createTestTeams(List<User> users) {
        System.out.println("Criando equipas de teste...");

        // Separar utilizadores por role
        List<User> gerentes = users.stream()
            .filter(u -> u.getRole() == UserRole.GERENTE)
            .toList();
        
        List<User> funcionarios = users.stream()
            .filter(u -> u.getRole() == UserRole.FUNCIONARIO)
            .toList();

        // Equipa 1 - Frontend
        Team teamFrontend = createTeam("Equipa Frontend", 
                                     "Responsável pelo desenvolvimento da interface de utilizador",
                                     gerentes.get(0));
        addMembersToTeam(teamFrontend, funcionarios.subList(0, 3));

        // Equipa 2 - Backend
        Team teamBackend = createTeam("Equipa Backend", 
                                    "Responsável pelo desenvolvimento do servidor e APIs",
                                    gerentes.get(1));
        addMembersToTeam(teamBackend, funcionarios.subList(3, 6));

        // Equipa 3 - QA/Testing
        Team teamQA = createTeam("Equipa Qualidade", 
                               "Responsável pelos testes e controle de qualidade",
                               gerentes.get(2));
        addMembersToTeam(teamQA, funcionarios.subList(6, 8));

        return Arrays.asList(teamFrontend, teamBackend, teamQA);
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
        for (User member : members) {
            team.getMembers().add(member);
        }
        teamRepository.save(team);
        System.out.println("  -> Adicionados " + members.size() + " membros à equipa " + team.getName());
    }

    private void createTestTasks(List<User> users, List<Team> teams) {
        System.out.println("Criando tarefas de teste...");

        String[] taskTitles = {
            "Implementar login de utilizador",
            "Criar dashboard principal",
            "Desenvolver API REST",
            "Testes de integração",
            "Corrigir bugs críticos",
            "Otimizar performance",
            "Documentação técnica",
            "Configurar ambiente produção",
            "Implementar notificações",
            "Refatorizar código legacy",
            "Criar relatórios automáticos",
            "Implementar backup automático",
            "Melhorar interface mobile",
            "Configurar monitorização",
            "Treinar utilizadores finais",
            "Validar requisitos funcionais",
            "Implementar autenticação 2FA",
            "Criar testes unitários",
            "Configurar CI/CD pipeline",
            "Análise de segurança"
        };

        String[] descriptions = {
            "Desenvolver sistema de autenticação seguro com validação de credenciais",
            "Criar interface principal com métricas e indicadores importantes",
            "Implementar endpoints RESTful para comunicação com frontend",
            "Realizar testes de integração entre componentes do sistema",
            "Identificar e corrigir problemas críticos reportados pelos utilizadores",
            "Otimizar consultas à base de dados e melhorar tempos de resposta",
            "Elaborar documentação técnica completa para o sistema",
            "Configurar ambiente de produção com alta disponibilidade",
            "Implementar sistema de notificações em tempo real",
            "Refatorizar código antigo para melhorar maintibilidade",
            "Criar sistema de relatórios automáticos para gestão",
            "Configurar sistema de backup automático dos dados",
            "Melhorar experiência do utilizador em dispositivos móveis",
            "Implementar monitorização de sistema e alertas",
            "Realizar formação dos utilizadores finais do sistema",
            "Validar se todos os requisitos funcionais estão implementados",
            "Implementar autenticação de dois fatores para maior segurança",
            "Desenvolver suite completa de testes unitários",
            "Configurar pipeline de integração e deployment contínuo",
            "Realizar análise completa de segurança do sistema"
        };

        Task.TaskPriority[] priorities = Task.TaskPriority.values();
        Task.TaskStatus[] statuses = Task.TaskStatus.values();

        List<User> funcionarios = users.stream()
            .filter(u -> u.getRole() == UserRole.FUNCIONARIO)
            .toList();

        List<User> gerentes = users.stream()
            .filter(u -> u.getRole() == UserRole.GERENTE)
            .toList();

        // Criar 30 tarefas aleatórias
        for (int i = 0; i < 30; i++) {
            Task task = new Task();
            
            int titleIndex = random.nextInt(taskTitles.length);
            task.setTitle(taskTitles[titleIndex]);
            task.setDescription(descriptions[titleIndex]);
            
            // Atribuir a funcionário aleatório
            User assignee = funcionarios.get(random.nextInt(funcionarios.size()));
            task.setUser(assignee);
            
            // Criada por gerente aleatório
            User creator = gerentes.get(random.nextInt(gerentes.size()));
            task.setCreatedBy(creator);
            
            // Equipa aleatória
            Team team = teams.get(random.nextInt(teams.size()));
            task.setAssignedTeam(team);
            
            task.setPriority(priorities[random.nextInt(priorities.length)]);
            task.setStatus(statuses[random.nextInt(statuses.length)]);
            
            task.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            
            // Data limite aleatória (alguns sem data limite)
            if (random.nextBoolean()) {
                task.setDueDate(LocalDateTime.now().plusDays(random.nextInt(60) + 1));
            }
            
            // Horas estimadas
            task.setEstimatedHours(random.nextInt(40) + 1);
            
            // Se tarefa está concluída, definir horas reais e data de conclusão
            if (task.getStatus() == Task.TaskStatus.CONCLUIDA) {
                task.setActualHours(task.getEstimatedHours() + random.nextInt(20) - 10);
                task.setCompletedAt(LocalDateTime.now().minusDays(random.nextInt(10)));
            }
            
            // Tags aleatórias
            String[] tagOptions = {"frontend", "backend", "bug", "feature", "urgent", "testing", "docs", "security"};
            if (random.nextBoolean()) {
                task.setTags(tagOptions[random.nextInt(tagOptions.length)]);
            }
            
            task.setUpdatedAt(LocalDateTime.now());
            
            taskRepository.save(task);
        }
        
        System.out.println("Criadas 30 tarefas de teste distribuídas pelas equipas");
    }
}