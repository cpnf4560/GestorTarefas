package com.gestortarefas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicação apenas backend para testes
 */
@SpringBootApplication
public class BackendOnlyApplication {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("INICIANDO GESTOR DE TAREFAS - BACKEND ONLY");
        System.out.println("===========================================");
        
        SpringApplication.run(BackendOnlyApplication.class, args);
        
        System.out.println("===========================================");
        System.out.println("BACKEND INICIADO COM SUCESSO!");
        System.out.println("API REST: http://localhost:8080/api/");
        System.out.println("Console H2: http://localhost:8080/h2-console");
        System.out.println("===========================================");
    }
}