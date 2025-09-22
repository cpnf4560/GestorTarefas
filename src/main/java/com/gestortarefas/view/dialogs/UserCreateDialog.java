package com.gestortarefas.view.dialogs;

import com.gestortarefas.util.RestApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Diálogo completo para criação de utilizadores pelos administradores
 */
public class UserCreateDialog extends JDialog {
    
    private final RestApiClient apiClient;
    private final Runnable onSuccess;
    
    // Campos do formulário
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JTextField phoneField;
    private JComboBox<String> roleComboBox;
    private JComboBox<String> teamComboBox;
    private JLabel photoLabel;
    private String photoBase64;
    
    // Botões
    private JButton selectPhotoButton;
    private JButton createButton;
    private JButton cancelButton;
    
    // Status
    private JLabel statusLabel;
    
    /**
     * Construtor
     */
    public UserCreateDialog(Window parent, RestApiClient apiClient, Runnable onSuccess) {
        super(parent, "Criar Novo Utilizador", ModalityType.APPLICATION_MODAL);
        this.apiClient = apiClient;
        this.onSuccess = onSuccess;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadTeams();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 650);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    /**
     * Inicializa componentes
     */
    private void initializeComponents() {
        // Campos de texto
        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        fullNameField = new JTextField(20);
        phoneField = new JTextField(20);
        
        // ComboBoxes
        roleComboBox = new JComboBox<>(new String[]{
            "FUNCIONARIO", "GERENTE", "ADMINISTRADOR"
        });
        teamComboBox = new JComboBox<>();
        teamComboBox.addItem("(Sem Equipa)");
        
        // Foto do utilizador
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(100, 100));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setText("<html><center>📷<br>Sem Foto</center></html>");
        photoLabel.setBackground(Color.LIGHT_GRAY);
        photoLabel.setOpaque(true);
        
        // Botões
        selectPhotoButton = new JButton("Selecionar Foto");
        createButton = new JButton("✅ Criar Utilizador");
        cancelButton = new JButton("❌ Cancelar");
        
