package com.gestortarefas.model;

/**
 * Classe para representar um utilizador logado no sistema
 */
public class LoggedUser {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private String token;
    
    public LoggedUser() {
    }
    
    public LoggedUser(Long id, String username, String email, UserRole role, String token) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.token = token;
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
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    /**
     * Verifica se o utilizador é administrador
     */
    public boolean isAdmin() {
        return role == UserRole.ADMINISTRADOR;
    }
    
    /**
     * Verifica se o utilizador é gestor
     */
    public boolean isManager() {
        return role == UserRole.GERENTE;
    }
    
    /**
     * Verifica se o utilizador é funcionário
     */
    public boolean isEmployee() {
        return role == UserRole.FUNCIONARIO;
    }
    
    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}