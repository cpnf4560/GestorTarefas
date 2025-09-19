package com.gestortarefas.repository;

import com.gestortarefas.model.Team;
import com.gestortarefas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações CRUD da entidade Team
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
    /**
     * Encontra equipas ativas
     */
    List<Team> findByActiveTrue();
    
    /**
     * Encontra equipas por nome (case-insensitive)
     */
    List<Team> findByNameContainingIgnoreCase(String name);
    
    /**
     * Encontra equipa por nome exato
     */
    Optional<Team> findByNameIgnoreCase(String name);
    
    /**
     * Encontra equipas geridas por um utilizador específico
     */
    List<Team> findByManagerAndActiveTrue(User manager);
    
    /**
     * Encontra equipas das quais o utilizador é membro
     */
    @Query("SELECT t FROM Team t JOIN t.members m WHERE m = :user AND t.active = true")
    List<Team> findTeamsByMember(@Param("user") User user);
    
    /**
     * Encontra equipas que um utilizador pode gerir (é manager ou admin)
     */
    @Query("SELECT DISTINCT t FROM Team t WHERE t.active = true AND " +
           "(t.manager = :user OR :user IN (SELECT u FROM User u WHERE u = :user AND u.role = 'ADMINISTRADOR'))")
    List<Team> findTeamsManageableBy(@Param("user") User user);
    
    /**
     * Conta o número de membros ativos numa equipa
     */
    @Query("SELECT COUNT(m) FROM Team t JOIN t.members m WHERE t.id = :teamId AND m.active = true")
    long countActiveMembersByTeamId(@Param("teamId") Long teamId);
    
    /**
     * Encontra equipas com tarefas em aberto
     */
    @Query("SELECT DISTINCT t FROM Team t JOIN t.tasks task WHERE task.status IN ('PENDENTE', 'EM_ANDAMENTO')")
    List<Team> findTeamsWithOpenTasks();
    
    /**
     * Encontra as equipas mais produtivas (com mais tarefas concluídas)
     */
    @Query("SELECT t FROM Team t JOIN t.tasks task WHERE task.status = 'CONCLUIDA' " +
           "GROUP BY t ORDER BY COUNT(task) DESC")
    List<Team> findMostProductiveTeams();
    
    /**
     * Verifica se uma equipa com o nome já existe
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Encontra equipas sem gerente atribuído
     */
    List<Team> findByManagerIsNullAndActiveTrue();
}