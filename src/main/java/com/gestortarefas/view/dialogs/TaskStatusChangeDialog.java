package com.gestortarefas.view.dialogs;

import com.gestortarefas.util.RestApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Diálogo para mudança rápida de estado das tarefas
 */
public class TaskStatusChangeDialog extends JDialog {
    
    private static final long serialVersionUID = 1L;
    
    private final Long taskId;
    private final String taskTitle;
    private final String currentStatus;
    private final Long currentUserId;
    private final RestApiClient apiClient;
    
    // Componentes da interface
    private JLabel taskInfoLabel;
    private JLabel currentStatusLabel;
    private JComboBox<StatusOption> statusComboBox;
    private JButton changeButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    
    private boolean statusChanged = false;
    
    // Classe para itens do combo de status
    private static class StatusOption {
        private final String value;
        private final String display;
        private final String emoji;
        
        public StatusOption(String value, String display, String emoji) {
            this.value = value;
            this.display = display;
            this.emoji = emoji;
        }
        
        public String getValue() { return value; }
        public String getDisplay() { return display; }
        public String getEmoji() { return emoji; }
        
        @Override
        public String toString() {
            return emoji + " " + display;
        }
    }
    
    public TaskStatusChangeDialog(Window parent, Long taskId, String taskTitle, String currentStatus, Long currentUserId, RestApiClient apiClient) {
        super(parent, "Alterar Estado da Tarefa", ModalityType.APPLICATION_MODAL);
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.currentStatus = currentStatus;
        this.currentUserId = currentUserId;
        this.apiClient = apiClient;
        
        initializeComponents();
        setupDialog();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Painel superior com informações da tarefa
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 15, 10, 15));
        
        taskInfoLabel = new JLabel("📋 " + taskTitle);
        taskInfoLabel.setFont(taskInfoLabel.getFont().deriveFont(Font.BOLD, 16f));
        
        currentStatusLabel = new JLabel("Estado atual: " + getStatusDisplay(currentStatus));
        currentStatusLabel.setFont(currentStatusLabel.getFont().deriveFont(14f));
        currentStatusLabel.setForeground(Color.GRAY);
        
        headerPanel.add(taskInfoLabel, BorderLayout.NORTH);
        headerPanel.add(currentStatusLabel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Painel central com seleção de novo estado
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel selectLabel = new JLabel("Novo Estado:");
        selectLabel.setFont(selectLabel.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(selectLabel, gbc);
        
        // ComboBox com opções de status
        statusComboBox = new JComboBox<>();
        populateStatusOptions();
        statusComboBox.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(statusComboBox, gbc);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Painel inferior com botões e status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 15, 15, 15));
        
        statusLabel = new JLabel(" ");
        statusLabel.setFont(statusLabel.getFont().deriveFont(11f));
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        changeButton = new JButton("🔄 Alterar Estado");
        changeButton.setBackground(new Color(70, 130, 180));
        changeButton.setForeground(Color.WHITE);
        changeButton.setFont(changeButton.getFont().deriveFont(Font.BOLD));
        changeButton.addActionListener(this::changeTaskStatus);
        
        cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(changeButton);
        
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void populateStatusOptions() {
        // Adicionar todas as opções disponíveis
        statusComboBox.addItem(new StatusOption("PENDENTE", "Pendente", "⏳"));
        statusComboBox.addItem(new StatusOption("EM_ANDAMENTO", "Em Andamento", "🔄"));
        statusComboBox.addItem(new StatusOption("CONCLUIDA", "Concluída", "✅"));
        statusComboBox.addItem(new StatusOption("CANCELADA", "Cancelada", "❌"));
        
        // Selecionar o próximo estado lógico baseado no atual
        String nextStatus = getNextLogicalStatus(currentStatus);
        for (int i = 0; i < statusComboBox.getItemCount(); i++) {
            StatusOption option = statusComboBox.getItemAt(i);
            if (option.getValue().equals(nextStatus)) {
                statusComboBox.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private String getNextLogicalStatus(String current) {
        switch (current) {
            case "PENDENTE":
                return "EM_ANDAMENTO";
            case "EM_ANDAMENTO":
                return "CONCLUIDA";
            case "CONCLUIDA":
                return "PENDENTE"; // Para reabrir se necessário
            case "CANCELADA":
                return "PENDENTE";
            default:
                return "EM_ANDAMENTO";
        }
    }
    
    private String getStatusDisplay(String status) {
        switch (status) {
            case "PENDENTE": return "⏳ Pendente";
            case "EM_ANDAMENTO": return "🔄 Em Andamento";
            case "CONCLUIDA": return "✅ Concluída";
            case "CANCELADA": return "❌ Cancelada";
            default: return status;
        }
    }
    
    private void changeTaskStatus(ActionEvent e) {
        StatusOption selectedOption = (StatusOption) statusComboBox.getSelectedItem();
        if (selectedOption == null) return;
        
        String newStatus = selectedOption.getValue();
        
        // Verificar se realmente mudou
        if (newStatus.equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, 
                "O estado selecionado é o mesmo que o atual.", 
                "Sem Alteração", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Confirmar mudança
        int result = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja alterar o estado de:\n\n" +
            "\"" + taskTitle + "\"\n\n" +
            "De: " + getStatusDisplay(currentStatus) + "\n" +
            "Para: " + selectedOption.toString() + "?", 
            "Confirmar Alteração de Estado", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (result != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Desabilitar botão durante operação
        changeButton.setEnabled(false);
        changeButton.setText("Alterando...");
        statusLabel.setText("Alterando estado da tarefa...");
        statusLabel.setForeground(Color.BLUE);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("status", newStatus);
                    updateData.put("userId", currentUserId);
                    
                    boolean success = apiClient.updateTask(taskId, updateData);
                    return success;
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
                        statusChanged = true;
                        statusLabel.setText("Estado alterado com sucesso!");
                        statusLabel.setForeground(new Color(34, 139, 34));
                        
                        JOptionPane.showMessageDialog(TaskStatusChangeDialog.this, 
                            "Estado da tarefa alterado com sucesso!\n\n" +
                            "Nova situação: " + selectedOption.toString(), 
                            "Sucesso", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Fechar diálogo após 1 segundo
                        Timer timer = new Timer(1000, evt -> dispose());
                        timer.setRepeats(false);
                        timer.start();
                        
                    } else {
                        statusLabel.setText("Erro ao alterar estado");
                        statusLabel.setForeground(Color.RED);
                        JOptionPane.showMessageDialog(TaskStatusChangeDialog.this, 
                            "Erro ao alterar o estado da tarefa.\nTente novamente.", 
                            "Erro", 
                            JOptionPane.ERROR_MESSAGE);
                        
                        changeButton.setEnabled(true);
                        changeButton.setText("🔄 Alterar Estado");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    statusLabel.setText("Erro interno");
                    statusLabel.setForeground(Color.RED);
                    changeButton.setEnabled(true);
                    changeButton.setText("🔄 Alterar Estado");
                }
            }
        };
        
        worker.execute();
    }
    
    private void setupDialog() {
        setSize(450, 250);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }
    
    public boolean isStatusChanged() {
        return statusChanged;
    }
}