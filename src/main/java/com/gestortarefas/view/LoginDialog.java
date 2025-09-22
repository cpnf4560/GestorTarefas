package com.gestortarefas.view;

import com.gestortarefas.model.LoggedUser;
import com.gestortarefas.model.UserRole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Diálogo de login para o sistema
 */
public class LoginDialog extends JDialog {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private LoggedUser loggedUser;
    // private RestApiClient apiClient; // Campo removido pois não é utilizado
    
    public LoginDialog(Frame parent) {
        super(parent, "Login - Sistema de Gestão de Tarefas", true);
        // this.apiClient = new RestApiClient(); // Removido pois não é utilizado
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        setSize(500, 450); // Aumentado para mostrar toda a informação
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
        setResizable(true); // Permitir redimensionar
        setMinimumSize(new Dimension(450, 400)); // Tamanho mínimo
        
        // Campos do formulário
        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(200, 30));
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 30));
        
        // Botões
        loginButton = new JButton("Entrar");
        loginButton.setPreferredSize(new Dimension(100, 30));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        
        cancelButton = new JButton("Cancelar");
        cancelButton.setPreferredSize(new Dimension(100, 30));
        
        // Configurar tecla Enter como padrão
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Título
        JLabel titleLabel = new JLabel("🚀 Sistema de Gestão de Tarefas");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleLabel, gbc);
        
        // Subtítulo
        JLabel subtitleLabel = new JLabel("<html><center>Faça login para aceder ao dashboard<br>personalizado por perfil</center></html>");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);
        
        // Espaçamento
        gbc.gridy = 2;
        gbc.ipady = 15;
        mainPanel.add(new JLabel(" "), gbc);
        gbc.ipady = 0;
        
        // Campo de utilizador
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel userLabel = new JLabel("👤 Utilizador:");
        userLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        mainPanel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(usernameField, gbc);
        
        // Campo de password
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        JLabel passwordLabel = new JLabel("🔒 Password:");
        passwordLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        mainPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(passwordField, gbc);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 5, 5, 5);
        mainPanel.add(buttonPanel, gbc);
        
        // Painel de utilizadores demo
        JPanel demoPanel = createDemoPanel();
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 5, 5, 5);
        mainPanel.add(demoPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createDemoPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 3, 3)); // Mais espaço entre componentes
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), 
            "👥 Utilizadores Demo (Funcionais)", 
            0, 0, new Font(Font.SANS_SERIF, Font.BOLD, 12)
        ));
        
        JLabel infoLabel = new JLabel("<html><center>Utilizadores totalmente funcionais.<br>Clique para preencher automaticamente:</center></html>");
        infoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(infoLabel);
        
        // Botões para utilizadores demo
        JButton adminBtn = new JButton("👑 Administrador (admin/admin)");
        adminBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        adminBtn.setBackground(new Color(220, 53, 69));
        adminBtn.setForeground(Color.WHITE);
        adminBtn.setPreferredSize(new Dimension(300, 25));
        adminBtn.addActionListener(e -> {
            usernameField.setText("admin");
            passwordField.setText("admin");
        });
        
        JButton managerBtn = new JButton("👨‍💼 Gerente (manager/manager)");
        managerBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        managerBtn.setBackground(new Color(255, 193, 7));
        managerBtn.setForeground(Color.BLACK);
        managerBtn.setPreferredSize(new Dimension(300, 25));
        managerBtn.addActionListener(e -> {
            usernameField.setText("manager");
            passwordField.setText("manager");
        });
        
        JButton employeeBtn = new JButton("👨‍💻 Funcionário (employee/employee)");
        employeeBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        employeeBtn.setBackground(new Color(40, 167, 69));
        employeeBtn.setForeground(Color.WHITE);
        employeeBtn.setPreferredSize(new Dimension(300, 25));
        employeeBtn.addActionListener(e -> {
            usernameField.setText("employee");
            passwordField.setText("employee");
        });
        
        panel.add(adminBtn);
        panel.add(managerBtn);
        panel.add(employeeBtn);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(new LoginActionListener());
        cancelButton.addActionListener(e -> {
            loggedUser = null;
            dispose();
        });
        
        // Permitir login com Enter nos campos
        usernameField.addActionListener(new LoginActionListener());
        passwordField.addActionListener(new LoginActionListener());
    }
    
    /**
     * Retorna o utilizador logado (null se cancelado)
     */
    public LoggedUser getLoggedUser() {
        return loggedUser;
    }
    
    /**
     * Classe para tratar o evento de login
     */
    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            performLogin();
        }
    }
    
    /**
     * Executa o processo de login
     */
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Validações básicas
        if (username.isEmpty()) {
            showError("Por favor, introduza o nome de utilizador");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Por favor, introduza a password");
            passwordField.requestFocus();
            return;
        }
        
        // Desabilitar botão durante o processo
        loginButton.setEnabled(false);
        loginButton.setText("A entrar...");
        
        // Executar login em thread separada
        SwingWorker<LoggedUser, Void> loginWorker = new SwingWorker<LoggedUser, Void>() {
            @Override
            protected LoggedUser doInBackground() throws Exception {
                return authenticateUser(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    LoggedUser user = get();
                    if (user != null) {
                        loggedUser = user;
                        String roleDescription = "";
                        switch (user.getRole()) {
                            case ADMINISTRADOR:
                                roleDescription = "Acesso total ao sistema - Gestão de utilizadores, equipas e relatórios globais";
                                break;
                            case GERENTE:
                                roleDescription = "Gestão de equipas - Dashboard com tarefas dos funcionários da sua equipa";
                                break;
                            case FUNCIONARIO:
                                roleDescription = "Dashboard pessoal - Tarefas atribuídas organizadas por estado";
                                break;
                        }
                        
                        JOptionPane.showMessageDialog(LoginDialog.this,
                            "<html><center>✅ <b>Login efetuado com sucesso!</b><br><br>" +
                            "🎉 Bem-vindo, <b>" + user.getUsername() + "</b><br>" +
                            "🏷️ Perfil: <b>" + user.getRole().getDisplayName() + "</b><br><br>" +
                            "📋 " + roleDescription + "</center></html>",
                            "Login Bem-sucedido",
                            JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        showError("❌ Credenciais inválidas!\n\nVerifique o utilizador e password e tente novamente.");
                    }
                } catch (Exception ex) {
                    showError("Erro de conexão: " + ex.getMessage());
                }
                
                // Reabilitar botão
                loginButton.setEnabled(true);
                loginButton.setText("Entrar");
            }
        };
        
        loginWorker.execute();
    }
    
    /**
     * Autentica o utilizador (simulação ou chamada real à API)
     */
    private LoggedUser authenticateUser(String username, String password) {
        try {
            // Por enquanto, vamos simular a autenticação com utilizadores demo
            if (isValidDemoUser(username, password)) {
                return createDemoUser(username);
            }
            
            // Aqui poderia ser feita uma chamada real à API de autenticação
            // Map<String, Object> response = apiClient.login(username, password);
            // return parseUserFromResponse(response);
            
            return null;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Verifica se é um utilizador demo válido
     */
    private boolean isValidDemoUser(String username, String password) {
        return (username.equals("admin") && password.equals("admin")) ||
               (username.equals("manager") && password.equals("manager")) ||
               (username.equals("employee") && password.equals("employee"));
    }
    
    /**
     * Cria utilizador demo baseado no username
     */
    private LoggedUser createDemoUser(String username) {
        switch (username) {
            case "admin":
                return new LoggedUser(1L, "admin", "admin@gestortarefas.com", 
                    UserRole.ADMINISTRADOR, "demo-token-admin");
                    
            case "manager":
                return new LoggedUser(2L, "manager", "manager@gestortarefas.com", 
                    UserRole.GERENTE, "demo-token-manager");
                    
            case "employee":
                return new LoggedUser(3L, "employee", "employee@gestortarefas.com", 
                    UserRole.FUNCIONARIO, "demo-token-employee");
                    
            default:
                return null;
        }
    }
    
    /**
     * Mostra mensagem de erro
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro de Login", JOptionPane.ERROR_MESSAGE);
    }
}