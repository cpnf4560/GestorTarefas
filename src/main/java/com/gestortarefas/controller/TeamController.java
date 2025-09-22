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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestão de equipas
 */
@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    /**
     * Lista todas as equipas ativas
     */
    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        try {
            List<Team> teams = teamService.getAllActiveTeams();
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca equipa por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        try {
            Team team = teamService.getTeamById(id);
            return ResponseEntity.ok(team);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cria uma nova equipa
     */
    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody TeamCreateRequest request) {
        try {
            User manager = userService.findById(request.getManagerId())
                .orElseThrow(() -> new IllegalArgumentException("Gerente não encontrado"));
            Team team = teamService.createTeam(request.getName(), request.getDescription(), manager);
            return ResponseEntity.status(HttpStatus.CREATED).body(team);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Atualiza uma equipa
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTeam(@PathVariable Long id, 
                                       @RequestBody TeamUpdateRequest request,
                                       @RequestHeader("User-Id") Long requesterId) {
        try {
            User requester = userService.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            Team team = teamService.updateTeam(id, request.getName(), request.getDescription(), requester);
            return ResponseEntity.ok(team);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Adiciona membro à equipa
     */
    @PostMapping("/{id}/members/{userId}")
    public ResponseEntity<?> addMember(@PathVariable Long id, 
                                      @PathVariable Long userId,
                                      @RequestHeader("User-Id") Long requesterId) {
        try {
            User requester = userService.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            Team team = teamService.addMemberToTeam(id, userId, requester);
            return ResponseEntity.ok(team);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Remove membro da equipa
     */
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable Long id, 
                                         @PathVariable Long userId,
                                         @RequestHeader("User-Id") Long requesterId) {
        try {
            User requester = userService.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            Team team = teamService.removeMemberFromTeam(id, userId, requester);
            return ResponseEntity.ok(team);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Define novo gerente da equipa
     */
    @PutMapping("/{id}/manager/{managerId}")
    public ResponseEntity<?> setManager(@PathVariable Long id, 
                                       @PathVariable Long managerId,
                                       @RequestHeader("User-Id") Long requesterId) {
        try {
            User requester = userService.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            Team team = teamService.setTeamManager(id, managerId, requester);
            return ResponseEntity.ok(team);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista equipas geridas por um utilizador
     */
    @GetMapping("/managed-by/{managerId}")
    public ResponseEntity<List<Team>> getTeamsManagedBy(@PathVariable Long managerId) {
        try {
            User manager = userService.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException("Gerente não encontrado"));
            List<Team> teams = teamService.getTeamsManagedBy(manager);
            return ResponseEntity.ok(teams);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista equipas onde o utilizador é membro
     */
    @GetMapping("/member/{userId}")
    public ResponseEntity<List<Team>> getTeamsForUser(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            List<Team> teams = teamService.getTeamsForUser(user);
            return ResponseEntity.ok(teams);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista membros de uma equipa específica
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<Map<String, Object>>> getTeamMembers(@PathVariable Long id) {
        try {
            Team team = teamService.getTeamById(id);
            List<User> members = team.getMembers();
            
            List<Map<String, Object>> memberData = new ArrayList<>();
            for (User member : members) {
                Map<String, Object> memberInfo = new HashMap<>();
                memberInfo.put("id", member.getId());
                memberInfo.put("username", member.getUsername());
                memberInfo.put("fullName", member.getFullName());
                memberInfo.put("email", member.getEmail());
                memberInfo.put("role", member.getRole().toString());
                
                // Contar tarefas ativas e concluídas do membro
                List<Task> memberTasks = taskService.findTasksByUser(member);
                long activeTasks = memberTasks.stream()
                    .filter(task -> task.getStatus() == Task.TaskStatus.PENDENTE || 
                                  task.getStatus() == Task.TaskStatus.EM_ANDAMENTO)
                    .count();
                long completedTasks = memberTasks.stream()
                    .filter(task -> task.getStatus() == Task.TaskStatus.CONCLUIDA)
                    .count();
                
                memberInfo.put("activeTasks", activeTasks);
                memberInfo.put("completedTasks", completedTasks);
                
                memberData.add(memberInfo);
            }
            
            return ResponseEntity.ok(memberData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca equipas por nome
     */
    @GetMapping("/search")
    public ResponseEntity<List<Team>> searchTeams(@RequestParam String name) {
        try {
            List<Team> teams = teamService.searchTeamsByName(name);
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtém estatísticas de uma equipa
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getTeamStats(@PathVariable Long id,
                                         @RequestHeader("User-Id") Long requesterId) {
        try {
            User requester = userService.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            TeamService.TeamStats stats = teamService.getTeamStats(id, requester);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Desativa uma equipa
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateTeam(@PathVariable Long id,
                                           @RequestHeader("User-Id") Long requesterId) {
        try {
            User requester = userService.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            Team team = teamService.deactivateTeam(id, requester);
            return ResponseEntity.ok(team);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Ativa uma equipa
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateTeam(@PathVariable Long id,
                                         @RequestHeader("User-Id") Long requesterId) {
        try {
            User requester = userService.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            Team team = teamService.activateTeam(id, requester);
            return ResponseEntity.ok(team);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Classes para requests
    public static class TeamCreateRequest {
        private String name;
        private String description;
        private Long managerId;

        // Getters e Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Long getManagerId() { return managerId; }
        public void setManagerId(Long managerId) { this.managerId = managerId; }
    }

    public static class TeamUpdateRequest {
        private String name;
        private String description;

        // Getters e Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}