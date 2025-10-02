package com.gestortarefas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA que representa um utilizador do sistema de gestão de tarefas.
 * 
 * Esta entidade mapeia para a tabela 'users' na base de dados e contém
 * informações básicas do utilizador como credenciais, perfil e relacionamentos
 * com tarefas e equipas.
 * 
 * Relacionamentos:
 * - OneToMany com Task (tarefas atribuídas ao utilizador)
 * - ManyToMany com Team (equipas das quais o utilizador faz parte)
 * - OneToOne com UserProfile (perfil detalhado do utilizador)
 * - OneToMany com Team como manager (equipas que o utilizador gere)
 */
@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignora propriedades do Hibernate no JSON
public class User {

    // Identificador único gerado automaticamente pela base de dados
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome de utilizador único para login (validado entre 3-50 caracteres)
    @NotBlank(message = "Nome de utilizador é obrigatório")
    @Size(min = 3, max = 50, message = "Nome de utilizador deve ter entre 3 e 50 caracteres")
    @Column(unique = true, nullable = false)
    private String username;

    // Email único para comunicações (deve ter formato válido)
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Column(unique = true, nullable = false)
    private String email;

    // Senha criptografada (mínimo 6 caracteres, excluída do JSON por segurança)
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Permite receber no JSON mas não enviar
    @Column(nullable = false)
    private String password;

    @Size(max = 100, message = "Nome completo não pode exceder 100 caracteres")
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "active")
    private Boolean active = true;
    
    // Papel do utilizador no sistema (ADMINISTRADOR, GERENTE, FUNCIONARIO)
    // Armazenado como String na BD para facilitar leitura
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole role = UserRole.FUNCIONARIO; // Valor padrão: FUNCIONARIO

    // ======================== RELACIONAMENTOS JPA ========================
    
    // Tarefas atribuídas a este utilizador
    // cascade=ALL: operações no User afetam as Tasks
    // orphanRemoval=true: remove Tasks se não tiverem User
    // fetch=LAZY: carrega Tasks apenas quando necessário
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore // Evita loops infinitos na serialização JSON
    private List<Task> tasks = new ArrayList<>();
    
    // Equipas das quais este utilizador é membro (relacionamento N:M)
    // Tabela de junção 'user_teams' para mapear utilizadores ↔ equipas
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_teams", // Nome da tabela de junção
        joinColumns = @JoinColumn(name = "user_id"), // Chave estrangeira para User
        inverseJoinColumns = @JoinColumn(name = "team_id") // Chave estrangeira para Team
    )
    @JsonIgnore // Evita loops infinitos na serialização JSON
    private List<Team> teams = new ArrayList<>();
    
    // Perfil detalhado do utilizador (relacionamento 1:1)
    // mappedBy="user": o UserProfile possui a chave estrangeira
    // cascade=ALL: operações no User afetam o UserProfile
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Perfil carregado separadamente quando necessário
    private UserProfile profile;
    
    // Equipas que este utilizador gere (apenas para GERENTE/ADMINISTRADOR)
    // mappedBy="manager": Team possui a chave estrangeira manager_id
    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    @JsonIgnore // Equipas geridas carregadas separadamente
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