        // Estilizar botões
        createButton.setBackground(new Color(46, 139, 87));
        createButton.setForeground(Color.WHITE);
        createButton.setFont(createButton.getFont().deriveFont(Font.BOLD));
        
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.WHITE);
        
        // Status
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.ITALIC));
    }
    
    /**
     * Configura layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Criar Novo Utilizador", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Painel central com formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Foto do utilizador
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Foto:"), gbc);
        
        JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        photoPanel.add(photoLabel);
        photoPanel.add(Box.createHorizontalStrut(10));
        photoPanel.add(selectPhotoButton);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(photoPanel, gbc);
        
        // Nome completo
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(fullNameField, gbc);
        
        // Nome de utilizador
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Utilizador:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(usernameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(emailField, gbc);
        
        // Telefone
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(phoneField, gbc);
        
        // Role
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Função:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(roleComboBox, gbc);
        
        // Equipa
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Equipa:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(teamComboBox, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 7; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(passwordField, gbc);
        
        // Confirmar Password
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Confirmar:"), gbc);
        gbc.gridx = 1; gbc.gridy = 8; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(confirmPasswordField, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Painel inferior com status e botões
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Configura event listeners
     */
    private void setupEventListeners() {
        selectPhotoButton.addActionListener(e -> selectPhoto());
        
        createButton.addActionListener(e -> createUser());
        
        cancelButton.addActionListener(e -> dispose());
    }
    
    /**
     * Carrega equipas disponíveis
     */
    private void loadTeams() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Buscar equipas via API
                var teams = apiClient.getAllTeams();
                if (teams != null) {
                    for (var team : teams) {
                        String teamName = (String) team.get("name");
                        Long teamId = ((Number) team.get("id")).longValue();
                        teamComboBox.addItem(teamName + " (ID: " + teamId + ")");
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao carregar equipas: " + e.getMessage());
                // Adicionar equipas demo
                teamComboBox.addItem("Desenvolvimento (ID: 1)");
                teamComboBox.addItem("Marketing (ID: 2)");
            }
        });
    }
    
    /**
     * Seleciona foto do utilizador
     */
    private void selectPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif");
            }
            
            @Override
            public String getDescription() {
                return "Imagens (*.jpg, *.jpeg, *.png, *.gif)";
            }
        });
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                byte[] imageData = Files.readAllBytes(selectedFile.toPath());
                photoBase64 = Base64.getEncoder().encodeToString(imageData);
                
                // Mostrar preview da imagem
                ImageIcon icon = new ImageIcon(imageData);
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(img));
                photoLabel.setText("");
                
                statusLabel.setText("Foto selecionada: " + selectedFile.getName());
                statusLabel.setForeground(new Color(34, 139, 34));
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar imagem: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Cria o utilizador
     */
    private void createUser() {
        // Validar dados
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();
        
        // Validações
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            showStatus("Por favor preencha todos os campos obrigatórios", true);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showStatus("As passwords não coincidem", true);
            return;
        }
        
        if (password.length() < 4) {
            showStatus("Password deve ter pelo menos 4 caracteres", true);
            return;
        }
        
        if (!email.contains("@")) {
            showStatus("Email inválido", true);
            return;
        }
        
        // Desabilitar botão durante criação
        createButton.setEnabled(false);
        showStatus("Criando utilizador...", false);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Preparar dados do utilizador
                Map<String, Object> userData = new HashMap<>();
                userData.put("username", username);
                userData.put("email", email);
                userData.put("password", password);
                userData.put("fullName", fullName);
                userData.put("phone", phone.isEmpty() ? null : phone);
                
                // Criar utilizador via API
                Map<String, Object> result = apiClient.post("/users/register", userData);
                
                if (result != null && result.get("id") != null) {
                    Long userId = ((Number) result.get("id")).longValue();
                    
                    // Atualizar role se diferente de FUNCIONARIO
                    if (!role.equals("FUNCIONARIO")) {
                        Map<String, Object> roleData = new HashMap<>();
                        roleData.put("role", role);
                        apiClient.put("/users/" + userId + "/role", roleData);
                    }
                    
                    // Criar perfil com foto se selecionada
                    if (photoBase64 != null && !photoBase64.isEmpty()) {
                        Map<String, Object> profileData = new HashMap<>();
                        profileData.put("userId", userId);
                        profileData.put("photo", photoBase64);
                        apiClient.post("/profiles", profileData);
                    }
                    
                    // Adicionar à equipa se selecionada
                    String selectedTeam = (String) teamComboBox.getSelectedItem();
                    if (selectedTeam != null && !selectedTeam.equals("(Sem Equipa)")) {
                        // Extrair ID da equipa do texto
                        try {
                            String teamIdStr = selectedTeam.substring(selectedTeam.indexOf("(ID: ") + 5);
                            teamIdStr = teamIdStr.substring(0, teamIdStr.indexOf(")"));
                            Long teamId = Long.parseLong(teamIdStr);
                            
                            apiClient.addTeamMember(teamId, userId, 1L); // Admin adiciona
                        } catch (Exception e) {
                            System.err.println("Erro ao adicionar à equipa: " + e.getMessage());
                        }
                    }
                    
                    showStatus("✅ Utilizador criado com sucesso!", false);
                    statusLabel.setForeground(new Color(34, 139, 34));
                    
                    // Chamar callback de sucesso
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                    
                    // Fechar diálogo após 2 segundos
                    Timer timer = new Timer(2000, e -> dispose());
                    timer.setRepeats(false);
                    timer.start();
                    
                } else {
                    showStatus("Erro ao criar utilizador. Verifique se o username/email já existem.", true);
                    createButton.setEnabled(true);
                }
                
            } catch (Exception e) {
                System.err.println("Erro ao criar utilizador: " + e.getMessage());
                showStatus("Erro: " + e.getMessage(), true);
                createButton.setEnabled(true);
            }
        });
    }
    
    /**
     * Mostra status na interface
     */
    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : new Color(34, 139, 34));
    }
}