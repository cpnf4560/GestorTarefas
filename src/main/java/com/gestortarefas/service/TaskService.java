package com.gestortarefas.service;

import com.gestortarefas.model.Task;
import com.gestortarefas.model.Task.TaskStatus;
import com.gestortarefas.model.Task.TaskPriority;
import com.gestortarefas.model.User;
import com.gestortarefas.model.Team;
import com.gestortarefas.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

/**
 * Serviço para gestão de tarefas
 */
@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Cria uma nova tarefa
     */
    public Task createTask(String title, String description, TaskPriority priority, User user) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority != null ? priority : TaskPriority.NORMAL);
        task.setUser(user);
        task.setStatus(TaskStatus.PENDENTE);
        
        return taskRepository.save(task);
    }

    /**
     * Cria uma nova tarefa com data limite
     */
    public Task createTask(String title, String description, TaskPriority priority, 
                          LocalDateTime dueDate, User user) {
        Task task = createTask(title, description, priority, user);
        task.setDueDate(dueDate);
        return taskRepository.save(task);
    }

    /**
     * Busca tarefa por ID
     */
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    /**
     * Lista todas as tarefas de um utilizador
     */
    public List<Task> findTasksByUser(User user) {
        return taskRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Lista tarefas de um utilizador ordenadas por prioridade
     */
    public List<Task> findTasksByUserOrderedByPriority(User user) {
        return taskRepository.findByUserOrderByPriorityAndCreatedAt(user);
    }

    /**
     * Lista tarefas por status
     */
    public List<Task> findTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    /**
     * Lista tarefas de um utilizador por status
     */
    public List<Task> findTasksByUserAndStatus(User user, TaskStatus status) {
        return taskRepository.findByUserAndStatus(user, status);
    }

    /**
     * Lista tarefas por prioridade
     */
    public List<Task> findTasksByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority);
    }

    /**
     * Lista tarefas de um utilizador por prioridade
     */
    public List<Task> findTasksByUserAndPriority(User user, TaskPriority priority) {
        return taskRepository.findByUserAndPriority(user, priority);
    }

    /**
     * Pesquisa tarefas por título
     */
    public List<Task> searchTasksByTitle(String title) {
        return taskRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Pesquisa tarefas de um utilizador por título
     */
    public List<Task> searchTasksByUserAndTitle(User user, String title) {
        return taskRepository.findByUserAndTitleContainingIgnoreCase(user, title);
    }

    /**
     * Atualiza uma tarefa
     */
    public Task updateTask(Task task) {
        if (task.getId() == null) {
            throw new IllegalArgumentException("ID da tarefa não pode ser nulo");
        }
        
        Optional<Task> existingTask = taskRepository.findById(task.getId());
        if (existingTask.isEmpty()) {
            throw new IllegalArgumentException("Tarefa não encontrada");
        }

        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    /**
     * Altera o status de uma tarefa
     */
    public boolean updateTaskStatus(Long taskId, TaskStatus newStatus) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return false;
        }

        Task task = taskOpt.get();
        task.setStatus(newStatus);
        taskRepository.save(task);
        return true;
    }

    /**
     * Marca uma tarefa como concluída
     */
    public boolean completeTask(Long taskId) {
        return updateTaskStatus(taskId, TaskStatus.CONCLUIDA);
    }

    /**
     * Marca uma tarefa como em andamento
     */
    public boolean startTask(Long taskId) {
        return updateTaskStatus(taskId, TaskStatus.EM_ANDAMENTO);
    }

    /**
     * Cancela uma tarefa
     */
    public boolean cancelTask(Long taskId) {
        return updateTaskStatus(taskId, TaskStatus.CANCELADA);
    }

    /**
     * Define/atualiza a data limite de uma tarefa
     */
    public boolean setTaskDueDate(Long taskId, LocalDateTime dueDate) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return false;
        }

        Task task = taskOpt.get();
        task.setDueDate(dueDate);
        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);
        return true;
    }

    /**
     * Lista tarefas em atraso
     */
    public List<Task> findOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDateTime.now());
    }

    /**
     * Lista tarefas em atraso de um utilizador
     */
    public List<Task> findOverdueTasksByUser(User user) {
        return taskRepository.findOverdueTasksByUser(user, LocalDateTime.now());
    }

    /**
     * Lista tarefas com prazo próximo (próximos 3 dias)
     */
    public List<Task> findTasksDueSoon() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysLater = now.plusDays(3);
        return taskRepository.findTasksDueSoon(now, threeDaysLater);
    }

    /**
     * Lista tarefas recentemente concluídas
     */
    public List<Task> findRecentlyCompletedTasks() {
        return taskRepository.findRecentlyCompletedTasks();
    }

    /**
     * Obtém estatísticas de tarefas por utilizador
     */
    public Map<String, Long> getTaskStatsByUser(User user) {
        Map<String, Long> stats = new HashMap<>();
        
        // Inicializar contadores
        for (TaskStatus status : TaskStatus.values()) {
            stats.put(status.getDisplayName(), 0L);
        }
        
        // Obter dados do repositório
        List<Object[]> results = taskRepository.getTaskStatsByUser(user);
        for (Object[] result : results) {
            TaskStatus status = (TaskStatus) result[0];
            Long count = (Long) result[1];
            stats.put(status.getDisplayName(), count);
        }
        
        return stats;
    }

    /**
     * Conta tarefas por status para um utilizador
     */
    public long countTasksByUserAndStatus(User user, TaskStatus status) {
        return taskRepository.countByUserAndStatus(user, status);
    }

    /**
     * Elimina uma tarefa
     */
    public boolean deleteTask(Long taskId) {
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
            return true;
        }
        return false;
    }

    /**
     * Elimina todas as tarefas de um utilizador
     */
    public void deleteAllTasksByUser(User user) {
        List<Task> userTasks = taskRepository.findByUser(user);
        taskRepository.deleteAll(userTasks);
    }

    /**
     * Lista todas as tarefas
     */
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Duplica uma tarefa existente
     */
    public Task duplicateTask(Long taskId, User user) {
        Optional<Task> originalTaskOpt = taskRepository.findById(taskId);
        if (originalTaskOpt.isEmpty()) {
            throw new IllegalArgumentException("Tarefa original não encontrada");
        }

        Task originalTask = originalTaskOpt.get();
        Task duplicatedTask = new Task();
        duplicatedTask.setTitle(originalTask.getTitle() + " (Cópia)");
        duplicatedTask.setDescription(originalTask.getDescription());
        duplicatedTask.setPriority(originalTask.getPriority());
        duplicatedTask.setDueDate(originalTask.getDueDate());
        duplicatedTask.setUser(user);
        duplicatedTask.setStatus(TaskStatus.PENDENTE);

        return taskRepository.save(duplicatedTask);
    }

    /**
     * Remove tarefas concluídas antigas (mais de 30 dias)
     */
    public void cleanupOldCompletedTasks() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        taskRepository.deleteOldCompletedTasks(cutoffDate);
    }

    // Novos métodos para suporte a equipas

    /**
     * Cria uma nova tarefa com equipa atribuída
     */
    public Task createTaskWithTeam(String title, String description, TaskPriority priority,
                                  LocalDateTime dueDate, User user, Team assignedTeam, 
                                  User createdBy, String tags, Integer estimatedHours) {
        
        // Verificar se o utilizador pode criar tarefas para a equipa
        if (assignedTeam != null && !canAssignTaskToTeam(assignedTeam, createdBy)) {
            throw new IllegalArgumentException("Sem permissão para atribuir tarefas a esta equipa");
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority != null ? priority : TaskPriority.NORMAL);
        task.setDueDate(dueDate);
        task.setUser(user);
        task.setAssignedTeam(assignedTeam);
        task.setCreatedBy(createdBy);
        task.setStatus(TaskStatus.PENDENTE);
        
        if (tags != null && !tags.trim().isEmpty()) {
            task.setTags(tags);
        }
        
        if (estimatedHours != null) {
            task.setEstimatedHours(estimatedHours);
        }

        return taskRepository.save(task);
    }

    /**
     * Busca tarefas visíveis para um utilizador (próprias e da equipa)
     */
    @Transactional(readOnly = true)
    public List<Task> getVisibleTasksForUser(User user) {
        return taskRepository.findVisibleTasksForUser(user);
    }

    /**
     * Busca tarefas para um gerente (das suas equipas)
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksForManager(User manager) {
        return taskRepository.findTasksForManagedTeams(manager);
    }

    /**
     * Busca tarefas de uma equipa específica
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByTeam(Team team, User requester) {
        // Verificar se pode ver as tarefas da equipa
        if (!canViewTeamTasks(team, requester)) {
            throw new IllegalArgumentException("Sem permissão para ver tarefas desta equipa");
        }
        
        return taskRepository.findByAssignedTeam(team);
    }

    /**
     * Busca tarefas por tag
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByTag(String tag) {
        return taskRepository.findByTag(tag);
    }

    /**
     * Dashboard - Busca tarefas pendentes do utilizador
     */
    @Transactional(readOnly = true)
    public List<Task> getPendingTasksForUser(User user) {
        return taskRepository.findPendingTasksByUser(user);
    }

    /**
     * Dashboard - Busca tarefas com prazo hoje do utilizador
     */
    @Transactional(readOnly = true)
    public List<Task> getTodayTasksForUser(User user) {
        return taskRepository.findTasksDueTodayByUser(user, LocalDateTime.now());
    }

    /**
     * Dashboard - Busca tarefas em atraso do utilizador
     */
    @Transactional(readOnly = true)
    public List<Task> getOverdueTasksForUser(User user) {
        return taskRepository.findOverdueTasksByUser(user, LocalDateTime.now());
    }

    /**
     * Dashboard - Busca tarefas concluídas do utilizador
     */
    @Transactional(readOnly = true)
    public List<Task> getCompletedTasksForUser(User user) {
        return taskRepository.findByUserAndStatus(user, TaskStatus.CONCLUIDA);
    }

    /**
     * Dashboard - Busca tarefas pendentes da equipa
     */
    @Transactional(readOnly = true)
    public List<Task> getPendingTasksForTeam(Team team, User requester) {
        if (!canViewTeamTasks(team, requester)) {
            throw new IllegalArgumentException("Sem permissão para ver tarefas desta equipa");
        }
        return taskRepository.findPendingTasksByTeam(team);
    }

    /**
     * Dashboard - Busca tarefas com prazo hoje da equipa
     */
    @Transactional(readOnly = true)
    public List<Task> getTodayTasksForTeam(Team team, User requester) {
        if (!canViewTeamTasks(team, requester)) {
            throw new IllegalArgumentException("Sem permissão para ver tarefas desta equipa");
        }
        return taskRepository.findTasksDueTodayByTeam(team, LocalDateTime.now());
    }

    /**
     * Dashboard - Busca tarefas em atraso da equipa
     */
    @Transactional(readOnly = true)
    public List<Task> getOverdueTasksForTeam(Team team, User requester) {
        if (!canViewTeamTasks(team, requester)) {
            throw new IllegalArgumentException("Sem permissão para ver tarefas desta equipa");
        }
        return taskRepository.findOverdueTasksByTeam(team, LocalDateTime.now());
    }

    /**
     * Dashboard - Busca tarefas concluídas da equipa
     */
    @Transactional(readOnly = true)
    public List<Task> getCompletedTasksForTeam(Team team, User requester) {
        if (!canViewTeamTasks(team, requester)) {
            throw new IllegalArgumentException("Sem permissão para ver tarefas desta equipa");
        }
        return taskRepository.findByAssignedTeamAndStatus(team, TaskStatus.CONCLUIDA);
    }

    /**
     * Atualiza horas reais de uma tarefa
     */
    public Task updateActualHours(Long taskId, Integer actualHours, User requester) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            throw new IllegalArgumentException("Tarefa não encontrada");
        }

        Task task = optionalTask.get();
        
        if (!task.canBeEditedBy(requester)) {
            throw new IllegalArgumentException("Sem permissão para editar esta tarefa");
        }

        task.setActualHours(actualHours);
        return taskRepository.save(task);
    }

    /**
     * Adiciona tags a uma tarefa
     */
    public Task addTagsToTask(Long taskId, String newTags, User requester) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            throw new IllegalArgumentException("Tarefa não encontrada");
        }

        Task task = optionalTask.get();
        
        if (!task.canBeEditedBy(requester)) {
            throw new IllegalArgumentException("Sem permissão para editar esta tarefa");
        }

        String currentTags = task.getTags();
        if (currentTags == null || currentTags.trim().isEmpty()) {
            task.setTags(newTags);
        } else {
            task.setTags(currentTags + ", " + newTags);
        }
        
        return taskRepository.save(task);
    }

    /**
     * Atualiza as tags de uma tarefa
     */
    public Task updateTaskTags(Long taskId, String tags, User requester) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            throw new IllegalArgumentException("Tarefa não encontrada");
        }

        Task task = optionalTask.get();
        
        if (!task.canBeEditedBy(requester)) {
            throw new IllegalArgumentException("Sem permissão para editar esta tarefa");
        }

        task.setTags(tags);
        return taskRepository.save(task);
    }

    /**
     * Estatísticas de tarefas por equipa
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTeamTaskStats(Team team, User requester) {
        if (!canViewTeamTasks(team, requester)) {
            throw new IllegalArgumentException("Sem permissão para ver estatísticas desta equipa");
        }

        Map<String, Object> stats = new HashMap<>();
        
        List<Object[]> rawStats = taskRepository.getTaskStatsByTeam(team);
        Map<String, Long> statusCounts = new HashMap<>();
        
        for (Object[] row : rawStats) {
            TaskStatus status = (TaskStatus) row[0];
            Long count = (Long) row[1];
            statusCounts.put(status.name(), count);
        }
        
        stats.put("statusCounts", statusCounts);
        stats.put("totalTasks", taskRepository.countByAssignedTeamAndStatus(team, null));
        
        return stats;
    }

    /**
     * Estatísticas gerais de tarefas
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getOverallTaskStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Object[]> rawStats = taskRepository.getOverallTaskStats();
        Map<String, Long> statusCounts = new HashMap<>();
        
        for (Object[] row : rawStats) {
            TaskStatus status = (TaskStatus) row[0];
            Long count = (Long) row[1];
            statusCounts.put(status.name(), count);
        }
        
        stats.put("statusCounts", statusCounts);
        stats.put("topUsers", taskRepository.getTopUsersByCompletedTasks());
        stats.put("topTeams", taskRepository.getTopTeamsByCompletedTasks());
        
        return stats;
    }

    // Métodos auxiliares para verificação de permissões

    /**
     * Verifica se um utilizador pode atribuir tarefas a uma equipa
     */
    private boolean canAssignTaskToTeam(Team team, User user) {
        return user.getRole().isAdmin() || 
               team.getManager().equals(user) || 
               team.getMembers().contains(user);
    }

    /**
     * Verifica se um utilizador pode ver tarefas de uma equipa
     */
    private boolean canViewTeamTasks(Team team, User user) {
        return user.getRole().isAdmin() || 
               team.getManager().equals(user) || 
               team.getMembers().contains(user);
    }
}