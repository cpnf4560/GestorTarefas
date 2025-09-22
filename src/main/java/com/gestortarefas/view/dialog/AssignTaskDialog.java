package com.gestortarefas.view.dialog;

import com.gestortarefas.model.Task;
import com.gestortarefas.util.RestApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diálogo para atribuição de nova tarefa a um membro da equipa
 */
public class AssignTaskDialog extends JDialog {
    
    private static final long serialVersionUID = 1L;
    
    private final Long managerId;
    private final Long teamId;
    private final RestApiClient apiClient;
    
    // Componentes da interface
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<UserComboItem> assigneeComboBox;
    private JComboBox<Task.TaskPriority> priorityComboBox;
    private JTextField dueDateField;
    private JTextField estimatedHoursField;
    private JTextField tagsField;
    
    private boolean taskCreated = false;
    
    public AssignTaskDialog(Frame parent, Long managerId, Long teamId) {
        super(parent, "Atribuir Nova Tarefa", true);
        this.managerId = managerId;
        this.teamId = teamId;
        this.apiClient = new RestApiClient();
        
        initializeComponents();
        loadTeamMembers();
        setupDialog();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Título do diálogo
        JLabel headerLabel = new JLabel("Nova Tarefa para a Equipa");
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
        
        // Atribuir a
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Atribuir a*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        assigneeComboBox = new JComboBox<>();
        assigneeComboBox.setRenderer(new UserComboRenderer());
        formPanel.add(assigneeComboBox, gbc);
        
        // Prioridade
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Prioridade*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        priorityComboBox = new JComboBox<>(Task.TaskPriority.values());
        priorityComboBox.setSelectedItem(Task.TaskPriority.NORMAL);
        formPanel.add(priorityComboBox, gbc);
        
        // Data limite
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Data Limite:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dueDateField = new JTextField("yyyy-MM-dd HH:mm");
        dueDateField.setToolTipText("Formato: yyyy-MM-dd HH:mm (ex: 2023-12-25 14:30)");
        formPanel.add(dueDateField, gbc);
        
        // Horas estimadas
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Horas Estimadas:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        estimatedHoursField = new JTextField();
        estimatedHoursField.setToolTipText("Número de horas estimadas para completar a tarefa");
        formPanel.add(estimatedHoursField, gbc);
        
        // Tags
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Tags:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        tagsField = new JTextField();
        tagsField.setToolTipText("Tags separadas por vírgula (ex: urgente, frontend, bugfix)");
        formPanel.add(tagsField, gbc);
        
        return formPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> dispose());
        
