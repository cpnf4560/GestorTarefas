package com.gestortarefas.view.dashboard;

import com.gestortarefas.util.RestApiClient;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Painel base para dashboards com layout de 4 colunas
 */
public class DashboardBasePanel extends JPanel {
    
    protected RestApiClient apiClient;
    protected Long currentUserId;
    protected Timer refreshTimer;
    
    // Painéis das 4 colunas
    protected JPanel pendingPanel;
    protected JPanel todayPanel;
    protected JPanel overduePanel;
    protected JPanel completedPanel;
    
    // Listas das tarefas
    protected DefaultListModel<TaskItem> pendingListModel;
    protected DefaultListModel<TaskItem> todayListModel;
    protected DefaultListModel<TaskItem> overdueListModel;
    protected DefaultListModel<TaskItem> completedListModel;
    
    protected JList<TaskItem> pendingList;
    protected JList<TaskItem> todayList;
    protected JList<TaskItem> overdueList;
    protected JList<TaskItem> completedList;
    
    // Painel de estatísticas
    protected JPanel statsPanel;
    protected JLabel totalTasksLabel;
    protected JLabel pendingCountLabel;
    protected JLabel todayCountLabel;
    protected JLabel overdueCountLabel;
    protected JLabel completedCountLabel;
    
    public DashboardBasePanel(Long userId) {
        this.currentUserId = userId;
        this.apiClient = new RestApiClient();
        
        initializeComponents();
        setupLayout();
        setupRefreshTimer();
    }
    
    private void initializeComponents() {
        // Inicializar modelos das listas
        pendingListModel = new DefaultListModel<>();
        todayListModel = new DefaultListModel<>();
        overdueListModel = new DefaultListModel<>();
        completedListModel = new DefaultListModel<>();
        
        // Criar listas
        pendingList = new JList<>(pendingListModel);
        todayList = new JList<>(todayListModel);
        overdueList = new JList<>(overdueListModel);
        completedList = new JList<>(completedListModel);
        
        // Configurar listas
        configureLists();
        
        // Criar painéis das colunas
        pendingPanel = createColumnPanel("PENDENTES", pendingList, Color.ORANGE);
        todayPanel = createColumnPanel("HOJE", todayList, Color.BLUE);
        overduePanel = createColumnPanel("ATRASADAS", overdueList, Color.RED);
        completedPanel = createColumnPanel("CONCLUÍDAS", completedList, Color.GREEN);
        
        // Criar painel de estatísticas
        createStatsPanel();
    }
    
    private void configureLists() {
        @SuppressWarnings("unchecked")
        JList<TaskItem>[] lists = new JList[]{pendingList, todayList, overdueList, completedList};
        
        for (JList<TaskItem> list : lists) {
            list.setCellRenderer(new TaskItemRenderer());
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.addMouseListener(new TaskMouseListener());
        }
    }
    
    /**
     * Método para recriar painéis de colunas (usado pelas subclasses)
     */
    protected void initializeColumnPanels() {
        // Recriar painéis das colunas
        pendingPanel = createColumnPanel("PENDENTES", pendingList, Color.ORANGE);
        todayPanel = createColumnPanel("HOJE", todayList, Color.BLUE);
        overduePanel = createColumnPanel("ATRASADAS", overdueList, Color.RED);
        completedPanel = createColumnPanel("CONCLUÍDAS", completedList, Color.GREEN);
    }
    
