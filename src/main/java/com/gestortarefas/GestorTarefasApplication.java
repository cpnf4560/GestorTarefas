package com.gestortarefas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.SwingUtilities;
import com.gestortarefas.view.MainWindow;
import com.gestortarefas.gui.LoginFrame;

/**
 * Classe principal da aplicação Gestor de Tarefas
 * 
 * Esta aplicação integra Spring Boot como backend com interface Swing
 * para criar um sistema completo de gestão de tarefas com autenticação.
 */
@SpringBootApplication
public class GestorTarefasApplication {

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        // Verificar se é modo backend apenas
        boolean isBackendOnly = false;
        boolean isGuiOnly = false;
        
        for (String arg : args) {
            if (arg.contains("spring.profiles.active=backend")) {
                isBackendOnly = true;
                break;
            }
            if (arg.contains("spring.profiles.active=gui") || arg.equals("--gui")) {
                isGuiOnly = true;
                break;
            }
        }
        
        if (isGuiOnly) {
            // Modo GUI apenas - conectar ao backend existente
            System.setProperty("java.awt.headless", "false");
            SwingUtilities.invokeLater(() -> {
                try {
                    new LoginFrame().setVisible(true);
                    System.out.println("Interface gráfica iniciada com sucesso!");
                    System.out.println("LoginFrame iniciado");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Erro ao iniciar interface gráfica: " + e.getMessage());
                }
            });
            return;
        }
        
        // Configurar propriedades do sistema para Swing (se não for backend apenas)
        if (!isBackendOnly) {
            System.setProperty("java.awt.headless", "false");
        }
        
        // Iniciar o contexto Spring Boot
        context = SpringApplication.run(GestorTarefasApplication.class, args);
        
        if (isBackendOnly) {
            System.out.println("===========================================");
            System.out.println("BACKEND INICIADO COM SUCESSO!");
            System.out.println("API REST: http://localhost:8080/api/");
            System.out.println("===========================================");
            return;
        }
        
        // Aguardar um momento para o servidor inicializar completamente
        try {
            Thread.sleep(5000); // Aumentado para 5 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Iniciar a interface gráfica Swing na EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
                System.out.println("Interface gráfica iniciada com sucesso!");
                System.out.println("Servidor Spring Boot rodando em: http://localhost:8080");
                System.out.println("LoginFrame iniciado com sucesso!");
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erro ao iniciar interface gráfica: " + e.getMessage());
            }
        });
    }
    
    /**
     * Retorna o contexto da aplicação Spring para uso em componentes Swing
     */
    public static ConfigurableApplicationContext getApplicationContext() {
        return context;
    }
    
    /**
     * Método para encerrar a aplicação completamente
     */
    public static void shutdown() {
        if (context != null) {
            context.close();
        }
        System.exit(0);
    }
}