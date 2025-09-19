package com.gestortarefas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.SwingUtilities;
import com.gestortarefas.view.MainWindow;

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
        // Configurar propriedades do sistema para Swing
        System.setProperty("java.awt.headless", "false");
        
        // Iniciar o contexto Spring Boot
        context = SpringApplication.run(GestorTarefasApplication.class, args);
        
        // Aguardar um momento para o servidor inicializar completamente
        try {
            Thread.sleep(5000); // Aumentado para 5 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Iniciar a interface gráfica Swing na EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            try {
                new MainWindow().setVisible(true);
                System.out.println("Interface gráfica iniciada com sucesso!");
                System.out.println("Servidor Spring Boot rodando em: http://localhost:8080");
                System.out.println("Console H2 disponível em: http://localhost:8080/h2-console");
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