package com.gestortarefas.view.dashboard;

import com.gestortarefas.view.dialog.AssignTaskDialog;
import com.gestortarefas.view.dialogs.TaskCommentsDialog;
import com.gestortarefas.view.dialogs.TaskAssignmentDialog;
import com.gestortarefas.gui.Colors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Dashboard específico para gestores
 */
public class ManagerDashboardPanel extends DashboardBasePanel {
    
    private JLabel managerInfoLabel;
    private JPanel managerPanel;
    private JTabbedPane tabbedPane;
    private JTable teamMembersTable;
    private DefaultTableModel teamMembersModel;
    private JComboBox<TeamComboItem> teamSelector;
    private Long selectedTeamId;
    
    public ManagerDashboardPanel(Long managerId) {
        super(managerId);
        initializeManagerComponents();
        loadManagerData();
    }
    
    private void initializeManagerComponents() {
        // Remover componentes padrão
        removeAll();
        setLayout(new BorderLayout());
        
        // Painel superior com informações do gestor
        managerPanel = new JPanel(new BorderLayout());
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        managerInfoLabel = new JLabel("Carregando informações do gestor...");
        managerInfoLabel.setFont(managerInfoLabel.getFont().deriveFont(Font.BOLD, 14f));
        infoPanel.add(new JLabel("Gestor: "));
        infoPanel.add(managerInfoLabel);
        
        // Seletor de equipa
        JPanel teamSelectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        teamSelectorPanel.add(new JLabel("Equipa: "));
        teamSelector = new JComboBox<>();
        teamSelector.addActionListener(e -> {
            TeamComboItem selected = (TeamComboItem) teamSelector.getSelectedItem();
            if (selected != null) {
                selectedTeamId = selected.getId();
                refreshDashboard();
                loadTeamMembers();
            }
        });
        teamSelectorPanel.add(teamSelector);
        
        managerPanel.add(infoPanel, BorderLayout.NORTH);
        managerPanel.add(teamSelectorPanel, BorderLayout.CENTER);
        add(managerPanel, BorderLayout.NORTH);
        
        // Painel com abas
        tabbedPane = new JTabbedPane();
        
        // Aba 1: Dashboard de tarefas
        JPanel dashboardTab = createDashboardTab();
        tabbedPane.addTab("Dashboard", dashboardTab);
        
        // Aba 2: Membros da equipa
        JPanel teamTab = createTeamTab();
        tabbedPane.addTab("Equipa", teamTab);
        
        // Aba 3: Relatórios
        JPanel reportsTab = createReportsTab();
        tabbedPane.addTab("Relatórios", reportsTab);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addManagerButtons(buttonPanel);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createDashboardTab() {
        JPanel dashboardTab = new JPanel(new BorderLayout());
        
        // Adicionar painel de estatísticas
        dashboardTab.add(statsPanel, BorderLayout.NORTH);
        
        // Painel das 4 colunas
        JPanel columnsPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        columnsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Recriar painéis de coluna com títulos específicos para gestor
        initializeColumnPanels();
        updateManagerColumnTitles();
        
        columnsPanel.add(pendingPanel);
        columnsPanel.add(todayPanel);
        columnsPanel.add(overduePanel);
        columnsPanel.add(completedPanel);
        
        dashboardTab.add(columnsPanel, BorderLayout.CENTER);
        
        return dashboardTab;
    }
    
    private void updateManagerColumnTitles() {
        pendingPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 140, 0), 2), 
            "PENDENTES DA EQUIPA"));
        
        todayPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLUE, 2), 
            "PARA HOJE"));
        
        overduePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.RED, 2), 
            "ATRASADAS"));
        
        completedPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(34, 139, 34), 2), 
            "CONCLUÍDAS"));
    }
    
    private JPanel createTeamTab() {
        JPanel teamTab = new JPanel(new BorderLayout());
        
        // Tabela de membros da equipa
        String[] columns = {"ID", "Nome", "Email", "Tarefas Ativas", "Tarefas Concluídas", "Taxa Conclusão"};
        teamMembersModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela apenas para visualização
            }
        };
        
        teamMembersTable = new JTable(teamMembersModel);
        teamMembersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamMembersTable.setRowHeight(32);
        
        // Aplicar tema moderno e alinhamento central (exceto nome - coluna 1)
        Colors.applyModernTable(teamMembersTable);
        Colors.applyCenterAlignment(teamMembersTable, 1);
        
        teamMembersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = teamMembersTable.getSelectedRow();
                    if (row >= 0) {
                        String memberId = teamMembersTable.getValueAt(row, 0).toString();
                        showMemberDetails(Long.parseLong(memberId));
                    }
                }
            }
        });
        
        JScrollPane tableScrollPane = new JScrollPane(teamMembersTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 300));
        
        teamTab.add(new JLabel("Duplo clique para ver detalhes do membro"), BorderLayout.NORTH);
        teamTab.add(tableScrollPane, BorderLayout.CENTER);
        
        return teamTab;
    }
    
    private JPanel createReportsTab() {
        JPanel reportsTab = new JPanel(new GridLayout(2, 2, 10, 10));
        reportsTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Botões para diferentes tipos de relatórios
        JButton productivityBtn = new JButton("Relatório de Produtividade");
        productivityBtn.addActionListener(e -> showProductivityReport());
        
        JButton tasksBtn = new JButton("Relatório de Tarefas");
        tasksBtn.addActionListener(e -> showTasksReport());
        
        JButton teamPerformanceBtn = new JButton("Performance da Equipa");
        teamPerformanceBtn.addActionListener(e -> showTeamPerformanceReport());
        
        JButton exportBtn = new JButton("Exportar Dados");
        exportBtn.addActionListener(e -> exportTeamData());
        
        reportsTab.add(productivityBtn);
        reportsTab.add(tasksBtn);
        reportsTab.add(teamPerformanceBtn);
        reportsTab.add(exportBtn);
        
        return reportsTab;
    }
    
    private void addManagerButtons(JPanel buttonPanel) {
        JButton assignTaskBtn = new JButton("Atribuir Tarefa");
        assignTaskBtn.setBackground(new Color(70, 130, 180));
        assignTaskBtn.setForeground(Color.WHITE);
        assignTaskBtn.addActionListener(e -> openAssignTaskDialog());
        
        JButton manageTeamBtn = new JButton("Gerir Equipa");
        manageTeamBtn.setBackground(new Color(34, 139, 34));
        manageTeamBtn.setForeground(Color.WHITE);
        manageTeamBtn.addActionListener(e -> openTeamManagementDialog());
        
        JButton refreshBtn = new JButton("Atualizar");
        refreshBtn.addActionListener(e -> {
            refreshDashboard();
            loadTeamMembers();
        });
        
        buttonPanel.add(assignTaskBtn);
        buttonPanel.add(manageTeamBtn);
        buttonPanel.add(refreshBtn);
    }
    
    @Override
    protected void refreshDashboard() {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("ManagerDashboard: Carregando dashboard para userId: " + currentUserId);
                Map<String, Object> dashboardData = apiClient.getManagerDashboard(currentUserId);
                
                if (dashboardData != null) {
                    System.out.println("ManagerDashboard: Dados recebidos com sucesso");
                    System.out.println("ManagerDashboard: Keys no dashboardData: " + dashboardData.keySet());
                    
                    updateTaskLists(dashboardData);
                    updateManagerInfo(dashboardData);
                } else {
                    System.err.println("ManagerDashboard: Dados null recebidos da API");
                    showErrorMessage("Erro ao carregar dashboard do gestor - dados vazios");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("ManagerDashboard: Erro na chamada da API: " + e.getMessage());
                showErrorMessage("Erro de conexão: " + e.getMessage());
            }
        });
    }
    
    private void loadManagerData() {
        // Carregar equipas do gestor
        loadManagedTeams();
        // Carregar dados iniciais
        refreshDashboard();
    }
    
    private void loadManagedTeams() {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("ManagerDashboard: Carregando equipas para managerId: " + currentUserId);
                List<Map<String, Object>> teams = apiClient.getManagedTeams(currentUserId);
                teamSelector.removeAllItems();
                
                if (teams != null && !teams.isEmpty()) {
                    System.out.println("ManagerDashboard: " + teams.size() + " equipas encontradas");
                    for (Map<String, Object> team : teams) {
                        Long id = ((Number) team.get("id")).longValue();
                        String name = (String) team.get("name");
                        teamSelector.addItem(new TeamComboItem(id, name));
                        System.out.println("ManagerDashboard: Equipa adicionada: " + name + " (ID: " + id + ")");
                    }
                    
                    // Selecionar primeira equipa por padrão
                    if (teamSelector.getItemCount() > 0) {
                        TeamComboItem firstTeam = teamSelector.getItemAt(0);
                        selectedTeamId = firstTeam.getId();
                        System.out.println("ManagerDashboard: Equipa selecionada por padrão: " + selectedTeamId);
                        loadTeamMembers();
                    }
                } else {
                    System.out.println("ManagerDashboard: Nenhuma equipa encontrada para o gestor");
                    managerInfoLabel.setText("Nenhuma equipa atribuída");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("ManagerDashboard: Erro ao carregar equipas: " + e.getMessage());
                showErrorMessage("Erro ao carregar equipas: " + e.getMessage());
            }
        });
    }
    
    private void loadTeamMembers() {
        if (selectedTeamId == null) return;
        
        SwingUtilities.invokeLater(() -> {
            try {
                List<Map<String, Object>> members = apiClient.getTeamMembers(selectedTeamId);
                teamMembersModel.setRowCount(0);
                
                if (members != null) {
                    for (Map<String, Object> member : members) {
                        Object[] row = {
                            member.get("id"),
                            member.get("username"),
                            member.get("email"),
                            member.get("activeTasks") != null ? member.get("activeTasks") : 0,
                            member.get("completedTasks") != null ? member.get("completedTasks") : 0,
                            calculateCompletionRate(member)
                        };
                        teamMembersModel.addRow(row);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showErrorMessage("Erro ao carregar membros da equipa: " + e.getMessage());
            }
        });
    }
    
    private String calculateCompletionRate(Map<String, Object> member) {
        Object activeObj = member.get("activeTasks");
        Object completedObj = member.get("completedTasks");
        
        int active = activeObj != null ? ((Number) activeObj).intValue() : 0;
        int completed = completedObj != null ? ((Number) completedObj).intValue() : 0;
        int total = active + completed;
        
        if (total == 0) return "0%";
        
        double rate = (double) completed / total * 100;
        return String.format("%.1f%%", rate);
    }
    
    @SuppressWarnings("unchecked")
    private void updateManagerInfo(Map<String, Object> dashboardData) {
        try {
            Object managerObj = dashboardData.get("manager");
            if (managerObj instanceof Map) {
                Map<String, Object> manager = (Map<String, Object>) managerObj;
                String username = (String) manager.get("username");
                String email = (String) manager.get("email");
                if (username != null) {
                    managerInfoLabel.setText(username + (email != null ? " (" + email + ")" : ""));
                    // Mostrar contagem global de comentários não lidos
                    try {
                        Map<String, Object> unread = apiClient.getUnreadCommentsCount(currentUserId);
                        if (unread != null && unread.containsKey("totalUnread")) {
                            int totalUnread = ((Number) unread.get("totalUnread")).intValue();
                            if (totalUnread > 0) {
                                managerInfoLabel.setText(username + (email != null ? " (" + email + ")" : "") + " - \uD83D\uDD14 " + totalUnread + " não lidos");
                            }
                        }
                    } catch (Exception ex) {
                        // não critico
                    }
                }
            } else {
                // Fallback: usar informações básicas se disponíveis
                Object userObj = dashboardData.get("user");
                if (userObj instanceof Map) {
                    Map<String, Object> user = (Map<String, Object>) userObj;
                    String username = (String) user.get("username");
                    String email = (String) user.get("email");
                    if (username != null) {
                        managerInfoLabel.setText(username + (email != null ? " (" + email + ")" : ""));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ManagerDashboard: Erro ao atualizar info do gestor: " + e.getMessage());
            managerInfoLabel.setText("Informações do gestor não disponíveis");
        }
    }
    
    private void showMemberDetails(Long memberId) {
        // Implementar diálogo com detalhes do membro
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Detalhes do Membro", true);
        dialog.setSize(500, 400);
        
        // Implementar conteúdo do diálogo
        JTextArea detailsArea = new JTextArea();
        detailsArea.setText("Detalhes do membro ID: " + memberId + "\n\n");
        detailsArea.append("Implementar carregamento de dados detalhados via API...");
        detailsArea.setEditable(false);
        
        dialog.add(new JScrollPane(detailsArea));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    // Métodos para relatórios
    private void showProductivityReport() {
        if (selectedTeamId == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma equipa primeiro!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Buscar dados da equipa
            List<Map<String, Object>> members = apiClient.getTeamMembers(selectedTeamId);
            
            StringBuilder report = new StringBuilder();
            report.append("=== RELATÓRIO DE PRODUTIVIDADE ===\n\n");
            
            if (members != null && !members.isEmpty()) {
                int totalActiveTasks = 0;
                int totalCompletedTasks = 0;
                
                for (Map<String, Object> member : members) {
                    String name = getStringValue(member, "username", "N/A");
                    int activeTasks = getIntValue(member, "activeTasks");
                    int completedTasks = getIntValue(member, "completedTasks");
                    
                    totalActiveTasks += activeTasks;
                    totalCompletedTasks += completedTasks;
                    
                    double completionRate = (activeTasks + completedTasks) > 0 
                        ? (completedTasks * 100.0) / (activeTasks + completedTasks) 
                        : 0;
                    
                    report.append(String.format("👤 %s:\n", name));
                    report.append(String.format("   • Tarefas Ativas: %d\n", activeTasks));
                    report.append(String.format("   • Tarefas Concluídas: %d\n", completedTasks));
                    report.append(String.format("   • Taxa de Conclusão: %.1f%%\n\n", completionRate));
                }
                
                report.append("=== RESUMO DA EQUIPA ===\n");
                report.append(String.format("Total de Tarefas Ativas: %d\n", totalActiveTasks));
                report.append(String.format("Total de Tarefas Concluídas: %d\n", totalCompletedTasks));
                
                double teamRate = (totalActiveTasks + totalCompletedTasks) > 0 
                    ? (totalCompletedTasks * 100.0) / (totalActiveTasks + totalCompletedTasks) 
                    : 0;
                report.append(String.format("Taxa Geral da Equipa: %.1f%%", teamRate));
            } else {
                report.append("Nenhum membro encontrado na equipa.");
            }
            
            JTextArea textArea = new JTextArea(report.toString());
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Relatório de Produtividade", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showTasksReport() {
        try {
            Map<String, Object> stats = apiClient.getManagerDashboard(currentUserId);
            
            StringBuilder report = new StringBuilder();
            report.append("=== RELATÓRIO DE TAREFAS ===\n\n");
            report.append("ESTATÍSTICAS GERAIS:\n");
            report.append("- Total de tarefas: ").append(getIntValue(stats, "totalTasks", 0)).append("\n");
            report.append("- Tarefas pendentes: ").append(getIntValue(stats, "pendingCount", 0)).append("\n");
            report.append("- Tarefas de hoje: ").append(getIntValue(stats, "todayCount", 0)).append("\n");
            report.append("- Tarefas atrasadas: ").append(getIntValue(stats, "overdueCount", 0)).append("\n");
            report.append("- Tarefas concluídas: ").append(getIntValue(stats, "completedCount", 0)).append("\n\n");
            
            // Listar algumas tarefas recentes se disponível
            List<Map<String, Object>> allTasks = apiClient.getUserTasks(currentUserId);
            if (!allTasks.isEmpty()) {
                report.append("ÚLTIMAS TAREFAS:\n");
                int count = 0;
                for (Map<String, Object> task : allTasks) {
                    if (count >= 10) break; // Limitar a 10
                    String title = getStringValue(task, "title", "Sem título");
                    String status = getStringValue(task, "status", "Desconhecido");
                    String priority = getStringValue(task, "priority", "Normal");
                    
                    report.append("- ").append(title)
                          .append(" | Estado: ").append(status)
                          .append(" | Prioridade: ").append(priority)
                          .append("\n");
                    count++;
                }
            }
            
            JDialog reportDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                             "Relatório de Tarefas", true);
            reportDialog.setSize(600, 400);
            reportDialog.setLocationRelativeTo(this);

            JTextArea reportArea = new JTextArea(report.toString());
            reportArea.setEditable(false);
            reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(reportArea);
            reportDialog.add(scrollPane, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton closeButton = new JButton("Fechar");
            closeButton.addActionListener(e -> reportDialog.dispose());
            buttonPanel.add(closeButton);
            
            reportDialog.add(buttonPanel, BorderLayout.SOUTH);
            reportDialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao gerar relatório: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showTeamPerformanceReport() {
        try {
            // Obter dados da equipa
            List<Map<String, Object>> teams = apiClient.getAllTeams();
            List<Map<String, Object>> users = apiClient.getAllUsers();
            List<Map<String, Object>> allTasks = apiClient.getAllTasks();
            
            StringBuilder report = new StringBuilder();
            report.append("=== RELATÓRIO DE PERFORMANCE DA EQUIPA ===\n\n");
            
            // Estatísticas por equipa
            for (Map<String, Object> team : teams) {
                String teamName = getStringValue(team, "name", "Equipa sem nome");
                Long teamId = getLongValue(team, "id");
                
                report.append("EQUIPA: ").append(teamName).append("\n");
                report.append("----------------------------------------\n");
                
                // Contar membros da equipa
                int teamMembers = 0;
                for (Map<String, Object> user : users) {
                    Long userTeamId = getLongValue(user, "teamId");
                    if (teamId != null && teamId.equals(userTeamId)) {
                        teamMembers++;
                    }
                }
                report.append("Membros: ").append(teamMembers).append("\n");
                
                // Estatísticas de tarefas da equipa
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
                        // Verificar se está atrasada (simplificado)
                        String dueDateStr = getStringValue(task, "dueDate", "");
                        if (!dueDateStr.isEmpty() && "Pendente".equalsIgnoreCase(status)) {
                            // Pode adicionar lógica de data aqui
                        }
                    }
                }
                
                report.append("Tarefas totais: ").append(teamTasks).append("\n");
                report.append("Tarefas concluídas: ").append(completedTasks).append("\n");
                
                if (teamTasks > 0) {
                    double completionRate = (completedTasks * 100.0) / teamTasks;
                    report.append("Taxa de conclusão: ").append(String.format("%.1f%%", completionRate)).append("\n");
                }
                
                report.append("\n");
            }
            
            // Estatísticas individuais dos membros
            report.append("\n=== PERFORMANCE INDIVIDUAL ===\n\n");
            for (Map<String, Object> user : users) {
                String userName = getStringValue(user, "name", "Utilizador sem nome");
                Long userId = getLongValue(user, "id");
                
                int userTasks = 0;
                int userCompleted = 0;
                
                for (Map<String, Object> task : allTasks) {
                    Long assignedUserId = getLongValue(task, "assignedUserId");
                    if (userId != null && userId.equals(assignedUserId)) {
                        userTasks++;
                        String status = getStringValue(task, "status", "");
                        if ("Concluída".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
                            userCompleted++;
                        }
                    }
                }
                
                if (userTasks > 0) {
                    double userCompletionRate = (userCompleted * 100.0) / userTasks;
                    report.append(userName).append(": ")
                          .append(userCompleted).append("/").append(userTasks)
                          .append(" (").append(String.format("%.1f%%", userCompletionRate)).append(")\n");
                }
            }
            
            // Exibir relatório
            JDialog reportDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                             "Relatório de Performance da Equipa", true);
            reportDialog.setSize(700, 500);
            reportDialog.setLocationRelativeTo(this);

            JTextArea reportArea = new JTextArea(report.toString());
            reportArea.setEditable(false);
            reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
            
            JScrollPane scrollPane = new JScrollPane(reportArea);
            reportDialog.add(scrollPane, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton closeButton = new JButton("Fechar");
            closeButton.addActionListener(e -> reportDialog.dispose());
            buttonPanel.add(closeButton);
            
            reportDialog.add(buttonPanel, BorderLayout.SOUTH);
            reportDialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao gerar relatório de performance: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportTeamData() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Exportar Dados da Equipa");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos CSV", "csv"));
            
            int userSelection = fileChooser.showSaveDialog(this);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.endsWith(".csv")) {
                    filePath += ".csv";
                }
                
                // Obter dados
                List<Map<String, Object>> teams = apiClient.getAllTeams();
                List<Map<String, Object>> users = apiClient.getAllUsers();
                List<Map<String, Object>> allTasks = apiClient.getAllTasks();
                
                // Criar CSV
                java.io.FileWriter csvWriter = new java.io.FileWriter(filePath);
                csvWriter.append("Equipa,Membro,Cargo,Email,Tarefas Totais,Tarefas Concluídas,Taxa Conclusão (%)\n");
                
                for (Map<String, Object> team : teams) {
                    String teamName = getStringValue(team, "name", "Equipa sem nome");
                    Long teamId = getLongValue(team, "id");
                    
                    for (Map<String, Object> user : users) {
                        Long userTeamId = getLongValue(user, "teamId");
                        if (teamId != null && teamId.equals(userTeamId)) {
                            String userName = getStringValue(user, "name", "Nome não disponível");
                            String userRole = getStringValue(user, "role", "Função não definida");
                            String userEmail = getStringValue(user, "email", "Email não disponível");
                            
                            // Contar tarefas do utilizador
                            Long userId = getLongValue(user, "id");
                            int userTasks = 0;
                            int userCompleted = 0;
                            
                            for (Map<String, Object> task : allTasks) {
                                Long assignedUserId = getLongValue(task, "assignedUserId");
                                if (userId != null && userId.equals(assignedUserId)) {
                                    userTasks++;
                                    String status = getStringValue(task, "status", "");
                                    if ("Concluída".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
                                        userCompleted++;
                                    }
                                }
                            }
                            
                            double completionRate = userTasks > 0 ? (userCompleted * 100.0) / userTasks : 0.0;
                            
                            csvWriter.append(String.format("%s,%s,%s,%s,%d,%d,%.1f\n",
                                escapeCSV(teamName),
                                escapeCSV(userName),
                                escapeCSV(userRole),
                                escapeCSV(userEmail),
                                userTasks,
                                userCompleted,
                                completionRate));
                        }
                    }
                }
                
                csvWriter.flush();
                csvWriter.close();
                
                JOptionPane.showMessageDialog(this, 
                    "Dados exportados com sucesso para:\n" + filePath,
                    "Exportação Concluída", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao exportar dados: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    private void openAssignTaskDialog() {
        if (selectedTeamId == null) {
            JOptionPane.showMessageDialog(this, 
                "Selecione uma equipa primeiro para atribuir tarefas!", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            AssignTaskDialog dialog = new AssignTaskDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), 
                currentUserId, 
                selectedTeamId
            );
            dialog.setVisible(true);
            
            if (dialog.isTaskCreated()) {
                // Atualizar dashboard após criar tarefa
                refreshDashboard();
                loadTeamMembers();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir diálogo de atribuição: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openTeamManagementDialog() {
        try {
            List<Map<String, Object>> teams = apiClient.getAllTeams();
            List<Map<String, Object>> users = apiClient.getAllUsers();
            
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                       "Gestão de Equipas", true);
            dialog.setSize(800, 600);
            dialog.setLocationRelativeTo(this);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            
            // Painel superior - seleção de equipa
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.add(new JLabel("Equipa:"));
            
            DefaultComboBoxModel<String> teamModel = new DefaultComboBoxModel<>();
            JComboBox<String> teamCombo = new JComboBox<>(teamModel);
            
            // Popular combo com equipas
            for (Map<String, Object> team : teams) {
                String teamName = getStringValue(team, "name", "Equipa sem nome");
                teamModel.addElement(teamName);
            }
            
            topPanel.add(teamCombo);
            mainPanel.add(topPanel, BorderLayout.NORTH);
            
            // Painel central - lista de membros
            DefaultListModel<String> memberListModel = new DefaultListModel<>();
            JList<String> memberList = new JList<>(memberListModel);
            memberList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            JScrollPane memberScrollPane = new JScrollPane(memberList);
            memberScrollPane.setBorder(BorderFactory.createTitledBorder("Membros da Equipa"));
            
            // Painel direito - detalhes do membro
            JPanel detailPanel = new JPanel(new GridBagLayout());
            detailPanel.setBorder(BorderFactory.createTitledBorder("Detalhes do Membro"));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            gbc.gridx = 0; gbc.gridy = 0;
            detailPanel.add(new JLabel("Nome:"), gbc);
            JTextField nameField = new JTextField(20);
            nameField.setEditable(false);
            gbc.gridx = 1;
            detailPanel.add(nameField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            detailPanel.add(new JLabel("Email:"), gbc);
            JTextField emailField = new JTextField(20);
            emailField.setEditable(false);
            gbc.gridx = 1;
            detailPanel.add(emailField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 2;
            detailPanel.add(new JLabel("Função:"), gbc);
            JTextField roleField = new JTextField(20);
            roleField.setEditable(false);
            gbc.gridx = 1;
            detailPanel.add(roleField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 3;
            detailPanel.add(new JLabel("Tarefas:"), gbc);
            JTextField tasksField = new JTextField(20);
            tasksField.setEditable(false);
            gbc.gridx = 1;
            detailPanel.add(tasksField, gbc);
            
            // Panel central dividido
            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.add(memberScrollPane, BorderLayout.WEST);
            centerPanel.add(detailPanel, BorderLayout.CENTER);
            
            mainPanel.add(centerPanel, BorderLayout.CENTER);
            
            // Função para actualizar lista de membros
            Runnable updateMemberList = () -> {
                memberListModel.clear();
                nameField.setText("");
                emailField.setText("");
                roleField.setText("");
                tasksField.setText("");
                
                int selectedTeamIndex = teamCombo.getSelectedIndex();
                if (selectedTeamIndex >= 0 && selectedTeamIndex < teams.size()) {
                    Map<String, Object> selectedTeam = teams.get(selectedTeamIndex);
                    Long teamId = getLongValue(selectedTeam, "id");
                    
                    for (Map<String, Object> user : users) {
                        Long userTeamId = getLongValue(user, "teamId");
                        if (teamId != null && teamId.equals(userTeamId)) {
                            String userName = getStringValue(user, "name", "Nome não disponível");
                            memberListModel.addElement(userName);
                        }
                    }
                }
            };
            
            // Listener para mudança de equipa
            teamCombo.addActionListener(e -> updateMemberList.run());
            
            // Listener para seleção de membro
            memberList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    String selectedMember = memberList.getSelectedValue();
                    if (selectedMember != null) {
                        // Encontrar o utilizador selecionado
                        for (Map<String, Object> user : users) {
                            String userName = getStringValue(user, "name", "");
                            if (userName.equals(selectedMember)) {
                                nameField.setText(userName);
                                emailField.setText(getStringValue(user, "email", ""));
                                roleField.setText(getStringValue(user, "role", ""));
                                
                                // Contar tarefas
                                Long userId = getLongValue(user, "id");
                                try {
                                    List<Map<String, Object>> userTasks = apiClient.getUserTasks(userId);
                                    tasksField.setText(String.valueOf(userTasks.size()));
                                } catch (Exception ex) {
                                    tasksField.setText("Erro");
                                }
                                break;
                            }
                        }
                    }
                }
            });
            
            // Botões
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton addMemberButton = new JButton("Adicionar Membro");
            JButton removeMemberButton = new JButton("Remover Membro");
            JButton refreshButton = new JButton("Actualizar");
            JButton closeButton = new JButton("Fechar");
            
            // Configurar botões
            addMemberButton.setBackground(new Color(34, 139, 34));
            addMemberButton.setForeground(Color.WHITE);
            removeMemberButton.setBackground(new Color(220, 20, 60));
            removeMemberButton.setForeground(Color.WHITE);
            
            // Ação do botão Adicionar Membro
            addMemberButton.addActionListener(addEvent -> {
                int selectedTeamIndex = teamCombo.getSelectedIndex();
                if (selectedTeamIndex >= 0 && selectedTeamIndex < teams.size()) {
                    Map<String, Object> selectedTeam = teams.get(selectedTeamIndex);
                    Long teamId = getLongValue(selectedTeam, "id");
                    String teamName = getStringValue(selectedTeam, "name", "Equipa");
                    
                    openAddMemberDialog(dialog, teamId, teamName, users, updateMemberList);
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                        "Por favor, selecione uma equipa primeiro.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            });
            
            // Ação do botão Remover Membro
            removeMemberButton.addActionListener(removeEvent -> {
                String selectedMember = memberList.getSelectedValue();
                if (selectedMember == null) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Por favor, selecione um membro para remover.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int selectedTeamIndex = teamCombo.getSelectedIndex();
                if (selectedTeamIndex >= 0 && selectedTeamIndex < teams.size()) {
                    Map<String, Object> selectedTeam = teams.get(selectedTeamIndex);
                    Long teamId = getLongValue(selectedTeam, "id");
                    
                    // Encontrar o ID do utilizador
                    Long userId = null;
                    for (Map<String, Object> user : users) {
                        String userName = getStringValue(user, "name", "");
                        if (userName.equals(selectedMember)) {
                            userId = getLongValue(user, "id");
                            break;
                        }
                    }
                    
                    if (userId != null) {
                        int confirm = JOptionPane.showConfirmDialog(dialog,
                            "Tem certeza que deseja remover " + selectedMember + " da equipa?",
                            "Confirmar Remoção", JOptionPane.YES_NO_OPTION);
                        
                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                boolean success = apiClient.removeTeamMember(teamId, userId, currentUserId);
                                if (success) {
                                    JOptionPane.showMessageDialog(dialog,
                                        "Membro removido com sucesso!");
                                    // Recarregar dados
                                    users.clear();
                                    users.addAll(apiClient.getAllUsers());
                                    updateMemberList.run();
                                } else {
                                    JOptionPane.showMessageDialog(dialog,
                                        "Erro ao remover membro da equipa.",
                                        "Erro", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(dialog,
                                    "Erro ao remover membro: " + ex.getMessage(),
                                    "Erro", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            });
            
            refreshButton.addActionListener(e -> {
                try {
                    // Recarregar dados
                    teams.clear();
                    teams.addAll(apiClient.getAllTeams());
                    users.clear();
                    users.addAll(apiClient.getAllUsers());
                    
                    teamModel.removeAllElements();
                    for (Map<String, Object> team : teams) {
                        String teamName = getStringValue(team, "name", "Equipa sem nome");
                        teamModel.addElement(teamName);
                    }
                    
                    updateMemberList.run();
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Erro ao actualizar dados: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            closeButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(addMemberButton);
            buttonPanel.add(removeMemberButton);
            buttonPanel.add(refreshButton);
            buttonPanel.add(closeButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.add(mainPanel);
            
            // Carregar dados iniciais
            updateMemberList.run();
            
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir gestão de equipas: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openAddMemberDialog(JDialog parentDialog, Long teamId, String teamName, 
                                   List<Map<String, Object>> allUsers, Runnable updateCallback) {
        try {
            // Obter membros atuais da equipa
            List<Map<String, Object>> currentMembers = apiClient.getTeamMembers(teamId);
            Set<Long> currentMemberIds = new HashSet<>();
            if (currentMembers != null) {
                for (Map<String, Object> member : currentMembers) {
                    Long memberId = getLongValue(member, "id");
                    if (memberId != null) {
                        currentMemberIds.add(memberId);
                    }
                }
            }
            
            // Criar diálogo
            JDialog dialog = new JDialog(parentDialog, "Adicionar Membro à " + teamName, true);
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(parentDialog);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            
            // Lista de utilizadores disponíveis (não membros da equipa)
            DefaultListModel<String> availableUsersModel = new DefaultListModel<>();
            JList<String> availableUsersList = new JList<>(availableUsersModel);
            availableUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            // Popular lista com utilizadores disponíveis
            for (Map<String, Object> user : allUsers) {
                Long userId = getLongValue(user, "id");
                if (userId != null && !currentMemberIds.contains(userId)) {
                    String userName = getStringValue(user, "name", "Nome não disponível");
                    String userRole = getStringValue(user, "role", "");
                    String displayName = userName + " (" + userRole + ")";
                    availableUsersModel.addElement(displayName);
                }
            }
            
            if (availableUsersModel.isEmpty()) {
                JOptionPane.showMessageDialog(parentDialog,
                    "Não há utilizadores disponíveis para adicionar à equipa.",
                    "Informação", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JScrollPane listScrollPane = new JScrollPane(availableUsersList);
            listScrollPane.setBorder(BorderFactory.createTitledBorder("Utilizadores Disponíveis"));
            mainPanel.add(listScrollPane, BorderLayout.CENTER);
            
            // Painel de informações do utilizador selecionado
            JPanel infoPanel = new JPanel(new GridBagLayout());
            infoPanel.setBorder(BorderFactory.createTitledBorder("Informações do Utilizador"));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            gbc.gridx = 0; gbc.gridy = 0;
            infoPanel.add(new JLabel("Nome:"), gbc);
            JTextField nameInfoField = new JTextField(25);
            nameInfoField.setEditable(false);
            gbc.gridx = 1;
            infoPanel.add(nameInfoField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            infoPanel.add(new JLabel("Email:"), gbc);
            JTextField emailInfoField = new JTextField(25);
            emailInfoField.setEditable(false);
            gbc.gridx = 1;
            infoPanel.add(emailInfoField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 2;
            infoPanel.add(new JLabel("Função:"), gbc);
            JTextField roleInfoField = new JTextField(25);
            roleInfoField.setEditable(false);
            gbc.gridx = 1;
            infoPanel.add(roleInfoField, gbc);
            
            mainPanel.add(infoPanel, BorderLayout.SOUTH);
            
            // Listener para mostrar informações do utilizador selecionado
            availableUsersList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    String selectedItem = availableUsersList.getSelectedValue();
                    if (selectedItem != null) {
                        // Extrair nome do utilizador (antes dos parênteses)
                        String userName = selectedItem.split(" \\(")[0];
                        
                        // Encontrar o utilizador na lista
                        for (Map<String, Object> user : allUsers) {
                            String userNameInList = getStringValue(user, "name", "");
                            if (userNameInList.equals(userName)) {
                                nameInfoField.setText(userNameInList);
                                emailInfoField.setText(getStringValue(user, "email", ""));
                                roleInfoField.setText(getStringValue(user, "role", ""));
                                break;
                            }
                        }
                    }
                }
            });
            
            // Botões
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton addButton = new JButton("Adicionar");
            JButton cancelButton = new JButton("Cancelar");
            
            addButton.setBackground(new Color(34, 139, 34));
            addButton.setForeground(Color.WHITE);
            
            addButton.addActionListener(e -> {
                String selectedItem = availableUsersList.getSelectedValue();
                if (selectedItem == null) {
                    JOptionPane.showMessageDialog(dialog,
                        "Por favor, selecione um utilizador para adicionar.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Extrair nome do utilizador
                String userName = selectedItem.split(" \\(")[0];
                
                // Encontrar o ID do utilizador
                Long userId = null;
                for (Map<String, Object> user : allUsers) {
                    String userNameInList = getStringValue(user, "name", "");
                    if (userNameInList.equals(userName)) {
                        userId = getLongValue(user, "id");
                        break;
                    }
                }
                
                if (userId != null) {
                    try {
                        boolean success = apiClient.addTeamMember(teamId, userId, currentUserId);
                        if (success) {
                            JOptionPane.showMessageDialog(dialog,
                                "Membro adicionado com sucesso à equipa!");
                            dialog.dispose();
                            // Executar callback para atualizar a lista
                            updateCallback.run();
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                "Erro ao adicionar membro à equipa.",
                                "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog,
                            "Erro ao adicionar membro: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            cancelButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(addButton);
            buttonPanel.add(cancelButton);
            
            mainPanel.add(buttonPanel, BorderLayout.NORTH);
            
            dialog.add(mainPanel);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentDialog,
                "Erro ao abrir diálogo de adicionar membro: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            if (managerInfoLabel != null) {
                managerInfoLabel.setText("Erro ao carregar dados");
            }
            JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
        });
    }
    
    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    private int getIntValue(Map<String, Object> map, String key, int defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }
    
    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }
    
    /**
     * Sobrescrever método para mostrar detalhes da tarefa com opções para gestor
     */
    @Override
    protected void showTaskDetails(TaskItem task) {
        String[] options = {"💬 Ver Comentários", "📋 Detalhes", "Fechar"};
        
        int choice = JOptionPane.showOptionDialog(this, 
            String.format("Tarefa: %s\n\nEscolha uma ação:", task.getTitle()),
            "Gerir Tarefa #" + task.getId(),
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

            if (commentsDialog.isMarkedAsRead()) {
                refreshDashboard();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir comentários da tarefa: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Classe interna para items do ComboBox de equipas
    private static class TeamComboItem {
        private final Long id;
        private final String name;
        
        public TeamComboItem(Long id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public Long getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
}