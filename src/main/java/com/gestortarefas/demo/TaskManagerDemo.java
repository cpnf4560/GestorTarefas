package com.gestortarefas.demo;

import com.gestortarefas.model.LoggedUser;
import com.gestortarefas.model.UserRole;
import com.gestortarefas.view.MainWindow;

import javax.swing.*;

/**
 * Demonstração do sistema para apresentação
 */
public class TaskManagerDemo {
    
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
        }
        
        // Executar na thread do Swing
        SwingUtilities.invokeLater(() -> {
            showDemoOptions();
        });
    }
    
    private static void showDemoOptions() {
        String[] options = {
            "Demo Funcionário",
            "Demo Gestor",
            "Demo Administrador",
            "Login Completo"
        };
        
        int choice = JOptionPane.showOptionDialog(
            null,
            "Escolha o tipo de demonstração:",
            "Sistema de Gestão de Tarefas - Demo",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice == -1) {
            System.exit(0);
            return;
        }
        
        switch (choice) {
            case 0:
                launchEmployeeDemo();
                break;
            case 1:
                launchManagerDemo();
                break;
            case 2:
                launchAdminDemo();
                break;
            case 3:
                launchFullLogin();
                break;
            default:
                System.exit(0);
        }
    }
    
    private static void launchEmployeeDemo() {
        LoggedUser employee = new LoggedUser(
            3L, "João Silva", "joao.silva@empresa.com", 
            UserRole.FUNCIONARIO, "demo-token-employee"
        );
        
        MainWindow window = new MainWindow();
        window.setCurrentUser(employee);
        window.setVisible(true);
        
        showDemoInfo("Funcionário", 
            "• Dashboard com 4 colunas (Pendentes, Hoje, Atrasadas, Concluídas)\n" +
            "• Visualização das suas tarefas pessoais\n" +
            "• Criação de novas tarefas\n" +
            "• Gestão do seu perfil");
    }
    
    private static void launchManagerDemo() {
        LoggedUser manager = new LoggedUser(
            2L, "Maria Santos", "maria.santos@empresa.com", 
            UserRole.GERENTE, "demo-token-manager"
        );
        
        MainWindow window = new MainWindow();
        window.setCurrentUser(manager);
        window.setVisible(true);
        
        showDemoInfo("Gestor", 
            "• Dashboard com visão das tarefas da equipa\n" +
            "• Gestão de membros da equipa\n" +
            "• Atribuição de tarefas\n" +
            "• Relatórios de produtividade\n" +
            "• Análise de performance da equipa");
    }
    
    private static void launchAdminDemo() {
        LoggedUser admin = new LoggedUser(
            1L, "Carlos Admin", "admin@empresa.com", 
            UserRole.ADMINISTRADOR, "demo-token-admin"
        );
        
        MainWindow window = new MainWindow();
        window.setCurrentUser(admin);
        window.setVisible(true);
        
        showDemoInfo("Administrador", 
            "• Dashboard global do sistema\n" +
            "• Gestão completa de utilizadores\n" +
            "• Criação e gestão de equipas\n" +
            "• Analytics e relatórios globais\n" +
            "• Configurações do sistema\n" +
            "• Backup e auditoria");
    }
    
    private static void launchFullLogin() {
        MainWindow window = new MainWindow();
        window.setVisible(true);
    }
    
    private static void showDemoInfo(String userType, String features) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                null,
                "Demo do perfil: " + userType + "\n\nFuncionalidades disponíveis:\n" + features + "\n\n" +
                "Nota: Esta é uma demonstração. Algumas funcionalidades podem estar simuladas.",
                "Informações da Demo",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }
}