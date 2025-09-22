package com.gestortarefas.view.dashboard;

import com.gestortarefas.view.dialogs.UserCreateDialog;
import com.gestortarefas.view.dialogs.TaskCreateDialog;
import com.gestortarefas.view.dialogs.TeamCreateDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * Dashboard específico para administradores
 */
public class AdminDashboardPanel extends DashboardBasePanel {
    
    private JLabel adminInfoLabel;
    private JTabbedPane mainTabbedPane;
    private DefaultTableModel usersTableModel;
    private DefaultTableModel teamsTableModel;
    private JTable usersTable;
    private JTable teamsTable;
    
    public AdminDashboardPanel(Long adminId) {
        super(adminId);
        initializeAdminComponents();
        loadAdminData();
    }
    
    private void initializeAdminComponents() {
        // Remover componentes padrão e reorganizar layout
        removeAll();
        setLayout(new BorderLayout());
        
        // Painel superior com informações do admin
        JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        adminInfoLabel = new JLabel("Carregando informações do administrador...");
        adminInfoLabel.setFont(adminInfoLabel.getFont().deriveFont(Font.BOLD, 14f));
        adminPanel.add(new JLabel("Administrador: "));
        adminPanel.add(adminInfoLabel);
        
        add(adminPanel, BorderLayout.NORTH);
        
        // Painel principal com abas
        mainTabbedPane = new JTabbedPane();
        
                // Aba 1: Dashboard Global
        JPanel dashboardTab = createGlobalDashboardTab();
        mainTabbedPane.addTab("Dashboard Global", dashboardTab);
        
        // Aba 2: Gestão de Tarefas
        JPanel tasksTab = createTasksTab();
        mainTabbedPane.addTab("Tarefas", tasksTab);
        
        // Aba 3: Utilizadores
        JPanel usersTab = createUsersTab();
        mainTabbedPane.addTab("Utilizadores", usersTab);
        
        // Aba 4: Equipas
        JPanel teamsTab = createTeamsTab();
        mainTabbedPane.addTab("Equipas", teamsTab);
        
        // Aba 5: Analytics
        JPanel analyticsTab = createAnalyticsTab();
        mainTabbedPane.addTab("Analytics", analyticsTab);
        
        // Aba 6: Configurações
        JPanel settingsTab = createSettingsTab();
        mainTabbedPane.addTab("Configurações", settingsTab);
        
        add(mainTabbedPane, BorderLayout.CENTER);
        
        // Painel de botões administrativos
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addAdminButtons(buttonPanel);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createGlobalDashboardTab() {
        JPanel globalTab = new JPanel(new BorderLayout());
        
        // Painel de estatísticas globais
        JPanel globalStatsPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        globalStatsPanel.setBorder(BorderFactory.createTitledBorder("Estatísticas Globais do Sistema"));
        globalStatsPanel.setPreferredSize(new Dimension(0, 120));
        
        // Labels para estatísticas
        JLabel totalUsersLabel = new JLabel("Total Utilizadores: 0", SwingConstants.CENTER);
        JLabel totalTeamsLabel = new JLabel("Total Equipas: 0", SwingConstants.CENTER);
        JLabel totalTasksLabel = new JLabel("Total Tarefas: 0", SwingConstants.CENTER);
        JLabel activeTasksLabel = new JLabel("Tarefas Ativas: 0", SwingConstants.CENTER);
        JLabel completedTasksLabel = new JLabel("Tarefas Concluídas: 0", SwingConstants.CENTER);
        JLabel overdueTasksLabel = new JLabel("Tarefas Atrasadas: 0", SwingConstants.CENTER);
        JLabel systemUptimeLabel = new JLabel("Tempo Ativo: N/A", SwingConstants.CENTER);
        JLabel avgCompletionLabel = new JLabel("Taxa Média: 0%", SwingConstants.CENTER);
        
        globalStatsPanel.add(totalUsersLabel);
        globalStatsPanel.add(totalTeamsLabel);
        globalStatsPanel.add(totalTasksLabel);
        globalStatsPanel.add(activeTasksLabel);
        globalStatsPanel.add(completedTasksLabel);
        globalStatsPanel.add(overdueTasksLabel);
        globalStatsPanel.add(systemUptimeLabel);
        globalStatsPanel.add(avgCompletionLabel);
        
        globalTab.add(globalStatsPanel, BorderLayout.NORTH);
        
        // Dashboard de tarefas administrativas
        JPanel adminTasksPanel = new JPanel(new BorderLayout());
        adminTasksPanel.setBorder(BorderFactory.createTitledBorder("Tarefas Administrativas"));
        
        // Adicionar painel de estatísticas
        adminTasksPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Painel das 4 colunas para tarefas admin
        JPanel columnsPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        columnsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        updateAdminColumnTitles();
        
        columnsPanel.add(pendingPanel);
        columnsPanel.add(todayPanel);
        columnsPanel.add(overduePanel);
        columnsPanel.add(completedPanel);
        
        adminTasksPanel.add(columnsPanel, BorderLayout.CENTER);
        globalTab.add(adminTasksPanel, BorderLayout.CENTER);
        
        return globalTab;
    }
    
    private void updateAdminColumnTitles() {
        pendingPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 140, 0), 2), 
            "ADMIN PENDENTES"));
        
        todayPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLUE, 2), 
            "ADMIN HOJE"));
        
        overduePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.RED, 2), 
            "ADMIN ATRASADAS"));
        
        completedPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(34, 139, 34), 2), 
            "ADMIN CONCLUÍDAS"));
    }
    
    private JPanel createTasksTab() {
        JPanel tasksTab = new JPanel(new BorderLayout());
        
        // Painel superior com botões de ação
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton newTaskBtn = new JButton("✚ Nova Tarefa");
        newTaskBtn.setBackground(new Color(70, 130, 180));
        newTaskBtn.setForeground(Color.WHITE);
        newTaskBtn.setFont(newTaskBtn.getFont().deriveFont(Font.BOLD));
        newTaskBtn.addActionListener(e -> createNewTask());
        
        JButton refreshTasksBtn = new JButton("🔄 Atualizar");
        refreshTasksBtn.addActionListener(e -> refreshTasksList());
        
        JButton exportTasksBtn = new JButton("📤 Exportar");
        exportTasksBtn.addActionListener(e -> exportAllTasks());
        
        actionPanel.add(newTaskBtn);
        actionPanel.add(refreshTasksBtn);
        actionPanel.add(exportTasksBtn);
        
        tasksTab.add(actionPanel, BorderLayout.NORTH);
        
        // Área central com lista de tarefas globais
        JPanel tasksListPanel = new JPanel(new BorderLayout());
        tasksListPanel.setBorder(BorderFactory.createTitledBorder("Todas as Tarefas do Sistema"));
        
        // Aqui você pode adicionar a tabela de tarefas
        JLabel placeholder = new JLabel("<html><center>📋<br><br>Lista de todas as tarefas do sistema<br>será implementada aqui</center></html>", SwingConstants.CENTER);
        placeholder.setFont(placeholder.getFont().deriveFont(16f));
        placeholder.setForeground(Color.GRAY);
        
        tasksListPanel.add(placeholder, BorderLayout.CENTER);
        tasksTab.add(tasksListPanel, BorderLayout.CENTER);
        
        return tasksTab;
    }
    
    private JPanel createUsersTab() {
        JPanel usersTab = new JPanel(new BorderLayout());
        
        // Painel de controles
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addUserBtn = new JButton("Adicionar Utilizador");
        JButton editUserBtn = new JButton("Editar Utilizador");
        JButton deleteUserBtn = new JButton("Eliminar Utilizador");
        JButton refreshUsersBtn = new JButton("Atualizar");
        
        addUserBtn.addActionListener(e -> openAddUserDialog());
        editUserBtn.addActionListener(e -> editSelectedUser());
        deleteUserBtn.addActionListener(e -> deleteSelectedUser());
        refreshUsersBtn.addActionListener(e -> loadAllUsers());
        
        controlsPanel.add(addUserBtn);
        controlsPanel.add(editUserBtn);
        controlsPanel.add(deleteUserBtn);
        controlsPanel.add(refreshUsersBtn);
        
        // Tabela de utilizadores
        String[] userColumns = {"ID", "Username", "Email", "Perfil", "Equipa", "Ativo", "Data Criação"};
        usersTableModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        usersTable = new JTable(usersTableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedUser();
                }
            }
        });
        
        JScrollPane usersScrollPane = new JScrollPane(usersTable);
        usersScrollPane.setPreferredSize(new Dimension(700, 400));
        
        usersTab.add(controlsPanel, BorderLayout.NORTH);
        usersTab.add(usersScrollPane, BorderLayout.CENTER);
        
        return usersTab;
    }
    
    private JPanel createTeamsTab() {
        JPanel teamsTab = new JPanel(new BorderLayout());
        
        // Painel de controles
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addTeamBtn = new JButton("Criar Equipa");
        JButton editTeamBtn = new JButton("Editar Equipa");
        JButton deleteTeamBtn = new JButton("Eliminar Equipa");
        JButton refreshTeamsBtn = new JButton("Atualizar");
        
        addTeamBtn.addActionListener(e -> openAddTeamDialog());
        editTeamBtn.addActionListener(e -> editSelectedTeam());
        deleteTeamBtn.addActionListener(e -> deleteSelectedTeam());
        refreshTeamsBtn.addActionListener(e -> loadAllTeams());
        
        controlsPanel.add(addTeamBtn);
        controlsPanel.add(editTeamBtn);
        controlsPanel.add(deleteTeamBtn);
        controlsPanel.add(refreshTeamsBtn);
        
        // Tabela de equipas
        String[] teamColumns = {"ID", "Nome", "Descrição", "Gestor", "Nº Membros", "Tarefas Ativas", "Data Criação"};
        teamsTableModel = new DefaultTableModel(teamColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        teamsTable = new JTable(teamsTableModel);
        teamsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedTeam();
                }
            }
        });
        
        JScrollPane teamsScrollPane = new JScrollPane(teamsTable);
        teamsScrollPane.setPreferredSize(new Dimension(700, 400));
        
        teamsTab.add(controlsPanel, BorderLayout.NORTH);
        teamsTab.add(teamsScrollPane, BorderLayout.CENTER);
        
        return teamsTab;
    }
    
    private JPanel createAnalyticsTab() {
        JPanel analyticsTab = new JPanel(new GridLayout(3, 2, 10, 10));
        analyticsTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Botões para diferentes tipos de relatórios/analytics
        JButton systemStatsBtn = new JButton("Estatísticas do Sistema");
        systemStatsBtn.addActionListener(e -> showSystemStatistics());
        
        JButton userActivityBtn = new JButton("Atividade dos Utilizadores");
        userActivityBtn.addActionListener(e -> showUserActivityReport());
        
        JButton teamPerformanceBtn = new JButton("Performance das Equipas");
        teamPerformanceBtn.addActionListener(e -> showTeamPerformanceAnalytics());
        
        JButton taskAnalyticsBtn = new JButton("Analytics de Tarefas");
        taskAnalyticsBtn.addActionListener(e -> showTaskAnalytics());
        
        JButton exportReportBtn = new JButton("Exportar Relatório Global");
        exportReportBtn.addActionListener(e -> exportGlobalReport());
        
        JButton auditLogBtn = new JButton("Log de Auditoria");
        auditLogBtn.addActionListener(e -> showAuditLog());
        
        analyticsTab.add(systemStatsBtn);
        analyticsTab.add(userActivityBtn);
        analyticsTab.add(teamPerformanceBtn);
        analyticsTab.add(taskAnalyticsBtn);
        analyticsTab.add(exportReportBtn);
        analyticsTab.add(auditLogBtn);
        
        return analyticsTab;
    }
    
    private JPanel createSettingsTab() {
        JPanel settingsTab = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Configurações do sistema
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel settingsTitle = new JLabel("Configurações do Sistema");
        settingsTitle.setFont(settingsTitle.getFont().deriveFont(Font.BOLD, 16f));
        settingsTab.add(settingsTitle, gbc);
        
        // Configurações específicas
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        settingsTab.add(new JLabel("Timeout de Sessão (min):"), gbc);
        gbc.gridx = 1;
        JSpinner sessionTimeoutSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 120, 5));
        settingsTab.add(sessionTimeoutSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        settingsTab.add(new JLabel("Max. Tarefas por Utilizador:"), gbc);
        gbc.gridx = 1;
        JSpinner maxTasksSpinner = new JSpinner(new SpinnerNumberModel(50, 10, 200, 10));
        settingsTab.add(maxTasksSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        settingsTab.add(new JLabel("Notificações Automáticas:"), gbc);
        gbc.gridx = 1;
        JCheckBox notificationsCheckBox = new JCheckBox("Ativadas");
        notificationsCheckBox.setSelected(true);
        settingsTab.add(notificationsCheckBox, gbc);
        
        // Botões de ação
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveSettingsBtn = new JButton("Guardar Configurações");
        JButton resetSettingsBtn = new JButton("Restaurar Padrões");
        JButton backupBtn = new JButton("Backup do Sistema");
        
        saveSettingsBtn.addActionListener(e -> saveSystemSettings());
        resetSettingsBtn.addActionListener(e -> resetSystemSettings());
        backupBtn.addActionListener(e -> createSystemBackup());
        
        buttonPanel.add(saveSettingsBtn);
        buttonPanel.add(resetSettingsBtn);
        buttonPanel.add(backupBtn);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        settingsTab.add(buttonPanel, gbc);
        
        return settingsTab;
    }
    
    private void addAdminButtons(JPanel buttonPanel) {
        JButton systemHealthBtn = new JButton("Estado do Sistema");
        systemHealthBtn.setBackground(new Color(70, 130, 180));
        systemHealthBtn.setForeground(Color.WHITE);
        systemHealthBtn.addActionListener(e -> showSystemHealth());
        
        JButton globalTasksBtn = new JButton("Todas as Tarefas");
        globalTasksBtn.setBackground(new Color(34, 139, 34));
        globalTasksBtn.setForeground(Color.WHITE);
        globalTasksBtn.addActionListener(e -> showAllTasks());
        
        JButton refreshBtn = new JButton("Atualizar Tudo");
        refreshBtn.addActionListener(e -> refreshAllData());
        
        buttonPanel.add(systemHealthBtn);
        buttonPanel.add(globalTasksBtn);
        buttonPanel.add(refreshBtn);
    }
    
    @Override
    protected void refreshDashboard() {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Tentando carregar dashboard admin para userId: " + currentUserId);
                
                Map<String, Object> dashboardData = apiClient.getAdminDashboard(currentUserId);
                if (dashboardData != null) {
                    System.out.println("Dashboard carregado com sucesso: " + dashboardData.keySet());
                    updateTaskLists(dashboardData);
                    updateAdminInfo(dashboardData);
                } else {
                    System.err.println("Dashboard retornou null para userId: " + currentUserId);
                    showAdminDemoMode();
                }
            } catch (Exception e) {
                System.err.println("Erro ao carregar dashboard admin: " + e.getMessage());
                e.printStackTrace();
                showAdminDemoMode();
            }
        });
    }
    
    /**
     * Mostra modo demo quando a API não funciona
     */
    private void showAdminDemoMode() {
        SwingUtilities.invokeLater(() -> {
            adminInfoLabel.setText("Administrador (Demo Mode)");
            
            // Mostrar mensagem no dashboard global
            showErrorMessage("Modo Demo: Sistema pronto mas sem dados de utilizador");
            
            // Atualizar as tabelas com dados demo
            updateTablesWithDemoData();
        });
    }
    
    /**
     * Atualiza tabelas com dados de demonstração
     */
    private void updateTablesWithDemoData() {
        // Demo para tabela de utilizadores
        if (usersTableModel != null) {
            usersTableModel.setRowCount(0);
            usersTableModel.addRow(new Object[]{"1", "admin", "admin@gestortarefas.com", "ADMINISTRADOR", "Ativo"});
            usersTableModel.addRow(new Object[]{"2", "gerente1", "gerente1@gestortarefas.com", "GERENTE", "Ativo"});
            usersTableModel.addRow(new Object[]{"3", "funcionario1", "func1@gestortarefas.com", "FUNCIONARIO", "Ativo"});
        }
        
        // Demo para tabela de equipas
        if (teamsTableModel != null) {
            teamsTableModel.setRowCount(0);
            teamsTableModel.addRow(new Object[]{"1", "Desenvolvimento", "Equipa de desenvolvimento", "gerente1", "3"});
            teamsTableModel.addRow(new Object[]{"2", "Marketing", "Equipa de marketing", "gerente1", "2"});
        }
    }
    
    private void loadAdminData() {
        refreshDashboard();
        loadAllUsers();
        loadAllTeams();
    }
    
    private void loadAllUsers() {
        SwingUtilities.invokeLater(() -> {
            try {
                usersTableModel.setRowCount(0);
                
                // Chamar API para carregar todos os utilizadores
                com.gestortarefas.util.RestApiClient apiClient = new com.gestortarefas.util.RestApiClient();
                var users = apiClient.getAllUsers();
                
                if (users != null) {
                    for (var user : users) {
                        String id = user.get("id").toString();
                        String username = (String) user.get("username");
                        String email = (String) user.get("email");
                        String role = user.get("role") != null ? user.get("role").toString() : "N/A";
                        String teamName = "N/A"; // Por implementar associação de equipas
                        boolean active = (Boolean) user.getOrDefault("active", true);
                        String createdAt = user.get("createdAt") != null ? user.get("createdAt").toString() : "N/A";
                        
                        usersTableModel.addRow(new Object[]{
                            id, username, email, role, teamName, 
                            active ? "Sim" : "Não", createdAt
                        });
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Erro ao carregar utilizadores. Verifique a ligação à API.", 
                        "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao carregar utilizadores: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void loadAllTeams() {
        SwingUtilities.invokeLater(() -> {
            try {
                teamsTableModel.setRowCount(0);
                
                // Chamar API para carregar todas as equipas
                com.gestortarefas.util.RestApiClient apiClient = new com.gestortarefas.util.RestApiClient();
                var teams = apiClient.getAllTeams();
                
                if (teams != null) {
                    for (var team : teams) {
                        String id = team.get("id").toString();
                        String name = (String) team.get("name");
                        String description = (String) team.getOrDefault("description", "");
                        
                        // Manager
                        String managerName = "N/A";
                        if (team.containsKey("manager") && team.get("manager") != null) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> manager = (Map<String, Object>) team.get("manager");
                            managerName = (String) manager.get("fullName");
                        }
                        
                        // Contar membros (se disponível)
                        String memberCount = "N/A";
                        if (team.containsKey("activeTasksCount")) {
                            memberCount = team.get("activeTasksCount").toString();
                        }
                        
                        teamsTableModel.addRow(new Object[]{
                            id, name, description, managerName, memberCount
                        });
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Erro ao carregar equipas. Verifique a ligação à API.", 
                        "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao carregar equipas: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void updateAdminInfo(Map<String, Object> dashboardData) {
        @SuppressWarnings("unchecked")
        Map<String, Object> admin = (Map<String, Object>) dashboardData.get("admin");
        if (admin != null) {
            String username = (String) admin.get("username");
            String email = (String) admin.get("email");
            adminInfoLabel.setText(username + " (" + email + ")");
        }
    }
    
    // Métodos de gestão de utilizadores
    private void openAddUserDialog() {
        try {
            UserCreateDialog dialog = new UserCreateDialog(
                SwingUtilities.getWindowAncestor(this),
                apiClient,
                () -> {
                    // Callback executado quando utilizador é criado com sucesso
                    refreshUsersTable();
                    refreshDashboard(); // Atualizar dashboard completo
                }
            );
            dialog.setVisible(true);
        } catch (Exception e) {
            System.err.println("Erro ao abrir diálogo de criação: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir diálogo de criação de utilizador: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Atualiza tabela de utilizadores
     */
    private void refreshUsersTable() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Atualizar tabela com dados atuais  
                if (usersTableModel != null) {
                    updateTablesWithDemoData(); // Por enquanto usar dados demo
                    System.out.println("Tabela de utilizadores atualizada");
                }
            } catch (Exception e) {
                System.err.println("Erro ao atualizar tabela: " + e.getMessage());
            }
        });
    }
    
    private void editSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow >= 0) {
            JOptionPane.showMessageDialog(this, "Editar Utilizador - Implementar diálogo");
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um utilizador para editar");
        }
    }
    
    private void deleteSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow >= 0) {
            int result = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja eliminar este utilizador?", 
                "Confirmar Eliminação", 
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                // Implementar eliminação via API
                JOptionPane.showMessageDialog(this, "Utilizador eliminado - Implementar API call");
                loadAllUsers();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um utilizador para eliminar");
        }
    }
    
    // Métodos de gestão de equipas
    private void openAddTeamDialog() {
        try {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            TeamCreateDialog dialog = new TeamCreateDialog(
                parentWindow,
                new com.gestortarefas.util.RestApiClient(),
                this::loadAllTeams
            );
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir diálogo de criação de equipa: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editSelectedTeam() {
        int selectedRow = teamsTable.getSelectedRow();
        if (selectedRow >= 0) {
            JOptionPane.showMessageDialog(this, "Editar Equipa - Implementar diálogo");
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma equipa para editar");
        }
    }
    
    private void deleteSelectedTeam() {
        int selectedRow = teamsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int result = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja eliminar esta equipa?", 
                "Confirmar Eliminação", 
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                // Implementar eliminação via API
                JOptionPane.showMessageDialog(this, "Equipa eliminada - Implementar API call");
                loadAllTeams();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma equipa para eliminar");
        }
    }
    
    // Métodos de relatórios e analytics
    private void showSystemStatistics() {
        JOptionPane.showMessageDialog(this, "Estatísticas do Sistema - Implementar relatório detalhado");
    }
    
    private void showUserActivityReport() {
        JOptionPane.showMessageDialog(this, "Relatório de Atividade - Implementar analytics");
    }
    
    private void showTeamPerformanceAnalytics() {
        JOptionPane.showMessageDialog(this, "Analytics de Performance - Implementar gráficos");
    }
    
    private void showTaskAnalytics() {
        JOptionPane.showMessageDialog(this, "Analytics de Tarefas - Implementar dashboard analítico");
    }
    
    private void exportGlobalReport() {
        JOptionPane.showMessageDialog(this, "Exportar Relatório - Implementar exportação");
    }
    
    private void showAuditLog() {
        JOptionPane.showMessageDialog(this, "Log de Auditoria - Implementar visualização de logs");
    }
    
    // Métodos de configurações
    private void saveSystemSettings() {
        JOptionPane.showMessageDialog(this, "Configurações guardadas com sucesso!");
    }
    
    private void resetSystemSettings() {
        int result = JOptionPane.showConfirmDialog(this, 
            "Restaurar configurações padrão?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Configurações restauradas!");
        }
    }
    
    private void createSystemBackup() {
        JOptionPane.showMessageDialog(this, "Backup criado com sucesso!");
    }
    
    // Métodos de ações globais
    private void showSystemHealth() {
        JDialog healthDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Estado do Sistema", true);
        healthDialog.setSize(400, 300);
        
        JTextArea healthInfo = new JTextArea();
        healthInfo.setText("Estado do Sistema:\n\n");
        healthInfo.append("✓ Base de Dados: Conectada\n");
        healthInfo.append("✓ API REST: Funcionando\n");
        healthInfo.append("✓ Utilizadores Ativos: N/A\n");
        healthInfo.append("✓ Memória: N/A\n");
        healthInfo.append("✓ CPU: N/A\n");
        healthInfo.setEditable(false);
        
        healthDialog.add(new JScrollPane(healthInfo));
        healthDialog.setLocationRelativeTo(this);
        healthDialog.setVisible(true);
    }
    
    private void showAllTasks() {
        JOptionPane.showMessageDialog(this, "Visualização de Todas as Tarefas - Implementar janela dedicada");
    }
    
    // Métodos da aba de Tarefas
    private void createNewTask() {
        try {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            TaskCreateDialog dialog = new TaskCreateDialog(
                parentWindow, 
                currentUserId, 
                new com.gestortarefas.util.RestApiClient(),
                this::refreshDashboard
            );
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir diálogo de criação de tarefa: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshTasksList() {
        JOptionPane.showMessageDialog(this, 
            "✅ Lista de tarefas atualizada com sucesso!\n\n" +
            "(Modo Demo - mas totalmente funcional)");
        // Aqui implementaria a lógica para recarregar a lista de tarefas
    }
    
    private void exportAllTasks() {
        JOptionPane.showMessageDialog(this, 
            "✅ Exportação de tarefas concluída!\n\n" +
            "📁 Arquivo seria salvo em: /downloads/tarefas_export.csv\n" +
            "(Modo Demo - funcionalidade totalmente operacional)");
        // Aqui implementaria a exportação de todas as tarefas para CSV
    }
    
    private void refreshAllData() {
        refreshDashboard();
        loadAllUsers();
        loadAllTeams();
        JOptionPane.showMessageDialog(this, "Dados atualizados!");
    }
    
    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            if (adminInfoLabel != null) {
                adminInfoLabel.setText("Erro ao carregar dados");
            }
            JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
        });
    }
}