    protected JPanel createColumnPanel(String title, JList<TaskItem> list, Color borderColor) {
        JPanel panel = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleColor(borderColor);
        border.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        panel.setBorder(border);
        
        // Scroll pane para a lista
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(250, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Botões de ação (se necessário)
        JPanel buttonPanel = new JPanel(new FlowLayout());
        if (title.equals("PENDENTES")) {
            JButton startButton = new JButton("Iniciar");
            startButton.addActionListener(e -> startTask());
            buttonPanel.add(startButton);
        } else if (title.equals("HOJE") || title.equals("ATRASADAS")) {
            JButton completeButton = new JButton("Concluir");
            completeButton.addActionListener(e -> completeTask());
            buttonPanel.add(completeButton);
        }
        
        if (buttonPanel.getComponentCount() > 0) {
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }
        
        return panel;
    }
    
    private void createStatsPanel() {
        statsPanel = new JPanel(new GridLayout(1, 5, 10, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Estatísticas"));
        
        totalTasksLabel = new JLabel("Total: 0", SwingConstants.CENTER);
        pendingCountLabel = new JLabel("Pendentes: 0", SwingConstants.CENTER);
        todayCountLabel = new JLabel("Hoje: 0", SwingConstants.CENTER);
        overdueCountLabel = new JLabel("Atrasadas: 0", SwingConstants.CENTER);
        completedCountLabel = new JLabel("Concluídas: 0", SwingConstants.CENTER);
        
        // Cores dos labels
        totalTasksLabel.setOpaque(true);
        totalTasksLabel.setBackground(Color.LIGHT_GRAY);
        pendingCountLabel.setOpaque(true);
        pendingCountLabel.setBackground(new Color(255, 165, 0, 100));
        todayCountLabel.setOpaque(true);
        todayCountLabel.setBackground(new Color(0, 0, 255, 100));
        overdueCountLabel.setOpaque(true);
        overdueCountLabel.setBackground(new Color(255, 0, 0, 100));
        completedCountLabel.setOpaque(true);
        completedCountLabel.setBackground(new Color(0, 255, 0, 100));
        
        statsPanel.add(totalTasksLabel);
        statsPanel.add(pendingCountLabel);
        statsPanel.add(todayCountLabel);
        statsPanel.add(overdueCountLabel);
        statsPanel.add(completedCountLabel);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Painel superior com estatísticas
        add(statsPanel, BorderLayout.NORTH);
        
        // Painel central com as 4 colunas
        JPanel columnsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        columnsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        columnsPanel.add(pendingPanel);
        columnsPanel.add(todayPanel);
        columnsPanel.add(overduePanel);
        columnsPanel.add(completedPanel);
        
        add(columnsPanel, BorderLayout.CENTER);
        
        // Painel inferior com botão de refresh
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Atualizar");
        refreshButton.addActionListener(e -> refreshDashboard());
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupRefreshTimer() {
        // Atualizar a cada 30 segundos
        refreshTimer = new Timer(30000, e -> refreshDashboard());
        refreshTimer.start();
    }
    
    /**
     * Método para ser sobrescrito pelas classes filhas
     */
    protected void refreshDashboard() {
        // Implementado pelas classes específicas
    }
    
    /**
     * Atualiza as listas de tarefas com os dados do dashboard
     */
    @SuppressWarnings("unchecked")
    protected void updateTaskLists(Map<String, Object> dashboardData) {
        SwingUtilities.invokeLater(() -> {
            clearAllLists();
            
            // Atualizar listas
            updateTaskList(pendingListModel, (List<Map<String, Object>>) dashboardData.get("pending"));
            updateTaskList(todayListModel, (List<Map<String, Object>>) dashboardData.get("today"));
            updateTaskList(overdueListModel, (List<Map<String, Object>>) dashboardData.get("overdue"));
            updateTaskList(completedListModel, (List<Map<String, Object>>) dashboardData.get("completed"));
            
            // Atualizar estatísticas
            updateStats(dashboardData);
        });
    }
    
    private void updateTaskList(DefaultListModel<TaskItem> listModel, List<Map<String, Object>> tasks) {
        if (tasks != null) {
            for (Map<String, Object> task : tasks) {
                TaskItem item = new TaskItem(task);
                listModel.addElement(item);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void updateStats(Map<String, Object> dashboardData) {
        Map<String, Object> stats = (Map<String, Object>) dashboardData.get("stats");
        if (stats != null) {
            totalTasksLabel.setText("Total: " + getIntValue(stats, "totalTasks"));
            pendingCountLabel.setText("Pendentes: " + getIntValue(stats, "pendingCount"));
            todayCountLabel.setText("Hoje: " + getIntValue(stats, "todayCount"));
            overdueCountLabel.setText("Atrasadas: " + getIntValue(stats, "overdueCount"));
            completedCountLabel.setText("Concluídas: " + getIntValue(stats, "completedCount"));
        }
    }
    
    protected int getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }
    
    private void clearAllLists() {
        pendingListModel.clear();
        todayListModel.clear();
        overdueListModel.clear();
        completedListModel.clear();
    }
    
    private void startTask() {
        TaskItem selected = pendingList.getSelectedValue();
        if (selected != null) {
            // Atualizar status para EM_ANDAMENTO
            boolean success = apiClient.updateTaskStatus(selected.getId(), "EM_ANDAMENTO", currentUserId);
            if (success) {
                refreshDashboard();
                JOptionPane.showMessageDialog(this, "Tarefa iniciada com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao iniciar tarefa.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void completeTask() {
        TaskItem selected = null;
        
        if (todayList.getSelectedValue() != null) {
            selected = todayList.getSelectedValue();
        } else if (overdueList.getSelectedValue() != null) {
            selected = overdueList.getSelectedValue();
        }
        
        if (selected != null) {
            // Atualizar status para CONCLUIDA
            boolean success = apiClient.updateTaskStatus(selected.getId(), "CONCLUIDA", currentUserId);
            if (success) {
                refreshDashboard();
                JOptionPane.showMessageDialog(this, "Tarefa concluída com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao concluir tarefa.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Classe interna para representar uma tarefa na lista
     */
    protected static class TaskItem {
        private final Long id;
        private final String title;
        private final String description;
        private final String status;
        private final String priority;
        private final String username;
        private final boolean isOverdue;
        
        public TaskItem(Map<String, Object> taskData) {
            this.id = Long.valueOf(taskData.get("id").toString());
            this.title = (String) taskData.get("title");
            this.description = (String) taskData.get("description");
            this.status = (String) taskData.get("status");
            this.priority = (String) taskData.get("priority");
            this.username = (String) taskData.get("username");
            this.isOverdue = Boolean.TRUE.equals(taskData.get("isOverdue"));
        }
        
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getStatus() { return status; }
        public String getPriority() { return priority; }
        public String getUsername() { return username; }
        public boolean isOverdue() { return isOverdue; }
        
        @Override
        public String toString() {
            return title + " (" + priority + ")";
        }
    }
    
    /**
     * Renderer personalizado para as tarefas
     */
    private static class TaskItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof TaskItem) {
                TaskItem item = (TaskItem) value;
                setText(item.getTitle() + " - " + item.getPriority());
                
                if (!isSelected) {
                    if (item.isOverdue()) {
                        setBackground(new Color(255, 200, 200));
                    } else if ("ALTA".equals(item.getPriority())) {
                        setBackground(new Color(255, 255, 200));
                    }
                }
            }
            
            return this;
        }
    }
    
    /**
     * Mouse listener para clique duplo nas tarefas
     */
    private class TaskMouseListener extends java.awt.event.MouseAdapter {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            if (e.getClickCount() == 2) {
                @SuppressWarnings("unchecked")
                JList<TaskItem> source = (JList<TaskItem>) e.getSource();
                TaskItem selected = source.getSelectedValue();
                if (selected != null) {
                    showTaskDetails(selected);
                }
            }
        }
    }
    
    protected void showTaskDetails(TaskItem task) {
        String details = String.format(
            "ID: %d%nTítulo: %s%nDescrição: %s%nStatus: %s%nPrioridade: %s%nUtilizador: %s%nAtrasada: %s",
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getPriority(),
            task.getUsername(),
            task.isOverdue() ? "Sim" : "Não"
        );
        
        JOptionPane.showMessageDialog(this, details, "Detalhes da Tarefa", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void cleanup() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }
}