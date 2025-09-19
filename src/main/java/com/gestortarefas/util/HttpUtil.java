package com.gestortarefas.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Utilitário para comunicação HTTP com a API REST
 */
public class HttpUtil {
    
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final HttpClient client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
    
    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    /**
     * Executa uma requisição GET
     */
    public static HttpResponse<String> get(String endpoint) throws IOException, InterruptedException {
        String fullUrl = BASE_URL + endpoint;
        System.out.println("HttpUtil: GET " + fullUrl);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(fullUrl))
            .timeout(Duration.ofSeconds(30))
            .GET()
            .build();
            
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("HttpUtil: Response status: " + response.statusCode() + ", body length: " + 
            (response.body() != null ? response.body().length() : "null"));
        
        return response;
    }

    /**
     * Executa uma requisição POST
     */
    public static HttpResponse<String> post(String endpoint, Object body) throws IOException, InterruptedException {
        String jsonBody = objectMapper.writeValueAsString(body);
        String fullUrl = BASE_URL + endpoint;
        System.out.println("HttpUtil: POST " + fullUrl + " with body: " + jsonBody);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(fullUrl))
            .timeout(Duration.ofSeconds(30))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();
            
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("HttpUtil: Response status: " + response.statusCode() + ", body: " + response.body());
        
        return response;
    }

    /**
     * Executa uma requisição PUT
     */
    public static HttpResponse<String> put(String endpoint, Object body) throws IOException, InterruptedException {
        String jsonBody = objectMapper.writeValueAsString(body);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + endpoint))
            .timeout(Duration.ofSeconds(30))
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();
            
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Executa uma requisição DELETE
     */
    public static HttpResponse<String> delete(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + endpoint))
            .timeout(Duration.ofSeconds(30))
            .DELETE()
            .build();
            
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Converte resposta JSON em Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseResponse(String jsonResponse) throws IOException {
        System.out.println("HttpUtil: Parsing response: '" + jsonResponse + "'");
        
        if (jsonResponse == null) {
            System.out.println("HttpUtil: Response is null");
            throw new IOException("Response is null");
        }
        
        String trimmed = jsonResponse.trim();
        if (trimmed.isEmpty()) {
            System.out.println("HttpUtil: Response is empty");
            throw new IOException("No content to map due to end-of-input at [Source: (String)\"\"; line 1, column 0]");
        }
        
        try {
            Map<String, Object> result = objectMapper.readValue(trimmed, Map.class);
            System.out.println("HttpUtil: Successfully parsed response");
            return result;
        } catch (Exception e) {
            System.out.println("HttpUtil: Failed to parse JSON: " + e.getMessage());
            throw new IOException("Failed to parse JSON response: " + e.getMessage(), e);
        }
    }

    /**
     * Converte objeto para JSON
     */
    public static String toJson(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Testa se a API está disponível
     */
    public static boolean isApiAvailable() {
        try {
            // Usar um timeout mais curto para verificação de conectividade
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/stats"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
                
            HttpClient quickClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
                
            HttpResponse<String> response = quickClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 && response.body() != null && !response.body().trim().isEmpty();
        } catch (Exception e) {
            System.out.println("API não disponível: " + e.getMessage());
            return false;
        }
    }
}