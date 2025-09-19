package com.gestortarefas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um utilizador do sistema
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome de utilizador é obrigatório")
    @Size(min = 3, max = 50, message = "Nome de utilizador deve ter entre 3 e 50 caracteres")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Size(max = 100, message = "Nome completo não pode exceder 100 caracteres")
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "active")
    private Boolean active = true;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole role = UserRole.FUNCIONARIO;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Task> tasks = new ArrayList<>();
    
    // Relacionamento many-to-many com equipas
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_teams",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    @JsonIgnore
    private List<Team> teams = new ArrayList<>();
    
    // Perfil do utilizador
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private UserProfile profile;
    
    // Equipas geridas por este utilizador (se for gerente)
    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Team> managedTeams = new ArrayList<>();

    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String username, String email, String password) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String username, String email, String password, String fullName) {
        this(username, email, password);
        this.fullName = fullName;
    }
    
    public User(String username, String email, String password, String fullName, UserRole role) {
        this(username, email, password, fullName);
        this.role = role;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public List<Team> getTeams() {
        return teams;
    }
    
    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
    
    public UserProfile getProfile() {
        return profile;
    }
    
    public void setProfile(UserProfile profile) {
        this.profile = profile;
        if (profile != null) {
            profile.setUser(this);
        }
    }
    
    public List<Team> getManagedTeams() {
        return managedTeams;
    }
    
    public void setManagedTeams(List<Team> managedTeams) {
        this.managedTeams = managedTeams;
    }

    // Métodos auxiliares
    public void addTask(Task task) {
        tasks.add(task);
        task.setUser(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setUser(null);
    }
    
    // Métodos de gestão de equipas
    public void addToTeam(Team team) {
        if (!teams.contains(team)) {
            teams.add(team);
            team.getMembers().add(this);
        }
    }
    
    public void removeFromTeam(Team team) {
        if (teams.contains(team)) {
            teams.remove(team);
            team.getMembers().remove(this);
        }
    }
    
    public boolean isMemberOfTeam(Team team) {
        return teams.contains(team);
    }
    
    public boolean isManagerOfTeam(Team team) {
        return managedTeams.contains(team);
    }
    
    // Métodos de verificação de permissões
    public boolean isAdmin() {
        return role != null && role.isAdmin();
    }
    
    public boolean isManagerOrAbove() {
        return role != null && role.isManagerOrAbove();
    }
    
    public boolean canManageTeams() {
        return role != null && role.canManageTeams();
    }
    
    public boolean canManageUsers() {
        return role != null && role.canManageUsers();
    }
    
    public boolean canViewTeamReports() {
        return role != null && role.canViewTeamReports();
    }
    
    // Método para verificar se pode ver tarefas de um utilizador específico
    public boolean canViewUserTasks(User otherUser) {
        if (this.equals(otherUser)) return true; // Pode ver as próprias tarefas
        if (isAdmin()) return true; // Admin pode ver todas
        
        // Gerente pode ver tarefas dos membros das suas equipas
        if (isManagerOrAbove()) {
            return managedTeams.stream()
                    .anyMatch(team -> team.hasMember(otherUser));
        }
        
        return false;
    }
    
    // Método para obter nome para exibição
    public String getDisplayName() {
        return fullName != null && !fullName.trim().isEmpty() ? fullName : username;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", teamsCount=" + (teams != null ? teams.size() : 0) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id != null && id.equals(user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}