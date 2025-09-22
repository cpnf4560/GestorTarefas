package com.gestortarefas.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Utilitário para chamadas REST API
 */
public class RestApiClient {
    
    private static final String BASE_URL = "http://localhost:8080/api";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public RestApiClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    /**
     * Busca dashboard do funcionário
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getEmployeeDashboard(Long userId) {
        try {
            String url = BASE_URL + "/dashboard/employee/" + userId;
            ResponseEntity<?> response = restTemplate.getForEntity(url, Map.class);
            return (Map<String, Object>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao buscar dashboard do funcionário: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Busca dashboard do gerente
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getManagerDashboard(Long userId) {
        try {
            String url = BASE_URL + "/dashboard/manager/" + userId;
            ResponseEntity<?> response = restTemplate.getForEntity(url, Map.class);
            return (Map<String, Object>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao buscar dashboard do gerente: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Busca dashboard do administrador
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAdminDashboard() {
        try {
            String url = BASE_URL + "/dashboard/admin";
            ResponseEntity<?> response = restTemplate.getForEntity(url, Map.class);
            return (Map<String, Object>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao buscar dashboard do administrador: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Busca dashboard do administrador com userId
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAdminDashboard(Long userId) {
        try {
            String url = BASE_URL + "/dashboard/admin/" + userId;
            ResponseEntity<?> response = restTemplate.getForEntity(url, Map.class);
            return (Map<String, Object>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao buscar dashboard do administrador: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtém utilizador por ID (com autenticação)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getUserById(Long userId, String token) {
        try {
            String url = BASE_URL + "/users/" + userId;
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            return (Map<String, Object>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao obter utilizador: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lista todas as equipas
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAllTeams() {
        try {
            String url = BASE_URL + "/teams";
            ResponseEntity<?> response = restTemplate.getForEntity(url, List.class);
            return (List<Map<String, Object>>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao listar equipas: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista todos os utilizadores
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAllUsers() {
        try {
            String url = BASE_URL + "/users";
            ResponseEntity<?> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> result = (Map<String, Object>) response.getBody();
            if (result != null && result.containsKey("users")) {
                return (List<Map<String, Object>>) result.get("users");
            }
            return null;
        } catch (Exception e) {
            System.err.println("Erro ao listar utilizadores: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lista todas as tarefas
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAllTasks() {
        try {
            String url = BASE_URL + "/tasks";
            ResponseEntity<?> response = restTemplate.getForEntity(url, List.class);
            return (List<Map<String, Object>>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao listar tarefas: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtém tarefa por ID
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getTaskById(Long taskId) {
        try {
            String url = BASE_URL + "/tasks/" + taskId;
            ResponseEntity<?> response = restTemplate.getForEntity(url, Map.class);
            return (Map<String, Object>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao obter tarefa: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Atualiza status de uma tarefa
     */
    public boolean updateTaskStatus(Long taskId, String status, Long userId) {
        try {
            String url = BASE_URL + "/tasks/" + taskId + "/status";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Id", userId.toString());
            
            Map<String, String> statusData = Map.of("status", status);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(statusData, headers);
            
            ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Erro ao atualizar status da tarefa: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Atualiza status de uma tarefa (sem userId)
     */
    public boolean updateTaskStatus(Long taskId, String status) {
        return updateTaskStatus(taskId, status, 1L); // Default user ID
    }
    
    /**
     * Cria novo utilizador
     */
    public boolean createUser(Map<String, Object> userData) {
        try {
            String url = BASE_URL + "/users";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userData, headers);
            
            ResponseEntity<?> response = restTemplate.postForEntity(url, entity, Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Erro ao criar utilizador: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cria nova equipa
     */
    public boolean createTeam(Map<String, Object> teamData) {
        try {
            String url = BASE_URL + "/teams";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(teamData, headers);
            
            ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Erro ao criar equipa: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lista tarefas de um utilizador
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getUserTasks(Long userId) {
        try {
            String url = BASE_URL + "/tasks/user/" + userId;
            ResponseEntity<?> response = restTemplate.getForEntity(url, List.class);
            return (List<Map<String, Object>>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao listar tarefas do utilizador: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lista tarefas de uma equipa
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getTeamTasks(Long teamId) {
        try {
            String url = BASE_URL + "/tasks/team/" + teamId;
            ResponseEntity<?> response = restTemplate.getForEntity(url, List.class);
            return (List<Map<String, Object>>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao listar tarefas da equipa: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lista tarefas por status
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getTasksByStatus(String status) {
        try {
            String url = BASE_URL + "/tasks/status/" + status;
            ResponseEntity<?> response = restTemplate.getForEntity(url, List.class);
            return (List<Map<String, Object>>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao listar tarefas por status: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Cria nova tarefa
     */
    public boolean createTask(Map<String, Object> taskData) {
        try {
            String url = BASE_URL + "/tasks";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(taskData, headers);
            
            ResponseEntity<?> response = restTemplate.postForEntity(url, entity, Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Erro ao criar tarefa: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Atualiza uma tarefa
     */
    public boolean updateTask(Long taskId, Map<String, Object> taskData) {
        try {
            String url = BASE_URL + "/tasks/" + taskId;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(taskData, headers);
            
            ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Erro ao atualizar tarefa: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtém perfil de utilizador
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getUserProfile(Long userId) {
        try {
            String url = BASE_URL + "/profiles/user/" + userId;
            ResponseEntity<?> response = restTemplate.getForEntity(url, Map.class);
            return (Map<String, Object>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao obter perfil: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lista equipas geridas por um utilizador
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getManagedTeams(Long managerId) {
        try {
            String url = BASE_URL + "/teams/managed-by/" + managerId;
            ResponseEntity<?> response = restTemplate.getForEntity(url, List.class);
            return (List<Map<String, Object>>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao listar equipas geridas: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lista membros de uma equipa
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getTeamMembers(Long teamId) {
        try {
            String url = BASE_URL + "/teams/" + teamId + "/members";
            ResponseEntity<?> response = restTemplate.getForEntity(url, List.class);
            return (List<Map<String, Object>>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao obter membros da equipa: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Método genérico POST
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> post(String endpoint, Map<String, Object> data) {
        try {
            String url = BASE_URL + endpoint;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(data, headers);
            ResponseEntity<?> response = restTemplate.postForEntity(url, entity, Map.class);
            return (Map<String, Object>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro na chamada POST " + endpoint + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Método genérico PUT
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> put(String endpoint, Map<String, Object> data) {
        try {
            String url = BASE_URL + endpoint;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(data, headers);
            ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);
            return (Map<String, Object>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro na chamada PUT " + endpoint + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Adiciona membro à equipa
     */
    public boolean addTeamMember(Long teamId, Long userId, Long requesterId) {
        try {
            String url = BASE_URL + "/teams/" + teamId + "/members/" + userId;
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Id", requesterId.toString());
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Erro ao adicionar membro à equipa: " + e.getMessage());
            return false;
        }
    }
}