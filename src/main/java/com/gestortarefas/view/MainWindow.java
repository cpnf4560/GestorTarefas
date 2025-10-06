package com.gestortarefas.view;

import com.gestortarefas.model.LoggedUser;
import com.gestortarefas.view.dashboard.AdminDashboardPanel;
import com.gestortarefas.view.dashboard.EmployeeDashboardPanel;
import com.gestortarefas.view.dashboard.ManagerDashboardPanel;
import com.gestortarefas.gui.LoginFrame;
import com.gestortarefas.util.I18nManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Janela principal do Sistema de Gestão de Tarefas
 */
public class MainWindow extends JFrame {
    
    private LoggedUser currentUser;
    private JPanel contentPanel;
    private JPanel currentDashboard;
    private JLabel welcomeLabel;
    private JLabel statusLabel;
    private JLabel titleLabel;
    private JButton refreshBtn;
    private JButton profileBtn;
    private JButton logoutBtn;
    private I18nManager i18n;
    
    public MainWindow() {
        this.i18n = I18nManager.getInstance();
        initializeWindow();
        showLoginDialog();
    }
    
    /**
     * Construtor para quando o utilizador já fez login
     */
    public MainWindow(boolean skipLogin) {
        this.i18n = I18nManager.getInstance();
        initializeWindow();
        if (!skipLogin) {
            showLoginDialog();
        }
    }
    
    private void initializeWindow() {
        setTitle(i18n.getText("title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 800));
        
        // Layout principal
        setLayout(new BorderLayout());
        
        // Barra superior
        createTopBar();
        add(createTopBar(), BorderLayout.NORTH);
        
        // Painel de conteúdo principal
        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
        
        // Barra de status
        createStatusBar();
        add(createStatusBar(), BorderLayout.SOUTH);
        
        // Configurar comportamento de fechamento
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onWindowClosing();
            }
        });
        
