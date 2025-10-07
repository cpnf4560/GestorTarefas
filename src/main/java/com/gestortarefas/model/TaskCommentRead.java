package com.gestortarefas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade que rastreia quando um usuário visualizou comentários de uma tarefa pela última vez
 * Usada para determinar quantos comentários não lidos existem
 */
@Entity
@Table(name = "task_comment_reads", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"task_id", "user_id"}))
public class TaskCommentRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "last_read_at", nullable = false)
    private LocalDateTime lastReadAt;

    // Construtores
    public TaskCommentRead() {
        this.lastReadAt = LocalDateTime.now();
    }

    public TaskCommentRead(Task task, User user) {
        this.task = task;
        this.user = user;
        this.lastReadAt = LocalDateTime.now();
    }

    public TaskCommentRead(Task task, User user, LocalDateTime lastReadAt) {
        this.task = task;
        this.user = user;
        this.lastReadAt = lastReadAt;
    }

    // Métodos de lifecycle JPA
    @PreUpdate
    public void preUpdate() {
        this.lastReadAt = LocalDateTime.now();
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

    public LocalDateTime getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(LocalDateTime lastReadAt) {
        this.lastReadAt = lastReadAt;
    }

    /**
     * Atualiza o timestamp de leitura para agora
     */
    public void markAsRead() {
        this.lastReadAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "TaskCommentRead{" +
                "id=" + id +
                ", taskId=" + (task != null ? task.getId() : null) +
                ", userId=" + (user != null ? user.getId() : null) +
                ", lastReadAt=" + lastReadAt +
                '}';
    }
}