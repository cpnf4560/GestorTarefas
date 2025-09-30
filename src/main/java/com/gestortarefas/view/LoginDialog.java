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
        setSize(550, 550); // Aumentado para acomodar painel com todos os utilizadores
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
        setResizable(true); // Permitir redimensionar
        setMinimumSize(new Dimension(500, 500)); // Tamanho mínimo maior
        
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20)); // Aumentado espaçamento do topo de 20 para 40
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Espaçamento adicional no topo
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.ipady = 10; // Espaçamento vertical adicional
        mainPanel.add(new JLabel(" "), gbc);
        gbc.ipady = 0; // Reset do ipady
        
        // Título
        JLabel titleLabel = new JLabel("🚀 Sistema de Gestão de Tarefas");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; // Mudou de gridy = 0 para gridy = 1
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleLabel, gbc);
        
        // Subtítulo
        JLabel subtitleLabel = new JLabel("<html><center>Faça login para aceder ao dashboard<br>personalizado por perfil</center></html>");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = 2; // Mudou de gridy = 1 para gridy = 2
        mainPanel.add(subtitleLabel, gbc);
        
        // Espaçamento
        gbc.gridy = 3; // Mudou de gridy = 2 para gridy = 3
        gbc.ipady = 15;
        mainPanel.add(new JLabel(" "), gbc);
        gbc.ipady = 0;
        
        // Campo de utilizador
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 4; // Mudou de gridy = 3 para gridy = 4
        gbc.anchor = GridBagConstraints.EAST;
        JLabel userLabel = new JLabel("👤 Utilizador:");
        userLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        mainPanel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(usernameField, gbc);
        
        // Campo de password
        gbc.gridx = 0; gbc.gridy = 5; // Mudou de gridy = 4 para gridy = 5
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
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; // Mudou de gridy = 5 para gridy = 6
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 5, 5, 5);
        mainPanel.add(buttonPanel, gbc);
        
        // Painel de utilizadores demo
        JPanel demoPanel = createDemoPanel();
        gbc.gridy = 7; // Mudou de gridy = 6 para gridy = 7
        gbc.insets = new Insets(10, 5, 5, 5);
        mainPanel.add(demoPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createDemoPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), 
            "👥 Utilizadores da Simulação Empresarial (29 utilizadores)", 
            0, 0, new Font(Font.SANS_SERIF, Font.BOLD, 12)
        ));
        
        // Painel de informação
        JLabel infoLabel = new JLabel("<html><center>Todos os utilizadores da simulação empresarial.<br>Clique para preencher automaticamente (username/username123):</center></html>");
        infoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        mainPanel.add(infoLabel, BorderLayout.NORTH);
        
        // Painel com abas para organizar por função
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        
        // Aba ADMINISTRADORES
        JPanel adminPanel = createUserButtonsPanel(new String[][]{
            {"martim.sottomayor", "👑 Martim Sottomayor"},
            {"catarina.balsemao", "👑 Catarina Balsemão"}
        }, new Color(220, 53, 69));
        tabbedPane.addTab("👑 Admins (2)", adminPanel);
        
        // Aba GERENTES
        JPanel managerPanel = createUserButtonsPanel(new String[][]{
            {"lucile.almeida", "👨‍💼 Lucile Almeida"},
            {"bessa.ribeiro", "👨‍💼 Bessa Ribeiro"},
            {"diana.brochado", "👨‍💼 Diana Brochado"},
            {"paulo.bessa", "👨‍💼 Paulo Bessa"},
            {"vania.lourenco", "👨‍💼 Vânia Lourenço"}
        }, new Color(255, 193, 7));
        tabbedPane.addTab("👨‍💼 Gerentes (5)", managerPanel);
        
        // Aba FUNCIONÁRIOS (divididos em duas colunas devido ao número)
        JPanel employeePanel = createScrollableUserPanel(new String[][]{
            {"ana.reis", "👨‍💻 Ana Reis"},
            {"joao.couto", "👨‍💻 João Couto"},
            {"carla.silva", "👨‍💻 Carla Silva"},
            {"rodrigo.silva", "👨‍💻 Rodrigo Silva"},
            {"silvia.silva", "👨‍💻 Sílvia Silva"},
            {"ines.rodrigues", "👨‍💻 Inês Rodrigues"},
            {"rita.almeida", "👨‍💻 Rita Almeida"},
            {"sandra.rocha", "👨‍💻 Sandra Rocha"},
            {"monica.lewinsky", "👨‍💻 Monica Lewinsky"},
            {"sara.pereira", "👨‍💻 Sara Pereira"},
            {"sofia.barbosa", "👨‍💻 Sofia Barbosa"},
            {"daniela.torres", "👨‍💻 Daniela Torres"},
            {"joaquina.torres", "👨‍💻 Joaquina Torres"},
            {"madalena.gomes", "👨‍💻 Madalena Gomes"},
            {"melinda.szekely", "👨‍💻 Melinda Szekely"},
            {"rita.oliveira", "👨‍💻 Rita Oliveira"},
            {"cristiana.oliveira", "👨‍💻 Cristiana Oliveira"},
            {"carolina.tavares", "👨‍💻 Carolina Tavares"},
            {"filipa.medeiros", "👨‍💻 Filipa Medeiros"},
            {"adelina.gaspar", "👨‍💻 Adelina Gaspar"},
            {"anca.tusa", "👨‍💻 Anca Tusa"},
            {"antonio.nolasco", "👨‍💻 António Nolasco"}
        }, new Color(40, 167, 69));
        tabbedPane.addTab("👨‍💻 Funcionários (22)", employeePanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        return mainPanel;
    }
    
    private JPanel createUserButtonsPanel(String[][] users, Color backgroundColor) {
        JPanel panel = new JPanel(new GridLayout(users.length, 1, 2, 2));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        for (String[] user : users) {
            JButton btn = new JButton(user[1]);
            btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            btn.setBackground(backgroundColor);
            btn.setForeground(backgroundColor.equals(new Color(255, 193, 7)) ? Color.BLACK : Color.WHITE);
            btn.setPreferredSize(new Dimension(280, 22));
            btn.addActionListener(e -> {
                usernameField.setText(user[0]);
                passwordField.setText(user[0] + "123");
            });
            panel.add(btn);
        }
        
        return panel;
    }
    
    private JPanel createScrollableUserPanel(String[][] users, Color backgroundColor) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Painel com duas colunas para os funcionários
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 3, 2));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        for (String[] user : users) {
            JButton btn = new JButton(user[1]);
            btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
            btn.setBackground(backgroundColor);
            btn.setForeground(Color.WHITE);
            btn.setPreferredSize(new Dimension(135, 20));
            btn.addActionListener(e -> {
                usernameField.setText(user[0]);
                passwordField.setText(user[0] + "123");
            });
            contentPanel.add(btn);
        }
        
        // Adicionar scroll para o caso de muitos utilizadores
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setPreferredSize(new Dimension(300, 120));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
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
            // Autenticação real via backend REST API
            java.net.URL url = new java.net.URL("http://localhost:8080/api/users/login");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            String json = String.format("{\"identifier\":\"%s\",\"password\":\"%s\"}", username, password);
            try (java.io.OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                try (java.io.InputStream is = conn.getInputStream()) {
                    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                    String result = s.hasNext() ? s.next() : "";
                    // Espera-se um JSON com os dados do utilizador autenticado
                    return parseUserFromJson(result);
                }
            } else {
                // Se não for 200, login falhou
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Verifica se é um utilizador demo válido
     */
    private boolean isValidDemoUser(String username, String password) {
    // Demo users desativados para produção
    return false;
    }
    
    /**
     * Cria utilizador demo baseado no username
     */
    private LoggedUser createDemoUser(String username) {
        // Demo users desativados para produção
        return null;
    }
    /**
     * Converte JSON da resposta do backend em LoggedUser
     */
    private LoggedUser parseUserFromJson(String json) {
        try {
            com.fasterxml.jackson.databind.JsonNode response = new com.fasterxml.jackson.databind.ObjectMapper().readTree(json);
            
            // A resposta do backend tem estrutura: {"success": true, "user": {...}}
            if (response.get("success") != null && response.get("success").asBoolean()) {
                com.fasterxml.jackson.databind.JsonNode userNode = response.get("user");
                if (userNode != null) {
                    long id = userNode.get("id").asLong();
                    String username = userNode.get("username").asText();
                    String email = userNode.get("email").asText();
                    
                    // Role e token podem não existir na resposta - usar valores padrão
                    String roleStr = userNode.has("role") ? userNode.get("role").asText() : "FUNCIONARIO";
                    String token = userNode.has("token") ? userNode.get("token").asText() : "";
                    
                    UserRole role = UserRole.valueOf(roleStr.toUpperCase());
                    return new LoggedUser(id, username, email, role, token);
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
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