        // Centralizar na tela
        setLocationRelativeTo(null);
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(70, 130, 180));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Título e informações do utilizador
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        
        titleLabel = new JLabel("🚀 " + i18n.getText("title"));
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);
        
        welcomeLabel = new JLabel(i18n.getText("please_wait"));
        welcomeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        leftPanel.add(welcomeLabel);
        
        // Botões de ação
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        // Botão de idioma PT/EN
        JButton languageBtn = new JButton("🌐 " + i18n.getCurrentLanguage());
        languageBtn.setPreferredSize(new Dimension(70, 30));
        languageBtn.setBackground(new Color(40, 167, 69));
        languageBtn.setForeground(Color.WHITE);
        languageBtn.setFocusPainted(false);
        languageBtn.setBorderPainted(false);
        languageBtn.addActionListener(e -> {
            i18n.toggleLanguage();
            updateLanguage();
            languageBtn.setText("🌐 " + i18n.getCurrentLanguage());
        });
        
        refreshBtn = new JButton("🔄 " + i18n.getText("refresh"));
        refreshBtn.setPreferredSize(new Dimension(120, 30));
        refreshBtn.addActionListener(e -> refreshDashboard());
        
        profileBtn = new JButton("👤 " + i18n.getText("user"));
        profileBtn.setPreferredSize(new Dimension(100, 30));
        profileBtn.addActionListener(e -> showProfile());
        
        logoutBtn = new JButton("🚪 " + i18n.getText("logout"));
        logoutBtn.setPreferredSize(new Dimension(90, 30));
        logoutBtn.addActionListener(e -> logout());
        
        rightPanel.add(languageBtn);
        rightPanel.add(refreshBtn);
        rightPanel.add(profileBtn);
        rightPanel.add(logoutBtn);
        
        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        statusLabel = new JLabel(i18n.getText("loading"));
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        
        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        
        // Timer para atualizar hora
        Timer timeTimer = new Timer(1000, e -> {
            timeLabel.setText(java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        });
        timeTimer.start();
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(timeLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    /**
     * Mostra a UI de login moderna (LoginFrame)
     */
    private void showLoginDialog() {
        // Migrado para usar o LoginFrame moderno: fecha esta janela e abre a nova UI de login
        SwingUtilities.invokeLater(() -> {
            try {
                dispose();
            } catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
    
    /**
     * Define o utilizador atual e configura o dashboard apropriado
     */
    public void setCurrentUser(LoggedUser user) {
        this.currentUser = user;
        updateWelcomeMessage();
        loadUserDashboard();
        updateStatus("Login efetuado com sucesso - Dashboard carregado");
    }
    
    private void updateWelcomeMessage() {
        if (currentUser != null) {
            welcomeLabel.setText(i18n.getText("welcome") + ", " + currentUser.getUsername() + 
                " (" + currentUser.getRole().getDisplayName() + ")");
        }
    }
    
    /**
     * Carrega o dashboard apropriado baseado no papel do utilizador
     */
    private void loadUserDashboard() {
        if (currentUser == null) return;
        
        contentPanel.removeAll();
        
        JPanel dashboard = null;
        
        switch (currentUser.getRole()) {
            case FUNCIONARIO:
                dashboard = new EmployeeDashboardPanel(currentUser.getId());
                updateStatus(i18n.getText("dashboard") + " - " + i18n.getText("role"));
                break;
                
            case GERENTE:
                dashboard = new ManagerDashboardPanel(currentUser.getId());
                updateStatus(i18n.getText("dashboard") + " - " + i18n.getText("manager"));
                break;
                
            case ADMINISTRADOR:
                dashboard = new AdminDashboardPanel(currentUser.getId());
                updateStatus(i18n.getText("dashboard") + " - " + i18n.getText("admin"));
                break;
                
            default:
                showErrorMessage(i18n.getText("error"));
                return;
        }
        
        if (dashboard != null) {
            currentDashboard = dashboard;
            contentPanel.add(dashboard, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }
    
    /**
     * Atualiza o dashboard atual
     */
    private void refreshDashboard() {
        if (currentUser != null) {
            updateStatus(i18n.getText("loading") + "...");
            loadUserDashboard();
            updateStatus(i18n.getText("dashboard") + " " + i18n.getText("refresh"));
        }
    }
    
    /**
     * Mostra o perfil do utilizador
     */
    private void showProfile() {
        if (currentUser == null) return;
        
        JDialog profileDialog = new JDialog(this, i18n.getText("user"), true);
        profileDialog.setSize(400, 300);
        profileDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Informações do perfil
        gbc.gridx = 0; gbc.gridy = 0;
        profileDialog.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        profileDialog.add(new JLabel(currentUser.getId().toString()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        profileDialog.add(new JLabel(i18n.getText("name") + ":"), gbc);
        gbc.gridx = 1;
        profileDialog.add(new JLabel(currentUser.getUsername()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        profileDialog.add(new JLabel(i18n.getText("email") + ":"), gbc);
        gbc.gridx = 1;
        profileDialog.add(new JLabel(currentUser.getEmail()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        profileDialog.add(new JLabel(i18n.getText("role") + ":"), gbc);
        gbc.gridx = 1;
        profileDialog.add(new JLabel(currentUser.getRole().getDisplayName()), gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton editBtn = new JButton(i18n.getText("edit"));
        JButton closeBtn = new JButton(i18n.getText("cancel"));
        
        editBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(profileDialog, 
                i18n.getText("info"));
        });
        
        closeBtn.addActionListener(e -> profileDialog.dispose());
        
        buttonPanel.add(editBtn);
        buttonPanel.add(closeBtn);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        profileDialog.add(buttonPanel, gbc);
        
        profileDialog.setLocationRelativeTo(this);
        profileDialog.setVisible(true);
    }
    
    /**
     * Efetua logout do sistema
     */
    private void logout() {
        int result = JOptionPane.showConfirmDialog(this,
            i18n.getText("confirm_exit"),
            i18n.getText("logout"),
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            currentUser = null;
            // Fechar esta janela e abrir a tela de Login moderna
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    /**
     * Atualiza a mensagem de status
     */
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
    
    /**
     * Mostra mensagem de erro
     */
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, i18n.getText("error"), JOptionPane.ERROR_MESSAGE);
        updateStatus(i18n.getText("error") + ": " + message);
    }
    
    /**
     * Tratamento do fechamento da janela
     */
    private void onWindowClosing() {
        int result = JOptionPane.showConfirmDialog(this,
            i18n.getText("confirm_exit"),
            i18n.getText("exit"),
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            updateStatus(i18n.getText("processing") + "...");
            System.exit(0);
        } else {
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }
    
    /**
     * Atualiza todos os textos da UI quando o idioma muda
     */
    private void updateLanguage() {
        setTitle(i18n.getText("title"));
        titleLabel.setText("🚀 " + i18n.getText("title"));
        refreshBtn.setText("🔄 " + i18n.getText("refresh"));
        profileBtn.setText("👤 " + i18n.getText("user"));
        logoutBtn.setText("🚪 " + i18n.getText("logout"));
        
        if (currentUser != null) {
            updateWelcomeMessage();
        }
        
        // Atualizar cabeçalhos das tabelas do dashboard atual
        if (currentDashboard instanceof com.gestortarefas.view.dashboard.DashboardBasePanel) {
            ((com.gestortarefas.view.dashboard.DashboardBasePanel) currentDashboard).updateTableHeaders();
        }
        
        // Recarregar dashboard para atualizar textos internos
        if (currentUser != null) {
            loadUserDashboard();
        }
        
        revalidate();
        repaint();
    }
    
    /**
     * Método principal para iniciar a aplicação
     */
    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Se Nimbus não estiver disponível, usar o padrão
            e.printStackTrace();
        }
        
        // Executar na thread do Swing
        SwingUtilities.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }
}