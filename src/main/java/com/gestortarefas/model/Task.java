package com.gestortarefas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * Entidade que representa uma tarefa no sistema
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 3, max = 100, message = "Título deve ter entre 3 e 100 caracteres")
    @Column(nullable = false)
    private String title;

    @Size(max = 500, message = "Descrição não pode exceder 500 caracteres")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDENTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.NORMAL;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Equipa à qual a tarefa está atribuída (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_team_id")
    private Team assignedTeam;
    
    // Utilizador que criou a tarefa (pode ser diferente do utilizador atribuído)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;
    
    // Tags ou etiquetas da tarefa
    @Column(name = "tags")
    private String tags;
    
    // Estimativa de horas para conclusão
    @Column(name = "estimated_hours")
    private Integer estimatedHours;
    
    // Horas realmente gastas na tarefa
    @Column(name = "actual_hours")
    private Integer actualHours;

    // Enums para Status e Prioridade
    public enum TaskStatus {
        PENDENTE("Pendente"),
        EM_ANDAMENTO("Em Andamento"),
        CONCLUIDA("Concluída"),
        CANCELADA("Cancelada");

        private final String displayName;

        TaskStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum TaskPriority {
        BAIXA("Baixa"),
        NORMAL("Normal"),
        ALTA("Alta"),
        URGENTE("Urgente");

        private final String displayName;

        TaskPriority(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Task() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Task(String title, String description, User user) {
        this();
        this.title = title;
        this.description = description;
        this.user = user;
    }

    public Task(String title, String description, TaskPriority priority, User user) {
        this(title, description, user);
        this.priority = priority;
    }
    
    public Task(String title, String description, TaskPriority priority, User user, Team assignedTeam) {
        this(title, description, priority, user);
        this.assignedTeam = assignedTeam;
    }
    
    public Task(String title, String description, TaskPriority priority, User user, User createdBy) {
        this(title, description, priority, user);
        this.createdBy = createdBy;
    }

    // Métodos de lifecycle JPA
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.status == TaskStatus.CONCLUIDA && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        } else if (this.status != TaskStatus.CONCLUIDA) {
            this.completedAt = null;
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        if (status == TaskStatus.CONCLUIDA && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        } else if (status != TaskStatus.CONCLUIDA) {
            this.completedAt = null;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public Team getAssignedTeam() {
        return assignedTeam;
    }
    
    public void setAssignedTeam(Team assignedTeam) {
        this.assignedTeam = assignedTeam;
    }
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public Integer getEstimatedHours() {
        return estimatedHours;
    }
    
    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }
    
    public Integer getActualHours() {
        return actualHours;
    }
    
    public void setActualHours(Integer actualHours) {
        this.actualHours = actualHours;
    }

    // Métodos auxiliares
    public boolean isCompleted() {
        return status == TaskStatus.CONCLUIDA;
    }

    public boolean isPending() {
        return status == TaskStatus.PENDENTE;
    }

    public boolean isOverdue() {
        return dueDate != null && 
               LocalDateTime.now().isAfter(dueDate) && 
               !isCompleted();
    }
    
    public boolean isDueToday() {
        if (dueDate == null) return false;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        return dueDate.isAfter(startOfDay) && dueDate.isBefore(endOfDay);
    }
    
    public boolean isAssignedToTeam() {
        return assignedTeam != null;
    }
    
    public boolean isAssignedToUser(User user) {
        return this.user != null && this.user.equals(user);
    }
    
    public boolean isAssignedToTeamMember(User user) {
        return isAssignedToTeam() && assignedTeam.hasMember(user);
    }
    
    public boolean canBeViewedBy(User user) {
        // Pode ver se for o utilizador atribuído
        if (isAssignedToUser(user)) return true;
        
        // Pode ver se for membro da equipa atribuída
        if (isAssignedToTeamMember(user)) return true;
        
        // Pode ver se for administrador
        if (user.isAdmin()) return true;
        
        // Pode ver se for gerente da equipa atribuída
        if (isAssignedToTeam() && assignedTeam.isManager(user)) return true;
        
        return false;
    }
    
    public boolean canBeEditedBy(User user) {
        // Pode editar se for o utilizador atribuído
        if (isAssignedToUser(user)) return true;
        
        // Pode editar se for administrador
        if (user.isAdmin()) return true;
        
        // Pode editar se for gerente da equipa atribuída
        if (isAssignedToTeam() && assignedTeam.isManager(user)) return true;
        
        // Pode editar se foi quem criou a tarefa
        if (createdBy != null && createdBy.equals(user)) return true;
        
        return false;
    }
    
    public String getAssignmentInfo() {
        StringBuilder sb = new StringBuilder();
        
        if (user != null) {
            sb.append("Atribuída a: ").append(user.getDisplayName());
        }
        
        if (assignedTeam != null) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append("Equipa: ").append(assignedTeam.getName());
        }
        
        return sb.toString();
    }
    
    public boolean hasEstimatedTime() {
        return estimatedHours != null && estimatedHours > 0;
    }
    
    public boolean isOnSchedule() {
        if (!hasEstimatedTime() || actualHours == null) return true;
        return actualHours <= estimatedHours;
    }

    public String getStatusDisplayName() {
        return status.getDisplayName();
    }

    public String getPriorityDisplayName() {
        return priority.getDisplayName();
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", assignedTo='" + (user != null ? user.getDisplayName() : "N/A") + '\'' +
                ", assignedTeam='" + (assignedTeam != null ? assignedTeam.getName() : "N/A") + '\'' +
                ", createdAt=" + createdAt +
                ", dueDate=" + dueDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id != null && id.equals(task.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}