        JButton createButton = new JButton("Criar Tarefa");
        createButton.setBackground(new Color(70, 130, 180));
        createButton.setForeground(Color.WHITE);
        createButton.addActionListener(new CreateTaskAction());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);
        
        return buttonPanel;
    }
    
    private void loadTeamMembers() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<Map<String, Object>> members = apiClient.getTeamMembers(teamId);
                assigneeComboBox.removeAllItems();
                
                if (members != null && !members.isEmpty()) {
                    for (Map<String, Object> member : members) {
                        Long id = ((Number) member.get("id")).longValue();
                        String username = (String) member.get("username");
                        String fullName = (String) member.get("fullName");
                        String email = (String) member.get("email");
                        
                        assigneeComboBox.addItem(new UserComboItem(id, username, fullName, email));
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Nenhum membro encontrado nesta equipa", 
                        "Aviso", 
                        JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Erro ao carregar membros da equipa: " + e.getMessage(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void setupDialog() {
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Focus no primeiro campo
        SwingUtilities.invokeLater(() -> titleField.requestFocus());
    }
    
    public boolean isTaskCreated() {
        return taskCreated;
    }
    
    private class CreateTaskAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (!validateFields()) {
                    return;
                }
                
                Map<String, Object> taskData = buildTaskData();
                Map<String, Object> result = apiClient.post("/tasks", taskData);
                
                if (result != null && result.get("id") != null) {
                    taskCreated = true;
                    JOptionPane.showMessageDialog(AssignTaskDialog.this,
                        "Tarefa criada com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(AssignTaskDialog.this,
                        "Erro ao criar tarefa. Tente novamente.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(AssignTaskDialog.this,
                    "Erro ao criar tarefa: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private boolean validateFields() {
            if (titleField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(AssignTaskDialog.this,
                    "O título da tarefa é obrigatório!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE);
                titleField.requestFocus();
                return false;
            }
            
            if (assigneeComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(AssignTaskDialog.this,
                    "Deve selecionar um membro para atribuir a tarefa!",
                    "Validação",
                    JOptionPane.WARNING_MESSAGE);
                assigneeComboBox.requestFocus();
                return false;
            }
            
            // Validar formato da data se preenchida
            String dateText = dueDateField.getText().trim();
            if (!dateText.isEmpty() && !dateText.equals("yyyy-MM-dd HH:mm")) {
                try {
                    LocalDateTime.parse(dateText, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AssignTaskDialog.this,
                        "Formato de data inválido! Use: yyyy-MM-dd HH:mm",
                        "Validação",
                        JOptionPane.WARNING_MESSAGE);
                    dueDateField.requestFocus();
                    return false;
                }
            }
            
            // Validar horas estimadas se preenchidas
            String hoursText = estimatedHoursField.getText().trim();
            if (!hoursText.isEmpty()) {
                try {
                    Integer.parseInt(hoursText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(AssignTaskDialog.this,
                        "Horas estimadas deve ser um número inteiro!",
                        "Validação",
                        JOptionPane.WARNING_MESSAGE);
                    estimatedHoursField.requestFocus();
                    return false;
                }
            }
            
            return true;
        }
        
        private Map<String, Object> buildTaskData() {
            Map<String, Object> taskData = new HashMap<>();
            
            UserComboItem assignee = (UserComboItem) assigneeComboBox.getSelectedItem();
            
            taskData.put("title", titleField.getText().trim());
            taskData.put("description", descriptionArea.getText().trim());
            taskData.put("userId", assignee.getId());
            taskData.put("createdByUserId", managerId);
            taskData.put("priority", ((Task.TaskPriority) priorityComboBox.getSelectedItem()).toString());
            taskData.put("status", Task.TaskStatus.PENDENTE.toString());
            taskData.put("assignedTeamId", teamId);
            
            // Data limite (opcional)
            String dateText = dueDateField.getText().trim();
            if (!dateText.isEmpty() && !dateText.equals("yyyy-MM-dd HH:mm")) {
                taskData.put("dueDate", dateText);
            }
            
            // Horas estimadas (opcional)
            String hoursText = estimatedHoursField.getText().trim();
            if (!hoursText.isEmpty()) {
                taskData.put("estimatedHours", Integer.parseInt(hoursText));
            }
            
            // Tags (opcional)
            String tagsText = tagsField.getText().trim();
            if (!tagsText.isEmpty()) {
                taskData.put("tags", tagsText);
            }
            
            return taskData;
        }
    }
    
    // Classe para itens do ComboBox de utilizadores
    private static class UserComboItem {
        private final Long id;
        private final String username;
        private final String fullName;
        private final String email;
        
        public UserComboItem(Long id, String username, String fullName, String email) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.email = email;
        }
        
        public Long getId() {
            return id;
        }
        
        public String getEmail() {
            return email;
        }
        
        @Override
        public String toString() {
            return fullName != null && !fullName.trim().isEmpty() 
                ? fullName + " (" + username + ")"
                : username;
        }
    }
    
    // Renderer personalizado para o ComboBox
    private class UserComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof UserComboItem) {
                UserComboItem user = (UserComboItem) value;
                setText(user.toString());
                setToolTipText(user.getEmail());
            }
            
            return this;
        }
    }
}