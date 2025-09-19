package com.gestortarefas.view.dashboard;

import com.gestortarefas.util.RestApiClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

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
                Map<String, Object> dashboardData = apiClient.getManagerDashboard(currentUserId);
                
                if (dashboardData != null) {
                    updateTaskLists(dashboardData);
                    updateManagerInfo(dashboardData);
                } else {
                    showErrorMessage("Erro ao carregar dashboard do gestor");
                }
            } catch (Exception e) {
                e.printStackTrace();
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
                List<Map<String, Object>> teams = apiClient.getManagedTeams(currentUserId);
                teamSelector.removeAllItems();
                
                if (teams != null && !teams.isEmpty()) {
                    for (Map<String, Object> team : teams) {
                        Long id = ((Number) team.get("id")).longValue();
                        String name = (String) team.get("name");
                        teamSelector.addItem(new TeamComboItem(id, name));
                    }
                    
                    // Selecionar primeira equipa por padrão
                    if (teamSelector.getItemCount() > 0) {
                        TeamComboItem firstTeam = teamSelector.getItemAt(0);
                        selectedTeamId = firstTeam.getId();
                        loadTeamMembers();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
    
    private void updateManagerInfo(Map<String, Object> dashboardData) {
        Map<String, Object> manager = (Map<String, Object>) dashboardData.get("manager");
        if (manager != null) {
            String username = (String) manager.get("username");
            String email = (String) manager.get("email");
            managerInfoLabel.setText(username + " (" + email + ")");
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
        JOptionPane.showMessageDialog(this, "Relatório de Produtividade - Implementar", 
            "Relatório", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showTasksReport() {
        JOptionPane.showMessageDialog(this, "Relatório de Tarefas - Implementar", 
            "Relatório", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showTeamPerformanceReport() {
        JOptionPane.showMessageDialog(this, "Performance da Equipa - Implementar", 
            "Relatório", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exportTeamData() {
        JOptionPane.showMessageDialog(this, "Exportar Dados - Implementar", 
            "Exportar", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openAssignTaskDialog() {
        JOptionPane.showMessageDialog(this, "Diálogo de Atribuição de Tarefa - Implementar", 
            "Atribuir Tarefa", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openTeamManagementDialog() {
        JOptionPane.showMessageDialog(this, "Diálogo de Gestão de Equipa - Implementar", 
            "Gerir Equipa", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            if (managerInfoLabel != null) {
                managerInfoLabel.setText("Erro ao carregar dados");
            }
            JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
        });
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