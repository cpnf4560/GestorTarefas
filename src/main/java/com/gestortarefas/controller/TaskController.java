package com.gestortarefas.controller;

import com.gestortarefas.model.Task;
import com.gestortarefas.model.Task.TaskStatus;
import com.gestortarefas.model.Task.TaskPriority;
import com.gestortarefas.model.User;
import com.gestortarefas.model.Team;
import com.gestortarefas.model.TaskComment;
import com.gestortarefas.service.TaskService;
import com.gestortarefas.service.UserService;
import com.gestortarefas.repository.TaskRepository;
import com.gestortarefas.repository.UserRepository;
import com.gestortarefas.repository.TeamRepository;
import com.gestortarefas.repository.TaskCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * Controlador REST para operações relacionadas às tarefas.
 * 
 * Este controlador expõe endpoints HTTP para:
 * - CRUD de tarefas (Create, Read, Update, Delete)
 * - Gestão de estado das tarefas (PENDENTE → EM_ANDAMENTO → CONCLUIDA)
 * - Atribuição de tarefas a utilizadores e equipas
 * - Filtros e pesquisas avançadas
 * - Gestão de comentários nas tarefas
 * 
 * Base URL: /api/tasks
 * Suporta CORS para permitir chamadas de qualquer origem (desenvolvimento)
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*") // Permite CORS para desenvolvimento - ATENÇÃO: restringir em produção
public class TaskController {

    // ======================== INJEÇÃO DE DEPENDÊNCIAS ========================
    
    // Serviço principal para lógica de negócio das tarefas
    @Autowired
    private TaskService taskService;

    // Serviço para operações com utilizadores
    @Autowired
    private UserService userService;

