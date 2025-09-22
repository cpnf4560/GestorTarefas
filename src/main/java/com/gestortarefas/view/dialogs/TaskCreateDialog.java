package com.gestortarefas.view.dialogs;

import com.gestortarefas.util.RestApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Diálogo para criação de novas tarefas
 */
public class TaskCreateDialog extends JDialog {
    
    private static final long serialVersionUID = 1L;
    
    private final Long userId;
    private final RestApiClient apiClient;
    private final Runnable onSuccess;
    
    // Componentes da interface
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> priorityComboBox;
    private JTextField dueDateField;
    private JTextField tagsField;
    
    private JButton createButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    
    private boolean taskCreated = false;
    
    public TaskCreateDialog(Window parent, Long userId, RestApiClient apiClient, Runnable onSuccess) {
        super(parent, "Nova Tarefa", ModalityType.APPLICATION_MODAL);
        this.userId = userId;
        this.apiClient = apiClient;
        this.onSuccess = onSuccess;
        
        initializeComponents();
        setupDialog();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Título do diálogo
        JLabel headerLabel = new JLabel("Criar Nova Tarefa");
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Painel do formulário
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Painel dos botões
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Título da tarefa
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Título*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        titleField = new JTextField(30);
        formPanel.add(titleField, gbc);
        
        // Descrição
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        descriptionArea = new JTextArea(4, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);
        
        // Prioridade
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Prioridade:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        priorityComboBox = new JComboBox<>(new String[]{"BAIXA", "NORMAL", "ALTA", "URGENTE"});
        priorityComboBox.setSelectedItem("NORMAL");
        formPanel.add(priorityComboBox, gbc);
        
        // Data limite
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Data Limite (dd/MM/yyyy HH:mm):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dueDateField = new JTextField(30);
        dueDateField.setToolTipText("Formato: dd/MM/yyyy HH:mm (ex: 31/12/2024 14:30)");
        // Definir data padrão para amanhã às 17:00
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(17).withMinute(0).withSecond(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dueDateField.setText(tomorrow.format(formatter));
        formPanel.add(dueDateField, gbc);
        
        // Tags
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Tags:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        tagsField = new JTextField(30);
        tagsField.setToolTipText("Separe as tags por vírgula");
        formPanel.add(tagsField, gbc);
        
        // Status
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        formPanel.add(statusLabel, gbc);
        
        return formPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        createButton = new JButton("Criar Tarefa");
        createButton.setBackground(new Color(0, 123, 255));
        createButton.setForeground(Color.WHITE);
        createButton.setPreferredSize(new Dimension(120, 35));
        createButton.addActionListener(e -> createTask());
        
        cancelButton = new JButton("Cancelar");
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        
        return buttonPanel;
    }
    
    private void setupDialog() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }
    
    private void createTask() {
        // Validar campos obrigatórios
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showStatus("Título é obrigatório!", Color.RED);
            titleField.requestFocus();
            return;
        }
        
        if (title.length() < 3) {
            showStatus("Título deve ter pelo menos 3 caracteres!", Color.RED);
            titleField.requestFocus();
            return;
        }
        
        // Preparar dados da tarefa
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("title", title);
        taskData.put("description", descriptionArea.getText().trim());
        taskData.put("priority", priorityComboBox.getSelectedItem().toString());
        taskData.put("userId", userId);
        
        // Processar data limite se fornecida
        String dueDateStr = dueDateField.getText().trim();
        if (!dueDateStr.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                LocalDateTime dueDate = LocalDateTime.parse(dueDateStr, formatter);
                taskData.put("dueDate", dueDate.toString());
            } catch (Exception e) {
                showStatus("Formato de data inválido! Use: dd/MM/yyyy HH:mm", Color.RED);
                dueDateField.requestFocus();
                return;
            }
        }
        
        // Processar tags se fornecidas
        String tagsStr = tagsField.getText().trim();
        if (!tagsStr.isEmpty()) {
            taskData.put("tags", tagsStr);
        }
        
        // Desabilitar botão durante criação
        createButton.setEnabled(false);
        showStatus("Criando tarefa...", Color.ORANGE);
        
        // Executar criação em thread separada
        SwingUtilities.invokeLater(() -> {
            try {
                boolean success = apiClient.createTask(taskData);
                
                if (success) {
                    showStatus("Tarefa criada com sucesso!", new Color(0, 128, 0));
                    taskCreated = true;
                    
                    // Fechar diálogo após sucesso
                    Timer timer = new Timer(1000, e -> {
                        dispose();
                        if (onSuccess != null) {
                            onSuccess.run();
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                    
                } else {
                    showStatus("Erro ao criar tarefa. Tente novamente.", Color.RED);
                    createButton.setEnabled(true);
                }
                
            } catch (Exception ex) {
                showStatus("Erro de conexão: " + ex.getMessage(), Color.RED);
                createButton.setEnabled(true);
                ex.printStackTrace();
            }
        });
    }
    
    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
    
    public boolean isTaskCreated() {
        return taskCreated;
    }
}