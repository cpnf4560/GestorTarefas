package com.gestortarefas.view;

import com.gestortarefas.model.LoggedUser;
import com.gestortarefas.model.UserRole;
import com.gestortarefas.view.dashboard.AdminDashboardPanel;
import com.gestortarefas.view.dashboard.ManagerDashboardPanel;
import com.gestortarefas.view.dashboard.EmployeeDashboardPanel;

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
    
    public DashboardCardPanel(Runnable onLogout) {
        this.onLogout = onLogout;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
    }
    
    /**
     * Define o usuário logado e carrega o dashboard apropriado
     */
    public void setLoggedUser(LoggedUser user) {
        this.currentUser = user;
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
}
