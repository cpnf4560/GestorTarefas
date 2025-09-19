package com.gestortarefas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa uma equipa no sistema
 */
@Entity
@Table(name = "teams")
public class Team {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relacionamento com utilizadores (membros da equipa)
    @ManyToMany(mappedBy = "teams", fetch = FetchType.LAZY)
    private List<User> members = new ArrayList<>();
    
    // Relacionamento com tarefas da equipa
    @OneToMany(mappedBy = "assignedTeam", fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();
    
    // Gerente da equipa (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;
    
    // Construtores
    public Team() {}
    
    public Team(String name, String description) {
        this.name = name;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
    
    public Team(String name, String description, User manager) {
        this(name, description);
        this.manager = manager;
    }
    
    // Métodos de negócio
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Adiciona um membro à equipa
     */
    public void addMember(User user) {
        if (!members.contains(user)) {
            members.add(user);
            user.getTeams().add(this);
        }
    }
    
    /**
     * Remove um membro da equipa
     */
    public void removeMember(User user) {
        if (members.contains(user)) {
            members.remove(user);
            user.getTeams().remove(this);
        }
    }
    
    /**
     * Verifica se um utilizador é membro desta equipa
     */
    public boolean hasMember(User user) {
        return members.contains(user);
    }
    
    /**
     * Verifica se um utilizador é gerente desta equipa
     */
    public boolean isManager(User user) {
        return manager != null && manager.equals(user);
    }
    
    /**
     * Conta o número de tarefas ativas da equipa
     */
    public long getActiveTasksCount() {
        return tasks.stream()
                .filter(task -> task.getStatus() != Task.TaskStatus.CONCLUIDA && 
                               task.getStatus() != Task.TaskStatus.CANCELADA)
                .count();
    }
    
    /**
     * Conta o número de tarefas concluídas da equipa
     */
    public long getCompletedTasksCount() {
        return tasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.CONCLUIDA)
                .count();
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
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
    
    public List<User> getMembers() {
        return members;
    }
    
    public void setMembers(List<User> members) {
        this.members = members;
    }
    
    public List<Task> getTasks() {
        return tasks;
    }
    
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    
    public User getManager() {
        return manager;
    }
    
    public void setManager(User manager) {
        this.manager = manager;
    }
    
    // Métodos equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team)) return false;
        Team team = (Team) o;
        return id != null && id.equals(team.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", membersCount=" + (members != null ? members.size() : 0) +
                '}';
    }
}