    // Repositórios para acesso direto aos dados (quando necessário)
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    /**
     * Cria uma nova tarefa
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTask(@RequestBody Map<String, Object> taskData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("TaskController: Received task data: " + taskData);
            
            String title = (String) taskData.get("title");
            String description = (String) taskData.get("description");
            Long userId = ((Number) taskData.get("userId")).longValue();
            String priorityStr = (String) taskData.get("priority");
            String dueDateStr = (String) taskData.get("dueDate");
            Long createdByUserId = taskData.get("createdByUserId") != null ? 
                ((Number) taskData.get("createdByUserId")).longValue() : userId;
            Long assignedTeamId = taskData.get("assignedTeamId") != null ? 
                ((Number) taskData.get("assignedTeamId")).longValue() : null;
            Long assignedUserId = taskData.get("assignedUserId") != null ? 
                ((Number) taskData.get("assignedUserId")).longValue() : null;
            Boolean isAssignedToTeam = taskData.get("isAssignedToTeam") != null ? 
                (Boolean) taskData.get("isAssignedToTeam") : false;
            
            System.out.println("TaskController: Parsed data - title: " + title + ", userId: " + userId + 
                             ", priority: " + priorityStr + ", dueDate: " + dueDateStr +
                             ", createdBy: " + createdByUserId + ", teamId: " + assignedTeamId +
                             ", assignedUserId: " + assignedUserId + ", isTeamTask: " + isAssignedToTeam);
            
            // Determinar o utilizador final baseado na atribuição
            Long finalUserId = assignedUserId != null ? assignedUserId : userId;
            
            Optional<User> userOpt = userService.findById(finalUserId);
            if (userOpt.isEmpty()) {
                System.out.println("TaskController: User not found for ID: " + finalUserId);
                response.put("success", false);
                response.put("message", "Utilizador atribuído não encontrado");
                return ResponseEntity.badRequest().body(response);
            }
            
            Optional<User> createdByOpt = userService.findById(createdByUserId);
            if (createdByOpt.isEmpty()) {
                System.out.println("TaskController: Creator not found for ID: " + createdByUserId);
                response.put("success", false);
                response.put("message", "Criador não encontrado");
                return ResponseEntity.badRequest().body(response);
            }
            
            TaskPriority priority = priorityStr != null ? 
                TaskPriority.valueOf(priorityStr.toUpperCase()) : TaskPriority.NORMAL;
                
            LocalDateTime dueDate = null;
            if (dueDateStr != null && !dueDateStr.trim().isEmpty()) {
                dueDate = LocalDateTime.parse(dueDateStr);
            }
            
            // Determinar a equipa baseado na atribuição
            Team assignedTeam = null;
            User assignedUser = userOpt.get();
            
            if (isAssignedToTeam && assignedTeamId != null) {
                // Tarefa atribuída especificamente a uma equipa
                assignedTeam = teamRepository.findById(assignedTeamId).orElse(null);
                System.out.println("TaskController: Task assigned to team: " + 
                                 (assignedTeam != null ? assignedTeam.getName() : "Team not found"));
            } else {
                // Tarefa individual - automaticamente associada à primeira equipa do utilizador
                // para que apareça nas visualizações do gerente
                if (!assignedUser.getTeams().isEmpty()) {
                    assignedTeam = assignedUser.getTeams().get(0);
                    System.out.println("TaskController: Individual task auto-assigned to team: " + 
                                     assignedTeam.getName() + " for monitoring by manager");
                }
            }
            
            System.out.println("TaskController: Creating task with priority: " + priority + 
                             ", dueDate: " + dueDate + ", team: " + 
                             (assignedTeam != null ? assignedTeam.getName() : "none"));
            
            Task task = taskService.createTaskWithTeam(
                title, description, priority, dueDate, 
                assignedUser, assignedTeam, createdByOpt.get(), 
                null, null // tags e estimatedHours
            );
            
            System.out.println("TaskController: Task created successfully with ID: " + task.getId());
            
            response.put("success", true);
            response.put("message", "Tarefa criada com sucesso");
            response.put("task", createTaskResponse(task));
            
            System.out.println("TaskController: Sending response: " + response);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("TaskController: Error creating task: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Erro ao criar tarefa: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Lista todas as tarefas do sistema com suporte a filtros, ordenação e paginação
     * 
     * @param status Filtro por status (opcional): PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA
     * @param priority Filtro por prioridade (opcional): BAIXA, NORMAL, ALTA, CRITICA
     * @param assignedUserId Filtro por utilizador atribuído (opcional)
     * @param teamId Filtro por equipa (opcional)
     * @param search Termo de pesquisa no título e descrição (opcional)
     * @param sortBy Campo para ordenação (opcional): title, priority, dueDate, createdAt, status
     * @param sortDirection Direção da ordenação (opcional): asc, desc
     * @param page Número da página (opcional, default: 0)
     * @param size Tamanho da página (opcional, default: 50)
     * @return Lista paginada de tarefas com metadados
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long assignedUserId,
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "50") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Buscar todas as tarefas do repositório
            List<Task> allTasks = taskRepository.findAll();
            
            // Aplicar filtros
            List<Task> filteredTasks = allTasks.stream()
                .filter(task -> {
                    // Filtro por status
                    if (status != null && !status.trim().isEmpty()) {
                        try {
                            TaskStatus statusEnum = TaskStatus.valueOf(status.toUpperCase());
                            if (task.getStatus() != statusEnum) {
                                return false;
                            }
                        } catch (IllegalArgumentException e) {
                            return false;
                        }
                    }
                    
                    // Filtro por prioridade
                    if (priority != null && !priority.trim().isEmpty()) {
                        try {
                            TaskPriority priorityEnum = TaskPriority.valueOf(priority.toUpperCase());
                            if (task.getPriority() != priorityEnum) {
                                return false;
                            }
                        } catch (IllegalArgumentException e) {
                            return false;
                        }
                    }
                    
                    // Filtro por utilizador atribuído
                    if (assignedUserId != null) {
                        if (task.getUser() == null || !task.getUser().getId().equals(assignedUserId)) {
                            return false;
                        }
                    }
                    
                    // Filtro por equipa
                    if (teamId != null) {
                        if (task.getAssignedTeam() == null || !task.getAssignedTeam().getId().equals(teamId)) {
                            return false;
                        }
                    }
                    
                    // Filtro por pesquisa no título e descrição
                    if (search != null && !search.trim().isEmpty()) {
                        String searchTerm = search.toLowerCase();
                        String title = task.getTitle() != null ? task.getTitle().toLowerCase() : "";
                        String description = task.getDescription() != null ? task.getDescription().toLowerCase() : "";
                        
                        if (!title.contains(searchTerm) && !description.contains(searchTerm)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .sorted((t1, t2) -> {
                    // Aplicar ordenação
                    int comparison = 0;
                    
                    switch (sortBy.toLowerCase()) {
                        case "title":
                            comparison = String.CASE_INSENSITIVE_ORDER.compare(
                                t1.getTitle() != null ? t1.getTitle() : "",
                                t2.getTitle() != null ? t2.getTitle() : ""
                            );
                            break;
                        case "priority":
                            comparison = Integer.compare(
                                t1.getPriority().ordinal(),
                                t2.getPriority().ordinal()
                            );
                            break;
                        case "duedate":
                            LocalDateTime date1 = t1.getDueDate();
                            LocalDateTime date2 = t2.getDueDate();
                            if (date1 == null && date2 == null) comparison = 0;
                            else if (date1 == null) comparison = 1;
                            else if (date2 == null) comparison = -1;
                            else comparison = date1.compareTo(date2);
                            break;
                        case "status":
                            comparison = Integer.compare(
                                t1.getStatus().ordinal(),
                                t2.getStatus().ordinal()
                            );
                            break;
                        case "createdat":
                        default:
                            comparison = t1.getCreatedAt().compareTo(t2.getCreatedAt());
                            break;
                    }
                    
                    return sortDirection.equalsIgnoreCase("desc") ? -comparison : comparison;
                })
                .toList();
            
            // Aplicar paginação
            int totalElements = filteredTasks.size();
            int totalPages = (int) Math.ceil((double) totalElements / size);
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, totalElements);
            
            List<Task> paginatedTasks = filteredTasks.subList(fromIndex, toIndex);
            
            // Converter para resposta JSON
            List<Map<String, Object>> tasksList = paginatedTasks.stream()
                .map(this::createTaskResponse)
                .toList();
            
            // Criar resposta com metadados de paginação
            response.put("success", true);
            response.put("tasks", tasksList);
            response.put("pagination", Map.of(
                "currentPage", page,
                "totalPages", totalPages,
                "totalElements", totalElements,
                "pageSize", size,
                "hasNext", page < totalPages - 1,
                "hasPrevious", page > 0
            ));
            response.put("filters", Map.of(
                "status", status != null ? status : "",
                "priority", priority != null ? priority : "",
                "assignedUserId", assignedUserId != null ? assignedUserId : "",
                "teamId", teamId != null ? teamId : "",
                "search", search != null ? search : "",
                "sortBy", sortBy,
                "sortDirection", sortDirection
            ));
            
            System.out.println("TaskController: Found " + totalElements + " tasks (showing " + 
                             paginatedTasks.size() + " on page " + page + ")");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("TaskController: Error fetching all tasks: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Erro ao buscar tarefas: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Lista todas as tarefas de um utilizador
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getTasksByUser(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Utilizador não encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        
        List<Task> tasks = taskService.findTasksByUser(userOpt.get());
        List<Map<String, Object>> tasksList = tasks.stream()
            .map(this::createTaskResponse)
            .toList();
            
        response.put("success", true);
        response.put("tasks", tasksList);
        response.put("total", tasks.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lista tarefas de um utilizador por status
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<Map<String, Object>> getTasksByUserAndStatus(@PathVariable Long userId, 
                                                                        @PathVariable String status) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Utilizador não encontrado");
                return ResponseEntity.badRequest().body(response);
            }
            
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            List<Task> tasks = taskService.findTasksByUserAndStatus(userOpt.get(), taskStatus);
            
            List<Map<String, Object>> tasksList = tasks.stream()
                .map(this::createTaskResponse)
                .toList();
                
            response.put("success", true);
            response.put("tasks", tasksList);
            response.put("total", tasks.size());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Status inválido");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Busca tarefa por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTaskById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Task> taskOpt = taskService.findById(id);
        
        if (taskOpt.isPresent()) {
            response.put("success", true);
            response.put("task", createTaskResponse(taskOpt.get()));
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Tarefa não encontrada");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Atualiza uma tarefa
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTask(@PathVariable Long id, 
                                                          @RequestBody Map<String, Object> taskData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Task> taskOpt = taskService.findById(id);
            if (taskOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Tarefa não encontrada");
                return ResponseEntity.notFound().build();
            }
            
            Task task = taskOpt.get();
            
            if (taskData.containsKey("title")) {
                task.setTitle((String) taskData.get("title"));
            }
            if (taskData.containsKey("description")) {
                task.setDescription((String) taskData.get("description"));
            }
            if (taskData.containsKey("priority")) {
                String priorityStr = (String) taskData.get("priority");
                task.setPriority(TaskPriority.valueOf(priorityStr.toUpperCase()));
            }
            if (taskData.containsKey("status")) {
                String statusStr = (String) taskData.get("status");
                task.setStatus(TaskStatus.valueOf(statusStr.toUpperCase()));
            }
            if (taskData.containsKey("dueDate")) {
                String dueDateStr = (String) taskData.get("dueDate");
                if (dueDateStr != null && !dueDateStr.trim().isEmpty()) {
                    task.setDueDate(LocalDateTime.parse(dueDateStr));
                } else {
                    task.setDueDate(null);
                }
            }
            
            Task updatedTask = taskService.updateTask(task);
            
            response.put("success", true);
            response.put("message", "Tarefa atualizada com sucesso");
            response.put("task", createTaskResponse(updatedTask));
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erro ao atualizar tarefa: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Altera apenas o status de uma tarefa
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateTaskStatus(@PathVariable Long id, 
                                                                @RequestBody Map<String, String> statusData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String statusStr = statusData.get("status");
            TaskStatus newStatus = TaskStatus.valueOf(statusStr.toUpperCase());
            
            boolean success = taskService.updateTaskStatus(id, newStatus);
            
            if (success) {
                response.put("success", true);
                response.put("message", "Status atualizado com sucesso");
                
                // Retornar a tarefa atualizada
                Optional<Task> taskOpt = taskService.findById(id);
                if (taskOpt.isPresent()) {
                    response.put("task", createTaskResponse(taskOpt.get()));
                }
            } else {
                response.put("success", false);
                response.put("message", "Tarefa não encontrada");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Status inválido");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Marca tarefa como concluída
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> completeTask(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        boolean success = taskService.completeTask(id);
        
        if (success) {
            response.put("success", true);
            response.put("message", "Tarefa marcada como concluída");
        } else {
            response.put("success", false);
            response.put("message", "Tarefa não encontrada");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Pesquisa tarefas por título
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchTasks(@RequestParam String title,
                                                           @RequestParam(required = false) Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        List<Task> tasks;
        if (userId != null) {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Utilizador não encontrado");
                return ResponseEntity.badRequest().body(response);
            }
            tasks = taskService.searchTasksByUserAndTitle(userOpt.get(), title);
        } else {
            tasks = taskService.searchTasksByTitle(title);
        }
        
        List<Map<String, Object>> tasksList = tasks.stream()
            .map(this::createTaskResponse)
            .toList();
            
        response.put("success", true);
        response.put("tasks", tasksList);
        response.put("total", tasks.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lista tarefas em atraso
     */
    @GetMapping("/overdue")
    public ResponseEntity<Map<String, Object>> getOverdueTasks(@RequestParam(required = false) Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        List<Task> tasks;
        if (userId != null) {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Utilizador não encontrado");
                return ResponseEntity.badRequest().body(response);
            }
            tasks = taskService.findOverdueTasksByUser(userOpt.get());
        } else {
            tasks = taskService.findOverdueTasks();
        }
        
