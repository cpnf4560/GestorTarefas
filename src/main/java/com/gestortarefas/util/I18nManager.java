package com.gestortarefas.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestor de internacionalização PT/EN
 */
public class I18nManager {
    
    private static I18nManager instance;
    private boolean isEnglish = false;
    
    // Textos em Português
    private static final Map<String, String> PT = new HashMap<String, String>() {{
        // Login
        put("title", "Gestor de Tarefas");
        put("login", "Entrar");
        put("user", "Utilizador");
        put("password", "Palavra-passe");
        put("username", "Nome de utilizador");
        
        // Menu/Navegação
        put("dashboard", "Dashboard");
        put("tasks", "Tarefas");
        put("teams", "Equipas");
        put("users", "Utilizadores");
        put("reports", "Relatórios");
        put("settings", "Definições");
        put("logout", "Sair");
        put("exit", "Sair");
        
        // Botões/Ações
        put("add", "Adicionar");
        put("edit", "Editar");
        put("delete", "Eliminar");
        put("save", "Guardar");
        put("cancel", "Cancelar");
        put("ok", "OK");
        put("yes", "Sim");
        put("no", "Não");
        put("search", "Pesquisar");
        put("filter", "Filtrar");
        put("export", "Exportar");
        put("import", "Importar");
        put("refresh", "Atualizar");
        
        // Status/Estados
        put("pending", "Pendente");
        put("in_progress", "Em Progresso");
        put("completed", "Concluída");
        put("cancelled", "Cancelada");
        put("active", "Ativo");
        put("inactive", "Inativo");
        
        // Campos
        put("name", "Nome");
        put("description", "Descrição");
        put("priority", "Prioridade");
        put("due_date", "Data Limite");
        put("created_at", "Criado em");
        put("updated_at", "Atualizado em");
        put("status", "Estado");
        put("role", "Função");
        put("email", "Email");
        put("phone", "Telefone");
        put("manager", "Gestor");
        put("members", "Membros");
        put("member_count", "Nº Membros");
        put("active_tasks", "Tarefas Ativas");
        put("creation_date", "Data de Criação");
        
        // Mensagens
        put("success", "Sucesso");
        put("error", "Erro");
        put("warning", "Aviso");
        put("info", "Informação");
        put("confirm_delete", "Tem a certeza que deseja eliminar este registo?");
        put("confirm_exit", "Tem a certeza que deseja sair da aplicação?");
        put("record_saved", "Registo guardado com sucesso!");
        put("record_deleted", "Registo eliminado com sucesso!");
        put("record_updated", "Registo atualizado com sucesso!");
        put("no_records", "Nenhum registo encontrado");
        put("total_records", "Total de registos");
        put("last_records", "Últimos registos");
        
        // Dashboard
        put("welcome", "Bem-vindo");
        put("statistics", "Estatísticas");
        put("recent_activities", "Atividades Recentes");
        put("task_overview", "Visão Geral das Tarefas");
        put("team_overview", "Visão Geral das Equipas");
        put("start", "Iniciar");
        put("complete", "Concluir");
        put("today", "Hoje");
        put("overdue", "Atrasadas");
        put("total", "Total");
        
        // Relatórios
        put("generate_report", "Gerar Relatório");
        put("export_csv", "Exportar CSV");
        put("print", "Imprimir");
        
        // Validações
        put("field_required", "Este campo é obrigatório");
        put("invalid_email", "Email inválido");
        put("password_short", "Palavra-passe muito curta");
        put("login_failed", "Login falhado. Verifique as suas credenciais.");
        put("access_denied", "Acesso negado");
        
        // Outros
        put("language", "Idioma");
        put("portuguese", "Português");
        put("english", "Inglês");
        put("loading", "A carregar...");
        put("processing", "A processar...");
        put("please_wait", "Por favor aguarde...");
        
        // Cabeçalhos de Tabelas
        put("table_priority", "P");
        put("table_task", "Tarefa");
        put("table_due_date", "Data Limite");
        put("table_assigned_to", "Atribuído a:");
        put("table_status", "Status");
        put("table_id", "ID");
        put("table_title", "Título");
        put("table_description", "Descrição");
        put("table_team", "Equipa");
        put("table_created_by", "Criado Por");
        put("table_creation_date", "Data Criação");
        put("table_completion_date", "Data Conclusão");
        put("table_actions", "Ações");
        put("table_username", "Username");
        put("table_full_name", "Nome Completo");
        put("table_profile", "Perfil");
        put("table_active", "Ativo");
        put("table_manager", "Gestor");
        put("table_members_count", "Nº Membros");
        put("table_active_tasks", "Tarefas Ativas");
        put("table_email", "Email");
        put("table_completed_tasks", "Tarefas Concluídas");
        put("table_completion_rate", "Taxa Conclusão");
    }};
    
