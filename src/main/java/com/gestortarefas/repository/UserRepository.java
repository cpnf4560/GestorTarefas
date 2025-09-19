package com.gestortarefas.repository;

import com.gestortarefas.model.User;
import com.gestortarefas.model.UserRole;
import com.gestortarefas.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de base de dados relacionadas aos utilizadores
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca utilizador por nome de utilizador
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca utilizador por email
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca utilizador por nome de utilizador ou email
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    /**
     * Verifica se existe utilizador com determinado nome de utilizador
     */
    boolean existsByUsername(String username);

    /**
     * Verifica se existe utilizador com determinado email
     */
    boolean existsByEmail(String email);

    /**
     * Busca todos os utilizadores ativos
     */
    List<User> findByActiveTrue();

    /**
     * Conta utilizadores ativos
     */
    long countByActiveTrue();

    /**
     * Busca utilizadores por nome completo (contém)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByFullNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Busca utilizadores com tarefas
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.tasks t WHERE u.active = true")
    List<User> findUsersWithTasks();

    /**
     * Conta número de utilizadores ativos
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
    long countActiveUsers();
    
    /**
     * Encontra utilizadores por role
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Encontra utilizadores ativos por role
     */
    List<User> findByRoleAndActiveTrue(UserRole role);
    
    /**
     * Encontra gerentes ativos
     */
    @Query("SELECT u FROM User u WHERE u.role IN ('GERENTE', 'ADMINISTRADOR') AND u.active = true")
    List<User> findActiveManagers();
    
    /**
     * Encontra membros de uma equipa específica
     */
    @Query("SELECT u FROM User u JOIN u.teams t WHERE t = :team AND u.active = true")
    List<User> findActiveTeamMembers(@Param("team") Team team);
    
    /**
     * Encontra utilizadores que não pertencem a nenhuma equipa
     */
    @Query("SELECT u FROM User u WHERE u.active = true AND u.teams IS EMPTY")
    List<User> findUsersWithoutTeams();
    
    /**
     * Encontra utilizadores que podem gerir equipas
     */
    @Query("SELECT u FROM User u WHERE u.role IN ('GERENTE', 'ADMINISTRADOR') AND u.active = true")
    List<User> findUsersWhoCanManageTeams();
    
    /**
     * Conta utilizadores por role
     */
    @Query("SELECT u.role, COUNT(u) FROM User u WHERE u.active = true GROUP BY u.role")
    List<Object[]> countUsersByRole();
}