package com.gestortarefas.test;

import com.gestortarefas.view.LoginDialog;
import javax.swing.SwingUtilities;

/**
 * Aplicação simples para testar apenas o login
 * sem iniciar o servidor Spring Boot
 */
public class LoginTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);
        });
    }
}