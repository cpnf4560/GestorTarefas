package com.gestortarefas.controller;

import com.gestortarefas.repository.UserRepository;
import com.gestortarefas.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "OK");
        result.put("totalUsers", userRepository.count());
        result.put("totalTasks", taskRepository.count());
        result.put("message", "Dados carregados com sucesso!");
        return result;
    }
    
    @GetMapping("/users")
    public Object getAllUsers() {
        return userRepository.findAll();
    }
    
    @GetMapping("/tasks")
    public Object getAllTasks() {
        return taskRepository.findAll();
    }
}