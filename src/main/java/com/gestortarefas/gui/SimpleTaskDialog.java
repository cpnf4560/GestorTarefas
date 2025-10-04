package com.gestortarefas.gui;

import com.gestortarefas.util.HttpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;

/**
 * Dialog SIMPLES para criar tarefas com atribuição
 */
public class SimpleTaskDialog extends JDialog {
    
    private Map<String, Object> currentUser;
    private MainFrame parentFrame;
    
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> priorityCombo;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    
    // Componentes de atribuição
    private JRadioButton assignToUserRadio;
    private JRadioButton assignToTeamRadio;
    private JComboBox<UserItem> userCombo;
    private JComboBox<TeamItem> teamCombo;
    private ButtonGroup assignmentGroup;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public SimpleTaskDialog(MainFrame parent, Map<String, Object> user) {
        super(parent, "Nova Tarefa", true);
        
        System.out.println("\n🚀 SIMPLE TASK DIALOG CREATED! 🚀");
        System.out.println("User: " + user);
        
        this.parentFrame = parent;
        this.currentUser = user;
        
        initComponents();
        setupLayout();
        loadData();
        
        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        System.out.println("✅ SimpleTaskDialog ready!");
    }

    private void initComponents() {
        titleField = new JTextField(30);
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        
        priorityCombo = new JComboBox<>(new String[]{"BAIXA", "NORMAL", "ALTA", "URGENTE"});
        priorityCombo.setSelectedItem("NORMAL");
        
        // Data/hora
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 17);
        cal.set(Calendar.MINUTE, 0);
        
