package com.gestortarefas.controller;

import com.gestortarefas.model.Task;
import com.gestortarefas.model.Task.TaskStatus;
import com.gestortarefas.model.Task.TaskPriority;
import com.gestortarefas.model.User;
import com.gestortarefas.service.TaskService;
import com.gestortarefas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * Controlador REST para operações relacionadas às tarefas
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

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
            
            System.out.println("TaskController: Parsed data - title: " + title + ", userId: " + userId + 
                             ", priority: " + priorityStr + ", dueDate: " + dueDateStr);
            
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                System.out.println("TaskController: User not found for ID: " + userId);
                response.put("success", false);
                response.put("message", "Utilizador não encontrado");
                return ResponseEntity.badRequest().body(response);
            }
            
            TaskPriority priority = priorityStr != null ? 
                TaskPriority.valueOf(priorityStr.toUpperCase()) : TaskPriority.NORMAL;
                
            LocalDateTime dueDate = null;
            if (dueDateStr != null && !dueDateStr.trim().isEmpty()) {
                dueDate = LocalDateTime.parse(dueDateStr);
            }
            
            System.out.println("TaskController: Creating task with priority: " + priority + ", dueDate: " + dueDate);
            
            Task task;
            if (dueDate != null) {
                task = taskService.createTask(title, description, priority, dueDate, userOpt.get());
            } else {
                task = taskService.createTask(title, description, priority, userOpt.get());
            }
            
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
}