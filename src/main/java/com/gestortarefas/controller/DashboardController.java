package com.gestortarefas.controller;

import com.gestortarefas.model.Task;
import com.gestortarefas.model.Team;
import com.gestortarefas.model.User;
import com.gestortarefas.service.TaskService;
import com.gestortarefas.service.TeamService;
import com.gestortarefas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Controller REST para dashboards específicos por perfil
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    /**
     * Dashboard do Funcionário - 4 colunas de tarefas
     */
    @GetMapping("/employee/{userId}")
    public ResponseEntity<?> getEmployeeDashboard(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));

            Map<String, Object> dashboard = new HashMap<>();
            
            // 4 colunas do dashboard
            dashboard.put("pending", taskService.getPendingTasksForUser(user));
            dashboard.put("today", taskService.getTodayTasksForUser(user));
            dashboard.put("overdue", taskService.getOverdueTasksForUser(user));
            dashboard.put("completed", taskService.getCompletedTasksForUser(user));
            
            // Estatísticas do utilizador
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalTasks", taskService.findTasksByUser(user).size());
            stats.put("pendingCount", taskService.getPendingTasksForUser(user).size());
            stats.put("todayCount", taskService.getTodayTasksForUser(user).size());
            stats.put("overdueCount", taskService.getOverdueTasksForUser(user).size());
            stats.put("completedCount", taskService.getCompletedTasksForUser(user).size());
            
            dashboard.put("stats", stats);
            dashboard.put("user", user);

            return ResponseEntity.ok(dashboard);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Dashboard do Gerente - tarefas das suas equipas
     */
    @GetMapping("/manager/{userId}")
    public ResponseEntity<?> getManagerDashboard(@PathVariable Long userId) {
        try {
            User manager = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Gerente não encontrado"));

            // Verificar se é gerente
            if (!manager.getRole().isManagerOrAbove()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Utilizador não tem permissões de gerente"));
            }

            Map<String, Object> dashboard = new HashMap<>();
            
            // Equipas geridas
            List<Team> managedTeams = teamService.getTeamsManagedBy(manager);
            dashboard.put("managedTeams", managedTeams);
            
            // Tarefas de todas as equipas geridas
            List<Task> allTeamTasks = taskService.getTasksForManager(manager);
            
            // Separar por status para as 4 colunas
            Map<String, List<Task>> tasksByStatus = new HashMap<>();
            for (Team team : managedTeams) {
                List<Task> pending = taskService.getPendingTasksForTeam(team, manager);
                List<Task> today = taskService.getTodayTasksForTeam(team, manager);
                List<Task> overdue = taskService.getOverdueTasksForTeam(team, manager);
                List<Task> completed = taskService.getCompletedTasksForTeam(team, manager);
                
                tasksByStatus.computeIfAbsent("pending", k -> new java.util.ArrayList<>()).addAll(pending);
                tasksByStatus.computeIfAbsent("today", k -> new java.util.ArrayList<>()).addAll(today);
                tasksByStatus.computeIfAbsent("overdue", k -> new java.util.ArrayList<>()).addAll(overdue);
                tasksByStatus.computeIfAbsent("completed", k -> new java.util.ArrayList<>()).addAll(completed);
            }
            
            dashboard.put("pending", tasksByStatus.getOrDefault("pending", new java.util.ArrayList<>()));
            dashboard.put("today", tasksByStatus.getOrDefault("today", new java.util.ArrayList<>()));
            dashboard.put("overdue", tasksByStatus.getOrDefault("overdue", new java.util.ArrayList<>()));
            dashboard.put("completed", tasksByStatus.getOrDefault("completed", new java.util.ArrayList<>()));
            
            // Estatísticas por equipa
            Map<String, Object> teamStats = new HashMap<>();
            for (Team team : managedTeams) {
                TeamService.TeamStats stats = teamService.getTeamStats(team.getId(), manager);
                teamStats.put(team.getName(), stats);
            }
            dashboard.put("teamStats", teamStats);
            
            // Estatísticas gerais do gerente
            Map<String, Object> managerStats = new HashMap<>();
            managerStats.put("totalTeams", managedTeams.size());
            managerStats.put("totalTasks", allTeamTasks.size());
            managerStats.put("pendingTasks", tasksByStatus.getOrDefault("pending", new java.util.ArrayList<>()).size());
            managerStats.put("todayTasks", tasksByStatus.getOrDefault("today", new java.util.ArrayList<>()).size());
            managerStats.put("overdueTasks", tasksByStatus.getOrDefault("overdue", new java.util.ArrayList<>()).size());
            managerStats.put("completedTasks", tasksByStatus.getOrDefault("completed", new java.util.ArrayList<>()).size());
            
            dashboard.put("stats", managerStats);
            dashboard.put("user", manager);

            return ResponseEntity.ok(dashboard);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Dashboard do Administrador - visão global
     */
    @GetMapping("/admin/{userId}")
    public ResponseEntity<?> getAdminDashboard(@PathVariable Long userId) {
        try {
            User admin = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Administrador não encontrado"));

            // Verificar se é administrador
            if (!admin.getRole().isAdmin()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Utilizador não tem permissões de administrador"));
            }

            Map<String, Object> dashboard = new HashMap<>();
            
            // Todas as equipas
            List<Team> allTeams = teamService.getAllTeams();
            dashboard.put("allTeams", allTeams);
            
            // Todos os utilizadores
            List<User> allUsers = userService.findAllActiveUsers();
            dashboard.put("allUsers", allUsers);
            
            // Tarefas globais - 4 colunas
            dashboard.put("pending", taskService.findTasksByStatus(Task.TaskStatus.PENDENTE));
            // Para tarefas de hoje, usar um método genérico
            dashboard.put("today", taskService.findTasksDueSoon());
            dashboard.put("overdue", taskService.findOverdueTasks());
            dashboard.put("completed", taskService.findCompletedTasksLast3Days());
            
            // Estatísticas globais
            Map<String, Object> globalStats = taskService.getOverallTaskStats();
            dashboard.put("taskStats", globalStats);
            
            // Estatísticas de utilizadores
            Map<String, Object> userStats = new HashMap<>();
            userStats.put("totalUsers", allUsers.size());
            userStats.put("adminCount", allUsers.stream().filter(u -> u.getRole().isAdmin()).count());
            userStats.put("managerCount", allUsers.stream().filter(u -> u.getRole().isManagerOrAbove() && !u.getRole().isAdmin()).count());
            userStats.put("employeeCount", allUsers.stream().filter(u -> !u.getRole().isManagerOrAbove()).count());
            
            dashboard.put("userStats", userStats);
            
            // Estatísticas de equipas
            Map<String, Object> teamStatsGlobal = new HashMap<>();
            teamStatsGlobal.put("totalTeams", allTeams.size());
            teamStatsGlobal.put("activeTeams", allTeams.stream().filter(team -> team.getActive()).count());
            
            dashboard.put("teamStatsGlobal", teamStatsGlobal);
            dashboard.put("user", admin);

            return ResponseEntity.ok(dashboard);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Dashboard específico de uma equipa
     */
    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> getTeamDashboard(@PathVariable Long teamId,
                                            @RequestHeader("User-Id") Long requesterId) {
        try {
            User requester = userService.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            
            Team team = teamService.getTeamById(teamId);
            
            Map<String, Object> dashboard = new HashMap<>();
            
            // 4 colunas da equipa
            dashboard.put("pending", taskService.getPendingTasksForTeam(team, requester));
            dashboard.put("today", taskService.getTodayTasksForTeam(team, requester));
            dashboard.put("overdue", taskService.getOverdueTasksForTeam(team, requester));
            dashboard.put("completed", taskService.getCompletedTasksForTeam(team, requester));
            
            // Estatísticas da equipa
            TeamService.TeamStats stats = teamService.getTeamStats(teamId, requester);
            dashboard.put("stats", stats);
            
            // Informações da equipa
            dashboard.put("team", team);
            dashboard.put("user", requester);

            return ResponseEntity.ok(dashboard);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}