        timeSpinner = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, Calendar.MINUTE));
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        
        // Atribuição
        assignToUserRadio = new JRadioButton("👤 Atribuir a utilizador", true);
        assignToTeamRadio = new JRadioButton("👥 Atribuir a equipa");
        assignmentGroup = new ButtonGroup();
        assignmentGroup.add(assignToUserRadio);
        assignmentGroup.add(assignToTeamRadio);
        
        userCombo = new JComboBox<>();
        teamCombo = new JComboBox<>();
        teamCombo.setEnabled(false);
        
        // Event listeners
        assignToUserRadio.addActionListener(e -> {
            userCombo.setEnabled(true);
            teamCombo.setEnabled(false);
        });
        
        assignToTeamRadio.addActionListener(e -> {
            userCombo.setEnabled(false);
            teamCombo.setEnabled(true);
        });
        
        System.out.println("✅ Components initialized");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Título
        gbc.gridx = 0; gbc.gridy = row++;
        mainPanel.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleField, gbc);
        
        // Descrição
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(new JScrollPane(descriptionArea), gbc);
        
        // Prioridade
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Prioridade:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 1;
        mainPanel.add(priorityCombo, gbc);
        
        // SEÇÃO DE ATRIBUIÇÃO
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 3;
        JLabel assignmentLabel = new JLabel("🎯 ATRIBUIÇÃO:");
        assignmentLabel.setFont(assignmentLabel.getFont().deriveFont(Font.BOLD, 14f));
        assignmentLabel.setForeground(Color.BLUE);
        mainPanel.add(assignmentLabel, gbc);
        
        // Radio buttons
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 3;
        mainPanel.add(assignToUserRadio, gbc);
        
        gbc.gridx = 1; gbc.gridy = row++; gbc.gridwidth = 2;
        mainPanel.add(userCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 3;
        mainPanel.add(assignToTeamRadio, gbc);
        
        gbc.gridx = 1; gbc.gridy = row++; gbc.gridwidth = 2;
        mainPanel.add(teamCombo, gbc);
        
        // Data limite
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Data Limite:"), gbc);
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.add(dateSpinner);
        datePanel.add(new JLabel("às"));
        datePanel.add(timeSpinner);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        mainPanel.add(datePanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createButton = new JButton("Criar Tarefa");
        JButton cancelButton = new JButton("Cancelar");
        
        createButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.setPreferredSize(new Dimension(100, 35));
        
        createButton.addActionListener(this::createTask);
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        System.out.println("✅ Layout configured");
    }
    
    private void loadData() {
        new Thread(() -> {
            try {
                System.out.println("🔄 Loading users and teams...");
                
                // Carregar utilizadores
                HttpResponse<String> usersResponse = HttpUtil.get("/users");
                Map<String, Object> usersResult = HttpUtil.parseResponse(usersResponse.body());
                
                if ((Boolean) usersResult.get("success")) {
                    @SuppressWarnings("unchecked")
                    java.util.List<Map<String, Object>> users = (java.util.List<Map<String, Object>>) usersResult.get("users");
                    
                    SwingUtilities.invokeLater(() -> {
                        userCombo.removeAllItems();
                        for (Map<String, Object> user : users) {
                            if ((Boolean) user.get("active")) {
                                userCombo.addItem(new UserItem(user));
                            }
                        }
                        System.out.println("✅ Users loaded: " + userCombo.getItemCount());
                    });
                }
                
                // Carregar equipas
                HttpResponse<String> teamsResponse = HttpUtil.get("/teams/summary");
                @SuppressWarnings("unchecked")
                java.util.List<Map<String, Object>> teams = objectMapper.readValue(teamsResponse.body(), 
                    objectMapper.getTypeFactory().constructCollectionType(java.util.List.class, Map.class));
                
                SwingUtilities.invokeLater(() -> {
                    teamCombo.removeAllItems();
                    for (Map<String, Object> team : teams) {
                        if ((Boolean) team.get("active")) {
                            teamCombo.addItem(new TeamItem(team));
                        }
                    }
                    System.out.println("✅ Teams loaded: " + teamCombo.getItemCount());
                });
                
            } catch (Exception e) {
                System.out.println("❌ Error loading data: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
    
    private void createTask(ActionEvent e) {
        try {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Título é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            System.out.println("🚀 Creating task: " + title);
            
            Map<String, Object> taskData = new HashMap<>();
            taskData.put("title", title);
            taskData.put("description", descriptionArea.getText().trim());
            taskData.put("priority", priorityCombo.getSelectedItem());
            taskData.put("userId", ((Number) currentUser.get("id")).longValue());
            
            // Data limite
            Calendar dateCal = Calendar.getInstance();
            dateCal.setTime((java.util.Date) dateSpinner.getValue());
            Calendar timeCal = Calendar.getInstance();
            timeCal.setTime((java.util.Date) timeSpinner.getValue());
            
            LocalDateTime dueDate = LocalDateTime.of(
                dateCal.get(Calendar.YEAR),
                dateCal.get(Calendar.MONTH) + 1,
                dateCal.get(Calendar.DAY_OF_MONTH),
                timeCal.get(Calendar.HOUR_OF_DAY),
                timeCal.get(Calendar.MINUTE)
            );
            
            taskData.put("dueDate", dueDate.toString());
            
            // Atribuição
            if (assignToUserRadio.isSelected() && userCombo.getSelectedItem() != null) {
                UserItem selectedUser = (UserItem) userCombo.getSelectedItem();
                taskData.put("assignedUserId", selectedUser.getId());
                taskData.put("isAssignedToTeam", false);
                System.out.println("👤 Assigning to user: " + selectedUser.getFullName());
            } else if (assignToTeamRadio.isSelected() && teamCombo.getSelectedItem() != null) {
                TeamItem selectedTeam = (TeamItem) teamCombo.getSelectedItem();
                taskData.put("assignedTeamId", selectedTeam.getId());
                taskData.put("isAssignedToTeam", true);
                System.out.println("👥 Assigning to team: " + selectedTeam.getName());
            }
            
            // Criar tarefa
            HttpResponse<String> response = HttpUtil.post("/tasks", taskData);
            Map<String, Object> result = HttpUtil.parseResponse(response.body());
            
            if ((Boolean) result.get("success")) {
                System.out.println("✅ Task created successfully!");
                JOptionPane.showMessageDialog(this, "Tarefa criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                parentFrame.refreshTasks();
                dispose();
            } else {
                System.out.println("❌ Error: " + result.get("message"));
                JOptionPane.showMessageDialog(this, result.get("message"), "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            System.out.println("❌ Exception: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Classes auxiliares
    private static class UserItem {
        private final Long id;
        private final String fullName;
        private final String teamName;
        
        public UserItem(Map<String, Object> userData) {
            this.id = ((Number) userData.get("id")).longValue();
            this.fullName = (String) userData.get("fullName");
            this.teamName = (String) userData.get("teamName");
        }
        
        public Long getId() { return id; }
        public String getFullName() { return fullName; }
        
        @Override
        public String toString() {
            return fullName + (teamName != null ? " (" + teamName + ")" : "");
        }
    }
    
    private static class TeamItem {
        private final Long id;
        private final String name;
        
        public TeamItem(Map<String, Object> teamData) {
            this.id = ((Number) teamData.get("id")).longValue();
            this.name = (String) teamData.get("name");
        }
        
        public Long getId() { return id; }
        public String getName() { return name; }
        
        @Override
        public String toString() {
            return name;
        }
    }
}