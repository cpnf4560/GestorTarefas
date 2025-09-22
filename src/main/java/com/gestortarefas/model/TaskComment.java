package com.gestortarefas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

/**
 * Entidade que representa um comentário numa tarefa (sistema de chat)
 */
@Entity
@Table(name = "task_comments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TaskComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonIgnoreProperties({"comments", "user", "assignedTeam", "createdBy"})
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"tasks", "teams", "managedTeams", "profile"})
    private User user;

    @NotBlank(message = "Comentário não pode estar vazio")
    @Size(max = 1000, message = "Comentário não pode exceder 1000 caracteres")
    @Column(name = "comment_text", nullable = false, columnDefinition = "TEXT")
    private String commentText;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_system_message", nullable = false)
    private Boolean isSystemMessage = false;

    // Constructors
    public TaskComment() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public TaskComment(Task task, User user, String commentText) {
        this();
        this.task = task;
        this.user = user;
        this.commentText = commentText;
    }

    public TaskComment(Task task, User user, String commentText, Boolean isSystemMessage) {
        this(task, user, commentText);
        this.isSystemMessage = isSystemMessage;
    }

    // Métodos de lifecycle JPA
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
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

    public Boolean getIsSystemMessage() {
        return isSystemMessage;
    }

    public void setIsSystemMessage(Boolean isSystemMessage) {
        this.isSystemMessage = isSystemMessage;
    }

    // Métodos utilitários
    public boolean isFromUser(User user) {
        return this.user != null && this.user.equals(user);
    }

    public String getDisplayTime() {
        // Formato: "22/09/2025 15:30"
        return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getUserDisplayName() {
        return user != null ? user.getFullName() : "Utilizador Desconhecido";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskComment)) return false;
        TaskComment that = (TaskComment) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "TaskComment{" +
                "id=" + id +
                ", taskId=" + (task != null ? task.getId() : "N/A") +
                ", userId=" + (user != null ? user.getId() : "N/A") +
                ", commentText='" + (commentText != null ? commentText.substring(0, Math.min(50, commentText.length())) : "") + "..." + '\'' +
                ", createdAt=" + createdAt +
                ", isSystemMessage=" + isSystemMessage +
                '}';
    }
}