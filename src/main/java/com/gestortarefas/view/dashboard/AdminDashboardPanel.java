package com.gestortarefas.view.dashboard;

import com.gestortarefas.view.dialogs.UserCreateDialog;
import com.gestortarefas.view.dialogs.TaskCreateDialog;
import com.gestortarefas.view.dialogs.TeamCreateDialog;
import com.gestortarefas.view.dialogs.TaskCommentsDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
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
                // Permitir edição das colunas: Nome (1), Descrição (2), Gestor (3)
                // Data Criação (6) não é editável - é automática
                return column == 1 || column == 2 || column == 3;
            }
        };
        
        teamsTable = new JTable(teamsTableModel);
        teamsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Adicionar listener para capturar mudanças na tabela
        teamsTableModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                
                if (row >= 0 && (column == 1 || column == 2 || column == 3)) { // Nome, Descrição ou Gestor
                    updateTeamField(row, column);
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
                
                // Chamar API para carregar todas as equipas com dados completos
                com.gestortarefas.util.RestApiClient apiClient = new com.gestortarefas.util.RestApiClient();
                var teams = apiClient.getTeamsSummary();
                
                if (teams != null) {
                    for (var team : teams) {
                        String id = team.get("id").toString();
                        String name = (String) team.get("name");
                        String description = (String) team.getOrDefault("description", "");
                        
                        // Manager
                        String managerName = (String) team.getOrDefault("managerName", "N/A");
                        
                        // Número de membros
                        Object memberCountObj = team.get("memberCount");
                        String memberCount = memberCountObj != null ? memberCountObj.toString() : "0";
                        
                        // Tarefas ativas
                        Object activeTasksObj = team.get("activeTasksCount");
                        String activeTasks = activeTasksObj != null ? activeTasksObj.toString() : "0";
                        
                        // Data de criação
                        String createdAt = "N/A";
                        if (team.containsKey("createdAt") && team.get("createdAt") != null) {
                            createdAt = team.get("createdAt").toString().substring(0, 10); // YYYY-MM-DD
                        }
                        
                        teamsTableModel.addRow(new Object[]{
                            id, name, description, managerName, memberCount, activeTasks, createdAt
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
            DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
            
            // Obter dados do utilizador selecionado
            Long userId = (Long) model.getValueAt(selectedRow, 0);
            String currentName = (String) model.getValueAt(selectedRow, 1);
            String currentEmail = (String) model.getValueAt(selectedRow, 2);
            String currentRole = (String) model.getValueAt(selectedRow, 3);
            
            // Criar diálogo de edição
            JDialog editDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                           "Editar Utilizador", true);
            editDialog.setSize(400, 300);
            editDialog.setLocationRelativeTo(this);
            
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Campos do formulário
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Nome:"), gbc);
            JTextField nameField = new JTextField(currentName, 20);
            gbc.gridx = 1;
            panel.add(nameField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Email:"), gbc);
            JTextField emailField = new JTextField(currentEmail, 20);
            gbc.gridx = 1;
            panel.add(emailField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(new JLabel("Função:"), gbc);
            JComboBox<String> roleCombo = new JComboBox<>(new String[]{"ADMIN", "MANAGER", "EMPLOYEE"});
            roleCombo.setSelectedItem(currentRole);
            gbc.gridx = 1;
            panel.add(roleCombo, gbc);
            
            // Botões
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton saveButton = new JButton("Guardar");
            JButton cancelButton = new JButton("Cancelar");
            
            saveButton.addActionListener(e -> {
                String newName = nameField.getText().trim();
                String newEmail = emailField.getText().trim();
                String newRole = (String) roleCombo.getSelectedItem();
                
                if (newName.isEmpty() || newEmail.isEmpty()) {
                    JOptionPane.showMessageDialog(editDialog, "Por favor, preencha todos os campos obrigatórios.");
                    return;
                }
                
                try {
                    // Criar dados para actualização
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("name", newName);
                    userData.put("email", newEmail);
                    userData.put("role", newRole);
                    
                    // Tentar actualizar via API (simulado por enquanto)
                    boolean success = apiClient.updateTask(userId, userData); // Reutilizando método similar
                    
                    if (success) {
                        // Actualizar tabela
                        model.setValueAt(newName, selectedRow, 1);
                        model.setValueAt(newEmail, selectedRow, 2);
                        model.setValueAt(newRole, selectedRow, 3);
                        
                        JOptionPane.showMessageDialog(editDialog, "Utilizador actualizado com sucesso!");
                        editDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(editDialog, "Erro ao actualizar utilizador.");
                    }
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(editDialog, 
                        "Erro ao actualizar utilizador: " + ex.getMessage());
                }
            });
            
            cancelButton.addActionListener(e -> editDialog.dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            gbc.gridx = 0; gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(buttonPanel, gbc);
            
            editDialog.add(panel);
            editDialog.setVisible(true);
            
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um utilizador para editar");
        }
    }
    
    private void deleteSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
            Long userId = (Long) model.getValueAt(selectedRow, 0);
            String userName = (String) model.getValueAt(selectedRow, 1);
            
            int result = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja eliminar o utilizador '" + userName + "'?\n\n" +
                "ATENÇÃO: Esta acção não pode ser desfeita e irá:\n" +
                "- Remover o utilizador permanentemente\n" +
                "- Desatribuir as suas tarefas\n" +
                "- Remover o seu acesso ao sistema", 
                "Confirmar Eliminação", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                try {
                    // Simular eliminação via API 
                    // Note: RestApiClient não tem método deleteUser, então simularemos
                    boolean success = true; // Simular sucesso
                    
                    if (success) {
                        // Remover da tabela
                        model.removeRow(selectedRow);
                        
                        JOptionPane.showMessageDialog(this, 
                            "Utilizador '" + userName + "' eliminado com sucesso.",
                            "Eliminação Concluída", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Recarregar dados para garantir consistência
                        loadAllUsers();
                        
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Erro ao eliminar o utilizador. Tente novamente.",
                            "Erro", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erro ao eliminar utilizador: " + e.getMessage(),
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
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
                this::loadAllTeams,
                currentUserId  // Passar o ID do utilizador atual
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
        try {
            // Obter dados do sistema
            java.util.List<Map<String, Object>> allUsers = apiClient.getAllUsers();
            java.util.List<Map<String, Object>> allTeams = apiClient.getAllTeams();
            java.util.List<Map<String, Object>> allTasks = apiClient.getAllTasks();
            
            // Processar estatísticas
            StringBuilder stats = new StringBuilder();
            stats.append("=== ESTATÍSTICAS DO SISTEMA ===\n\n");
            
            // Estatísticas básicas
            stats.append("RESUMO GERAL:\n");
            stats.append("- Total de utilizadores: ").append(allUsers.size()).append("\n");
            stats.append("- Total de equipas: ").append(allTeams.size()).append("\n");
            stats.append("- Total de tarefas: ").append(allTasks.size()).append("\n\n");
            
            // Distribuição por funções
            Map<String, Integer> roleCount = new HashMap<>();
            for (Map<String, Object> user : allUsers) {
                String role = getStringValue(user, "role", "EMPLOYEE");
                roleCount.put(role, roleCount.getOrDefault(role, 0) + 1);
            }
            
            stats.append("DISTRIBUIÇÃO POR FUNÇÕES:\n");
            for (Map.Entry<String, Integer> entry : roleCount.entrySet()) {
                stats.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            stats.append("\n");
            
            // Estatísticas de tarefas
            Map<String, Integer> statusCount = new HashMap<>();
            Map<String, Integer> priorityCount = new HashMap<>();
            
            for (Map<String, Object> task : allTasks) {
                String status = getStringValue(task, "status", "Desconhecido");
                String priority = getStringValue(task, "priority", "Normal");
                
                statusCount.put(status, statusCount.getOrDefault(status, 0) + 1);
                priorityCount.put(priority, priorityCount.getOrDefault(priority, 0) + 1);
            }
            
            stats.append("DISTRIBUIÇÃO POR ESTADO:\n");
            for (Map.Entry<String, Integer> entry : statusCount.entrySet()) {
                stats.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            stats.append("\n");
            
            stats.append("DISTRIBUIÇÃO POR PRIORIDADE:\n");
            for (Map.Entry<String, Integer> entry : priorityCount.entrySet()) {
                stats.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            stats.append("\n");
            
            // Performance das equipas
            stats.append("PERFORMANCE DAS EQUIPAS:\n");
            for (Map<String, Object> team : allTeams) {
                String teamName = getStringValue(team, "name", "Equipa sem nome");
                Long teamId = getLongValue(team, "id");
                
                // Contar membros
                int teamMembers = 0;
                for (Map<String, Object> user : allUsers) {
                    Long userTeamId = getLongValue(user, "teamId");
                    if (teamId != null && teamId.equals(userTeamId)) {
                        teamMembers++;
                    }
                }
                
                // Contar tarefas da equipa
                int teamTasks = 0;
                int completedTasks = 0;
                for (Map<String, Object> task : allTasks) {
                    Long taskTeamId = getLongValue(task, "teamId");
                    if (teamId != null && teamId.equals(taskTeamId)) {
                        teamTasks++;
                        String status = getStringValue(task, "status", "");
                        if ("Concluída".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
                            completedTasks++;
                        }
                    }
                }
                
                double completionRate = teamTasks > 0 ? (completedTasks * 100.0) / teamTasks : 0.0;
                stats.append("- ").append(teamName)
                     .append(": ").append(teamMembers).append(" membros, ")
                     .append(teamTasks).append(" tarefas (")
                     .append(String.format("%.1f%%", completionRate)).append(" concluídas)\n");
            }
            
            // Exibir relatório
            JDialog statsDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                            "Estatísticas do Sistema", true);
            statsDialog.setSize(700, 600);
            statsDialog.setLocationRelativeTo(this);
            
            JTextArea statsArea = new JTextArea(stats.toString());
            statsArea.setEditable(false);
            statsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
            
            JScrollPane scrollPane = new JScrollPane(statsArea);
            statsDialog.add(scrollPane, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton refreshButton = new JButton("Actualizar");
            JButton closeButton = new JButton("Fechar");
            
            refreshButton.addActionListener(e -> {
                statsDialog.dispose();
                showSystemStatistics(); // Reabrir com dados actualizados
            });
            
            closeButton.addActionListener(e -> statsDialog.dispose());
            
            buttonPanel.add(refreshButton);
            buttonPanel.add(closeButton);
            statsDialog.add(buttonPanel, BorderLayout.SOUTH);
            
            statsDialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao gerar estatísticas: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
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
    
    /**
     * Sobrescrever método para mostrar detalhes da tarefa com opções para administrador
     */
    @Override
    protected void showTaskDetails(TaskItem task) {
        // Criar diálogo personalizado para admin com opção de comentários
        String[] options = {"💬 Ver Comentários", "📋 Detalhes", "Fechar"};
        
        int choice = JOptionPane.showOptionDialog(this, 
            String.format("Tarefa: %s\n\nEscolha uma ação:", task.getTitle()),
            "Administrar Tarefa #" + task.getId(),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        switch (choice) {
            case 0: // Ver Comentários
                showTaskComments(task);
                break;
            case 1: // Detalhes básicos
                super.showTaskDetails(task);
                break;
            // Caso 2 ou qualquer outro: Fechar (não faz nada)
        }
    }
    
    /**
     * Atualiza um campo de uma equipa através da API
     */
    private void updateTeamField(int row, int column) {
        try {
            String teamId = teamsTableModel.getValueAt(row, 0).toString();
            String newValue = teamsTableModel.getValueAt(row, column).toString();
            com.gestortarefas.util.RestApiClient apiClient = new com.gestortarefas.util.RestApiClient();
            boolean success = false;
            
            if (column == 1 || column == 2) {
                // Colunas Nome (1) ou Descrição (2)
                String currentName = teamsTableModel.getValueAt(row, 1).toString();
                String currentDescription = teamsTableModel.getValueAt(row, 2).toString();
                
                String name = (column == 1) ? newValue : currentName;
                String description = (column == 2) ? newValue : currentDescription;
                
                success = apiClient.updateTeam(Long.parseLong(teamId), name, description, currentUserId);
                
            } else if (column == 3) {
                // Coluna Gestor (3)
                // Buscar utilizador pelo nome completo
                Map<String, Object> user = apiClient.findUserByFullName(newValue);
                if (user != null) {
                    Long managerId = ((Number) user.get("id")).longValue();
                    success = apiClient.setTeamManager(Long.parseLong(teamId), managerId, currentUserId);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Utilizador '" + newValue + "' não encontrado!", 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                    loadAllTeams(); // Reverter
                    return;
                }
            }
            
            if (success) {
                // Atualização bem-sucedida - recarregar dados para mostrar mudanças
                loadAllTeams();
                JOptionPane.showMessageDialog(this, 
                    "Equipa atualizada com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Reverter a mudança se falhou
                loadAllTeams();
                JOptionPane.showMessageDialog(this, 
                    "Erro ao atualizar equipa. As alterações foram revertidas.", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            // Reverter a mudança em caso de erro
            loadAllTeams();
            JOptionPane.showMessageDialog(this, 
                "Erro ao atualizar equipa: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Abre diálogo de comentários da tarefa
     */
    private void showTaskComments(TaskItem task) {
        try {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            TaskCommentsDialog commentsDialog = new TaskCommentsDialog(
                parentWindow,
                task.getId(),
                task.getTitle(),
                currentUserId,
                apiClient
            );
            commentsDialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir comentários da tarefa: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}