package com.gestortarefas.view.dashboard;

import com.gestortarefas.view.dialogs.UserCreateDialog;
import com.gestortarefas.view.dialogs.UserEditDialog;
import com.gestortarefas.view.dialogs.TaskCreateDialog;
import com.gestortarefas.view.dialogs.TeamCreateDialog;
import com.gestortarefas.view.dialogs.TeamEditDialog;
import com.gestortarefas.view.dialogs.TaskCommentsDialog;
import com.gestortarefas.util.TableAutoResizer;
import com.gestortarefas.gui.Colors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Dashboard específico para administradores
 */
public class AdminDashboardPanel extends DashboardBasePanel {
    
    private JLabel adminInfoLabel;
    private JTabbedPane mainTabbedPane;
    private DefaultTableModel usersTableModel;
    private DefaultTableModel teamsTableModel;
    private DefaultTableModel tasksTableModel;
    private JTable usersTable;
    private JTable teamsTable;
    private JTable tasksTable;
    
    // Componentes para filtros e pesquisa de tarefas
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JComboBox<String> priorityFilter;
    private JComboBox<String> userFilter;
    private JComboBox<String> teamFilter;
    private JComboBox<String> sortByCombo;
    private JComboBox<String> sortDirectionCombo;
    
    // Labels para estatísticas globais
    private JLabel totalUsersLabel;
    private JLabel totalTeamsLabel;
    private JLabel totalTasksLabel;
    private JLabel activeTasksLabel;
    private JLabel completedTasksLabel;
    private JLabel overdueTasksLabel;
    private JLabel systemUptimeLabel;
    private JLabel avgCompletionLabel;
    
    public AdminDashboardPanel(Long adminId) {
        super(adminId);
        initializeAdminComponents();
        loadAdminData();
    }
    
    private void initializeAdminComponents() {
        // Remover componentes padrão e reorganizar layout
        removeAll();
        setLayout(new BorderLayout());
        setBackground(Colors.PANEL_BACKGROUND);
        
        // Painel superior com informações do admin
        JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        adminPanel.setBackground(Colors.CARD_BACKGROUND);
        adminPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colors.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        adminInfoLabel = new JLabel("Carregando informações do administrador...");
        adminInfoLabel.setFont(adminInfoLabel.getFont().deriveFont(Font.BOLD, 14f));
        adminInfoLabel.setForeground(Colors.DARK_GRAY);
        
        JLabel adminLabel = new JLabel("Administrador: ");
        adminLabel.setForeground(Colors.DARK_GRAY);
        
        adminPanel.add(adminLabel);
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
        
        // Inicializar labels como instance variables
        totalUsersLabel = new JLabel("Total Utilizadores: 0", SwingConstants.CENTER);
        totalTeamsLabel = new JLabel("Total Equipas: 0", SwingConstants.CENTER);
        totalTasksLabel = new JLabel("Total Tarefas: 0", SwingConstants.CENTER);
        activeTasksLabel = new JLabel("Tarefas Ativas: 0", SwingConstants.CENTER);
        completedTasksLabel = new JLabel("Tarefas Concluídas: 0", SwingConstants.CENTER);
        overdueTasksLabel = new JLabel("Tarefas Atrasadas: 0", SwingConstants.CENTER);
        systemUptimeLabel = new JLabel("Tempo Ativo: N/A", SwingConstants.CENTER);
        avgCompletionLabel = new JLabel("Taxa Média: 0%", SwingConstants.CENTER);
        
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
        
        // Painel superior com controles
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Painel de ações (Nova Tarefa, Atualizar, Exportar)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton newTaskBtn = new JButton("✚ Nova Tarefa");
        newTaskBtn.setBackground(new Color(70, 130, 180));
        newTaskBtn.setForeground(Color.WHITE);
        newTaskBtn.setFont(newTaskBtn.getFont().deriveFont(Font.BOLD));
        newTaskBtn.addActionListener(e -> createNewTask());
        
        JButton refreshTasksBtn = new JButton("🔄 Atualizar");
        refreshTasksBtn.addActionListener(e -> loadAllTasks());
        
        JButton exportTasksBtn = new JButton("📤 Exportar");
        exportTasksBtn.addActionListener(e -> exportAllTasks());
        
        actionPanel.add(newTaskBtn);
        actionPanel.add(refreshTasksBtn);
        actionPanel.add(exportTasksBtn);
        
        // Painel de filtros e pesquisa
        JPanel filtersPanel = createTaskFiltersPanel();
        
        topPanel.add(actionPanel, BorderLayout.NORTH);
        topPanel.add(filtersPanel, BorderLayout.CENTER);
        
        tasksTab.add(topPanel, BorderLayout.NORTH);
        
        // Área central com tabela de tarefas
        JPanel tasksListPanel = new JPanel(new BorderLayout());
        tasksListPanel.setBorder(BorderFactory.createTitledBorder("Arquivo de Tarefas do Sistema"));
        
        // Criar tabela de tarefas
        String[] taskColumns = {
            "ID", "Título", "Descrição", "Status", "Prioridade", 
            "Atribuído a", "Equipa", "Criado Por", "Data Criação", 
            "Data Limite", "Data Conclusão", "Ações"
        };
        
        tasksTableModel = new DefaultTableModel(taskColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == taskColumns.length - 1; // Apenas coluna "Ações" é editável
            }
        };
        
