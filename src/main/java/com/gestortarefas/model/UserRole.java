package com.gestortarefas.model;

/**
 * Enum para definir os tipos de perfil de utilizador no sistema
 */
public enum UserRole {
    
    FUNCIONARIO("Funcionário", "Utilizador padrão com acesso às suas tarefas"),
    GERENTE("Gerente", "Gestor de equipa com acesso às tarefas da sua equipa"),
    ADMINISTRADOR("Administrador", "Acesso total ao sistema, gestão de utilizadores e equipas");
    
    private final String displayName;
    private final String description;
    
    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Verifica se o utilizador tem permissões de administrador
     */
    public boolean isAdmin() {
        return this == ADMINISTRADOR;
    }
    
    /**
     * Verifica se o utilizador tem permissões de gerente ou superior
     */
    public boolean isManagerOrAbove() {
        return this == GERENTE || this == ADMINISTRADOR;
    }
    
    /**
     * Verifica se o utilizador pode gerir equipas
     */
    public boolean canManageTeams() {
        return this == ADMINISTRADOR;
    }
    
    /**
     * Verifica se o utilizador pode gerir utilizadores
     */
    public boolean canManageUsers() {
        return this == ADMINISTRADOR;
    }
    
    /**
     * Verifica se o utilizador pode ver relatórios da equipa
     */
    public boolean canViewTeamReports() {
        return this == GERENTE || this == ADMINISTRADOR;
    }
}