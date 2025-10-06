package com.gestortarefas.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
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
        // Configurar timeouts para evitar problemas de conexão
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 segundos para conectar
        factory.setReadTimeout(10000);   // 10 segundos para ler resposta
        
        this.restTemplate = new RestTemplate(factory);
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
     * Lista equipas com dados completos para administração
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getTeamsSummary() {
        try {
            String url = BASE_URL + "/teams/summary";
            ResponseEntity<?> response = restTemplate.getForEntity(url, List.class);
            return (List<Map<String, Object>>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao listar resumo de equipas: " + e.getMessage());
            return null;
        }
    }

    /**
     * Atualiza uma equipa
     */
    public boolean updateTeam(Long teamId, String name, String description, Boolean active, Long requesterId) {
        try {
            String url = BASE_URL + "/teams/" + teamId;
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", name);
            requestBody.put("description", description);
            if (active != null) {
                requestBody.put("active", active);
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Id", requesterId.toString());
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Object.class);
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Erro ao atualizar equipa: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza uma equipa completa (incluindo definir gerente)
     */
    public boolean updateTeamComplete(Long teamId, Map<String, Object> teamData, Long requesterId) {
        try {
            // Primeiro atualizar dados básicos da equipa
            String name = (String) teamData.get("name");
            String description = (String) teamData.get("description");
            Boolean active = (Boolean) teamData.get("active");
            
            boolean basicUpdateSuccess = updateTeam(teamId, name, description, active, requesterId);
            if (!basicUpdateSuccess) {
                return false;
            }
            
            // Se há managerId, definir o gerente
            if (teamData.containsKey("managerId") && teamData.get("managerId") != null) {
                Long managerId = ((Number) teamData.get("managerId")).longValue();
                return setTeamManager(teamId, managerId, requesterId);
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar equipa completa: " + e.getMessage());
            return false;
        }
    }

    /**
     * Define o gestor de uma equipa
     */
    public boolean setTeamManager(Long teamId, Long managerId, Long requesterId) {
        try {
            String url = BASE_URL + "/teams/" + teamId + "/manager/" + managerId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Id", requesterId.toString());
            
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Object.class);
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Erro ao definir gestor da equipa: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca utilizador por nome completo
     */
    public Map<String, Object> findUserByFullName(String fullName) {
        try {
            List<Map<String, Object>> users = getAllUsers();
            if (users != null) {
                for (Map<String, Object> user : users) {
                    String userFullName = (String) user.get("fullName");
                    if (fullName.equals(userFullName)) {
                        return user;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("Erro ao buscar utilizador por nome: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista todos os utilizadores (incluindo inativos)
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAllUsers() {
        try {
            String url = BASE_URL + "/users?includeInactive=true";
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
     * Atualiza um utilizador
     */
    @SuppressWarnings("unchecked")
    public boolean updateUser(Long userId, Map<String, Object> userData) {
        try {
            String url = BASE_URL + "/users/" + userId;
            
            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Criar entidade HTTP
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(userData, headers);
            
            // Fazer requisição PUT
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.PUT, 
                request, 
                (Class<Map<String, Object>>) (Class<?>) Map.class
            );
            
            // Verificar resposta
            Map<String, Object> result = response.getBody();
            if (result != null && result.containsKey("success")) {
                Boolean success = (Boolean) result.get("success");
                if (success != null && success) {
                    System.out.println("Utilizador atualizado com sucesso: " + result.get("message"));
                    return true;
                } else {
                    System.err.println("Erro ao atualizar utilizador: " + result.get("message"));
                    return false;
                }
            }
            
            return false;
            
        } catch (Exception e) {
            System.err.println("Erro ao atualizar utilizador: " + e.getMessage());
            e.printStackTrace();
            return false;
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
     * Lista todas as tarefas com filtros
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAllTasks(Map<String, String> filters) {
        try {
            StringBuilder url = new StringBuilder(BASE_URL + "/tasks");
            
            if (filters != null && !filters.isEmpty()) {
                url.append("?");
                boolean first = true;
                for (Map.Entry<String, String> entry : filters.entrySet()) {
                    if (!first) url.append("&");
                    url.append(entry.getKey()).append("=").append(entry.getValue());
                    first = false;
                }
            }
            
            ResponseEntity<?> response = restTemplate.getForEntity(url.toString(), Map.class);
            return (Map<String, Object>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao listar tarefas com filtros: " + e.getMessage());
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
    
    /**
     * Remove membro da equipa
     */
    public boolean removeTeamMember(Long teamId, Long userId, Long requesterId) {
        try {
            String url = BASE_URL + "/teams/" + teamId + "/members/" + userId;
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Id", requesterId.toString());
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Erro ao remover membro da equipa: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Busca comentários de uma tarefa
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getTaskComments(Long taskId) {
        try {
            String url = BASE_URL + "/tasks/" + taskId + "/comments";
            ResponseEntity<?> response = restTemplate.getForEntity(url, Map.class);
            return (Map<String, Object>) response.getBody();
        } catch (Exception e) {
            System.err.println("Erro ao buscar comentários da tarefa: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Adiciona comentário a uma tarefa
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> addTaskComment(Long taskId, Map<String, Object> commentData) {
        try {
            String url = BASE_URL + "/tasks/" + taskId + "/comments";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(commentData, headers);
            
            ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            Map<String, Object> result = (Map<String, Object>) response.getBody();
            if (result == null) {
                result = Map.of("success", response.getStatusCode().is2xxSuccessful());
            }
            return result;
        } catch (Exception e) {
            System.err.println("Erro ao adicionar comentário: " + e.getMessage());
            return Map.of("success", false, "error", e.getMessage());
        }
    }
    
    /**
     * Atribui tarefa a um funcionário (para gerentes)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> assignTask(Long taskId, Long assignedUserId, Long managerId) {
        try {
            String url = BASE_URL + "/tasks/" + taskId + "/assign";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Id", managerId.toString());
            
            Map<String, Object> assignData = Map.of("assignedUserId", assignedUserId);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(assignData, headers);
            
            ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);
            Map<String, Object> result = (Map<String, Object>) response.getBody();
            if (result == null) {
                result = Map.of("success", response.getStatusCode().is2xxSuccessful());
            }
            return result;
        } catch (Exception e) {
            System.err.println("Erro ao atribuir tarefa: " + e.getMessage());
            return Map.of("success", false, "error", e.getMessage());
        }
    }

    /**
     * Elimina utilizador
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> deleteUser(Long userId) {
        try {
            String url = BASE_URL + "/users/" + userId;
            
            // Configurar headers de autenticação
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth("admin", "admin123");
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Map.class);
            
            Map<String, Object> result = new HashMap<>();
            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.NO_CONTENT) {
                result.put("success", true);
                result.put("message", "Utilizador eliminado com sucesso");
                return result;
            } else {
                result.put("success", false);
                result.put("message", "Erro HTTP: " + response.getStatusCode());
                return result;
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao eliminar utilizador: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erro ao eliminar utilizador: " + e.getMessage());
            return errorResponse;
        }
    }
}