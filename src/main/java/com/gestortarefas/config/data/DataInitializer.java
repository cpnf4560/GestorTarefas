package com.gestortarefas.config.data;

import com.gestortarefas.model.User;
import com.gestortarefas.model.Task;
import com.gestortarefas.model.Task.TaskStatus;
import com.gestortarefas.model.Task.TaskPriority;
import com.gestortarefas.model.UserRole;
import com.gestortarefas.service.UserService;
import com.gestortarefas.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component; // Comentado - classe desativada

import java.time.LocalDateTime;

/**
 * Classe para inserir dados iniciais na aplicação
 * DESATIVADA TEMPORARIAMENTE
 */
// @Component  // Comentado para desativar
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Override
    public void run(String... args) throws Exception {
        // Verificar se já existem dados
        if (userService.countActiveUsers() == 0) {
            createInitialData();
        }
    }

    private void createInitialData() {
        System.out.println("Criando dados iniciais...");

        // Criar utilizador funcionário demo
        User demoUser = userService.registerUser(
            "demo", 
            "demo@gestortarefas.com", 
            "demo123", 
            "Utilizador Demonstração"
        );
        
        // Criar utilizador administrador - usar registerUser e depois atualizar role
        User adminUser = userService.registerUser(
            "admin", 
            "admin@gestortarefas.com", 
            "admin123", 
            "Administrador Sistema"
        );
        adminUser.setRole(UserRole.ADMINISTRADOR);
        adminUser = userService.updateUser(adminUser);
        
        // Criar utilizador gerente
        User managerUser = userService.registerUser(
            "manager", 
            "manager@gestortarefas.com", 
            "manager123", 
            "Gerente de Equipa"
        );
        managerUser.setRole(UserRole.GERENTE);
        managerUser = userService.updateUser(managerUser);

        // Criar tarefas de exemplo
        createSampleTasks(demoUser);
        createSampleTasks(adminUser);
        createSampleTasks(managerUser);

        System.out.println("Dados iniciais criados com sucesso!");
        System.out.println("===========================================");
        System.out.println("UTILIZADORES DE DEMONSTRAÇÃO:");
        System.out.println("Username: demo | Senha: demo123 | Role: FUNCIONARIO");
        System.out.println("Username: manager | Senha: manager123 | Role: GERENTE");
        System.out.println("Username: admin | Senha: admin123 | Role: ADMINISTRADOR");
        System.out.println("===========================================");
    }

    private void createSampleTasks(User user) {
        // Tarefa urgente
        taskService.createTask(
            "Revisar relatório mensal",
            "Revisar e aprovar o relatório financeiro mensal antes da reunião de amanhã",
            TaskPriority.URGENTE,
            LocalDateTime.now().plusDays(1),
            user
        );

        // Tarefa em andamento
        Task inProgressTask = taskService.createTask(
            "Desenvolver nova funcionalidade",
            "Implementar sistema de notificações por email para tarefas próximas do prazo",
            TaskPriority.ALTA,
            LocalDateTime.now().plusDays(7),
            user
        );
        taskService.updateTaskStatus(inProgressTask.getId(), TaskStatus.EM_ANDAMENTO);

        // Tarefa concluída
        Task completedTask = taskService.createTask(
            "Atualizar documentação",
            "Atualizar documentação técnica do projeto com as últimas alterações",
            TaskPriority.NORMAL,
            user
        );
        taskService.updateTaskStatus(completedTask.getId(), TaskStatus.CONCLUIDA);

        // Tarefa simples pendente
        taskService.createTask(
            "Organizar reunião equipe",
            "Agendar reunião semanal da equipe para planeamento do próximo sprint",
            TaskPriority.NORMAL,
            LocalDateTime.now().plusDays(3),
            user
        );

        // Tarefa de baixa prioridade
        taskService.createTask(
            "Limpar arquivos temporários",
            "Fazer limpeza dos arquivos temporários e logs antigos do servidor",
            TaskPriority.BAIXA,
            user
        );

        // Tarefa sem data limite
        taskService.createTask(
            "Pesquisar novas tecnologias",
            "Investigar novas tecnologias e frameworks que possam melhorar a produtividade da equipe",
            TaskPriority.BAIXA,
            user
        );
    }
}