        List<Map<String, Object>> tasksList = tasks.stream()
            .map(this::createTaskResponse)
            .toList();
            
        response.put("success", true);
        response.put("tasks", tasksList);
        response.put("total", tasks.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtém estatísticas de tarefas de um utilizador
     */
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getTaskStatsByUser(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Utilizador não encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        
        Map<String, Long> stats = taskService.getTaskStatsByUser(userOpt.get());
        
        response.put("success", true);
        response.put("stats", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina uma tarefa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        boolean success = taskService.deleteTask(id);
        
        if (success) {
            response.put("success", true);
            response.put("message", "Tarefa eliminada com sucesso");
        } else {
            response.put("success", false);
            response.put("message", "Tarefa não encontrada");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Duplica uma tarefa
     */
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<Map<String, Object>> duplicateTask(@PathVariable Long id, 
                                                             @RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = ((Number) data.get("userId")).longValue();
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Utilizador não encontrado");
                return ResponseEntity.badRequest().body(response);
            }
            
            Task duplicatedTask = taskService.duplicateTask(id, userOpt.get());
            
            response.put("success", true);
            response.put("message", "Tarefa duplicada com sucesso");
            response.put("task", createTaskResponse(duplicatedTask));
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erro ao duplicar tarefa: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtém todos os status possíveis
     */
    @GetMapping("/statuses")
    public ResponseEntity<Map<String, Object>> getAllStatuses() {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, String> statuses = new HashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            statuses.put(status.name(), status.getDisplayName());
        }
        
        response.put("success", true);
        response.put("statuses", statuses);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtém todas as prioridades possíveis
     */
    @GetMapping("/priorities")
    public ResponseEntity<Map<String, Object>> getAllPriorities() {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, String> priorities = new HashMap<>();
        for (TaskPriority priority : TaskPriority.values()) {
            priorities.put(priority.name(), priority.getDisplayName());
        }
        
        response.put("success", true);
        response.put("priorities", priorities);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Cria resposta da tarefa
     */
    private Map<String, Object> createTaskResponse(Task task) {
        Map<String, Object> taskResponse = new HashMap<>();
        taskResponse.put("id", task.getId());
        taskResponse.put("title", task.getTitle());
        taskResponse.put("description", task.getDescription());
        taskResponse.put("status", task.getStatus().name());
        taskResponse.put("statusDisplay", task.getStatusDisplayName());
        taskResponse.put("priority", task.getPriority().name());
        taskResponse.put("priorityDisplay", task.getPriorityDisplayName());
        taskResponse.put("createdAt", task.getCreatedAt());
        taskResponse.put("updatedAt", task.getUpdatedAt());
        taskResponse.put("dueDate", task.getDueDate());
        taskResponse.put("completedAt", task.getCompletedAt());
        taskResponse.put("userId", task.getUser().getId());
        taskResponse.put("username", task.getUser().getUsername());
        taskResponse.put("isOverdue", task.isOverdue());
        taskResponse.put("isCompleted", task.isCompleted());
        return taskResponse;
    }

    // Novos endpoints para funcionalidades de equipa

    /**
     * Cria tarefa com atribuição a equipa
     */
    @PostMapping("/team")
    public ResponseEntity<?> createTaskWithTeam(@RequestBody TaskWithTeamRequest request) {
        try {
            User user = userService.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            User createdBy = userService.findById(request.getCreatedById())
                .orElseThrow(() -> new IllegalArgumentException("Criador não encontrado"));
            
            com.gestortarefas.model.Team team = null;
            if (request.getTeamId() != null) {
                // Assumindo que temos acesso ao TeamService aqui
                // team = teamService.getTeamById(request.getTeamId());
            }
            
            Task task = taskService.createTaskWithTeam(
                request.getTitle(),
                request.getDescription(), 
                request.getPriority(),
                request.getDueDate(),
                user,
                team,
                createdBy,
                request.getTags(),
                request.getEstimatedHours()
            );

            return ResponseEntity.ok(createTaskResponse(task));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Busca tarefas por tag
     */
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<Map<String, Object>>> getTasksByTag(@PathVariable String tag) {
        try {
            List<Task> tasks = taskService.getTasksByTag(tag);
            List<Map<String, Object>> response = tasks.stream()
                .map(this::createTaskResponse)
                .toList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Atualiza horas reais de uma tarefa
     */
    @PutMapping("/{id}/actual-hours")
    public ResponseEntity<?> updateActualHours(@PathVariable Long id, 
                                              @RequestBody Map<String, Integer> request,
                                              @RequestHeader("User-Id") Long requesterId) {
        try {
            User requester = userService.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            
            Task task = taskService.updateActualHours(id, request.get("actualHours"), requester);
            return ResponseEntity.ok(createTaskResponse(task));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Adiciona tags a uma tarefa
     */
    @PutMapping("/{id}/tags")
    public ResponseEntity<?> updateTaskTags(@PathVariable Long id,
                                           @RequestBody Map<String, String> request,
                                           @RequestHeader("User-Id") Long requesterId) {
        try {
            User requester = userService.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            
            Task task = taskService.updateTaskTags(id, request.get("tags"), requester);
            return ResponseEntity.ok(createTaskResponse(task));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Classe para request de criação de tarefa com equipa
    public static class TaskWithTeamRequest {
        private String title;
        private String description;
        private TaskPriority priority;
        private LocalDateTime dueDate;
        private Long userId;
        private Long teamId;
        private Long createdById;
        private String tags;
        private Integer estimatedHours;

        // Getters e Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public TaskPriority getPriority() { return priority; }
        public void setPriority(TaskPriority priority) { this.priority = priority; }

        public LocalDateTime getDueDate() { return dueDate; }
        public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getTeamId() { return teamId; }
        public void setTeamId(Long teamId) { this.teamId = teamId; }

        public Long getCreatedById() { return createdById; }
        public void setCreatedById(Long createdById) { this.createdById = createdById; }

        public String getTags() { return tags; }
        public void setTags(String tags) { this.tags = tags; }

        public Integer getEstimatedHours() { return estimatedHours; }
        public void setEstimatedHours(Integer estimatedHours) { this.estimatedHours = estimatedHours; }
    }

    /**
     * Endpoint para gerentes atribuírem tarefas a funcionários específicos
     */
    @PutMapping("/{id}/assign")
    public ResponseEntity<?> assignTaskToUser(@PathVariable Long id,
                                             @RequestBody AssignTaskRequest request) {
        try {
            // Validar dados obrigatórios
            if (request.getUserId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "ID do utilizador a atribuir é obrigatório"));
            }

            if (request.getAssignedById() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "ID do gerente que está a atribuir é obrigatório"));
            }

            // Verificar se a tarefa existe
            Optional<Task> taskOpt = taskRepository.findById(id);
            if (!taskOpt.isPresent()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Tarefa não encontrada");
                return ResponseEntity.notFound().build();
            }

            // Verificar se o utilizador a atribuir existe
            Optional<User> userToAssignOpt = userRepository.findById(request.getUserId());
            if (!userToAssignOpt.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Utilizador a atribuir não encontrado"));
            }

            // Verificar se o gerente existe
            Optional<User> managerOpt = userRepository.findById(request.getAssignedById());
            if (!managerOpt.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Gerente não encontrado"));
            }

            Task task = taskOpt.get();
            User userToAssign = userToAssignOpt.get();
            User manager = managerOpt.get();

            // Verificar se o gerente tem permissão para atribuir esta tarefa
            if (!task.canBeEditedBy(manager)) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Não tem permissão para atribuir esta tarefa"));
            }

            // Verificar se o utilizador está ativo
            if (!userToAssign.getActive()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Não é possível atribuir tarefa a utilizador inativo"));
            }

            // Guardar utilizador anterior para histórico
            String previousAssignment = task.getAssignmentInfo();

            // Atribuir a tarefa
            task.setUser(userToAssign);
            task.setAssignedBy(manager);
            
            // Atribuir equipa se especificada
            if (request.getTeamId() != null) {
                Optional<Team> teamOpt = teamRepository.findById(request.getTeamId());
                if (teamOpt.isPresent()) {
                    task.setAssignedTeam(teamOpt.get());
                }
            }

            // Guardar mudanças
            Task savedTask = taskRepository.save(task);

            // Criar comentário automático do sistema sobre a atribuição
            if (taskCommentRepository != null) {
                try {
                    String assignmentMessage = String.format(
                        "Tarefa atribuída por %s para %s", 
                        manager.getFullName(), 
                        userToAssign.getFullName()
                    );
                    
                    if (request.getTeamId() != null && task.getAssignedTeam() != null) {
                        assignmentMessage += " (Equipa: " + task.getAssignedTeam().getName() + ")";
                    }

                    TaskComment systemComment = new TaskComment(task, manager, assignmentMessage, true);
                    taskCommentRepository.save(systemComment);
                } catch (Exception e) {
                    // Não falhar a atribuição se o comentário falhar
                    System.err.println("Erro ao criar comentário do sistema: " + e.getMessage());
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tarefa atribuída com sucesso");
            response.put("task", savedTask);
            response.put("assignedTo", userToAssign.getFullName());
            response.put("assignedBy", manager.getFullName());
            response.put("previousAssignment", previousAssignment);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erro ao atribuir tarefa: " + e.getMessage()));
        }
    }

    /**
     * Reatribuir uma tarefa a outro utilizador ou equipa
     */
    @PutMapping("/{id}/reassign")
    public ResponseEntity<Map<String, Object>> reassignTask(@PathVariable Long id, @RequestBody Map<String, Object> reassignData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("TaskController: Reassigning task " + id + " with data: " + reassignData);
            
            // Verificar se a tarefa existe
            Optional<Task> taskOpt = taskRepository.findById(id);
            if (!taskOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Tarefa não encontrada");
                return ResponseEntity.status(404).body(response);
            }
            
            Task task = taskOpt.get();
            
            // Extrair dados da reatribuição
            Long assignedUserId = reassignData.get("assignedUserId") != null ? 
                ((Number) reassignData.get("assignedUserId")).longValue() : null;
            Long assignedTeamId = reassignData.get("assignedTeamId") != null ? 
                ((Number) reassignData.get("assignedTeamId")).longValue() : null;
            Boolean isAssignedToTeam = reassignData.get("isAssignedToTeam") != null ? 
                (Boolean) reassignData.get("isAssignedToTeam") : false;
            
            // Guardar informação da atribuição anterior para histórico
            String previousAssignment = task.getAssignmentInfo();
            
            // Limpar atribuições anteriores
            task.setAssignedTeam(null);
            
            String newAssignmentInfo = "";
            
            if (isAssignedToTeam && assignedTeamId != null) {
                // Atribuir a equipa
                Optional<Team> teamOpt = teamRepository.findById(assignedTeamId);
                if (!teamOpt.isPresent()) {
                    response.put("success", false);
                    response.put("message", "Equipa não encontrada");
                    return ResponseEntity.status(404).body(response);
                }
                
                Team team = teamOpt.get();
                task.setAssignedTeam(team);
                // Para tarefas de equipa, manter o utilizador original ou usar o primeiro gerente da equipa
                if (task.getUser() == null) {
                    // Se não há utilizador definido, usar o criador da tarefa ou admin
                    Optional<User> adminOpt = userRepository.findByUsername("admin");
                    if (adminOpt.isPresent()) {
                        task.setUser(adminOpt.get());
                    }
                }
                newAssignmentInfo = "Equipa: " + team.getName();
                
                System.out.println("TaskController: Task reassigned to team: " + team.getName());
            } else if (assignedUserId != null) {
                // Atribuir a utilizador específico
                Optional<User> userOpt = userRepository.findById(assignedUserId);
                if (!userOpt.isPresent()) {
                    response.put("success", false);
                    response.put("message", "Utilizador não encontrado");
                    return ResponseEntity.status(404).body(response);
                }
                
                User user = userOpt.get();
                if (!user.getActive()) {
                    response.put("success", false);
                    response.put("message", "Utilizador não está ativo");
                    return ResponseEntity.status(400).body(response);
                }
                
                task.setUser(user);
                
                // Para tarefas individuais, também associar à equipa do utilizador para visibilidade do gerente
                if (user.getTeams() != null && !user.getTeams().isEmpty()) {
                    task.setAssignedTeam(user.getTeams().get(0)); // Usar primeira equipa
                }
                
                newAssignmentInfo = "Utilizador: " + user.getFullName();
                if (user.getTeams() != null && !user.getTeams().isEmpty()) {
                    newAssignmentInfo += " (Equipa: " + user.getTeams().get(0).getName() + ")";
                }
                
                System.out.println("TaskController: Task reassigned to user: " + user.getFullName());
            } else {
                response.put("success", false);
                response.put("message", "Deve especificar um utilizador ou equipa para atribuição");
                return ResponseEntity.status(400).body(response);
            }
            
            // Atualizar timestamp de modificação
            task.setUpdatedAt(LocalDateTime.now());
            
            // Guardar mudanças
            Task savedTask = taskRepository.save(task);
            
            // Criar comentário automático do sistema sobre a reatribuição
            if (taskCommentRepository != null) {
                try {
                    String reassignmentMessage = String.format(
                        "Tarefa reatribuída de [%s] para [%s]", 
                        previousAssignment != null ? previousAssignment : "Não atribuída", 
                        newAssignmentInfo
                    );
                    
                    // Usar o utilizador do sistema ou o primeiro admin encontrado
                    Optional<User> systemUserOpt = userRepository.findByUsername("admin");
                    if (!systemUserOpt.isPresent()) {
                        systemUserOpt = userRepository.findAll().stream()
                            .filter(u -> u.getRole().name().contains("ADMIN"))
                            .findFirst();
                    }
                    
                    if (systemUserOpt.isPresent()) {
                        TaskComment systemComment = new TaskComment(savedTask, systemUserOpt.get(), reassignmentMessage, true);
                        taskCommentRepository.save(systemComment);
                    }
                } catch (Exception e) {
                    // Não falhar a reatribuição se o comentário falhar
                    System.err.println("Erro ao criar comentário do sistema: " + e.getMessage());
                }
            }
            
            response.put("success", true);
            response.put("message", "Tarefa reatribuída com sucesso");
            response.put("task", savedTask);
            response.put("newAssignment", newAssignmentInfo);
            response.put("previousAssignment", previousAssignment);
            
            System.out.println("TaskController: Task reassignment completed successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("TaskController: Error reassigning task: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "Erro interno do servidor: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Classe para request de atribuição de tarefa
    public static class AssignTaskRequest {
        private Long userId;
        private Long assignedById;
        private Long teamId;

        public AssignTaskRequest() {}

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getAssignedById() {
            return assignedById;
        }

        public void setAssignedById(Long assignedById) {
            this.assignedById = assignedById;
        }

        public Long getTeamId() {
            return teamId;
        }

        public void setTeamId(Long teamId) {
            this.teamId = teamId;
        }
    }
}