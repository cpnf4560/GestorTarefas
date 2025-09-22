package com.gestortarefas.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

/**
 * Entidade que representa o perfil estendido do utilizador
 */
@Entity
@Table(name = "user_profiles")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "profile_picture_path")
    private String profilePicturePath;
    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    
    @Column(name = "department", length = 100)
    private String department;
    
    @Column(name = "job_title", length = 100)
    private String jobTitle;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "location", length = 100)
    private String location;
    
    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Preferências do utilizador
    @Column(name = "email_notifications")
    private Boolean emailNotifications = true;
    
    @Column(name = "theme_preference", length = 20)
    private String themePreference = "LIGHT";
    
    @Column(name = "language_preference", length = 10)
    private String languagePreference = "PT";
    
    // Construtores
    public UserProfile() {}
    
    public UserProfile(User user) {
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
    
    // Métodos de negócio
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Verifica se o utilizador tem foto de perfil
     */
    public boolean hasProfilePicture() {
        return profilePicturePath != null && !profilePicturePath.trim().isEmpty();
    }
    
    /**
     * Obtém o nome do ficheiro da foto de perfil
     */
    public String getProfilePictureFileName() {
        if (!hasProfilePicture()) return null;
        
        String[] parts = profilePicturePath.split("/");
        return parts[parts.length - 1];
    }
    
    /**
     * Define o caminho da foto de perfil
     */
    public void setProfilePicture(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            this.profilePicturePath = null;
        } else {
            this.profilePicturePath = "profiles/" + user.getId() + "/" + fileName;
        }
    }
    
    /**
     * Verifica se as notificações por email estão ativadas
     */
    public boolean isEmailNotificationsEnabled() {
        return emailNotifications != null && emailNotifications;
    }
    
    /**
     * Obtém o nome completo para exibição
     */
    public String getDisplayName() {
        if (user != null && user.getFullName() != null) {
            return user.getFullName();
        }
        return user != null ? user.getUsername() : "Utilizador";
    }
    
    /**
     * Obtém informações de contacto formatadas
     */
    public String getFormattedContactInfo() {
        StringBuilder sb = new StringBuilder();
        
        if (user != null && user.getEmail() != null) {
            sb.append("Email: ").append(user.getEmail());
        }
        
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append("Tel: ").append(phoneNumber);
        }
        
        if (location != null && !location.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append("Localização: ").append(location);
        }
        
        return sb.toString();
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getProfilePicturePath() {
        return profilePicturePath;
    }
    
    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getJobTitle() {
        return jobTitle;
    }
    
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Boolean getEmailNotifications() {
        return emailNotifications;
    }
    
    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }
    
    public String getThemePreference() {
        return themePreference;
    }
    
    public void setThemePreference(String themePreference) {
        this.themePreference = themePreference;
    }
    
    public String getLanguagePreference() {
        return languagePreference;
    }
    
    public void setLanguagePreference(String languagePreference) {
        this.languagePreference = languagePreference;
    }
    
    // Métodos equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfile)) return false;
        UserProfile that = (UserProfile) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : null) +
                ", department='" + department + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                '}';
    }
}