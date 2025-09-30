package com.gestortarefas.test;

import com.gestortarefas.model.LoggedUser;
import com.gestortarefas.model.UserRole;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJsonParsing {
    public static void main(String[] args) {
        String testJson = "{\"success\":true,\"message\":\"Login realizado com sucesso\",\"user\":{\"createdAt\":\"2025-09-29T23:00:14\",\"role\":\"ADMINISTRADOR\",\"fullName\":\"Martim Sottomayor\",\"active\":true,\"id\":128,\"email\":\"martim.sottomayor@gestordetarefas.pt\",\"username\":\"martim.sottomayor\"}}";
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(testJson);
            
            if (response.get("success") != null && response.get("success").asBoolean()) {
                JsonNode userNode = response.get("user");
                if (userNode != null) {
                    long id = userNode.get("id").asLong();
                    String username = userNode.get("username").asText();
                    String email = userNode.get("email").asText();
                    
                    String roleStr = userNode.has("role") ? userNode.get("role").asText() : "FUNCIONARIO";
                    String token = userNode.has("token") ? userNode.get("token").asText() : "";
                    
                    UserRole role = UserRole.valueOf(roleStr.toUpperCase());
                    LoggedUser user = new LoggedUser(id, username, email, role, token);
                    
                    System.out.println("Parsing bem-sucedido!");
                    System.out.println("User ID: " + user.getId());
                    System.out.println("Username: " + user.getUsername());
                    System.out.println("Email: " + user.getEmail());
                    System.out.println("Role: " + user.getRole());
                } else {
                    System.out.println("ERRO: userNode é null");
                }
            } else {
                System.out.println("ERRO: success é false");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}