package com.gestortarefas.view.dialogs;

import com.gestortarefas.util.RestApiClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Diálogo para editar equipas existentes
 */
public class TeamEditDialog extends JDialog {
    
    private final RestApiClient apiClient;
    private final Runnable onTeamUpdated;
    private final Long teamId;
    private final Long requesterId;
    
    // Componentes do formulário
    private JTextField teamNameField;
    private JTextArea descriptionArea;
    private JComboBox<String> managerComboBox;
    private JCheckBox activeCheckBox;
    
    // Componentes para gestão de membros
    private DefaultListModel<String> currentMembersModel;
    private JList<String> currentMembersList;
    
    // Dados dos gerentes para o combobox
    private Map<String, Long> managerNameToIdMap = new HashMap<>();
    
    public TeamEditDialog(Window parent, RestApiClient apiClient, Long teamId, 
                         String currentName, String currentDescription, 
                         String currentManager, boolean currentActive,
                         Long requesterId, Runnable onTeamUpdated) {
        super(parent, "Editar Equipa", ModalityType.APPLICATION_MODAL);
        
        this.apiClient = apiClient;
        this.onTeamUpdated = onTeamUpdated;
        this.teamId = teamId;
        this.requesterId = requesterId;
        
        initializeComponents();
        loadManagers();
        
        // Preencher campos com dados atuais
        teamNameField.setText(currentName);
        descriptionArea.setText(currentDescription != null ? currentDescription : "");
        activeCheckBox.setSelected(currentActive);
        
        // Selecionar gerente atual (se existir)
        if (currentManager != null && !currentManager.isEmpty() && !currentManager.equals("N/A")) {
            managerComboBox.setSelectedItem(currentManager);
        }
        
        // Carregar membros da equipa
        loadTeamMembers();
        
        setupDialog();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Criar JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Aba 1: Informações Básicas
        JPanel basicInfoPanel = createBasicInfoPanel();
        tabbedPane.addTab("Informações Básicas", basicInfoPanel);
        
        // Aba 2: Gestão de Membros
        JPanel membersPanel = createMembersPanel();
        tabbedPane.addTab("Membros da Equipa", membersPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Guardar");
        JButton cancelButton = new JButton("Cancelar");
        
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(saveButton.getFont().deriveFont(Font.BOLD));
        
        saveButton.addActionListener(this::saveTeam);
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nome da equipa
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nome da Equipa:"), gbc);
        teamNameField = new JTextField(25);
        gbc.gridx = 1;
        panel.add(teamNameField, gbc);
        
        // Descrição
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Descrição:"), gbc);
        descriptionArea = new JTextArea(4, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);
        
        // Gerente
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Gerente:"), gbc);
        managerComboBox = new JComboBox<>();
        managerComboBox.addItem("-- Sem Gerente --");
        gbc.gridx = 1;
        panel.add(managerComboBox, gbc);
        
        // Ativo
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Equipa Ativa:"), gbc);
        activeCheckBox = new JCheckBox();
        gbc.gridx = 1;
        panel.add(activeCheckBox, gbc);
        
        return panel;
    }
    
    private JPanel createMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Lista de membros atuais
        currentMembersModel = new DefaultListModel<>();
        currentMembersList = new JList<>(currentMembersModel);
        currentMembersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane membersScrollPane = new JScrollPane(currentMembersList);
        membersScrollPane.setBorder(BorderFactory.createTitledBorder("Membros Atuais"));
        membersScrollPane.setPreferredSize(new Dimension(400, 200));
        
        panel.add(membersScrollPane, BorderLayout.CENTER);
        
        // Painel de botões para gestão de membros
        JPanel memberButtonPanel = new JPanel(new FlowLayout());
        
        JButton addMemberBtn = new JButton("Adicionar Membro");
        JButton removeMemberBtn = new JButton("Remover Membro");
        JButton refreshMembersBtn = new JButton("Atualizar");
        
        addMemberBtn.setBackground(new Color(34, 139, 34));
        addMemberBtn.setForeground(Color.WHITE);
        removeMemberBtn.setBackground(new Color(220, 20, 60));
        removeMemberBtn.setForeground(Color.WHITE);
        
        // Ações dos botões
        addMemberBtn.addActionListener(e -> openAddMemberToTeamDialog());
        removeMemberBtn.addActionListener(e -> removeMemberFromTeam());
        refreshMembersBtn.addActionListener(e -> loadTeamMembers());
        
        memberButtonPanel.add(addMemberBtn);
        memberButtonPanel.add(removeMemberBtn);
        memberButtonPanel.add(refreshMembersBtn);
        
        panel.add(memberButtonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadManagers() {
        try {
            // Obter lista de utilizadores e filtrar apenas gerentes
            List<Map<String, Object>> users = apiClient.getAllUsers();
            if (users != null) {
                managerNameToIdMap.clear();
                for (Map<String, Object> user : users) {
                    String role = (String) user.get("role");
                    if ("GERENTE".equals(role) || "MANAGER".equals(role)) {
                        String userName = (String) user.get("fullName");
                        if (userName == null) {
                            userName = (String) user.get("username");
                        }
                        
                        Object userIdObj = user.get("id");
                        Long userId;
                        if (userIdObj instanceof Number) {
                            userId = ((Number) userIdObj).longValue();
                        } else {
                            continue; // Pular se não conseguir obter ID
                        }
                        
                        managerComboBox.addItem(userName);
                        managerNameToIdMap.put(userName, userId);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar gerentes: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Aviso: Não foi possível carregar os gerentes disponíveis.", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void saveTeam(ActionEvent e) {
        // Validar campos
        String teamName = teamNameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String selectedManager = (String) managerComboBox.getSelectedItem();
        boolean active = activeCheckBox.isSelected();
        
        if (teamName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, preencha o nome da equipa.", 
                "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Preparar dados para atualização
            Map<String, Object> teamData = new HashMap<>();
            teamData.put("name", teamName);
            teamData.put("description", description.isEmpty() ? null : description);
            teamData.put("active", active);
            
            // Adicionar gerente se selecionado
            if (selectedManager != null && !selectedManager.equals("-- Sem Gerente --")) {
                Long managerId = managerNameToIdMap.get(selectedManager);
                if (managerId != null) {
                    teamData.put("managerId", managerId);
                }
            }
            
            // Atualizar via API real
            boolean success = apiClient.updateTeamComplete(teamId, teamData, requesterId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Equipa atualizada com sucesso!", 
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
                // Executar callback
                if (onTeamUpdated != null) {
                    onTeamUpdated.run();
                }
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao atualizar equipa. Tente novamente.", 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro inesperado ao atualizar equipa: " + ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void loadTeamMembers() {
        if (teamId == null) return;
        
        SwingUtilities.invokeLater(() -> {
            try {
                List<Map<String, Object>> members = apiClient.getTeamMembers(teamId);
                currentMembersModel.clear();
                
                if (members != null) {
                    for (Map<String, Object> member : members) {
                        String memberName = (String) member.get("name");
                        if (memberName == null) {
                            memberName = (String) member.get("username");
                        }
                        String role = (String) member.get("role");
                        String displayName = memberName + " (" + (role != null ? role : "N/A") + ")";
                        currentMembersModel.addElement(displayName);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao carregar membros da equipa: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void openAddMemberToTeamDialog() {
        try {
            // Obter todos os utilizadores
            List<Map<String, Object>> allUsers = apiClient.getAllUsers();
            if (allUsers == null || allUsers.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Não foram encontrados utilizadores disponíveis.",
                    "Informação", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Obter membros atuais para excluir da seleção
            List<Map<String, Object>> currentMembers = apiClient.getTeamMembers(teamId);
            Set<Long> currentMemberIds = new HashSet<>();
            if (currentMembers != null) {
                for (Map<String, Object> member : currentMembers) {
                    Object memberIdObj = member.get("id");
                    if (memberIdObj instanceof Number) {
                        currentMemberIds.add(((Number) memberIdObj).longValue());
                    }
                }
            }
            
            // Criar lista de utilizadores disponíveis
            DefaultComboBoxModel<String> userModel = new DefaultComboBoxModel<>();
            Map<String, Long> userDisplayToIdMap = new HashMap<>();
            
            for (Map<String, Object> user : allUsers) {
                Object userIdObj = user.get("id");
                if (userIdObj instanceof Number) {
                    Long userId = ((Number) userIdObj).longValue();
                    if (!currentMemberIds.contains(userId)) {
                        String userName = (String) user.get("name");
                        if (userName == null) {
                            userName = (String) user.get("username");
                        }
                        String role = (String) user.get("role");
                        String displayName = userName + " (" + (role != null ? role : "N/A") + ")";
                        
                        userModel.addElement(displayName);
                        userDisplayToIdMap.put(displayName, userId);
                    }
                }
            }
            
            if (userModel.getSize() == 0) {
                JOptionPane.showMessageDialog(this,
                    "Todos os utilizadores já são membros desta equipa.",
                    "Informação", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Mostrar diálogo de seleção
            JComboBox<String> userCombo = new JComboBox<>(userModel);
            int result = JOptionPane.showConfirmDialog(this, userCombo, 
                "Selecionar Utilizador para Adicionar", JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                String selectedUser = (String) userCombo.getSelectedItem();
                if (selectedUser != null) {
                    Long userId = userDisplayToIdMap.get(selectedUser);
                    if (userId != null) {
                        boolean success = apiClient.addTeamMember(teamId, userId, requesterId);
                        if (success) {
                            JOptionPane.showMessageDialog(this,
                                "Membro adicionado com sucesso à equipa!");
                            loadTeamMembers(); // Atualizar lista
                        } else {
                            JOptionPane.showMessageDialog(this,
                                "Erro ao adicionar membro à equipa.",
                                "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao abrir diálogo de adição: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removeMemberFromTeam() {
        String selectedMember = currentMembersList.getSelectedValue();
        if (selectedMember == null) {
            JOptionPane.showMessageDialog(this,
                "Por favor, selecione um membro para remover.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Extrair nome do utilizador (antes dos parênteses)
            String userName = selectedMember.split(" \\(")[0];
            
            // Encontrar o ID do utilizador
            List<Map<String, Object>> currentMembers = apiClient.getTeamMembers(teamId);
            Long userId = null;
            
            if (currentMembers != null) {
                for (Map<String, Object> member : currentMembers) {
                    String memberName = (String) member.get("name");
                    if (memberName == null) {
                        memberName = (String) member.get("username");
                    }
                    if (userName.equals(memberName)) {
                        Object userIdObj = member.get("id");
                        if (userIdObj instanceof Number) {
                            userId = ((Number) userIdObj).longValue();
                        }
                        break;
                    }
                }
            }
            
            if (userId != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja remover " + userName + " da equipa?",
                    "Confirmar Remoção", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = apiClient.removeTeamMember(teamId, userId, requesterId);
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                            "Membro removido com sucesso da equipa!");
                        loadTeamMembers(); // Atualizar lista
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Erro ao remover membro da equipa.",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Não foi possível encontrar o utilizador selecionado.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao remover membro: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void setupDialog() {
        setSize(650, 550);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        
        // Ícone
        try {
            // Pode adicionar um ícone personalizado aqui se desejar
        } catch (Exception e) {
            // Ignorar se não conseguir carregar ícone
        }
    }
}