    // Textos em Inglês
    private static final Map<String, String> EN = new HashMap<String, String>() {{
        // Login
        put("title", "Task Manager");
        put("login", "Login");
        put("user", "User");
        put("password", "Password");
        put("username", "Username");
        
        // Menu/Navigation
        put("dashboard", "Dashboard");
        put("tasks", "Tasks");
        put("teams", "Teams");
        put("users", "Users");
        put("reports", "Reports");
        put("settings", "Settings");
        put("logout", "Logout");
        put("exit", "Exit Application");
        
        // Buttons/Actions
        put("add", "Add");
        put("edit", "Edit");
        put("delete", "Delete");
        put("save", "Save");
        put("cancel", "Cancel");
        put("ok", "OK");
        put("yes", "Yes");
        put("no", "No");
        put("search", "Search");
        put("filter", "Filter");
        put("export", "Export");
        put("import", "Import");
        put("refresh", "Refresh");
        
        // Status/States
        put("pending", "Pending");
        put("in_progress", "In Progress");
        put("completed", "Completed");
        put("cancelled", "Cancelled");
        put("active", "Active");
        put("inactive", "Inactive");
        
        // Fields
        put("name", "Name");
        put("description", "Description");
        put("priority", "Priority");
        put("due_date", "Due Date");
        put("created_at", "Created at");
        put("updated_at", "Updated at");
        put("status", "Status");
        put("role", "Role");
        put("email", "Email");
        put("phone", "Phone");
        put("manager", "Manager");
        put("members", "Members");
        put("member_count", "Member Count");
        put("active_tasks", "Active Tasks");
        put("creation_date", "Creation Date");
        
        // Messages
        put("success", "Success");
        put("error", "Error");
        put("warning", "Warning");
        put("info", "Information");
        put("confirm_delete", "Are you sure you want to delete this record?");
        put("confirm_exit", "Are you sure you want to exit the application?");
        put("record_saved", "Record saved successfully!");
        put("record_deleted", "Record deleted successfully!");
        put("record_updated", "Record updated successfully!");
        put("no_records", "No records found");
        put("total_records", "Total records");
        put("last_records", "Last records");
        
        // Dashboard
        put("welcome", "Welcome");
        put("statistics", "Statistics");
        put("recent_activities", "Recent Activities");
        put("task_overview", "Task Overview");
        put("team_overview", "Team Overview");
        put("start", "Start");
        put("complete", "Complete");
        put("today", "Today");
        put("overdue", "Overdue");
        put("total", "Total");
        
        // Reports
        put("generate_report", "Generate Report");
        put("export_csv", "Export CSV");
        put("print", "Print");
        
        // Validations
        put("field_required", "This field is required");
        put("invalid_email", "Invalid email");
        put("password_short", "Password too short");
        put("login_failed", "Login failed. Please check your credentials.");
        put("access_denied", "Access denied");
        
        // Others
        put("language", "Language");
        put("portuguese", "Portuguese");
        put("english", "English");
        put("loading", "Loading...");
        put("processing", "Processing...");
        put("please_wait", "Please wait...");
        
        // Table Headers
        put("table_priority", "P");
        put("table_task", "Task");
        put("table_due_date", "Due Date");
        put("table_assigned_to", "Assigned to:");
        put("table_status", "Status");
        put("table_id", "ID");
        put("table_title", "Title");
        put("table_description", "Description");
        put("table_team", "Team");
        put("table_created_by", "Created By");
        put("table_creation_date", "Creation Date");
        put("table_completion_date", "Completion Date");
        put("table_actions", "Actions");
        put("table_username", "Username");
        put("table_full_name", "Full Name");
        put("table_profile", "Profile");
        put("table_active", "Active");
        put("table_manager", "Manager");
        put("table_members_count", "Members Count");
        put("table_active_tasks", "Active Tasks");
        put("table_email", "Email");
        put("table_completed_tasks", "Completed Tasks");
        put("table_completion_rate", "Completion Rate");
    }};
    
    private I18nManager() {}
    
    public static I18nManager getInstance() {
        if (instance == null) {
            instance = new I18nManager();
        }
        return instance;
    }
    
    public String getText(String key) {
        Map<String, String> currentMap = isEnglish ? EN : PT;
        return currentMap.getOrDefault(key, key);
    }
    
    public void toggleLanguage() {
        isEnglish = !isEnglish;
    }
    
    public boolean isEnglish() {
        return isEnglish;
    }
    
    public String getCurrentLanguage() {
        return isEnglish ? "EN" : "PT";
    }
}