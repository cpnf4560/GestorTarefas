package com.gestortarefas.view.dashboard;

import com.gestortarefas.util.RestApiClient;
import com.gestortarefas.util.I18nManager;
import com.gestortarefas.gui.Colors;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Painel base para dashboards com layout de 4 colunas
 */
public class DashboardBasePanel extends JPanel {
    
    /**
     * Enum para identificar o tipo de coluna
     */
    protected enum ColumnType {
        PENDING,
        TODAY,
        OVERDUE,
        COMPLETED
    }
    
    protected RestApiClient apiClient;
    protected Long currentUserId;
    protected Timer refreshTimer;
    protected I18nManager i18n;
    
    // Painéis das 4 colunas
    protected JPanel pendingPanel;
    protected JPanel todayPanel;
    protected JPanel overduePanel;
    protected JPanel completedPanel;
    
    // Tabelas das tarefas (estilo Excel)
    protected TaskTableModel pendingTableModel;
    protected TaskTableModel todayTableModel;
    protected TaskTableModel overdueTableModel;
    protected TaskTableModel completedTableModel;
    
    protected JTable pendingTable;
    protected JTable todayTable;
    protected JTable overdueTable;
    protected JTable completedTable;
    
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
        this.i18n = I18nManager.getInstance();
        
        initializeComponents();
        setupLayout();
        setupRefreshTimer();
    }
    
    private void initializeComponents() {
        // Inicializar modelos das tabelas
        pendingTableModel = new TaskTableModel();
        todayTableModel = new TaskTableModel();
        overdueTableModel = new TaskTableModel();
        completedTableModel = new TaskTableModel();
        
        // Criar tabelas
        pendingTable = new JTable(pendingTableModel);
        todayTable = new JTable(todayTableModel);
        overdueTable = new JTable(overdueTableModel);
        completedTable = new JTable(completedTableModel);
        
        // Configurar tabelas
        configureTables();
        
        // Criar painéis das colunas
        pendingPanel = createColumnPanel(ColumnType.PENDING, pendingTable, Color.ORANGE);
        todayPanel = createColumnPanel(ColumnType.TODAY, todayTable, Color.BLUE);
        overduePanel = createColumnPanel(ColumnType.OVERDUE, overdueTable, Color.RED);
        completedPanel = createColumnPanel(ColumnType.COMPLETED, completedTable, Color.GREEN);
        
        // Criar painel de estatísticas
        createStatsPanel();
    }
    
    private void configureTables() {
        JTable[] tables = new JTable[]{pendingTable, todayTable, overdueTable, completedTable};
        
        for (JTable table : tables) {
            // Configurações básicas da tabela
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowSelectionAllowed(true);
            table.setColumnSelectionAllowed(false);
            table.setShowGrid(false);
            table.setIntercellSpacing(new Dimension(0, 1));
            table.setRowHeight(25);
            table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            
            // Cabeçalho da tabela
            table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            table.getTableHeader().setBackground(new Color(240, 240, 240));
            table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY));
            
            // Definir larguras das colunas (estilo Excel otimizado)
            table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Prioridade
            table.getColumnModel().getColumn(1).setPreferredWidth(250); // Tarefa
            table.getColumnModel().getColumn(2).setPreferredWidth(100); // Data Limite
            table.getColumnModel().getColumn(3).setPreferredWidth(130); // Atribuído a
            table.getColumnModel().getColumn(4).setPreferredWidth(100); // Status
            
            // Aplicar tema moderno e alinhamento central (exceto Tarefa - coluna 1)
            Colors.applyModernTable(table);

            // Renderer personalizado para as células (aplicado a todas as colunas)
            TaskCellRenderer renderer = new TaskCellRenderer();
            table.setDefaultRenderer(Object.class, renderer);
            for (int col = 0; col < table.getColumnCount(); col++) {
                table.getColumnModel().getColumn(col).setCellRenderer(renderer);
            }
            
            // Mouse listener para clique duplo
            table.addMouseListener(new TaskTableMouseListener());
        }
    }
    
    /**
     * Método para recriar painéis de colunas (usado pelas subclasses)
     */
    protected void initializeColumnPanels() {
        // Recriar painéis das colunas
        pendingPanel = createColumnPanel(ColumnType.PENDING, pendingTable, Color.ORANGE);
        todayPanel = createColumnPanel(ColumnType.TODAY, todayTable, Color.BLUE);
        overduePanel = createColumnPanel(ColumnType.OVERDUE, overdueTable, Color.RED);
        completedPanel = createColumnPanel(ColumnType.COMPLETED, completedTable, Color.GREEN);
    }
    
    protected JPanel createColumnPanel(ColumnType columnType, JTable table, Color borderColor) {
        String title = getTitleForColumnType(columnType);
        JPanel panel = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleColor(borderColor);
        border.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        panel.setBorder(border);
        
        // Scroll pane para a tabela (estilo Excel)
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(610, 400)); // Mais largo para acomodar 5 colunas
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel inferior com botões de ação e legenda
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Legenda das prioridades (por baixo da coluna) com cores HTML
        JLabel legendLabel = new JLabel();
        legendLabel.setText(
            "<html><div style='padding: 3px 5px; font-size: 10px; text-align: center;'>" +
            "<span style='color: #dc3545;'>●</span> Urgente &nbsp; " +
            "<span style='color: #fd7e14;'>●</span> Alta &nbsp; " +
            "<span style='color: #2196f3;'>●</span> Normal &nbsp; " +
            "<span style='color: #28a745;'>●</span> Baixa" +
            "</div></html>"
        );
        legendLabel.setOpaque(true);
        legendLabel.setBackground(new Color(248, 249, 250));
        legendLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        legendLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(legendLabel, BorderLayout.NORTH);
        
        // Botões de ação baseados no tipo de coluna
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        switch (columnType) {
            case PENDING:
                JButton startButton = new JButton(i18n.getText("start"));
                startButton.addActionListener(e -> startTask());
                buttonPanel.add(startButton);
                break;
                
            case TODAY:
            case OVERDUE:
                JButton completeButton = new JButton(i18n.getText("complete"));
                completeButton.addActionListener(e -> completeTask());
                buttonPanel.add(completeButton);
                break;
                
            case COMPLETED:
                // Nenhum botão para tarefas concluídas
                break;
        }
        
        if (buttonPanel.getComponentCount() > 0) {
            bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        }
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Obtém o título traduzido para o tipo de coluna
     */
    private String getTitleForColumnType(ColumnType type) {
        switch (type) {
            case PENDING:
                return i18n.getText("pending").toUpperCase();
            case TODAY:
                return i18n.getText("today").toUpperCase();
            case OVERDUE:
                return i18n.getText("overdue").toUpperCase();
            case COMPLETED:
                return i18n.getText("completed").toUpperCase();
            default:
                return "";
        }
    }
    
    private void createStatsPanel() {
        statsPanel = new JPanel(new GridLayout(1, 5, 10, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder(i18n.getText("statistics")));
        
        totalTasksLabel = new JLabel(i18n.getText("total") + ": 0", SwingConstants.CENTER);
        pendingCountLabel = new JLabel(i18n.getText("pending") + ": 0", SwingConstants.CENTER);
        todayCountLabel = new JLabel(i18n.getText("today") + ": 0", SwingConstants.CENTER);
        overdueCountLabel = new JLabel(i18n.getText("overdue") + ": 0", SwingConstants.CENTER);
        completedCountLabel = new JLabel(i18n.getText("completed") + ": 0", SwingConstants.CENTER);
        
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
        JButton refreshButton = new JButton(i18n.getText("refresh"));
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
     * Atualiza os cabeçalhos das tabelas quando o idioma muda
     */
    public void updateTableHeaders() {
        SwingUtilities.invokeLater(() -> {
            pendingTableModel.updateHeaders();
            todayTableModel.updateHeaders();
            overdueTableModel.updateHeaders();
            completedTableModel.updateHeaders();
        });
    }
    
    /**
     * Atualiza as listas de tarefas com os dados do dashboard
     */
    @SuppressWarnings("unchecked")
    protected void updateTaskLists(Map<String, Object> dashboardData) {
        SwingUtilities.invokeLater(() -> {
            clearAllLists();
            
            // Atualizar tabelas
            updateTaskTable(pendingTableModel, (List<Map<String, Object>>) dashboardData.get("pending"));
            updateTaskTable(todayTableModel, (List<Map<String, Object>>) dashboardData.get("today"));
            updateTaskTable(overdueTableModel, (List<Map<String, Object>>) dashboardData.get("overdue"));
            updateTaskTable(completedTableModel, (List<Map<String, Object>>) dashboardData.get("completed"));
            
            // Atualizar estatísticas
            updateStats(dashboardData);
        });
    }
    
    private void updateTaskTable(TaskTableModel tableModel, List<Map<String, Object>> tasks) {
        java.util.List<TaskItem> taskItems = new java.util.ArrayList<>();
        if (tasks != null) {
            for (Map<String, Object> task : tasks) {
                TaskItem item = new TaskItem(task);
                taskItems.add(item);
            }
        }
        tableModel.setTasks(taskItems);
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
        pendingTableModel.setTasks(new java.util.ArrayList<>());
        todayTableModel.setTasks(new java.util.ArrayList<>());
        overdueTableModel.setTasks(new java.util.ArrayList<>());
        completedTableModel.setTasks(new java.util.ArrayList<>());
    }
    
    private void startTask() {
        int selectedRow = pendingTable.getSelectedRow();
        if (selectedRow >= 0) {
            TaskItem selected = pendingTableModel.getTaskAt(selectedRow);
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
    }
    
    private void completeTask() {
        TaskItem selected = null;
        
        int todayRow = todayTable.getSelectedRow();
        int overdueRow = overdueTable.getSelectedRow();
        
        if (todayRow >= 0) {
            selected = todayTableModel.getTaskAt(todayRow);
        } else if (overdueRow >= 0) {
            selected = overdueTableModel.getTaskAt(overdueRow);
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
        private final boolean isAssignedToTeam;
        private final String assignedTeamName;
        private final String dueDate;
        private final int unreadComments;
        
        public TaskItem(Map<String, Object> taskData) {
            this.id = Long.valueOf(taskData.get("id").toString());
            this.title = (String) taskData.get("title");
            this.description = (String) taskData.get("description");
            this.status = (String) taskData.get("status");
            this.priority = (String) taskData.get("priority");
            this.username = (String) taskData.get("username");
            this.isOverdue = Boolean.TRUE.equals(taskData.get("isOverdue"));
            this.dueDate = (String) taskData.get("dueDate");
            
            // Determinar se é tarefa de equipa ou individual
            Object assignedTeamObj = taskData.get("assignedTeam");
            if (assignedTeamObj instanceof Map) {
                Map<String, Object> teamData = (Map<String, Object>) assignedTeamObj;
                this.isAssignedToTeam = true;
                this.assignedTeamName = (String) teamData.get("name");
            } else {
                this.isAssignedToTeam = taskData.get("assignedTeamId") != null;
                this.assignedTeamName = (String) taskData.get("assignedTeamName");
            }
            
            // Carregar contagem de comentários não lidos
            Object unreadObj = taskData.get("unreadComments");
            this.unreadComments = (unreadObj != null) ? ((Number) unreadObj).intValue() : 0;
        }
        
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getStatus() { return status; }
        public String getPriority() { return priority; }
        public String getUsername() { return username; }
        public boolean isOverdue() { return isOverdue; }
        public boolean isAssignedToTeam() { return isAssignedToTeam; }
        public String getAssignedTeamName() { return assignedTeamName; }
        public String getDueDate() { return dueDate; }
        public int getUnreadComments() { return unreadComments; }
        
        @Override
        public String toString() {
            return title + " (" + priority + ")";
        }
    }
    
    /**
     * Renderer personalizado para as tarefas com layout de colunas
     */
    private static class TaskItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof TaskItem) {
                TaskItem item = (TaskItem) value;
                
                // Emoji para distinguir individual vs equipa (usando Unicode explícito)
                String assignmentEmoji = item.isAssignedToTeam() ? "\uD83D\uDC65" : "\uD83D\uDC64"; // 👥 e 👤
                
                // Formatar data limite
                String formattedDate = formatDueDate(item.getDueDate());
                
                // Layout de colunas: Tarefa - Prioridade - Data Limite - Tipo
                String userOrTeam = item.isAssignedToTeam() ? 
                    (item.getAssignedTeamName() != null ? truncateText(item.getAssignedTeamName(), 12) : "Equipa") :
                    truncateText(item.getUsername(), 12);
                
                String typeText = assignmentEmoji + " " + userOrTeam;
                
                // Usar HTML para renderizar a bola de prioridade com cor
                String priorityHTML = getPriorityHTML(item.getPriority());
                
                // Criar HTML para renderização com bola colorida
                String taskTitle = truncateText(item.getTitle(), 35);
                String paddedTask = taskTitle + " ".repeat(Math.max(0, 35 - taskTitle.length()));
                String paddedDate = formattedDate + " ".repeat(Math.max(0, 11 - formattedDate.length()));
                
                String htmlText = String.format(
                    "<html><div style='font-family: %s; font-size: 12px;'>%s | %s | %s | %s</div></html>",
                    Font.SANS_SERIF,
                    paddedTask,
                    priorityHTML,
                    paddedDate,
                    typeText
                );
                
                setText(htmlText);
                
                // Usar fonte Sans Serif (igual aos menus) para consistência visual
                setFont(new java.awt.Font(Font.SANS_SERIF, Font.PLAIN, 12));
                
                // Cores de fundo suaves por prioridade (consistentes com a legenda)
                if (!isSelected) {
                    if (item.isOverdue()) {
                        setBackground(new Color(255, 235, 235)); // Rosa muito claro para atrasadas
                    } else if ("URGENTE".equals(item.getPriority())) {
                        setBackground(new Color(255, 245, 245)); // Vermelho muito claro
                    } else if ("ALTA".equals(item.getPriority())) {
                        setBackground(new Color(255, 248, 230)); // Laranja muito claro
                    } else if ("NORMAL".equals(item.getPriority())) {
                        setBackground(new Color(225, 239, 252)); // Azul muito claro
                    } else if ("BAIXA".equals(item.getPriority())) {
                        setBackground(new Color(240, 255, 240)); // Verde muito claro
                    } else {
                        setBackground(Color.WHITE); // Branco para prioridade desconhecida
                    }
                }
            }
            
            return this;
        }
        
        /**
         * Formatar data limite para exibição
         */
        private String formatDueDate(String dueDate) {
            if (dueDate == null || dueDate.isEmpty()) {
                return "Sem prazo";
            }
            
            try {
                java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(dueDate);
                return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception e) {
                return "Data inválida";
            }
        }
        
        /**
         * Truncar texto para caber na coluna
         */
        private String truncateText(String text, int maxLength) {
            if (text == null) return "";
            if (text.length() <= maxLength) return text;
            return text.substring(0, maxLength - 3) + "...";
        }
        
        /**
         * Retorna uma bola colorida baseada na prioridade (usando caracteres Unicode compatíveis)
         */
        private String getPriorityBall(String priority) {
            if (priority == null) return "●"; // bola sólida por padrão
            
            switch (priority.toUpperCase()) {
                case "URGENTE":
                    return "●"; // Bola sólida (vermelho no CSS)
                case "ALTA":
                    return "●"; // Bola sólida (laranja no CSS)
                case "NORMAL":
                    return "●"; // Bola sólida (amarelo no CSS)
                case "BAIXA":
                    return "●"; // Bola sólida (verde no CSS)
                default:
                    return "○"; // Bola vazia para desconhecido
            }
        }
        
        /**
         * Retorna HTML com bola colorida baseada na prioridade
         */
        private String getPriorityHTML(String priority) {
            if (priority == null) return "<span style='color: #999;'>●</span>";
            switch (priority.toUpperCase()) {
                case "URGENTE":
                    return "<span style='color: #dc3545; font-weight: bold;'>●</span>";
                case "ALTA":
                    return "<span style='color: #fd7e14; font-weight: bold;'>●</span>";
                case "NORMAL":
                    return "<span style='color: #2196f3; font-weight: bold;'>●</span>"; // Azul
                case "BAIXA":
                    return "<span style='color: #28a745; font-weight: bold;'>●</span>";
                default:
                    return "<span style='color: #999;'>○</span>";
            }
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
        // Criar diálogo personalizado com opções
        String assignmentInfo = task.isAssignedToTeam() ? 
            "👥 Equipa: " + task.getAssignedTeamName() : 
            "👤 Individual: " + task.getUsername();
            
        String[] options = {"🔄 Alterar Estado", "� Reatribuir Tarefa", "�💬 Ver Comentários", "📋 Detalhes", "Fechar"};
        
        int choice = JOptionPane.showOptionDialog(this, 
            String.format("Tarefa: %s\n%s\nEstado: %s\nPrioridade: %s\n\nEscolha uma ação:", 
                         task.getTitle(), assignmentInfo, task.getStatus(), task.getPriority()),
            "Tarefa #" + task.getId(),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        switch (choice) {
            case 0: // Alterar Estado
                showTaskStatusChangeDialog(task);
                break;
            case 1: // Reatribuir Tarefa
                showTaskReassignDialog(task);
                break;
            case 2: // Ver Comentários
                showTaskCommentsDialog(task);
                break;
            case 3: // Detalhes
                showTaskDetailsInfo(task);
                break;
            default:
                // Fechar - não fazer nada
                break;
        }
    }
    
    /**
     * Mostra diálogo para mudança de estado da tarefa
     */
    protected void showTaskStatusChangeDialog(TaskItem task) {
        try {
            if (apiClient != null && currentUserId != null) {
                Window parentWindow = SwingUtilities.getWindowAncestor(this);
                com.gestortarefas.view.dialogs.TaskStatusChangeDialog statusDialog = 
                    new com.gestortarefas.view.dialogs.TaskStatusChangeDialog(
                        parentWindow,
                        task.getId(),
                        task.getTitle(),
                        task.getStatus(),
                        currentUserId,
                        apiClient
                    );
                statusDialog.setVisible(true);
                
                // Se o estado foi alterado, recarregar dashboard
                if (statusDialog.isStatusChanged()) {
                    refreshDashboard();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Não foi possível abrir diálogo de alteração de estado.\nVerifique a ligação à API.", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir diálogo de alteração de estado: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Mostra diálogo de comentários da tarefa
     */
    protected void showTaskCommentsDialog(TaskItem task) {
        try {
            if (apiClient != null && currentUserId != null) {
                Window parentWindow = SwingUtilities.getWindowAncestor(this);
                com.gestortarefas.view.dialogs.TaskCommentsDialog commentsDialog = 
                    new com.gestortarefas.view.dialogs.TaskCommentsDialog(
                        parentWindow,
                        task.getId(),
                        task.getTitle(),
                        currentUserId,
                        apiClient
                    );
                commentsDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Não foi possível abrir comentários da tarefa.\nVerifique a ligação à API.", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir comentários da tarefa: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Mostra informações detalhadas da tarefa
     */
    protected void showTaskDetailsInfo(TaskItem task) {
        String assignmentInfo = task.isAssignedToTeam() ? 
            "Equipa: " + task.getAssignedTeamName() : 
            "Utilizador: " + task.getUsername();
            
        String details = String.format(
            "ID: %d%nTítulo: %s%nDescrição: %s%nStatus: %s%nPrioridade: %s%n%s%nAtrasada: %s",
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getPriority(),
            assignmentInfo,
            task.isOverdue() ? "Sim" : "Não"
        );
        
        JOptionPane.showMessageDialog(this, details, "Detalhes da Tarefa", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void cleanup() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }
    
    /**
     * Modelo de tabela personalizado para as tarefas (estilo Excel)
     */
    protected static class TaskTableModel extends javax.swing.table.AbstractTableModel {
        private java.util.List<TaskItem> tasks = new java.util.ArrayList<>();
        private I18nManager i18n = I18nManager.getInstance();
        
        private String[] getColumnNames() {
            return new String[] {
                i18n.getText("table_priority"),
                i18n.getText("table_task"),
                i18n.getText("table_due_date"),
                i18n.getText("table_assigned_to"),
                i18n.getText("table_team"),
                i18n.getText("table_status")
            };
        }
        
        @Override
        public int getRowCount() {
            return tasks.size();
        }
        
        @Override
        public int getColumnCount() {
            return 6;
        }
        
        @Override
        public String getColumnName(int column) {
            return getColumnNames()[column];
        }
        
        public void updateHeaders() {
            fireTableStructureChanged();
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= tasks.size()) return "";
            
            TaskItem task = tasks.get(rowIndex);
            switch (columnIndex) {
                case 0: return task.getPriority();
                case 1: return task.getTitle();
                case 2: return formatDueDate(task.getDueDate());
                case 3: return getAssignmentInfo(task);
                case 4: return getTeamInfo(task);
                case 5: return getStatusText(task.getStatus());
                default: return "";
            }
        }
        
        public TaskItem getTaskAt(int rowIndex) {
            if (rowIndex >= 0 && rowIndex < tasks.size()) {
                return tasks.get(rowIndex);
            }
            return null;
        }
        
        public void setTasks(java.util.List<TaskItem> newTasks) {
            this.tasks.clear();
            if (newTasks != null) {
                this.tasks.addAll(newTasks);
            }
            fireTableDataChanged();
        }
        
        private String formatDueDate(String dueDate) {
            if (dueDate == null || dueDate.isEmpty()) {
                return "Sem prazo";
            }
            try {
                java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(dueDate);
                return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception e) {
                return "Data inválida";
            }
        }
        
        private String getAssignmentInfo(TaskItem task) {
            // Priorizar mostrar o nome do utilizador se existir
            String username = task.getUsername();
            if (username != null && !username.isEmpty() && !username.equalsIgnoreCase("null")) {
                return username;
            } else {
                return "Não atribuído";
            }
        }
        
        private String getTeamInfo(TaskItem task) {
            // Mostrar nome da equipa se tarefa for de equipa
            if (task.isAssignedToTeam()) {
                String teamName = task.getAssignedTeamName();
                return teamName != null ? teamName : "-";
            }
            return "-";
        }
        
        private String getStatusText(String status) {
            if (status == null) return "";
            switch (status.toUpperCase()) {
                case "PENDENTE": return "Pendente";
                case "EM_ANDAMENTO": return "Em Andamento";
                case "CONCLUIDA": return "Concluída";
                case "CANCELADA": return "Cancelada";
                default: return status;
            }
        }
    }
    
    /**
     * Renderer personalizado para células da tabela (estilo Excel)
     */
    protected static class TaskCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            TaskTableModel model = (TaskTableModel) table.getModel();
            TaskItem task = model.getTaskAt(row);
            
            if (task != null) {
                // Configurar fonte consistente
                setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
                
                // Coluna da prioridade com bolinha colorida usando HTML (apenas dashboard)
                if (column == 0 && value != null) {
                    String priority = value.toString();
                    setText(getPriorityHTML(priority));
                    // Fundo azul suave para prioridade NORMAL
                    if (!isSelected && "NORMAL".equals(priority.toUpperCase())) {
                        setBackground(new Color(225, 239, 252)); // Azul suave
                    } else if (!isSelected && "URGENTE".equals(priority.toUpperCase())) {
                        setBackground(new Color(255, 245, 245));
                    } else if (!isSelected && "ALTA".equals(priority.toUpperCase())) {
                        setBackground(new Color(255, 248, 230));
                    } else if (!isSelected && "BAIXA".equals(priority.toUpperCase())) {
                        setBackground(new Color(240, 255, 240));
                    } else if (!isSelected && task.isOverdue()) {
                        setBackground(new Color(255, 235, 235));
                    } else if (!isSelected) {
                        setBackground(Color.WHITE);
                    }
                } else if (column == 3 && task.getUnreadComments() > 0) {
                    // Coluna "Atribuído a" com badge de comentários não lidos
                    String assignmentText = value != null ? value.toString() : "";
                    setText("<html>" + assignmentText + " <span style='background-color: #dc3545; color: white; padding: 2px 6px; border-radius: 10px; font-size: 10px; font-weight: bold;'>" + task.getUnreadComments() + "</span></html>");
                    if (!isSelected) setBackground(Color.WHITE);
                } else {
                    setText(value != null ? value.toString() : "");
                    if (!isSelected) setBackground(Color.WHITE);
                }
                
                // Alinhamento das colunas
                switch (column) {
                    case 0: setHorizontalAlignment(SwingConstants.CENTER); break; // Prioridade
                    case 1: setHorizontalAlignment(SwingConstants.LEFT); break;   // Tarefa
                    case 2: setHorizontalAlignment(SwingConstants.CENTER); break; // Data
                    case 3: setHorizontalAlignment(SwingConstants.LEFT); break;   // Atribuído a
                    case 4: setHorizontalAlignment(SwingConstants.LEFT); break;   // Equipa
                    case 5: setHorizontalAlignment(SwingConstants.CENTER); break; // Status
                }
            }
            
            return this;
        }
        
        private String getPriorityHTML(String priority) {
            if (priority == null) return "<html><span style='color: #999;'>●</span></html>";
            
            // Retornar HTML com bolinha colorida
            switch (priority.toUpperCase()) {
                case "URGENTE": return "<html><span style='color: #dc3545; font-size: 16px;'>●</span></html>";
                case "ALTA": return "<html><span style='color: #fd7e14; font-size: 16px;'>●</span></html>";
                case "NORMAL": return "<html><span style='color: #2196f3; font-size: 16px;'>●</span></html>"; // Azul
                case "BAIXA": return "<html><span style='color: #28a745; font-size: 16px;'>●</span></html>";
                default: return "<html><span style='color: #999; font-size: 16px;'>○</span></html>";
            }
        }
    }
    
    /**
     * Mouse listener para clique duplo na tabela
     */
    protected class TaskTableMouseListener extends java.awt.event.MouseAdapter {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            if (e.getClickCount() == 2) {
                JTable source = (JTable) e.getSource();
                int row = source.getSelectedRow();
                if (row >= 0) {
                    TaskTableModel model = (TaskTableModel) source.getModel();
                    TaskItem task = model.getTaskAt(row);
                    if (task != null) {
                        showTaskDetails(task);
                    }
                }
            }
        }
    }
    
    /**
     * Mostra diálogo para reatribuir uma tarefa a outro utilizador ou equipa
     */
    protected void showTaskReassignDialog(TaskItem task) {
        try {
            SwingUtilities.invokeLater(() -> {
                JDialog reassignDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                    "Reatribuir Tarefa #" + task.getId(), true);
                reassignDialog.setLayout(new BorderLayout());
                
                JPanel mainPanel = new JPanel(new GridBagLayout());
                mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
                
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(8, 8, 8, 8);
                gbc.anchor = GridBagConstraints.WEST;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                
                // Informações da tarefa com melhor formatação
                gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
                JPanel taskInfoPanel = new JPanel(new BorderLayout());
                taskInfoPanel.setBorder(BorderFactory.createTitledBorder("Informações da Tarefa"));
                
                String taskTitle = task.getTitle();
                if (taskTitle.length() > 50) {
                    taskTitle = taskTitle.substring(0, 47) + "...";
                }
                
                JLabel taskInfo = new JLabel("<html>" +
                    "<div style='margin: 5px;'>" +
                    "<b>Tarefa:</b> " + taskTitle + "<br/><br/>" +
                    "<b>Atualmente atribuída a:</b><br/>" + 
                    (task.isAssignedToTeam() ? 
                        "👥 " + (task.getAssignedTeamName() != null ? task.getAssignedTeamName() : "Equipa") : 
                        "👤 " + (task.getUsername() != null ? task.getUsername() : "Utilizador")) +
                    "</div></html>");
                taskInfoPanel.add(taskInfo);
                mainPanel.add(taskInfoPanel, gbc);
                
                // Espaço
                gbc.gridy = 1; gbc.insets = new Insets(15, 8, 8, 8);
                mainPanel.add(new JLabel(""), gbc);
                
                // Seção de nova atribuição
                gbc.gridy = 2; gbc.insets = new Insets(8, 8, 8, 8);
                JPanel assignmentPanel = new JPanel(new GridBagLayout());
                assignmentPanel.setBorder(BorderFactory.createTitledBorder("Nova Atribuição"));
                
                GridBagConstraints assignGbc = new GridBagConstraints();
                assignGbc.insets = new Insets(8, 8, 8, 8);
                assignGbc.anchor = GridBagConstraints.WEST;
                assignGbc.fill = GridBagConstraints.HORIZONTAL;
                
                // Radio buttons para tipo de atribuição
                JRadioButton assignToUserRadio = new JRadioButton("Atribuir a utilizador específico");
                JRadioButton assignToTeamRadio = new JRadioButton("Atribuir a equipa (tarefa coletiva)");
                ButtonGroup assignmentGroup = new ButtonGroup();
                assignmentGroup.add(assignToUserRadio);
                assignmentGroup.add(assignToTeamRadio);
                
                // Por padrão, selecionar atribuição individual se atualmente for individual
                if (!task.isAssignedToTeam()) {
                    assignToUserRadio.setSelected(true);
                } else {
                    assignToTeamRadio.setSelected(true);
                }
                
                // Combo boxes com tamanho fixo
                JComboBox<UserItem> userCombo = new JComboBox<>();
                JComboBox<TeamItem> teamCombo = new JComboBox<>();
                userCombo.setPreferredSize(new Dimension(300, 25));
                teamCombo.setPreferredSize(new Dimension(300, 25));
                
                // Layout dos componentes de atribuição
                assignGbc.gridx = 0; assignGbc.gridy = 0; assignGbc.gridwidth = 2; assignGbc.weightx = 1.0;
                assignmentPanel.add(assignToUserRadio, assignGbc);
                
                assignGbc.gridy = 1; assignGbc.gridx = 0; assignGbc.gridwidth = 1; assignGbc.weightx = 0.0;
                assignmentPanel.add(new JLabel("   Utilizador:"), assignGbc);
                assignGbc.gridx = 1; assignGbc.weightx = 1.0;
                assignmentPanel.add(userCombo, assignGbc);
                
                assignGbc.gridy = 2; assignGbc.gridx = 0; assignGbc.gridwidth = 2; assignGbc.weightx = 1.0;
                assignGbc.insets = new Insets(15, 8, 8, 8);
                assignmentPanel.add(assignToTeamRadio, assignGbc);
                
                assignGbc.gridy = 3; assignGbc.gridx = 0; assignGbc.gridwidth = 1; assignGbc.weightx = 0.0;
                assignGbc.insets = new Insets(8, 8, 8, 8);
                assignmentPanel.add(new JLabel("   Equipa:"), assignGbc);
                assignGbc.gridx = 1; assignGbc.weightx = 1.0;
                assignmentPanel.add(teamCombo, assignGbc);
                
                mainPanel.add(assignmentPanel, gbc);
                
                // Botões com melhor layout
                gbc.gridy = 3; gbc.insets = new Insets(20, 8, 8, 8);
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                JButton reassignButton = new JButton("Reatribuir");
                JButton cancelButton = new JButton("Cancelar");
                
                // Tamanhos uniformes dos botões
                Dimension buttonSize = new Dimension(120, 35);
                reassignButton.setPreferredSize(buttonSize);
                cancelButton.setPreferredSize(buttonSize);
                
                buttonPanel.add(reassignButton);
                buttonPanel.add(cancelButton);
                mainPanel.add(buttonPanel, gbc);
                
                // Adicionar painel principal ao diálogo
                reassignDialog.add(mainPanel, BorderLayout.CENTER);
                
                // Event listeners
                assignToUserRadio.addActionListener(e -> {
                    userCombo.setEnabled(true);
                    teamCombo.setEnabled(false);
                });
                
                assignToTeamRadio.addActionListener(e -> {
                    userCombo.setEnabled(false);
                    teamCombo.setEnabled(true);
                });
                
                cancelButton.addActionListener(e -> reassignDialog.dispose());
                
                reassignButton.addActionListener(e -> {
                    // Validar seleção
                    if (!assignToUserRadio.isSelected() && !assignToTeamRadio.isSelected()) {
                        JOptionPane.showMessageDialog(reassignDialog, "Selecione o tipo de atribuição", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (assignToUserRadio.isSelected() && userCombo.getSelectedItem() == null) {
                        JOptionPane.showMessageDialog(reassignDialog, "Selecione um utilizador", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (assignToTeamRadio.isSelected() && teamCombo.getSelectedItem() == null) {
                        JOptionPane.showMessageDialog(reassignDialog, "Selecione uma equipa", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Executar reatribuição
                    reassignButton.setEnabled(false);
                    new Thread(() -> {
                        try {
                            java.util.Map<String, Object> updateData = new java.util.HashMap<>();
                            
                            if (assignToUserRadio.isSelected()) {
                                UserItem selectedUser = (UserItem) userCombo.getSelectedItem();
                                updateData.put("assignedUserId", selectedUser.getId());
                                updateData.put("assignedTeamId", null);
                                updateData.put("isAssignedToTeam", false);
                            } else {
                                TeamItem selectedTeam = (TeamItem) teamCombo.getSelectedItem();
                                updateData.put("assignedUserId", null);
                                updateData.put("assignedTeamId", selectedTeam.getId());
                                updateData.put("isAssignedToTeam", true);
                            }
                            
                            java.net.http.HttpResponse<String> response = com.gestortarefas.util.HttpUtil.put("/tasks/" + task.getId() + "/reassign", updateData);
                            java.util.Map<String, Object> result = com.gestortarefas.util.HttpUtil.parseResponse(response.body());
                            
                            SwingUtilities.invokeLater(() -> {
                                if ((Boolean) result.get("success")) {
                                    JOptionPane.showMessageDialog(reassignDialog, "Tarefa reatribuída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                                    reassignDialog.dispose();
                                    refreshDashboard(); // Atualizar dashboard
                                } else {
                                    JOptionPane.showMessageDialog(reassignDialog, "Erro ao reatribuir tarefa: " + result.get("message"), "Erro", JOptionPane.ERROR_MESSAGE);
                                    reassignButton.setEnabled(true);
                                }
                            });
                            
                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(reassignDialog, "Erro de conexão: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                                reassignButton.setEnabled(true);
                            });
                        }
                    }).start();
                });
                
                // Carregar dados em background
                new Thread(() -> {
                    try {
                        // Carregar utilizadores
                        java.net.http.HttpResponse<String> usersResponse = com.gestortarefas.util.HttpUtil.get("/users");
                        java.util.Map<String, Object> usersResult = com.gestortarefas.util.HttpUtil.parseResponse(usersResponse.body());
                        
                        if ((Boolean) usersResult.get("success")) {
                            @SuppressWarnings("unchecked")
                            java.util.List<java.util.Map<String, Object>> users = (java.util.List<java.util.Map<String, Object>>) usersResult.get("users");
                            
                            SwingUtilities.invokeLater(() -> {
                                userCombo.removeAllItems();
                                for (java.util.Map<String, Object> user : users) {
                                    if ((Boolean) user.get("active")) {
                                        userCombo.addItem(new UserItem(user));
                                    }
                                }
                            });
                        }
                        
                        // Carregar equipas
                        java.net.http.HttpResponse<String> teamsResponse = com.gestortarefas.util.HttpUtil.get("/teams/summary");
                        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        @SuppressWarnings("unchecked")
                        java.util.List<java.util.Map<String, Object>> teams = objectMapper.readValue(teamsResponse.body(), 
                            objectMapper.getTypeFactory().constructCollectionType(java.util.List.class, java.util.Map.class));
                        
                        SwingUtilities.invokeLater(() -> {
                            teamCombo.removeAllItems();
                            for (java.util.Map<String, Object> team : teams) {
                                if ((Boolean) team.get("active")) {
                                    teamCombo.addItem(new TeamItem(team));
                                }
                            }
                            
                            // Definir seleção padrão baseada na atribuição atual
                            if (task.isAssignedToTeam()) {
                                assignToTeamRadio.setSelected(true);
                                userCombo.setEnabled(false);
                                teamCombo.setEnabled(true);
                            } else {
                                assignToUserRadio.setSelected(true);
                                userCombo.setEnabled(true);
                                teamCombo.setEnabled(false);
                            }
                        });
                        
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(reassignDialog, "Erro ao carregar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                }).start();
                
                // Configurar diálogo com dimensões adequadas
                reassignDialog.pack();
                
                // Definir tamanho ideal para a janela
                Dimension preferredSize = new Dimension(520, 450);
                reassignDialog.setSize(preferredSize);
                reassignDialog.setMinimumSize(new Dimension(480, 400));
                reassignDialog.setResizable(true);
                
                reassignDialog.setLocationRelativeTo(this);
                reassignDialog.setVisible(true);
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao abrir diálogo de reatribuição: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Classe para representar um utilizador no ComboBox de reatribuição
     */
    private static class UserItem {
        private final Long id;
        private final String fullName;
        private final String teamName;
        
        public UserItem(java.util.Map<String, Object> userData) {
            this.id = ((Number) userData.get("id")).longValue();
            this.fullName = (String) userData.get("fullName");
            this.teamName = (String) userData.get("teamName");
        }
        
        public Long getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return fullName + (teamName != null ? " (" + teamName + ")" : "");
        }
    }
    
    /**
     * Classe para representar uma equipa no ComboBox de reatribuição
     */
    private static class TeamItem {
        private final Long id;
        private final String name;
        private final String description;
        
        public TeamItem(java.util.Map<String, Object> teamData) {
            this.id = ((Number) teamData.get("id")).longValue();
            this.name = (String) teamData.get("name");
            this.description = (String) teamData.get("description");
        }
        
        public Long getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return name + (description != null ? " - " + description : "");
        }
    }
}