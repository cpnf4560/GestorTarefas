package com.gestortarefas.gui;

import com.gestortarefas.util.HttpUtil;
import com.gestortarefas.util.I18nManager;
import com.gestortarefas.view.MainWindow;
import com.gestortarefas.model.LoggedUser;
import com.gestortarefas.model.UserRole;

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
    private JButton languageButton;
    private JLabel statusLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private I18nManager i18n;

    public LoginFrame() {
        this.i18n = I18nManager.getInstance();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        updateLanguage();
        
        // Aguardar a API estar disponível
        checkApiConnection();
    }

    private void initializeComponents() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(true);
        
        // Listener para fechar aplicação com confirmação
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                confirmExit();
            }
        });
        
        usernameField = new JTextField(25);
        usernameField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(300, 40));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colors.MEDIUM_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        passwordField = new JPasswordField(25);
        passwordField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colors.MEDIUM_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        loginButton = new JButton();
        loginButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
    registerButton = new JButton();
        registerButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        registerButton.setBackground(new Color(108, 117, 125));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
    // Botão de idioma (mostra a língua de destino)
    languageButton = new JButton();
    languageButton.setPreferredSize(new Dimension(60, 25));
    languageButton.addActionListener(e -> toggleLanguage());
        
        // Labels para textos traduzíveis
        usernameLabel = new JLabel();
        usernameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        usernameLabel.setForeground(Colors.DARK_GRAY);
        
        passwordLabel = new JLabel();
        passwordLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        passwordLabel.setForeground(Colors.DARK_GRAY);
        
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Colors.ERROR_RED);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(new Color(245, 248, 250));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        
        // Botão de idioma no canto superior direito
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(Colors.SOFT_WHITE);
        topPanel.add(languageButton);
        
        // Logo e Título
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        headerPanel.setBackground(Colors.SOFT_WHITE);
        
        // Logo
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(createLogoIcon());
        headerPanel.add(logoLabel);
        
        // Título
        JLabel titleLabel = new JLabel("🚀 Sistema de Gestão de Tarefas");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(titleLabel);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(headerPanel, gbc);
        
        gbc.gridwidth = 1;
        
        // Utilizador
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(usernameLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(usernameField, gbc);
        
        // Senha
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passwordLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(passwordField, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Colors.SOFT_WHITE);
        loginButton.setPreferredSize(new Dimension(120, 40));
    registerButton.setPreferredSize(new Dimension(120, 40));
    buttonPanel.add(loginButton);
    buttonPanel.add(registerButton);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        mainPanel.add(statusLabel, gbc);
        
        // Painel de utilizadores demo
        JPanel demoPanel = createDemoPanel();
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 15, 5, 15);
        mainPanel.add(demoPanel, gbc);
        
        add(topPanel, BorderLayout.NORTH);
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
        
        // Definir tamanho explícito para o ecrã de login
        setSize(650, 750);
        setMinimumSize(new Dimension(580, 650));
        setPreferredSize(new Dimension(650, 750));
        
        setLocationRelativeTo(null);
    }

    private void setupEventListeners() {
        loginButton.addActionListener(e -> performLogin());

    // Repurpose register button to act as Exit with confirmation
    registerButton.addActionListener(e -> confirmExitFromButton());

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
                        
                        // Abrir janela principal - sistema completo
                        SwingUtilities.invokeLater(() -> {
                            // Converter Map para LoggedUser
                            LoggedUser loggedUser = createLoggedUserFromMap(user);
                            
                            MainWindow mainWindow = new MainWindow(true);
                            mainWindow.setCurrentUser(loggedUser);
                            mainWindow.setVisible(true);
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
    
    // Métodos para internacionalização PT/EN
    private void toggleLanguage() {
        i18n.toggleLanguage();
        updateLanguage();
    }
    
    private void updateLanguage() {
        // Atualizar título da janela
        setTitle(i18n.getText("title") + " - " + i18n.getText("login"));
        
        // Atualizar labels
        usernameLabel.setText(i18n.getText("username") + ":");
        passwordLabel.setText(i18n.getText("password") + ":");
        
        // Atualizar botões
        loginButton.setText(i18n.getText("login"));
        // Repurpose register button to be an Exit button
        registerButton.setText(i18n.getText("exit") != null ? i18n.getText("exit") : "Sair");

        // Show target language on languageButton: if current is PT show EN (action will switch to EN), and vice-versa
        String targetLang = i18n.isEnglish() ? "PT" : "EN";
        languageButton.setText(targetLang);
        
        // Forçar repaint
        revalidate();
        repaint();
    }

    private void confirmExitFromButton() {
        String message = i18n.getText("confirm_exit") != null ? i18n.getText("confirm_exit") : "Tem a certeza que deseja sair da aplicação?";
        String title = i18n.getText("exit") != null ? i18n.getText("exit") : "Sair";

        int option = JOptionPane.showConfirmDialog(
            this,
            message,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    private void confirmExit() {
        String message = i18n.getText("confirm_exit");
        String title = i18n.getText("exit");
        
        int option = JOptionPane.showConfirmDialog(
            this,
            message,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    /**
     * Carrega o logo do ficheiro ou cria um ícone programático como backup
     */
    private ImageIcon createLogoIcon() {
        System.out.println("=== TENTANDO CARREGAR LOGO ===");
        
        try {
            // Tentar carregar o logo do ficheiro
            java.net.URL logoUrl = getClass().getClassLoader().getResource("images/logo.png");
            System.out.println("URL do logo: " + logoUrl);
            
            if (logoUrl != null) {
                System.out.println("Logo encontrado! Carregando...");
                ImageIcon originalIcon = new ImageIcon(logoUrl);
                System.out.println("Logo original: " + originalIcon.getIconWidth() + "x" + originalIcon.getIconHeight());
                
                // Redimensionar para 60x60 pixels
                Image img = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(img);
                System.out.println("Logo redimensionado com sucesso!");
                return scaledIcon;
            } else {
                System.out.println("Logo não encontrado no resources!");
            }
        } catch (Exception e) {
            System.out.println("ERRO ao carregar logo: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Usando logo programático como backup...");
        // Backup: criar logo programático se não conseguir carregar o ficheiro
        int size = 60;
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Ativar antialiasing para melhor qualidade
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Desenhar fundo circular azul
        g2d.setColor(Colors.MAGASTEEL_BLUE);
        g2d.fillRoundRect(6, 6, size-12, size-12, 20, 20);
        
        // Desenhar borda
        g2d.setColor(Colors.DARK_GRAY);
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(6, 6, size-12, size-12, 20, 20);
        
        // Desenhar texto "GT" no centro
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "GT";
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int x = (size - textWidth) / 2;
        int y = (size + textHeight) / 2 - 2;
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        return new ImageIcon(image);
    }    public void onRegistrationSuccess(String message) {
        showStatus(message, Colors.SUCCESS_GREEN);
    }
    
    /**
     * Converte o Map retornado pela API para LoggedUser
     */
    private LoggedUser createLoggedUserFromMap(Map<String, Object> userMap) {
        Integer idInt = (Integer) userMap.get("id");
        Long id = idInt != null ? idInt.longValue() : null;
        String username = (String) userMap.get("username");
        String email = (String) userMap.get("email");
        String roleStr = (String) userMap.get("role");
        
        UserRole role = UserRole.valueOf(roleStr);
        
        LoggedUser loggedUser = new LoggedUser();
        loggedUser.setId(id);
        loggedUser.setUsername(username);
        loggedUser.setEmail(email);
        loggedUser.setRole(role);
        
        return loggedUser;
    }
    
    private JPanel createDemoPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180)), 
            "👥 Login Rápido - 29 Utilizadores", 
            0, 0, new Font(Font.SANS_SERIF, Font.BOLD, 11)
        ));
        
        // Painel de informação
        JLabel infoLabel = new JLabel("<html><center>👥 Utilizadores da simulação empresarial<br><small>Clique para preencher automaticamente (username/username123)</small></center></html>");
        infoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(3, 5, 5, 5));
        mainPanel.add(infoLabel, BorderLayout.NORTH);
        
        // Painel com abas para organizar por função
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        
        // Aba ADMINISTRADORES
        JPanel adminPanel = createUserButtonsPanel(new String[][]{
            {"martim.sottomayor", "👑 Martim Sottomayor"},
            {"catarina.balsemao", "👑 Catarina Balsemão"},
            {"admin.correia", "👑 Admin Carlos Correia"}
        }, new Color(220, 53, 69));
        tabbedPane.addTab("👑 Admins (3)", adminPanel);
        
        // Aba GERENTES
        JPanel managerPanel = createUserButtonsPanel(new String[][]{
            {"lucile.almeida", "👨‍💼 Lucile Almeida - Gestão Admin"},
            {"bessa.ribeiro", "👨‍💼 Bessa Ribeiro - Financeiro"},
            {"diana.brochado", "👨‍💼 Diana Brochado - Compras"},
            {"paulo.bessa", "👨‍💼 Paulo Bessa - Produção"},
            {"pedro.lopes", "👨‍💼 Pedro Lopes - Apoio Cliente"},
            {"antonio.nolasco", "👨‍💼 António Nolasco - Logística"},
            {"rui.goncalves", "👨‍💼 Rui Gonçalves - Comercial"}
        }, new Color(255, 193, 7));
        tabbedPane.addTab("👨‍💼 Gerentes (7)", managerPanel);
        
        // Aba FUNCIONÁRIOS (organizados por departamento)
        JPanel employeePanel = createScrollableUserPanel(new String[][]{
            {"rita.almeida", "👨‍💻 Rita Almeida - Admin"},
            {"sandra.rocha", "👨‍💻 Sandra Rocha - Admin"},
            {"ricardo.leal", "👨‍💻 Ricardo Leal - Admin"},
            {"carla.silva", "👨‍💻 Carla Silva - Financeiro"},
            {"melinda.szekely", "👨‍💻 Melinda Szekely - Financeiro"},
            {"tatiana.albuquerque", "👨‍💻 Tatiana Albuquerque - Compras"},
            {"rita.oliveira", "👨‍💻 Rita Oliveira - Compras"},
            {"ana.reis", "👨‍💻 Ana Reis - Comercial"},
            {"joao.couto", "👨‍💻 João Couto - Comercial"},
            {"ines.rodrigues", "👨‍💻 Inês Rodrigues - Comercial"},
            {"teresa.correia", "👨‍💻 Teresa Correia - Comercial"},
            {"vania.lourenco", "👨‍💻 Vânia Lourenço - Logística"},
            {"anca.tusa", "👨‍💻 Anca Tusa - Logística"},
            {"rogerio.silva", "👨‍💻 Rogério Silva - Produção"},
            {"tiago.rodrigues", "👨‍💻 Tiago Rodrigues - Produção"},
            {"mohammad.aldossari", "👨‍💻 Mohammad Al-Dossari - Produção"},
            {"vijay.kumar", "👨‍💻 Vijay Kumar - Produção"},
            {"sanita.rahman", "👨‍💻 Sanita Rahman - Produção"},
            {"monica.lewinsky", "👨‍💻 Mónica Lewinsky - Apoio Cliente"},
            {"cristiana.oliveira", "👨‍💻 Cristiana Oliveira - Apoio Cliente"}
        }, new Color(40, 167, 69));
        tabbedPane.addTab("👨‍💻 Funcionários (20)", employeePanel);
        
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
            btn.setPreferredSize(new Dimension(180, 22));
            btn.addActionListener(e -> {
                usernameField.setText(user[0]);
                passwordField.setText(user[0] + "123");
            });
            contentPanel.add(btn);
        }
        
        // Adicionar scroll para o caso de muitos utilizadores
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setPreferredSize(new Dimension(380, 140));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }
}