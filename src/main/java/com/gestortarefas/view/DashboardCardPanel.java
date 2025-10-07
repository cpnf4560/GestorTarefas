package com.gestortarefas.view;

import com.gestortarefas.model.LoggedUser;
import com.gestortarefas.model.UserRole;
import com.gestortarefas.view.dashboard.AdminDashboardPanel;
import com.gestortarefas.view.dashboard.ManagerDashboardPanel;
import com.gestortarefas.view.dashboard.EmployeeDashboardPanel;
import com.gestortarefas.util.I18nManager;

import javax.swing.*;
import java.awt.*;

/**
 * Painel que encapsula os dashboards e gerencia navegação
 * Implementa padrão de callback para logout
 */
public class DashboardCardPanel extends JPanel {
    
    private LoggedUser currentUser;
    private JPanel currentDashboard;
    private Runnable onLogout;
    private I18nManager i18n;
    private JLabel welcomeLabel;
    private JLabel titleLabel;
    private JButton refreshBtn;
    private JButton profileBtn;
    private JButton logoutBtn;
    
    public DashboardCardPanel(Runnable onLogout) {
        this.onLogout = onLogout;
        this.i18n = I18nManager.getInstance();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Adicionar barra superior
        add(createTopBar(), BorderLayout.NORTH);
    }
    
    /**
     * Define o usuário logado e carrega o dashboard apropriado
     */
    public void setLoggedUser(LoggedUser user) {
        this.currentUser = user;
        updateWelcomeMessage();
        loadDashboard();
    }
    
    /**
     * Carrega o dashboard apropriado baseado no perfil do usuário
     */
    private void loadDashboard() {
        // Remover dashboard anterior se existir
        removeAll();
        
        if (currentUser == null) {
            return;
        }
        
        // Criar dashboard baseado no perfil
        UserRole role = currentUser.getRole();
        Long userId = currentUser.getId();
        
        if (role == UserRole.ADMINISTRADOR) {
            currentDashboard = new AdminDashboardPanel(userId);
        } else if (role == UserRole.GERENTE) {
            currentDashboard = new ManagerDashboardPanel(userId);
        } else {
            currentDashboard = new EmployeeDashboardPanel(userId);
        }
        
        // Adicionar dashboard ao painel
        add(currentDashboard, BorderLayout.CENTER);
        
        // Forçar atualização visual
        revalidate();
        repaint();
    }
    
    /**
     * Retorna o dashboard atual
     */
    public JPanel getCurrentDashboard() {
        return currentDashboard;
    }
    
    /**
     * Executa logout
     */
    public void performLogout() {
        if (onLogout != null) {
            onLogout.run();
        }
    }
    
    /**
     * Cria a barra superior com botões e informações
     */
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(70, 130, 180));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Painel esquerdo com título e boas-vindas
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
        
        // Painel direito com botões
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        // Botão idioma
        JButton languageBtn = new JButton("🌐 " + i18n.getCurrentLanguage());
        languageBtn.setPreferredSize(new Dimension(70, 30));
        languageBtn.setBackground(new Color(40, 167, 69));
        languageBtn.setForeground(Color.WHITE);
        languageBtn.setFocusPainted(false);
        languageBtn.setBorderPainted(false);
        languageBtn.addActionListener(e -> {
            i18n.toggleLanguage();
            languageBtn.setText("🌐 " + i18n.getCurrentLanguage());
            updateLanguage();
        });
        
        refreshBtn = new JButton("🔄 " + i18n.getText("refresh"));
        refreshBtn.setPreferredSize(new Dimension(120, 30));
        refreshBtn.addActionListener(e -> refreshDashboard());
        
        profileBtn = new JButton("👤 " + i18n.getText("user"));
        profileBtn.setPreferredSize(new Dimension(100, 30));
        profileBtn.addActionListener(e -> showProfile());
        
        logoutBtn = new JButton("🚪 " + i18n.getText("logout"));
        logoutBtn.setPreferredSize(new Dimension(90, 30));
        logoutBtn.addActionListener(e -> performLogout());
        
        rightPanel.add(languageBtn);
        rightPanel.add(refreshBtn);
        rightPanel.add(profileBtn);
        rightPanel.add(logoutBtn);
        
        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    private void updateWelcomeMessage() {
        if (currentUser != null && welcomeLabel != null) {
            welcomeLabel.setText(i18n.getText("welcome") + ", " + currentUser.getUsername() + 
                " (" + currentUser.getRole().getDisplayName() + ")");
        }
    }
    
    private void updateLanguage() {
        if (titleLabel != null) titleLabel.setText("🚀 " + i18n.getText("title"));
        if (refreshBtn != null) refreshBtn.setText("🔄 " + i18n.getText("refresh"));
        if (profileBtn != null) profileBtn.setText("👤 " + i18n.getText("user"));
        if (logoutBtn != null) logoutBtn.setText("🚪 " + i18n.getText("logout"));
        updateWelcomeMessage();
        loadDashboard(); // Recarregar para atualizar textos internos
    }
    
    private void refreshDashboard() {
        loadDashboard();
    }
    
    private void showProfile() {
        if (currentUser == null) return;
        
        String message = "ID: " + currentUser.getId() + "\n" +
                        i18n.getText("name") + ": " + currentUser.getUsername() + "\n" +
                        i18n.getText("email") + ": " + currentUser.getEmail() + "\n" +
                        i18n.getText("role") + ": " + currentUser.getRole().getDisplayName();
        
        JOptionPane.showMessageDialog(this, message, i18n.getText("user"), JOptionPane.INFORMATION_MESSAGE);
    }
}
