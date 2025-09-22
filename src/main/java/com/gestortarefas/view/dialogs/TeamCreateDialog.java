package com.gestortarefas.view.dialogs;

import com.gestortarefas.util.RestApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Diálogo para criação de novas equipas
 */
public class TeamCreateDialog extends JDialog {
    
    private static final long serialVersionUID = 1L;
    
    private final RestApiClient apiClient;
    private final Runnable onSuccess;
    
    // Componentes da interface
    private JTextField nameField;
    private JTextArea descriptionArea;
    
    private JButton createButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    
    private boolean teamCreated = false;
    
    public TeamCreateDialog(Window parent, RestApiClient apiClient, Runnable onSuccess) {
        super(parent, "Nova Equipa", ModalityType.APPLICATION_MODAL);
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
        JLabel headerLabel = new JLabel("Criar Nova Equipa");
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
        
        // Nome da equipa
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Nome*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        nameField = new JTextField(30);
        formPanel.add(nameField, gbc);
        
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
        
        // Status
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.weighty = 0;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        formPanel.add(statusLabel, gbc);
        
        return formPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        createButton = new JButton("Criar Equipa");
        createButton.setBackground(new Color(0, 123, 255));
        createButton.setForeground(Color.WHITE);
        createButton.setPreferredSize(new Dimension(120, 35));
        createButton.addActionListener(e -> createTeam());
        
        cancelButton = new JButton("Cancelar");
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        
        return buttonPanel;
    }
    
    private void setupDialog() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }
    
    private void createTeam() {
        // Validar campos obrigatórios
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showStatus("Nome é obrigatório!", Color.RED);
            nameField.requestFocus();
            return;
        }
        
        if (name.length() < 2) {
            showStatus("Nome deve ter pelo menos 2 caracteres!", Color.RED);
            nameField.requestFocus();
            return;
        }
        
        // Preparar dados da equipa
        Map<String, Object> teamData = new HashMap<>();
        teamData.put("name", name);
        teamData.put("description", descriptionArea.getText().trim());
        
        // Desabilitar botão durante criação
        createButton.setEnabled(false);
        showStatus("Criando equipa...", Color.ORANGE);
        
        // Executar criação em thread separada
        SwingUtilities.invokeLater(() -> {
            try {
                boolean success = apiClient.createTeam(teamData);
                
                if (success) {
                    showStatus("Equipa criada com sucesso!", new Color(0, 128, 0));
                    teamCreated = true;
                    
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
                    showStatus("Erro ao criar equipa. Tente novamente.", Color.RED);
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
    
    public boolean isTeamCreated() {
        return teamCreated;
    }
}