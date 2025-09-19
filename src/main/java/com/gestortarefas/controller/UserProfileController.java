package com.gestortarefas.controller;

import com.gestortarefas.model.User;
import com.gestortarefas.model.UserProfile;
import com.gestortarefas.service.UserProfileService;
import com.gestortarefas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestão de perfis de utilizador
 */
@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "*")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserService userService;

    /**
     * Lista todos os perfis
     */
    @GetMapping
    public ResponseEntity<List<UserProfile>> getAllProfiles() {
        try {
            List<UserProfile> profiles = userProfileService.getAllProfiles();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca perfil por ID do utilizador
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserProfile> getProfileByUserId(@PathVariable Long userId) {
        try {
            UserProfile profile = userProfileService.getProfileByUserId(userId);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cria um perfil para um utilizador
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createProfile(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            UserProfile profile = userProfileService.createProfile(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(profile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Atualiza informações do perfil
     */
    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId,
                                         @RequestBody ProfileUpdateRequest request,
                                         @RequestHeader("User-Id") Long requesterId) {
        try {
            User requester = userService.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            
            UserProfile profile = userProfileService.updateProfile(
                userId, 
                request.getJobTitle(), 
                request.getBio(),
                request.getPhoneNumber(), 
                request.getDepartment(), 
                request.getLocation(),
                requester
            );
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Upload de foto de perfil
     */
    @PostMapping(value = "/user/{userId}/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfilePicture(@PathVariable Long userId,
                                                 @RequestParam("file") MultipartFile file,
                                                 @RequestHeader("User-Id") Long requesterId) {
        try {
            User requester = userService.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            
            UserProfile profile = userProfileService.updateProfilePicture(userId, file, requester);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erro ao fazer upload da foto: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Remove foto de perfil
     */
    @DeleteMapping("/user/{userId}/picture")
    public ResponseEntity<?> removeProfilePicture(@PathVariable Long userId,
                                                 @RequestHeader("User-Id") Long requesterId) {
        try {
            User requester = userService.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            
            UserProfile profile = userProfileService.removeProfilePicture(userId, requester);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Atualiza preferências do utilizador
     */
    @PutMapping("/user/{userId}/preferences")
    public ResponseEntity<?> updatePreferences(@PathVariable Long userId,
                                             @RequestBody PreferencesUpdateRequest request,
                                             @RequestHeader("User-Id") Long requesterId) {
        try {
            User requester = userService.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
            
            UserProfile profile = userProfileService.updatePreferences(
                userId, 
                request.getTheme(), 
                request.getLanguage(),
                request.getEmailNotifications(),
                requester
            );
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca perfis por departamento
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<List<UserProfile>> getProfilesByDepartment(@PathVariable String department) {
        try {
            List<UserProfile> profiles = userProfileService.getProfilesByDepartment(department);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca perfis com fotos
     */
    @GetMapping("/with-pictures")
    public ResponseEntity<List<UserProfile>> getProfilesWithPictures() {
        try {
            List<UserProfile> profiles = userProfileService.getProfilesWithPictures();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca perfis por cargo
     */
    @GetMapping("/search/job-title")
    public ResponseEntity<List<UserProfile>> searchProfilesByJobTitle(@RequestParam String jobTitle) {
        try {
            List<UserProfile> profiles = userProfileService.searchProfilesByJobTitle(jobTitle);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Classes para requests
    public static class ProfileUpdateRequest {
        private String jobTitle;
        private String bio;
        private String phoneNumber;
        private String department;
        private String location;

        // Getters e Setters
        public String getJobTitle() { return jobTitle; }
        public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }

    public static class PreferencesUpdateRequest {
        private String theme;
        private String language;
        private Boolean emailNotifications;

        // Getters e Setters
        public String getTheme() { return theme; }
        public void setTheme(String theme) { this.theme = theme; }

        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }

        public Boolean getEmailNotifications() { return emailNotifications; }
        public void setEmailNotifications(Boolean emailNotifications) { this.emailNotifications = emailNotifications; }
    }
}