package com.gestortarefas.dto;

import java.time.LocalDateTime;

/**
 * DTO para resumo de equipas com informações completas para administração
 */
public class TeamSummaryDTO {
    private Long id;
    private String name;
    private String description;
    private String managerName;
    private Long managerId;
    private Integer memberCount;
    private Integer activeTasksCount;
    private LocalDateTime createdAt;
    private Boolean active;
    
    public TeamSummaryDTO() {}
    
    public TeamSummaryDTO(Long id, String name, String description, String managerName, 
                         Long managerId, Integer memberCount, Integer activeTasksCount, 
                         LocalDateTime createdAt, Boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.managerName = managerName;
        this.managerId = managerId;
        this.memberCount = memberCount;
        this.activeTasksCount = activeTasksCount;
        this.createdAt = createdAt;
        this.active = active;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Integer getActiveTasksCount() {
        return activeTasksCount;
    }

    public void setActiveTasksCount(Integer activeTasksCount) {
        this.activeTasksCount = activeTasksCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}