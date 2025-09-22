package com.gestortarefas.gui;

import com.gestortarefas.util.HttpUtil;

import javax.swing.*;
import java.awt.*;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Frame de login da aplicação
 */
public class LoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;

    public LoginFrame() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        // Aguardar a API estar disponível
        checkApiConnection();
    }

    private void initializeComponents() {
        setTitle("Gestor de Tarefas - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Configurar Look and Feel
        /*
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeel());
        } catch (Exception e) {
            // Usar look padrão se não conseguir
        }
        */
        
        usernameField = new JTextField(20);
        usernameField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colors.MEDIUM_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colors.MEDIUM_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        loginButton = new JButton("Entrar");
        loginButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        loginButton.setBackground(Colors.MAGASTEEL_BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        registerButton = new JButton("Registar");
        registerButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        registerButton.setBackground(Colors.LIGHT_GRAY);
        registerButton.setForeground(Colors.DARK_GRAY);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Colors.ERROR_RED);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Colors.SOFT_WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        
        // Título
        JLabel titleLabel = new JLabel("Gestor de Tarefas", JLabel.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        titleLabel.setForeground(Colors.MAGASTEEL_BLUE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        
                // Utilizador
        JLabel userLabel = new JLabel("Utilizador:");
        userLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        userLabel.setForeground(Colors.DARK_GRAY);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(userLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(usernameField, gbc);
        
        // Senha
        JLabel passLabel = new JLabel("Senha:");
        passLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        passLabel.setForeground(Colors.DARK_GRAY);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(passwordField, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Colors.SOFT_WHITE);
        loginButton.setPreferredSize(new Dimension(100, 30));
        registerButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        mainPanel.add(statusLabel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Definir cor de fundo da janela
        getContentPane().setBackground(Colors.SOFT_WHITE);
        
        // Informações na parte inferior
        JLabel infoLabel = new JLabel("<html><center>Sistema de Gestão de Tarefas<br/>Spring Boot + Swing</center></html>", JLabel.CENTER);
        infoLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        infoLabel.setForeground(Colors.MEDIUM_GRAY);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        infoLabel.setOpaque(true);
        infoLabel.setBackground(Colors.SOFT_WHITE);
        add(infoLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }

    private void setupEventListeners() {
        loginButton.addActionListener(e -> performLogin());

        registerButton.addActionListener(e -> openRegisterDialog());

        // Enter key no campo senha faz login
        passwordField.addActionListener(e -> performLogin());
    }

    private void checkApiConnection() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Verificando conexão com servidor...");
            statusLabel.setForeground(Color.ORANGE);
        });
        
        // Verificar conexão numa thread separada
        new Thread(() -> {
            int attempts = 0;
            while (attempts < 15 && !HttpUtil.isApiAvailable()) {
                try {
                    Thread.sleep(2000); // Aguardar 2 segundos entre tentativas
                    attempts++;
                    final int currentAttempt = attempts;
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Aguardando servidor... (" + currentAttempt + "/15)");
                    });
                } catch (InterruptedException e) {
                    break;
                }
            }
            
            SwingUtilities.invokeLater(() -> {
                if (HttpUtil.isApiAvailable()) {
                    statusLabel.setText("Servidor conectado. Pode fazer login.");
                    statusLabel.setForeground(Colors.SUCCESS_GREEN);
                    loginButton.setEnabled(true);
                    registerButton.setEnabled(true);
                    
                    // Focar no campo username
                    usernameField.requestFocus();
                } else {
                    statusLabel.setText("Erro: Servidor não disponível!");
                    statusLabel.setForeground(Colors.ERROR_RED);
                    loginButton.setEnabled(false);
                    registerButton.setEnabled(false);
                }
            });
        }).start();
        
        // Desabilitar botões até conexão ser estabelecida
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Por favor, preencha todos os campos", Color.RED);
            return;
        }

        loginButton.setEnabled(false);
        showStatus("Autenticando...", Color.ORANGE);

        // Executar login numa thread separada para não bloquear UI
        new Thread(() -> {
            try {
                Map<String, String> credentials = new HashMap<>();
                credentials.put("identifier", username);
                credentials.put("password", password);

                HttpResponse<String> response = HttpUtil.post("/users/login", credentials);
                Map<String, Object> result = HttpUtil.parseResponse(response.body());

                SwingUtilities.invokeLater(() -> {
                    if ((Boolean) result.get("success")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> user = (Map<String, Object>) result.get("user");
                        
                        showStatus("Login realizado com sucesso!", Colors.SUCCESS_GREEN);
                        
                        // Abrir janela principal
                        SwingUtilities.invokeLater(() -> {
                            new MainFrame(user).setVisible(true);
                            dispose();
                        });
                    } else {
                        showStatus((String) result.get("message"), Colors.ERROR_RED);
                        loginButton.setEnabled(true);
                    }
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains("No content to map due to end-of-input")) {
                        showStatus("Erro: Servidor ainda não está pronto. Aguarde alguns segundos.", Colors.ERROR_RED);
                    } else if (errorMessage != null && errorMessage.contains("Connection refused")) {
                        showStatus("Erro: Servidor não disponível!", Colors.ERROR_RED);
                    } else {
                        showStatus("Erro de conexão: " + errorMessage, Colors.ERROR_RED);
                    }
                    loginButton.setEnabled(true);
                });
            }
        }).start();
    }

    private void openRegisterDialog() {
        RegisterDialog registerDialog = new RegisterDialog(this);
        registerDialog.setVisible(true);
    }

    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    public void onRegistrationSuccess(String message) {
        showStatus(message, Colors.SUCCESS_GREEN);
    }
}