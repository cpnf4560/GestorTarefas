package com.gestortarefas.controller;

import com.gestortarefas.model.User;
import com.gestortarefas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * Controlador REST para operações relacionadas aos utilizadores
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Endpoint para autenticação de utilizador
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String identifier = credentials.get("identifier");
        String password = credentials.get("password");
        
        Map<String, Object> response = new HashMap<>();
        
        if (identifier == null || password == null) {
            response.put("success", false);
            response.put("message", "Credenciais incompletas");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> userOpt = userService.authenticate(identifier, password);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            response.put("success", true);
            response.put("message", "Login realizado com sucesso");
            response.put("user", createUserResponse(user));
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Credenciais inválidas");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para registro de novo utilizador
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody @Valid User user) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            User newUser = userService.registerUser(
                user.getUsername(), 
                user.getEmail(), 
                user.getPassword(), 
                user.getFullName()
            );
            
            response.put("success", true);
            response.put("message", "Utilizador registado com sucesso");
            response.put("user", createUserResponse(newUser));
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Busca utilizador por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> userOpt = userService.findById(id);
        
        if (userOpt.isPresent()) {
            response.put("success", true);
            response.put("user", createUserResponse(userOpt.get()));
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Utilizador não encontrado");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lista todos os utilizadores ativos
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        List<User> users = userService.findAllActiveUsers();
        
        List<Map<String, Object>> usersList = users.stream()
            .map(this::createUserResponse)
            .toList();
            
        response.put("success", true);
        response.put("users", usersList);
        response.put("total", users.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza informações do utilizador
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            
            response.put("success", true);
            response.put("message", "Utilizador atualizado com sucesso");
            response.put("user", createUserResponse(updatedUser));
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Altera senha do utilizador
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<Map<String, Object>> changePassword(@PathVariable Long id, 
                                                              @RequestBody Map<String, String> passwords) {
        Map<String, Object> response = new HashMap<>();
        
        String currentPassword = passwords.get("currentPassword");
        String newPassword = passwords.get("newPassword");
        
        if (currentPassword == null || newPassword == null) {
            response.put("success", false);
            response.put("message", "Senhas não fornecidas");
            return ResponseEntity.badRequest().body(response);
        }
        
        boolean success = userService.changePassword(id, currentPassword, newPassword);
        
        if (success) {
            response.put("success", true);
            response.put("message", "Senha alterada com sucesso");
        } else {
            response.put("success", false);
            response.put("message", "Senha atual incorreta");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Desativa utilizador
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        boolean success = userService.deactivateUser(id);
        
        if (success) {
            response.put("success", true);
            response.put("message", "Utilizador desativado com sucesso");
        } else {
            response.put("success", false);
            response.put("message", "Utilizador não encontrado");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica disponibilidade de username
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Object>> checkUsernameAvailability(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        boolean available = userService.isUsernameAvailable(username);
        
        response.put("available", available);
        response.put("message", available ? "Username disponível" : "Username já existe");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica disponibilidade de email
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@PathVariable String email) {
        Map<String, Object> response = new HashMap<>();
        boolean available = userService.isEmailAvailable(email);
        
        response.put("available", available);
        response.put("message", available ? "Email disponível" : "Email já registado");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Pesquisa utilizadores por nome
     */
    @GetMapping("/search/{name}")
    public ResponseEntity<Map<String, Object>> searchUsersByName(@PathVariable String name) {
        Map<String, Object> response = new HashMap<>();
        List<User> users = userService.searchUsersByName(name);
        
        List<Map<String, Object>> usersList = users.stream()
            .map(this::createUserResponse)
            .toList();
            
        response.put("success", true);
        response.put("users", usersList);
        response.put("total", users.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtém estatísticas de utilizadores
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> response = new HashMap<>();
        
        long activeUsers = userService.countActiveUsers();
        List<User> usersWithTasks = userService.findUsersWithTasks();
        
        response.put("success", true);
        response.put("activeUsers", activeUsers);
        response.put("usersWithTasks", usersWithTasks.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Cria resposta simplificada do utilizador (sem senha)
     */
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("username", user.getUsername());
        userResponse.put("email", user.getEmail());
        userResponse.put("fullName", user.getFullName());
        userResponse.put("active", user.getActive());
        userResponse.put("createdAt", user.getCreatedAt());
        return userResponse;
    }
}