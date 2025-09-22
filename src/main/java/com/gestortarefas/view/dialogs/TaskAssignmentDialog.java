package com.gestortarefas.view.dialogs;

import com.gestortarefas.util.RestApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

/**
 * Diálogo para atribuição de tarefa a um funcionário (para gerentes)
 */
public class TaskAssignmentDialog extends JDialog {
    
    private static final long serialVersionUID = 1L;
    
    private final Long taskId;
    private final String taskTitle;
    private final Long managerId;
    private final RestApiClient apiClient;
    
    // Componentes da interface
    private JComboBox<UserComboItem> employeeComboBox;
    private JButton assignButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    private JTextArea taskInfoArea;
    
    private List<Map<String, Object>> availableEmployees;
    private boolean assignmentCompleted = false;
    
    public TaskAssignmentDialog(Window parent, Long taskId, String taskTitle, Long managerId, RestApiClient apiClient) {
        super(parent, "Atribuir Tarefa", ModalityType.APPLICATION_MODAL);
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.managerId = managerId;
        this.apiClient = apiClient;
        
        initializeComponents();
        setupDialog();
        loadEmployees();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Painel superior com informações da tarefa
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("👤 Atribuir Tarefa a Funcionário");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        
        taskInfoArea = new JTextArea();
        taskInfoArea.setEditable(false);
        taskInfoArea.setBackground(new Color(248, 248, 248));
        taskInfoArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        taskInfoArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        taskInfoArea.setText("Tarefa: " + taskTitle + "\n" +
                           "ID: #" + taskId + "\n" +
                           "Selecione o funcionário para atribuir esta tarefa:");
        taskInfoArea.setRows(4);
        
        JScrollPane taskInfoScrollPane = new JScrollPane(taskInfoArea);
        taskInfoScrollPane.setBorder(BorderFactory.createTitledBorder("Informações da Tarefa"));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(taskInfoScrollPane, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Painel central com seleção de funcionário
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JPanel selectionPanel = new JPanel(new BorderLayout());
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Selecionar Funcionário"));
        
        JLabel instructionLabel = new JLabel("Escolha o funcionário que será responsável por esta tarefa:");
        instructionLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        employeeComboBox = new JComboBox<>();
        employeeComboBox.setPreferredSize(new Dimension(300, 30));
        employeeComboBox.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        employeeComboBox.addItem(new UserComboItem(null, "Carregando funcionários..."));
        employeeComboBox.setEnabled(false);
        
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        comboPanel.add(employeeComboBox);
        
        selectionPanel.add(instructionLabel, BorderLayout.NORTH);
        selectionPanel.add(comboPanel, BorderLayout.CENTER);
        
        centerPanel.add(selectionPanel, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Painel inferior com status e botões
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 15, 15, 15));
        
        statusLabel = new JLabel("Carregando lista de funcionários...");
        statusLabel.setFont(statusLabel.getFont().deriveFont(11f));
        statusLabel.setForeground(Color.GRAY);
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> dispose());
        
        assignButton = new JButton("✅ Atribuir Tarefa");
        assignButton.setBackground(new Color(70, 130, 180));
        assignButton.setForeground(Color.WHITE);
        assignButton.setFont(assignButton.getFont().deriveFont(Font.BOLD));
        assignButton.setEnabled(false);
        assignButton.addActionListener(this::performAssignment);
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(assignButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupDialog() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }
    
