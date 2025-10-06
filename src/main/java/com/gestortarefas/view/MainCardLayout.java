package com.gestortarefas.view;

import com.gestortarefas.gui.LoginPanel;
import com.gestortarefas.model.LoggedUser;
import com.gestortarefas.model.UserRole;

import javax.swing.*;
import java.awt.*;

/**
 * JFrame principal da aplicação usando CardLayout para navegação
 * Implementa padrão de single-window navigation conforme UFCD 5425
 */
public class MainCardLayout extends JFrame {
    
    // Constantes para identificar os cards
    public static final String LOGIN_CARD = "LOGIN";
    public static final String DASHBOARD_CARD = "DASHBOARD";
    
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private LoginPanel loginPanel;
    private DashboardCardPanel dashboardPanel;
    
    public MainCardLayout() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        // Mostrar tela de login inicialmente
        showCard(LOGIN_CARD);
    }
    
    private void initializeComponents() {
        setTitle("Gestor de Tarefas - Sistema de Gestão");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // CardLayout para navegação
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // Criar LoginPanel com callback para navegação
        loginPanel = new LoginPanel(this::onLoginSuccess);
        
        // Criar DashboardCardPanel com callback para logout
        dashboardPanel = new DashboardCardPanel(this::onLogout);
        
        // Adicionar cards ao painel
        cardPanel.add(loginPanel, LOGIN_CARD);
        cardPanel.add(dashboardPanel, DASHBOARD_CARD);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);
    }
    
    private void setupEventListeners() {
        // Listener para fechar aplicação com confirmação
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                confirmExit();
            }
        });
    }
    
    /**
     * Navega para um card específico
     */
    public void showCard(String cardName) {
        SwingUtilities.invokeLater(() -> {
            cardLayout.show(cardPanel, cardName);
            
            // Atualizar título da janela
            if (LOGIN_CARD.equals(cardName)) {
                setTitle("Gestor de Tarefas - Login");
            } else if (DASHBOARD_CARD.equals(cardName)) {
                // Título será atualizado quando usuário fizer login
                setTitle("Gestor de Tarefas - Dashboard");
            }
        });
    }
    
    /**
     * Callback chamado quando login é bem-sucedido
     */
    private void onLoginSuccess(LoggedUser user) {
        // Atualizar dashboard com dados do usuário
        dashboardPanel.setLoggedUser(user);
        
        // Navegar para dashboard
        showCard(DASHBOARD_CARD);
    }
    
    /**
     * Callback chamado quando usuário faz logout
     */
    private void onLogout() {
        // Limpar campos de login
        loginPanel.clearFields();
        
        // Navegar de volta para login
        showCard(LOGIN_CARD);
    }
    
    /**
     * Confirma saída da aplicação
     */
    private void confirmExit() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja sair da aplicação?",
            "Confirmar Saída",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    /**
     * Retorna a instância do LoginPanel
     */
    public LoginPanel getLoginPanel() {
        return loginPanel;
    }
    
    /**
     * Retorna a instância do DashboardCardPanel
     */
    public DashboardCardPanel getDashboardPanel() {
        return dashboardPanel;
    }
}
