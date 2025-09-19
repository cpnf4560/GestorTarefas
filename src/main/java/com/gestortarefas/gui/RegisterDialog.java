package com.gestortarefas.gui;

import com.gestortarefas.util.HttpUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Dialog para registro de novos utilizadores
 */
public class RegisterDialog extends JDialog {
    
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JButton registerButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    private LoginFrame parentFrame;

    public RegisterDialog(LoginFrame parent) {
        super(parent, "Registar Novo Utilizador", true);
        this.parentFrame = parent;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        fullNameField = new JTextField(20);
        registerButton = new JButton("Registar");
        cancelButton = new JButton("Cancelar");
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Título
        JLabel titleLabel = new JLabel("Registar Novo Utilizador", JLabel.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        
        // Nome completo
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(fullNameField, gbc);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("*Nome de Utilizador:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(usernameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("*Email:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(emailField, gbc);
        
        // Senha
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("*Senha:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passwordField, gbc);
        
        // Confirmar senha
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("*Confirmar Senha:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(confirmPasswordField, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        registerButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        mainPanel.add(statusLabel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Nota sobre campos obrigatórios
        JLabel noteLabel = new JLabel("<html><center>* Campos obrigatórios<br/>A senha deve ter pelo menos 6 caracteres</center></html>", JLabel.CENTER);
        noteLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));
        noteLabel.setForeground(Color.GRAY);
        noteLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        add(noteLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setupEventListeners() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Enter key no último campo executa registro
        confirmPasswordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });
    }

    private void performRegistration() {
        // Validar campos
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String fullName = fullNameField.getText().trim();

        if (username.isEmpty()) {
            showStatus("Nome de utilizador é obrigatório", Color.RED);
            usernameField.requestFocus();
            return;
        }
        
        if (username.length() < 3) {
            showStatus("Nome de utilizador deve ter pelo menos 3 caracteres", Color.RED);
            usernameField.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            showStatus("Email é obrigatório", Color.RED);
            emailField.requestFocus();
            return;
        }
        
        if (!isValidEmail(email)) {
            showStatus("Email deve ter formato válido", Color.RED);
            emailField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showStatus("Senha é obrigatória", Color.RED);
            passwordField.requestFocus();
            return;
        }
        
        if (password.length() < 6) {
            showStatus("Senha deve ter pelo menos 6 caracteres", Color.RED);
            passwordField.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            showStatus("Senhas não coincidem", Color.RED);
            confirmPasswordField.requestFocus();
            return;
        }

        registerButton.setEnabled(false);
        showStatus("Registando utilizador...", Color.ORANGE);

        // Executar registro numa thread separada
        new Thread(() -> {
            try {
                Map<String, String> userData = new HashMap<>();
                userData.put("username", username);
                userData.put("email", email);
                userData.put("password", password);
                userData.put("fullName", fullName);

                HttpResponse<String> response = HttpUtil.post("/users/register", userData);
                Map<String, Object> result = HttpUtil.parseResponse(response.body());

                SwingUtilities.invokeLater(() -> {
                    if ((Boolean) result.get("success")) {
                        showStatus("Utilizador registado com sucesso!", new Color(0, 120, 0));
                        
                        // Notificar o frame pai
                        parentFrame.onRegistrationSuccess("Utilizador registado! Pode fazer login.");
                        
                        // Fechar dialog após 2 segundos
                        Timer timer = new Timer(2000, e -> dispose());
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        showStatus((String) result.get("message"), Color.RED);
                        registerButton.setEnabled(true);
                    }
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    showStatus("Erro de conexão: " + e.getMessage(), Color.RED);
                    registerButton.setEnabled(true);
                });
            }
        }).start();
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }

    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
}