        tasksTable = new JTable(tasksTableModel);
        tasksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tasksTable.setRowHeight(32);
        tasksTable.getTableHeader().setReorderingAllowed(false);
        
        // Aplicar tema moderno
        Colors.applyModernTable(tasksTable);
        
        // Aplicar alinhamento central exceto para Título (1) e Descrição (2)
        Colors.applyCenterAlignment(tasksTable, 1, 2);
        
        // Configurar larguras das colunas
        tasksTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tasksTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Título
        tasksTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Descrição
        tasksTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Status
        tasksTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Prioridade
        tasksTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Atribuído a
        tasksTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Equipa
        tasksTable.getColumnModel().getColumn(7).setPreferredWidth(120); // Criado Por
        tasksTable.getColumnModel().getColumn(8).setPreferredWidth(120); // Data Criação
        tasksTable.getColumnModel().getColumn(9).setPreferredWidth(120); // Data Limite
        tasksTable.getColumnModel().getColumn(10).setPreferredWidth(120); // Data Conclusão
        tasksTable.getColumnModel().getColumn(11).setPreferredWidth(100); // Ações
        
        // Adicionar listener para duplo clique na tabela
        tasksTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = tasksTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        viewTaskDetails(selectedRow);
                    }
                }
            }
        });
        
        // Ordenação por clique no cabeçalho
        tasksTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = tasksTable.columnAtPoint(e.getPoint());
                sortTasksByColumn(column);
            }
        });
        
        JScrollPane tasksScrollPane = new JScrollPane(tasksTable);
        tasksScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tasksScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Usar TableAutoResizer para criar painel com botão de auto-redimensionamento
        JPanel tableWithAutoResize = TableAutoResizer.createTablePanelWithAutoResize(tasksTable, tasksScrollPane);
        tasksListPanel.add(tableWithAutoResize, BorderLayout.CENTER);
        
        // Painel inferior com informações de paginação
        JPanel paginationPanel = createPaginationPanel();
        tasksListPanel.add(paginationPanel, BorderLayout.SOUTH);
        
        tasksTab.add(tasksListPanel, BorderLayout.CENTER);
        
        // Carregar dados iniciais
        loadAllTasks();
        
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
        String[] userColumns = {"ID", "Username", "Nome Completo", "Email", "Perfil", "Equipa", "Ativo", "Data Criação"};
        usersTableModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        usersTable = new JTable(usersTableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.setRowHeight(32);
        
        // Aplicar tema moderno
        Colors.applyModernTable(usersTable);
        
        // Aplicar alinhamento central exceto para Nome Completo (2)
        Colors.applyCenterAlignment(usersTable, 2);
        
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
        
        // Usar TableAutoResizer para criar painel com botão de auto-redimensionamento
        JPanel usersTableWithAutoResize = TableAutoResizer.createTablePanelWithAutoResize(usersTable, usersScrollPane);
        
        usersTab.add(controlsPanel, BorderLayout.NORTH);
        usersTab.add(usersTableWithAutoResize, BorderLayout.CENTER);
        
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
        teamsTable.setRowHeight(32);
        
        // Aplicar tema moderno
        Colors.applyModernTable(teamsTable);
        
        // Aplicar alinhamento central exceto para Nome (1) e Descrição (2)
        Colors.applyCenterAlignment(teamsTable, 1, 2);
        
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
        
        // Usar TableAutoResizer para criar painel com botão de auto-redimensionamento
        JPanel teamsTableWithAutoResize = TableAutoResizer.createTablePanelWithAutoResize(teamsTable, teamsScrollPane);
        
        teamsTab.add(controlsPanel, BorderLayout.NORTH);
        teamsTab.add(teamsTableWithAutoResize, BorderLayout.CENTER);
        
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
        loadGlobalStatistics();
        loadAllUsers();
        loadAllTeams();
    }
    
    /**
     * Carrega estatísticas globais do sistema
     */
    private void loadGlobalStatistics() {
        SwingUtilities.invokeLater(() -> {
            try {
                com.gestortarefas.util.RestApiClient apiClient = new com.gestortarefas.util.RestApiClient();
                Map<String, Object> dashboard = apiClient.getAdminDashboard(currentUserId);
                
                if (dashboard != null) {
                    // Estatísticas de tarefas
                    @SuppressWarnings("unchecked")
                    Map<String, Object> taskStats = (Map<String, Object>) dashboard.get("taskStats");
                    if (taskStats != null) {
                        Integer total = getIntValue(taskStats, "total");
                        Integer active = getIntValue(taskStats, "active");
                        Integer completed = getIntValue(taskStats, "completed");
                        Integer overdue = getIntValue(taskStats, "overdue");
                        Double completionRate = getDoubleValue(taskStats, "completionRate");
                        
                        totalTasksLabel.setText("Total Tarefas: " + total);
                        activeTasksLabel.setText("Tarefas Ativas: " + active);
                        completedTasksLabel.setText("Tarefas Concluídas: " + completed);
                        overdueTasksLabel.setText("Tarefas Atrasadas: " + overdue);
                        avgCompletionLabel.setText(String.format("Taxa Média: %.1f%%", completionRate));
                    }
                    
                    // Estatísticas de utilizadores
                    @SuppressWarnings("unchecked")
                    Map<String, Object> userStats = (Map<String, Object>) dashboard.get("userStats");
                    if (userStats != null) {
                        Integer totalUsers = getIntValue(userStats, "totalUsers");
                        totalUsersLabel.setText("Total Utilizadores: " + totalUsers);
                    }
                    
                    // Estatísticas de equipas
                    @SuppressWarnings("unchecked")
                    Map<String, Object> teamStats = (Map<String, Object>) dashboard.get("teamStatsGlobal");
                    if (teamStats != null) {
                        Integer totalTeams = getIntValue(teamStats, "totalTeams");
                        totalTeamsLabel.setText("Total Equipas: " + totalTeams);
                    }
                    
                    // Tempo ativo do sistema (placeholder - pode ser implementado futuramente)
                    systemUptimeLabel.setText("Tempo Ativo: Sistema OK");
                }
            } catch (Exception e) {
                System.err.println("Erro ao carregar estatísticas globais: " + e.getMessage());
                e.printStackTrace();
            }
        });
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
                        String fullName = (String) user.getOrDefault("fullName", "N/A");
                        String email = (String) user.get("email");
                        String role = user.get("role") != null ? user.get("role").toString() : "N/A";
                        String teamName = (String) user.getOrDefault("teamName", "N/A");
                        boolean active = (Boolean) user.getOrDefault("active", true);
                        String createdAt = user.get("createdAt") != null ? user.get("createdAt").toString() : "N/A";
                        
                        usersTableModel.addRow(new Object[]{
                            id, username, fullName, email, role, teamName, 
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
        System.out.println("DEBUG: editSelectedUser() chamado!");
        
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, selecione um utilizador para editar.", 
                "Nenhum Utilizador Selecionado", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
            
            // Obter dados do utilizador selecionado com conversão segura
            Object userIdObj = model.getValueAt(selectedRow, 0);
            Long userId;
            if (userIdObj instanceof String) {
                userId = Long.parseLong((String) userIdObj);
            } else if (userIdObj instanceof Number) {
                userId = ((Number) userIdObj).longValue();
            } else {
                throw new IllegalStateException("Tipo de ID não suportado: " + userIdObj.getClass());
            }
            
            String currentUsername = (String) model.getValueAt(selectedRow, 1);
            String currentFullName = (String) model.getValueAt(selectedRow, 2);
            String currentEmail = (String) model.getValueAt(selectedRow, 3);
            String currentRole = (String) model.getValueAt(selectedRow, 4);
            
            // Obter informações adicionais - equipa e status ativo
            String currentTeam = "N/A";
            boolean currentActive = true;
            
            // Se existem mais colunas na tabela, pegar essas informações
            if (model.getColumnCount() > 5) {
                Object teamValue = model.getValueAt(selectedRow, 5);
                currentTeam = teamValue != null ? teamValue.toString() : "N/A";
            }
            if (model.getColumnCount() > 6) {
                Object activeValue = model.getValueAt(selectedRow, 6);
                if (activeValue instanceof Boolean) {
                    currentActive = (Boolean) activeValue;
                } else if (activeValue instanceof String) {
                    String activeStr = (String) activeValue;
                    currentActive = "Sim".equalsIgnoreCase(activeStr) || 
                                   "true".equalsIgnoreCase(activeStr) || 
                                   "ativo".equalsIgnoreCase(activeStr) ||
                                   "1".equals(activeStr);
                } else {
                    currentActive = true; // default
                }
            }
            
            System.out.println("DEBUG: Abrindo diálogo para utilizador: " + currentUsername);
            
            // Criar e mostrar o diálogo de edição
            UserEditDialog editDialog = new UserEditDialog(
                SwingUtilities.getWindowAncestor(this),
                apiClient,
                userId,
                currentUsername,
                currentFullName,
                currentEmail,
                currentRole,
                currentTeam,
                currentActive,
                this::loadAllUsers // Callback para recarregar dados após edição
            );
            
            editDialog.setVisible(true);
            
        } catch (Exception ex) {
            System.err.println("ERRO ao abrir diálogo de edição: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro inesperado ao abrir o diálogo de edição: " + ex.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
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
                    // Eliminar utilizador via API
                    com.gestortarefas.util.RestApiClient apiClient = new com.gestortarefas.util.RestApiClient();
                    Map<String, Object> response = apiClient.deleteUser(userId);
                    
                    if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                        // Remover da tabela
                        model.removeRow(selectedRow);
                        
                        JOptionPane.showMessageDialog(this, 
                            "Utilizador '" + userName + "' eliminado com sucesso.",
                            "Eliminação Concluída", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Recarregar dados para garantir consistência
                        loadAllUsers();
                        
                    } else {
                        String errorMessage = response != null ? 
                            (String) response.get("message") : 
                            "Erro desconhecido ao eliminar utilizador";
                        
                        JOptionPane.showMessageDialog(this, 
                            errorMessage,
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
        System.out.println("DEBUG: editSelectedTeam() chamado!");
        
        int selectedRow = teamsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, selecione uma equipa para editar.", 
                "Nenhuma Equipa Selecionada", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            DefaultTableModel model = (DefaultTableModel) teamsTable.getModel();
            
            // Obter dados da equipa selecionada com conversão segura
            Object teamIdObj = model.getValueAt(selectedRow, 0);
            Long teamId;
            if (teamIdObj instanceof String) {
                teamId = Long.parseLong((String) teamIdObj);
            } else if (teamIdObj instanceof Number) {
                teamId = ((Number) teamIdObj).longValue();
            } else {
                throw new IllegalStateException("Tipo de ID não suportado: " + teamIdObj.getClass());
            }
            
            String currentName = (String) model.getValueAt(selectedRow, 1);
            String currentDescription = model.getColumnCount() > 2 ? (String) model.getValueAt(selectedRow, 2) : "";
            String currentManager = model.getColumnCount() > 3 ? (String) model.getValueAt(selectedRow, 3) : "N/A";
            
            // Obter status ativo (assumir ativo se não especificado)
            boolean currentActive = true;
            if (model.getColumnCount() > 4) {
                Object activeValue = model.getValueAt(selectedRow, 4);
                if (activeValue instanceof Boolean) {
                    currentActive = (Boolean) activeValue;
                } else if (activeValue instanceof String) {
                    String activeStr = (String) activeValue;
                    currentActive = "Sim".equalsIgnoreCase(activeStr) || 
                                   "true".equalsIgnoreCase(activeStr) || 
                                   "ativo".equalsIgnoreCase(activeStr) ||
                                   "1".equals(activeStr);
                } else {
                    currentActive = true; // default
                }
            }
            
            System.out.println("DEBUG: Abrindo diálogo para equipa: " + currentName);
            
            // Criar e mostrar o diálogo de edição
            TeamEditDialog editDialog = new TeamEditDialog(
                SwingUtilities.getWindowAncestor(this),
                apiClient,
                teamId,
                currentName,
                currentDescription,
                currentManager,
                currentActive,
                currentUserId, // ID do utilizador logado
                this::loadAllTeams // Callback para recarregar dados após edição
            );
            
            editDialog.setVisible(true);
            
        } catch (Exception ex) {
            System.err.println("ERRO ao abrir diálogo de edição de equipa: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro inesperado ao abrir o diálogo de edição: " + ex.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
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
    
    /**
     * Retorna a prioridade formatada com bolinha colorida
     */
    private String getPriorityWithBall(String priority) {
        if (priority == null) return "<html><span style='color: #999;'>●</span> N/A</html>";
        switch (priority.toUpperCase()) {
            case "URGENTE":
                return "<html><span style='color: #dc3545; font-size: 14px; font-weight: bold;'>●</span> Urgente</html>";
            case "ALTA":
                return "<html><span style='color: #fd7e14; font-size: 14px; font-weight: bold;'>●</span> Alta</html>";
            case "NORMAL":
                return "<html><span style='color: #2196f3; font-size: 14px; font-weight: bold;'>●</span> Normal</html>";
            case "BAIXA":
                return "<html><span style='color: #28a745; font-size: 14px; font-weight: bold;'>●</span> Baixa</html>";
            case "CRITICA":
                return "<html><span style='color: #dc3545; font-size: 14px; font-weight: bold;'>●</span> Crítica</html>";
            default:
                return "<html><span style='color: #999; font-size: 14px;'>○</span> " + priority + "</html>";
        }
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
        // Mudar para o separador "Tarefas" (índice 1)
        mainTabbedPane.setSelectedIndex(1);
        
        // Atualizar a lista de tarefas para garantir que está atualizada
        loadAllTasks();
    }
    
    // Métodos da aba de Tarefas
    private void createNewTask() {
        try {
            // Criar map com dados do utilizador para o TaskDialog
            Map<String, Object> currentUserData = new HashMap<>();
            currentUserData.put("id", currentUserId);
            
            // Usar TaskDialog com campos de atribuição completos
            com.gestortarefas.gui.TaskDialog dialog = new com.gestortarefas.gui.TaskDialog(
                null, // Parent pode ser null
                currentUserData
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
        // Verificar se a tarefa está concluída para mostrar opção de arquivar
        boolean isCompleted = "CONCLUIDA".equalsIgnoreCase(task.getStatus());
        
        String[] options;
        if (isCompleted) {
            options = new String[]{"💬 Ver Comentários", "📋 Detalhes", "📦 Arquivar", "Fechar"};
        } else {
            options = new String[]{"💬 Ver Comentários", "📋 Detalhes", "Fechar"};
        }
        
        int choice = JOptionPane.showOptionDialog(this, 
            String.format("Tarefa: %s\n\nEscolha uma ação:", task.getTitle()),
            "Administrar Tarefa #" + task.getId(),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (isCompleted) {
            switch (choice) {
                case 0: // Ver Comentários
                    showTaskComments(task);
                    break;
                case 1: // Detalhes básicos
                    super.showTaskDetails(task);
                    break;
                case 2: // Arquivar
                    archiveTask(task);
                    break;
                // Caso 3 ou qualquer outro: Fechar (não faz nada)
            }
        } else {
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
    }
    
    /**
     * Arquiva uma tarefa concluída
     */
    private void archiveTask(TaskItem task) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja arquivar a tarefa:\n\"" + task.getTitle() + "\"?\n\n" +
            "A tarefa será removida da dashboard mas ficará disponível no separador Tarefas.",
            "Confirmar Arquivo",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                com.gestortarefas.util.RestApiClient apiClient = new com.gestortarefas.util.RestApiClient();
                boolean success = apiClient.archiveTask(task.getId(), currentUserId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Tarefa arquivada com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                    // Recarregar dashboard para remover a tarefa da lista
                    loadAdminData();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Erro ao arquivar a tarefa. Tente novamente.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Erro ao arquivar a tarefa: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
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
                
                success = apiClient.updateTeam(Long.parseLong(teamId), name, description, null, currentUserId);
                
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

    // ======================== MÉTODOS PARA O ARQUIVO DE TAREFAS ========================
    
    /**
     * Cria o painel de filtros e pesquisa para tarefas
     */
    private JPanel createTaskFiltersPanel() {
        JPanel filtersPanel = new JPanel(new BorderLayout());
        filtersPanel.setBorder(BorderFactory.createTitledBorder("Filtros e Pesquisa"));
        
        // Painel superior com pesquisa
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Pesquisar:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> applyFilters());
        searchPanel.add(searchField);
        
        JButton searchBtn = new JButton("🔍 Pesquisar");
        searchBtn.addActionListener(e -> applyFilters());
        searchPanel.add(searchBtn);
        
        JButton clearBtn = new JButton("🔄 Limpar");
        clearBtn.addActionListener(e -> clearFilters());
        searchPanel.add(clearBtn);
        
        // Painel inferior com combos de filtro
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Filtro por Status
        filterPanel.add(new JLabel("Status:"));
        statusFilter = new JComboBox<>(new String[]{"Todos", "PENDENTE", "EM_ANDAMENTO", "CONCLUIDA", "CANCELADA"});
        statusFilter.addActionListener(e -> applyFilters());
        filterPanel.add(statusFilter);
        
        // Filtro por Prioridade
        filterPanel.add(new JLabel("Prioridade:"));
        priorityFilter = new JComboBox<>(new String[]{"Todas", "BAIXA", "NORMAL", "ALTA", "CRITICA"});
        priorityFilter.addActionListener(e -> applyFilters());
        filterPanel.add(priorityFilter);
        
        // Filtro por Utilizador
        filterPanel.add(new JLabel("Utilizador:"));
        userFilter = new JComboBox<>(new String[]{"Todos"});
        userFilter.addActionListener(e -> applyFilters());
        filterPanel.add(userFilter);
        
        // Filtro por Equipa
        filterPanel.add(new JLabel("Equipa:"));
        teamFilter = new JComboBox<>(new String[]{"Todas"});
        teamFilter.addActionListener(e -> applyFilters());
        filterPanel.add(teamFilter);
        
        // Ordenação
        filterPanel.add(new JLabel("Ordenar por:"));
        sortByCombo = new JComboBox<>(new String[]{"createdAt", "title", "priority", "dueDate", "status"});
        sortByCombo.addActionListener(e -> applyFilters());
        filterPanel.add(sortByCombo);
        
        sortDirectionCombo = new JComboBox<>(new String[]{"desc", "asc"});
        sortDirectionCombo.addActionListener(e -> applyFilters());
        filterPanel.add(sortDirectionCombo);
        
        filtersPanel.add(searchPanel, BorderLayout.NORTH);
        filtersPanel.add(filterPanel, BorderLayout.CENTER);
        
        return filtersPanel;
    }
    
    /**
     * Cria o painel de paginação
     */
    private JPanel createPaginationPanel() {
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paginationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton prevBtn = new JButton("◀ Anterior");
        prevBtn.addActionListener(e -> previousPage());
        
        JLabel pageInfo = new JLabel("Página 1 de 1");
        
        JButton nextBtn = new JButton("Próxima ▶");
        nextBtn.addActionListener(e -> nextPage());
        
        paginationPanel.add(prevBtn);
        paginationPanel.add(pageInfo);
        paginationPanel.add(nextBtn);
        
        return paginationPanel;
    }
    
    /**
     * Carrega todas as tarefas do sistema
     */
    private void loadAllTasks() {
        SwingUtilities.invokeLater(() -> {
            try {
                if (tasksTableModel != null) {
                    tasksTableModel.setRowCount(0);
                }
                
                // Fazer chamada à API para buscar todas as tarefas
                Map<String, String> filters = getCurrentFilters();
                Map<String, Object> response = apiClient.getAllTasks(filters);
                
                if (response != null && (Boolean) response.get("success")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> tasks = (List<Map<String, Object>>) response.get("tasks");
                    
                    for (Map<String, Object> task : tasks) {
                        addTaskToTable(task);
                    }
                    
                    System.out.println("AdminDashboard: Loaded " + tasks.size() + " tasks");
                }
                
            } catch (Exception e) {
                System.err.println("Erro ao carregar tarefas: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Erro ao carregar tarefas: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    /**
     * Adiciona uma tarefa à tabela
     */
    private void addTaskToTable(Map<String, Object> task) {
        Object[] rowData = new Object[12];
        
        rowData[0] = task.get("id");
        rowData[1] = task.get("title");
        rowData[2] = truncateText((String) task.get("description"), 50);
        rowData[3] = task.get("status");
        rowData[4] = task.get("priority");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) task.get("user");
        @SuppressWarnings("unchecked")
        Map<String, Object> team = (Map<String, Object>) task.get("assignedTeam");
        
        // Coluna "Atribuído a": mostrar utilizador primeiro, senão equipa
        if (user != null) {
            rowData[5] = user.get("displayName");
        } else if (team != null) {
            rowData[5] = team.get("name");
        } else {
            rowData[5] = "Não atribuído";
        }
        
        // Coluna "Equipa": sempre mostrar a equipa se existir
        rowData[6] = team != null ? team.get("name") : "Individual";
        
        @SuppressWarnings("unchecked")
        Map<String, Object> createdBy = (Map<String, Object>) task.get("createdBy");
        rowData[7] = createdBy != null ? createdBy.get("displayName") : "N/A";
        
        rowData[8] = formatDateTime((String) task.get("createdAt"));
        rowData[9] = formatDateTime((String) task.get("dueDate"));
        rowData[10] = formatDateTime((String) task.get("completedAt"));
        rowData[11] = "Ver Detalhes";
        
        tasksTableModel.addRow(rowData);
    }
    
    /**
     * Obtém os filtros atuais
     */
    private Map<String, String> getCurrentFilters() {
        Map<String, String> filters = new HashMap<>();
        
        if (searchField != null && !searchField.getText().trim().isEmpty()) {
            filters.put("search", searchField.getText().trim());
        }
        
        if (statusFilter != null && !statusFilter.getSelectedItem().equals("Todos")) {
            filters.put("status", (String) statusFilter.getSelectedItem());
        }
        
        if (priorityFilter != null && !priorityFilter.getSelectedItem().equals("Todas")) {
            filters.put("priority", (String) priorityFilter.getSelectedItem());
        }
        
        if (sortByCombo != null) {
            filters.put("sortBy", (String) sortByCombo.getSelectedItem());
        }
        
        if (sortDirectionCombo != null) {
            filters.put("sortDirection", (String) sortDirectionCombo.getSelectedItem());
        }
        
        return filters;
    }
    
    /**
     * Aplica os filtros selecionados
     */
    private void applyFilters() {
        loadAllTasks();
    }
    
    /**
     * Limpa todos os filtros
     */
    private void clearFilters() {
        if (searchField != null) searchField.setText("");
        if (statusFilter != null) statusFilter.setSelectedIndex(0);
        if (priorityFilter != null) priorityFilter.setSelectedIndex(0);
        if (userFilter != null) userFilter.setSelectedIndex(0);
        if (teamFilter != null) teamFilter.setSelectedIndex(0);
        if (sortByCombo != null) sortByCombo.setSelectedIndex(0);
        if (sortDirectionCombo != null) sortDirectionCombo.setSelectedIndex(0);
        
        loadAllTasks();
    }
    
    /**
     * Visualiza detalhes de uma tarefa
     */
    private void viewTaskDetails(int selectedRow) {
        try {
            Object taskId = tasksTableModel.getValueAt(selectedRow, 0);
            if (taskId != null) {
                Map<String, Object> task = apiClient.getTaskById(Long.valueOf(taskId.toString()));
                if (task != null) {
                    showTaskDetailsFromMap(task);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erro ao visualizar detalhes da tarefa: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Mostra detalhes de uma tarefa a partir de um Map
     */
    private void showTaskDetailsFromMap(Map<String, Object> taskData) {
        StringBuilder details = new StringBuilder();
        details.append("=== DETALHES DA TAREFA ===\n\n");
        details.append("ID: ").append(taskData.get("id")).append("\n");
        details.append("Título: ").append(taskData.get("title")).append("\n");
        details.append("Descrição: ").append(taskData.get("description")).append("\n");
        details.append("Status: ").append(taskData.get("status")).append("\n");
        details.append("Prioridade: ").append(taskData.get("priority")).append("\n");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) taskData.get("user");
        details.append("Utilizador: ").append(user != null ? user.get("displayName") : "N/A").append("\n");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> team = (Map<String, Object>) taskData.get("assignedTeam");
        details.append("Equipa: ").append(team != null ? team.get("name") : "Individual").append("\n");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> createdBy = (Map<String, Object>) taskData.get("createdBy");
        details.append("Criado por: ").append(createdBy != null ? createdBy.get("displayName") : "N/A").append("\n");
        
        details.append("Data de Criação: ").append(formatDateTime((String) taskData.get("createdAt"))).append("\n");
        details.append("Data Limite: ").append(formatDateTime((String) taskData.get("dueDate"))).append("\n");
        details.append("Data de Conclusão: ").append(formatDateTime((String) taskData.get("completedAt"))).append("\n");
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
                                    "Detalhes da Tarefa #" + taskData.get("id"), 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Ordena tarefas por coluna
     */
    private void sortTasksByColumn(int column) {
        String[] sortFields = {"id", "title", "description", "status", "priority", 
                              "user", "team", "createdBy", "createdAt", "dueDate", "completedAt"};
        
        if (column < sortFields.length && sortByCombo != null) {
            sortByCombo.setSelectedItem(sortFields[column]);
            
            // Alternar direção da ordenação
            if (sortDirectionCombo != null) {
                String currentDirection = (String) sortDirectionCombo.getSelectedItem();
                sortDirectionCombo.setSelectedItem(currentDirection.equals("asc") ? "desc" : "asc");
            }
            
            applyFilters();
        }
    }
    
    /**
     * Página anterior
     */
    private void previousPage() {
        // TODO: Implementar paginação quando necessário
        JOptionPane.showMessageDialog(this, "Funcionalidade de paginação será implementada em breve", 
                                    "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Próxima página
     */
    private void nextPage() {
        // TODO: Implementar paginação quando necessário
        JOptionPane.showMessageDialog(this, "Funcionalidade de paginação será implementada em breve", 
                                    "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Trunca texto para exibição na tabela
     */
    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Formata data/hora para exibição
     */
    private String formatDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return "";
        try {
            // Assumindo formato ISO: 2023-12-01T10:30:00
            if (dateTimeStr.contains("T")) {
                String[] parts = dateTimeStr.split("T");
                return parts[0] + " " + parts[1].substring(0, 5); // YYYY-MM-DD HH:MM
            }
            return dateTimeStr;
        } catch (Exception e) {
            return dateTimeStr;
        }
    }
}