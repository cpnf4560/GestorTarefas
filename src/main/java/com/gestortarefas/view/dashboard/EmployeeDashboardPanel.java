package com.gestortarefas.view.dashboard;

import com.gestortarefas.view.dialogs.TaskCreateDialog;
import com.gestortarefas.view.dialogs.TaskCommentsDialog;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Dashboard específico para funcionários
 */
public class EmployeeDashboardPanel extends DashboardBasePanel {
    
    private JLabel userInfoLabel;
    private JPanel userPanel;
    
    public EmployeeDashboardPanel(Long userId) {
        super(userId);
        initializeEmployeeComponents();
        loadEmployeeData();
    }
    
    private void initializeEmployeeComponents() {
        // Painel de informações do utilizador
        userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userInfoLabel = new JLabel("Carregando informações do utilizador...");
        userInfoLabel.setFont(userInfoLabel.getFont().deriveFont(Font.BOLD, 14f));
        userPanel.add(new JLabel("Funcionário: "));
        userPanel.add(userInfoLabel);
        
        // Adicionar ao topo
        add(userPanel, BorderLayout.NORTH);
        remove(statsPanel);
        
        // Reorganizar layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(userPanel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Personalizar título das colunas para funcionário
        updateColumnTitles();
    }
    
    private void updateColumnTitles() {
        // Atualizar bordas com cores específicas para funcionário
        pendingPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 140, 0), 2), 
            "MINHAS PENDENTES"));
        
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
    
    @Override
    protected void refreshDashboard() {
        SwingUtilities.invokeLater(() -> {
            try {
                Map<String, Object> dashboardData = apiClient.getEmployeeDashboard(currentUserId);
                if (dashboardData != null) {
                    updateTaskLists(dashboardData);
                    updateUserInfo(dashboardData);
                } else {
                    showErrorMessage("Erro ao carregar dashboard do funcionário");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showErrorMessage("Erro de conexão: " + e.getMessage());
            }
        });
    }
    
    private void loadEmployeeData() {
        // Carregar dados iniciais
        refreshDashboard();
    }
    
    private void updateUserInfo(Map<String, Object> dashboardData) {
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) dashboardData.get("user");
        if (user != null) {
            String username = (String) user.get("username");
            String email = (String) user.get("email");
            userInfoLabel.setText(username + " (" + email + ")");
        }
    }
    
    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            userInfoLabel.setText("Erro ao carregar dados");
            JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
        });
    }
    
    /**
     * Adiciona funcionalidades específicas do funcionário
     */
    public void addEmployeeSpecificFeatures() {
        // Botão para criar nova tarefa pessoal
        JPanel bottomPanel = (JPanel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        
        JButton newTaskButton = new JButton("Nova Tarefa Pessoal");
        newTaskButton.setBackground(new Color(70, 130, 180));
        newTaskButton.setForeground(Color.WHITE);
        newTaskButton.addActionListener(e -> openNewTaskDialog());
        
        bottomPanel.add(newTaskButton);
        
        // Botão para ver perfil
        JButton profileButton = new JButton("Meu Perfil");
        profileButton.setBackground(new Color(34, 139, 34));
        profileButton.setForeground(Color.WHITE);
        profileButton.addActionListener(e -> openProfileDialog());
        
        bottomPanel.add(profileButton);
    }
    
    private void openNewTaskDialog() {
        try {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            TaskCreateDialog dialog = new TaskCreateDialog(
                parentWindow, 
                currentUserId, 
                apiClient,
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
    
    private void openProfileDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Meu Perfil", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        
        // Painel de informações
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Buscar informações do perfil via API
        Map<String, Object> profile = apiClient.getUserProfile(currentUserId);
        
        if (profile != null) {
            gbc.gridx = 0; gbc.gridy = 0;
            infoPanel.add(new JLabel("ID:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(profile.get("id").toString()), gbc);
            
            // Adicionar mais campos conforme necessário
        } else {
            infoPanel.add(new JLabel("Erro ao carregar perfil"));
        }
        
        dialog.add(infoPanel, BorderLayout.CENTER);
        
        // Botão fechar
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("Fechar");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Método para notificações específicas do funcionário
     */
    public void showEmployeeNotifications() {
        SwingUtilities.invokeLater(() -> {
            Map<String, Object> dashboardData = apiClient.getEmployeeDashboard(currentUserId);
            if (dashboardData != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> stats = (Map<String, Object>) dashboardData.get("stats");
                if (stats != null) {
                    int overdueCount = getIntValue(stats, "overdueCount");
                    int todayCount = getIntValue(stats, "todayCount");
                    
                    if (overdueCount > 0) {
                        JOptionPane.showMessageDialog(this,
                            "Atenção: Você tem " + overdueCount + " tarefa(s) em atraso!",
                            "Tarefas em Atraso",
                            JOptionPane.WARNING_MESSAGE);
                    } else if (todayCount > 0) {
                        JOptionPane.showMessageDialog(this,
                            "Você tem " + todayCount + " tarefa(s) para hoje.",
                            "Tarefas para Hoje",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
    }
    
    protected int getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }
    
    /**
     * Sobrescrever método para mostrar detalhes da tarefa com opções para funcionário
     */
    @Override
    protected void showTaskDetails(TaskItem task) {
        // Criar diálogo personalizado para funcionário com opção de comentários
        String[] options = {"💬 Ver Comentários", "📋 Detalhes", "Fechar"};
        
        int choice = JOptionPane.showOptionDialog(this, 
            String.format("Tarefa: %s\n\nEscolha uma ação:", task.getTitle()),
            "Ver Tarefa #" + task.getId(),
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
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir comentários da tarefa: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}