    private void loadEmployees() {
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                try {
                    // Obter dashboard do gerente que contém a lista de funcionários da equipa
                    Map<String, Object> dashboard = apiClient.getManagerDashboard(managerId);
                    
                    if (dashboard != null && dashboard.containsKey("teamEmployees")) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> employees = (List<Map<String, Object>>) dashboard.get("teamEmployees");
                        return employees;
                    }
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            
            @Override
            protected void done() {
                try {
                    availableEmployees = get();
                    
                    // Limpar combo box
                    employeeComboBox.removeAllItems();
                    
                    if (availableEmployees != null && !availableEmployees.isEmpty()) {
                        // Adicionar opção padrão
                        employeeComboBox.addItem(new UserComboItem(null, "-- Selecione um funcionário --"));
                        
                        // Adicionar funcionários disponíveis
                        for (Map<String, Object> employee : availableEmployees) {
                            Long userId = getLongValue(employee, "id");
                            String fullName = getStringValue(employee, "fullName", "Nome não disponível");
                            String username = getStringValue(employee, "username", "");
                            String email = getStringValue(employee, "email", "");
                            
                            String displayName = fullName;
                            if (!username.isEmpty()) {
                                displayName += " (" + username + ")";
                            }
                            if (!email.isEmpty()) {
                                displayName += " - " + email;
                            }
                            
                            employeeComboBox.addItem(new UserComboItem(userId, displayName));
                        }
                        
                        employeeComboBox.setEnabled(true);
                        assignButton.setEnabled(true);
                        statusLabel.setText("Selecione um funcionário para atribuir a tarefa");
                        statusLabel.setForeground(Color.BLACK);
                        
                        // Listener para habilitar botão quando seleção for feita
                        employeeComboBox.addActionListener(e -> {
                            UserComboItem selected = (UserComboItem) employeeComboBox.getSelectedItem();
                            assignButton.setEnabled(selected != null && selected.getUserId() != null);
                        });
                        
                    } else {
                        employeeComboBox.addItem(new UserComboItem(null, "Nenhum funcionário disponível"));
                        statusLabel.setText("Nenhum funcionário encontrado na sua equipa");
                        statusLabel.setForeground(Color.ORANGE);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    employeeComboBox.removeAllItems();
                    employeeComboBox.addItem(new UserComboItem(null, "Erro ao carregar funcionários"));
                    statusLabel.setText("Erro ao carregar lista de funcionários");
                    statusLabel.setForeground(Color.RED);
                }
            }
        };
        
        worker.execute();
    }
    
    private void performAssignment(ActionEvent e) {
        UserComboItem selectedItem = (UserComboItem) employeeComboBox.getSelectedItem();
        
        if (selectedItem == null || selectedItem.getUserId() == null) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, selecione um funcionário para atribuir a tarefa.", 
                "Seleção Necessária", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Long selectedUserId = selectedItem.getUserId();
        String selectedUserName = selectedItem.getDisplayName();
        
        // Confirmação
        int result = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja atribuir a tarefa:\n\n" +
            "\"" + taskTitle + "\"\n\n" +
            "para o funcionário:\n" + selectedUserName + "?", 
            "Confirmar Atribuição", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE);
        
        if (result != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Desabilitar componentes durante a operação
        assignButton.setEnabled(false);
        employeeComboBox.setEnabled(false);
        assignButton.setText("Atribuindo...");
        statusLabel.setText("Atribuindo tarefa ao funcionário...");
        statusLabel.setForeground(Color.BLUE);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    Map<String, Object> response = apiClient.assignTask(taskId, selectedUserId, managerId);
                    return response != null && Boolean.TRUE.equals(response.get("success"));
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    
                    if (success) {
                        assignmentCompleted = true;
                        statusLabel.setText("Tarefa atribuída com sucesso!");
                        statusLabel.setForeground(new Color(34, 139, 34));
                        
                        JOptionPane.showMessageDialog(TaskAssignmentDialog.this, 
                            "Tarefa atribuída com sucesso!\n\n" +
                            "A tarefa \"" + taskTitle + "\" foi atribuída a " + selectedUserName + ".", 
                            "Atribuição Realizada", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Fechar diálogo após 1 segundo
                        Timer timer = new Timer(1000, evt -> dispose());
                        timer.setRepeats(false);
                        timer.start();
                        
                    } else {
                        statusLabel.setText("Erro ao atribuir tarefa");
                        statusLabel.setForeground(Color.RED);
                        
                        JOptionPane.showMessageDialog(TaskAssignmentDialog.this, 
                            "Erro ao atribuir tarefa.\n\nVerifique suas permissões e tente novamente.", 
                            "Erro na Atribuição", 
                            JOptionPane.ERROR_MESSAGE);
                        
                        // Reabilitar componentes
                        assignButton.setEnabled(true);
                        employeeComboBox.setEnabled(true);
                        assignButton.setText("✅ Atribuir Tarefa");
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    statusLabel.setText("Erro ao atribuir tarefa");
                    statusLabel.setForeground(Color.RED);
                    
                    // Reabilitar componentes
                    assignButton.setEnabled(true);
                    employeeComboBox.setEnabled(true);
                    assignButton.setText("✅ Atribuir Tarefa");
                }
            }
        };
        
        worker.execute();
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
    
    public boolean isAssignmentCompleted() {
        return assignmentCompleted;
    }
    
    /**
     * Item do combo box para representar utilizadores
     */
    private static class UserComboItem {
        private final Long userId;
        private final String displayName;
        
        public UserComboItem(Long userId, String displayName) {
            this.userId = userId;
            this.displayName = displayName;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
}