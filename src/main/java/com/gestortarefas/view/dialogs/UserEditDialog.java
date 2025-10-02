package com.gestortarefas.view.dialogs;

import com.gestortarefas.util.RestApiClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diálogo para editar utilizadores existentes
 */
public class UserEditDialog extends JDialog {
    
    private final RestApiClient apiClient;
    private final Runnable onUserUpdated;
    private final Long userId;
    
    // Componentes do formulário
    private JTextField usernameField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JComboBox<String> roleComboBox;
    private JComboBox<String> teamComboBox;
    private JCheckBox activeCheckBox;
    
    // Dados das equipas para o combobox
    private Map<String, Long> teamNameToIdMap = new HashMap<>();
    
    public UserEditDialog(Window parent, RestApiClient apiClient, Long userId, 
                         String currentUsername, String currentFullName, String currentEmail, 
                         String currentRole, String currentTeam, boolean currentActive,
                         Runnable onUserUpdated) {
        super(parent, "Editar Utilizador", ModalityType.APPLICATION_MODAL);
        
        this.apiClient = apiClient;
        this.onUserUpdated = onUserUpdated;
        this.userId = userId;
        
        initializeComponents();
        loadTeams();
        
        // Preencher campos com dados atuais
        usernameField.setText(currentUsername);
        fullNameField.setText(currentFullName);
        emailField.setText(currentEmail);
        roleComboBox.setSelectedItem(currentRole);
        activeCheckBox.setSelected(currentActive);
        
        // Selecionar equipa atual (se existir)
        if (currentTeam != null && !currentTeam.isEmpty() && !currentTeam.equals("N/A")) {
            teamComboBox.setSelectedItem(currentTeam);
        }
        
        setupDialog();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username (apenas leitura)
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(20);
        usernameField.setEditable(false);
        usernameField.setBackground(Color.LIGHT_GRAY);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);
        
        // Nome completo
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Nome Completo:"), gbc);
        fullNameField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(fullNameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);
        
        // Perfil/Role
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Perfil:"), gbc);
        roleComboBox = new JComboBox<>(new String[]{"FUNCIONARIO", "GERENTE", "ADMINISTRADOR"});
        gbc.gridx = 1;
        mainPanel.add(roleComboBox, gbc);
        
        // Equipa
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Equipa:"), gbc);
        teamComboBox = new JComboBox<>();
        teamComboBox.addItem("-- Sem Equipa --");
        gbc.gridx = 1;
        mainPanel.add(teamComboBox, gbc);
        
        // Ativo
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Ativo:"), gbc);
        activeCheckBox = new JCheckBox();
        gbc.gridx = 1;
        mainPanel.add(activeCheckBox, gbc);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Guardar");
        JButton cancelButton = new JButton("Cancelar");
        
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(saveButton.getFont().deriveFont(Font.BOLD));
        
        saveButton.addActionListener(this::saveUser);
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void loadTeams() {
        try {
            List<Map<String, Object>> teams = apiClient.getTeamsSummary();
            if (teams != null) {
                teamNameToIdMap.clear();
                for (Map<String, Object> team : teams) {
                    String teamName = (String) team.get("name");
                    Long teamId = ((Number) team.get("id")).longValue();
                    
                    teamComboBox.addItem(teamName);
                    teamNameToIdMap.put(teamName, teamId);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar equipas: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Aviso: Não foi possível carregar as equipas disponíveis.", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void saveUser(ActionEvent e) {
        // Validar campos
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();
        String selectedTeam = (String) teamComboBox.getSelectedItem();
        boolean active = activeCheckBox.isSelected();
        
        if (fullName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, preencha todos os campos obrigatórios (Nome e Email).", 
                "Campos Obrigatórios", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validar email básico
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, insira um email válido.", 
                "Email Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Preparar dados para atualização
            Map<String, Object> userData = new HashMap<>();
            userData.put("fullName", fullName);
            userData.put("email", email);
            userData.put("role", role);
            userData.put("active", active);
            
            // Adicionar equipa se selecionada
            if (selectedTeam != null && !selectedTeam.equals("-- Sem Equipa --")) {
                Long teamId = teamNameToIdMap.get(selectedTeam);
                if (teamId != null) {
                    userData.put("teamId", teamId);
                }
            }
            
            // Atualizar via API real
            boolean success = apiClient.updateUser(userId, userData);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Utilizador atualizado com sucesso!", 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
                // Executar callback
                if (onUserUpdated != null) {
                    onUserUpdated.run();
                }
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao atualizar utilizador. Tente novamente.", 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro inesperado ao atualizar utilizador: " + ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void setupDialog() {
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Ícone
        try {
            // Pode adicionar um ícone personalizado aqui se desejar
        } catch (Exception e) {
            // Ignorar se não conseguir carregar ícone
        }
    }
}