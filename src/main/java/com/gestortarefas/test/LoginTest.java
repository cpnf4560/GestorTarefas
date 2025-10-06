package com.gestortarefas.test;

import com.gestortarefas.gui.LoginFrame;
import javax.swing.SwingUtilities;

/**
 * Aplicação simples para testar apenas o login
 * sem iniciar o servidor Spring Boot
 */
public class LoginTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}