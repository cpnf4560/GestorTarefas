package com.gestortarefas.repository;

import com.gestortarefas.model.User;
import com.gestortarefas.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações CRUD da entidade UserProfile
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    /**
     * Encontra perfil por utilizador
     */
    Optional<UserProfile> findByUser(User user);
    
    /**
     * Encontra perfil por ID do utilizador
     */
    Optional<UserProfile> findByUserId(Long userId);
    
    /**
     * Encontra perfis por departamento
     */
    List<UserProfile> findByDepartmentIgnoreCase(String department);
    
    /**
     * Encontra perfis por cargo
     */
    List<UserProfile> findByJobTitleContainingIgnoreCase(String jobTitle);
    
    /**
     * Encontra perfis por localização
     */
    List<UserProfile> findByLocationIgnoreCase(String location);
    
    /**
     * Encontra perfis com foto de perfil
     */
    List<UserProfile> findByProfilePicturePathIsNotNull();
    
    /**
     * Encontra perfis sem foto de perfil
     */
    List<UserProfile> findByProfilePicturePathIsNull();
    
    /**
     * Conta perfis por departamento
     */
    @Query("SELECT p.department, COUNT(p) FROM UserProfile p WHERE p.department IS NOT NULL GROUP BY p.department")
    List<Object[]> countByDepartment();
    
    /**
     * Encontra perfis com notificações por email ativadas
     */
    List<UserProfile> findByEmailNotificationsTrue();
    
    /**
     * Encontra perfis por preferência de tema
     */
    List<UserProfile> findByThemePreference(String themePreference);
    
    /**
     * Encontra perfis por preferência de idioma
     */
    List<UserProfile> findByLanguagePreference(String languagePreference);
    
    /**
     * Procura perfis por texto na biografia
     */
    List<UserProfile> findByBioContainingIgnoreCase(String searchText);
    
    /**
     * Verifica se um utilizador já tem perfil
     */
    boolean existsByUser(User user);
    
    /**
     * Verifica se um utilizador tem perfil por ID
     */
    boolean existsByUserId(Long userId);
}