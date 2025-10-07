package com.gestortarefas.repository;

import com.gestortarefas.model.Task;
import com.gestortarefas.model.Task.TaskStatus;
import com.gestortarefas.model.Task.TaskPriority;
import com.gestortarefas.model.User;
import com.gestortarefas.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para operações de base de dados relacionadas às tarefas
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Busca todas as tarefas de um utilizador
     */
    List<Task> findByUser(User user);

    /**
     * Busca tarefas de um utilizador ordenadas por data de criação (mais recente primeiro)
     */
    List<Task> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Busca tarefas por status
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Busca tarefas por múltiplos status
     */
    List<Task> findByStatusIn(List<TaskStatus> statuses);

    /**
     * Busca tarefas de um utilizador por status
     */
    List<Task> findByUserAndStatus(User user, TaskStatus status);

  /**
   * Busca tarefas de um utilizador por múltiplos status
   */
  List<Task> findByUserAndStatusIn(User user, List<TaskStatus> statuses);

    /**
     * Busca tarefas por prioridade
     */
    List<Task> findByPriority(TaskPriority priority);

    /**
     * Busca tarefas de um utilizador por prioridade
     */
    List<Task> findByUserAndPriority(User user, TaskPriority priority);

    /**
     * Busca tarefas que contêm determinado texto no título
     */
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Task> findByTitleContainingIgnoreCase(@Param("title") String title);

    /**
     * Busca tarefas de um utilizador que contêm determinado texto no título
     */
    @Query("SELECT t FROM Task t WHERE t.user = :user AND LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Task> findByUserAndTitleContainingIgnoreCase(@Param("user") User user, @Param("title") String title);

    /**
     * Busca tarefas em atraso (data limite passou e não estão concluídas)
     */
       @Query("SELECT t FROM Task t WHERE t.dueDate < :currentDate AND t.status NOT IN ('CONCLUIDA','FINALIZADO') AND (t.archived = false OR t.archived IS NULL)")
       List<Task> findOverdueTasks(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Busca tarefas em atraso de um utilizador específico
     */
       @Query("SELECT t FROM Task t WHERE t.user = :user AND t.dueDate < :currentDate AND t.status NOT IN ('CONCLUIDA','FINALIZADO') AND (t.archived = false OR t.archived IS NULL)")
       List<Task> findOverdueTasksByUser(@Param("user") User user, @Param("currentDate") LocalDateTime currentDate);

    /**
     * Busca tarefas com prazo próximo (próximos dias)
     */
       @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate AND t.status NOT IN ('CONCLUIDA','FINALIZADO') AND (t.archived = false OR t.archived IS NULL)")
       List<Task> findTasksDueSoon(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Busca tarefas recentemente concluídas
     */
       @Query("SELECT t FROM Task t WHERE t.status = 'CONCLUIDA' AND (t.archived = false OR t.archived IS NULL) ORDER BY t.completedAt DESC")
       List<Task> findRecentlyCompletedTasks();

    /**
     * Conta tarefas por status para um utilizador
     */
    long countByUserAndStatus(User user, TaskStatus status);

    /**
     * Busca tarefas ordenadas por prioridade (urgente primeiro) e data de criação
     */
    @Query("SELECT t FROM Task t WHERE t.user = :user ORDER BY " +
           "CASE t.priority WHEN 'URGENTE' THEN 1 WHEN 'ALTA' THEN 2 WHEN 'NORMAL' THEN 3 WHEN 'BAIXA' THEN 4 END, " +
           "t.createdAt DESC")
    List<Task> findByUserOrderByPriorityAndCreatedAt(@Param("user") User user);

    /**
     * Busca estatísticas de tarefas por utilizador
     */
    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE t.user = :user GROUP BY t.status")
    List<Object[]> getTaskStatsByUser(@Param("user") User user);

    /**
     * Remove todas as tarefas concluídas há mais de X dias
     */
    @Query("DELETE FROM Task t WHERE t.status = 'CONCLUIDA' AND t.completedAt < :cutoffDate")
    void deleteOldCompletedTasks(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Novos métodos para suporte à funcionalidade de equipas

    /**
     * Busca tarefas atribuídas a uma equipa
     */
    List<Task> findByAssignedTeam(Team assignedTeam);

    /**
     * Busca tarefas atribuídas a uma equipa por status
     */
    List<Task> findByAssignedTeamAndStatus(Team assignedTeam, TaskStatus status);

  /**
   * Busca tarefas atribuídas a uma equipa por múltiplos status
   */
  List<Task> findByAssignedTeamAndStatusIn(Team assignedTeam, List<TaskStatus> statuses);

    /**
     * Busca tarefas criadas por um utilizador específico
     */
    List<Task> findByCreatedBy(User createdBy);

    /**
     * Busca tarefas criadas por um utilizador específico com status
     */
    List<Task> findByCreatedByAndStatus(User createdBy, TaskStatus status);

    /**
     * Busca tarefas que podem ser vistas por um utilizador (próprias ou da sua equipa)
     */
    @Query("SELECT t FROM Task t WHERE t.user = :user OR " +
           "(t.assignedTeam IS NOT NULL AND :user MEMBER OF t.assignedTeam.members)")
    List<Task> findVisibleTasksForUser(@Param("user") User user);

    /**
     * Busca tarefas da equipa de um gerente
     */
    @Query("SELECT t FROM Task t WHERE t.assignedTeam IN " +
           "(SELECT tm FROM Team tm WHERE tm.manager = :manager)")
    List<Task> findTasksForManagedTeams(@Param("manager") User manager);

    /**
     * Busca tarefas por tag (procura na string de tags separadas por vírgula)
     */
    @Query("SELECT t FROM Task t WHERE t.tags IS NOT NULL AND LOWER(t.tags) LIKE LOWER(CONCAT('%', :tag, '%'))")
    List<Task> findByTag(@Param("tag") String tag);

    /**
     * Busca tarefas pendentes (não concluídas)
     */
    @Query("SELECT t FROM Task t WHERE t.status != 'CONCLUIDA'")
    List<Task> findPendingTasks();

    /**
     * Busca tarefas pendentes de um utilizador (exclui arquivadas)
     */
    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.status != 'CONCLUIDA' AND (t.archived = false OR t.archived IS NULL)")
    List<Task> findPendingTasksByUser(@Param("user") User user);

    /**
     * Busca tarefas pendentes de uma equipa (exclui arquivadas)
     */
    @Query("SELECT t FROM Task t WHERE t.assignedTeam = :team AND t.status != 'CONCLUIDA' AND (t.archived = false OR t.archived IS NULL)")
    List<Task> findPendingTasksByTeam(@Param("team") Team team);

    /**
     * Busca tarefas com prazo hoje (exclui arquivadas)
     */
       @Query("SELECT t FROM Task t WHERE CAST(t.dueDate AS DATE) = CAST(:today AS DATE) AND t.status NOT IN ('CONCLUIDA','FINALIZADO') AND (t.archived = false OR t.archived IS NULL)")
       List<Task> findTasksDueToday(@Param("today") LocalDateTime today);

    /**
     * Busca tarefas com prazo hoje de um utilizador (exclui arquivadas)
     */
       @Query("SELECT t FROM Task t WHERE t.user = :user AND CAST(t.dueDate AS DATE) = CAST(:today AS DATE) AND t.status NOT IN ('CONCLUIDA','FINALIZADO') AND (t.archived = false OR t.archived IS NULL)")
       List<Task> findTasksDueTodayByUser(@Param("user") User user, @Param("today") LocalDateTime today);

    /**
     * Busca tarefas com prazo hoje de uma equipa (exclui arquivadas)
     */
       @Query("SELECT t FROM Task t WHERE t.assignedTeam = :team AND CAST(t.dueDate AS DATE) = CAST(:today AS DATE) AND t.status NOT IN ('CONCLUIDA','FINALIZADO') AND (t.archived = false OR t.archived IS NULL)")
       List<Task> findTasksDueTodayByTeam(@Param("team") Team team, @Param("today") LocalDateTime today);

    /**
     * Busca tarefas em atraso de uma equipa
     */
       @Query("SELECT t FROM Task t WHERE t.assignedTeam = :team AND t.dueDate < :currentDate AND t.status NOT IN ('CONCLUIDA','FINALIZADO') AND (t.archived = false OR t.archived IS NULL)")
       List<Task> findOverdueTasksByTeam(@Param("team") Team team, @Param("currentDate") LocalDateTime currentDate);

    /**
     * Conta tarefas por status para uma equipa
     */
    long countByAssignedTeamAndStatus(Team assignedTeam, TaskStatus status);

    /**
     * Estatísticas de tarefas por equipa
     */
    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE t.assignedTeam = :team GROUP BY t.status")
    List<Object[]> getTaskStatsByTeam(@Param("team") Team team);

    /**
     * Estatísticas gerais de tarefas
     */
    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> getOverallTaskStats();

    /**
     * Busca tarefas por período de criação
     */
    @Query("SELECT t FROM Task t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Task> findTasksCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Busca tarefas por período de conclusão
     */
    @Query("SELECT t FROM Task t WHERE t.completedAt BETWEEN :startDate AND :endDate AND t.status = 'CONCLUIDA'")
    List<Task> findTasksCompletedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Média de horas estimadas vs reais por equipa
     */
    @Query("SELECT AVG(t.estimatedHours), AVG(t.actualHours) FROM Task t WHERE t.assignedTeam = :team AND t.actualHours IS NOT NULL")
    Object[] getAverageHoursByTeam(@Param("team") Team team);

    /**
     * Top utilizadores por tarefas concluídas
     */
       @Query("SELECT t.user, COUNT(t) FROM Task t WHERE t.status = 'CONCLUIDA' AND (t.archived = false OR t.archived IS NULL) GROUP BY t.user ORDER BY COUNT(t) DESC")
       List<Object[]> getTopUsersByCompletedTasks();

    /**
     * Top equipas por tarefas concluídas
     */
       @Query("SELECT t.assignedTeam, COUNT(t) FROM Task t WHERE t.status = 'CONCLUIDA' AND (t.archived = false OR t.archived IS NULL) AND t.assignedTeam IS NOT NULL GROUP BY t.assignedTeam ORDER BY COUNT(t) DESC")
       List<Object[]> getTopTeamsByCompletedTasks();

    /**
     * Busca tarefas arquivadas ordenadas por data de conclusão (mais recente primeiro)
     */
    List<Task> findByArchivedTrueOrderByCompletedAtDesc();

    /**
     * Busca tarefas não arquivadas (para não mostrar arquivadas na dashboard principal)
     */
    List<Task> findByArchivedFalse();

    /**
     * Busca tarefas não arquivadas por status
     */
    List<Task> findByArchivedFalseAndStatus(TaskStatus status);

    /**
     * Busca tarefas não arquivadas de um utilizador
     */
    List<Task> findByArchivedFalseAndUser(User user);
}