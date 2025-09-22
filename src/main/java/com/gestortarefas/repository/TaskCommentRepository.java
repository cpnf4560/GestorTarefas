package com.gestortarefas.repository;

import com.gestortarefas.model.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para operações de base de dados relacionadas aos comentários de tarefas
 */
@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {

    /**
     * Encontra todos os comentários de uma tarefa ordenados por data (mais recentes primeiro)
     */
    List<TaskComment> findByTaskIdOrderByCreatedAtDesc(Long taskId);

    /**
     * Encontra todos os comentários de uma tarefa ordenados por data (mais antigos primeiro)
     */
    List<TaskComment> findByTaskIdOrderByCreatedAtAsc(Long taskId);

    /**
     * Encontra comentários de uma tarefa de um utilizador específico
     */
    List<TaskComment> findByTaskIdAndUserIdOrderByCreatedAtDesc(Long taskId, Long userId);

    /**
     * Encontra os comentários mais recentes de uma tarefa (limite)
     */
    @Query("SELECT c FROM TaskComment c WHERE c.task.id = :taskId ORDER BY c.createdAt DESC")
    List<TaskComment> findRecentCommentsByTaskId(@Param("taskId") Long taskId, 
                                                  org.springframework.data.domain.Pageable pageable);

    /**
     * Conta o número total de comentários de uma tarefa
     */
    long countByTaskId(Long taskId);

    /**
     * Encontra comentários de uma tarefa num período específico
     */
    List<TaskComment> findByTaskIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long taskId, 
                                                                          LocalDateTime startDate, 
                                                                          LocalDateTime endDate);

    /**
     * Encontra comentários do sistema (mensagens automáticas)
     */
    List<TaskComment> findByTaskIdAndIsSystemMessageTrueOrderByCreatedAtDesc(Long taskId);

    /**
     * Encontra comentários de utilizadores (não são mensagens do sistema)
     */
    List<TaskComment> findByTaskIdAndIsSystemMessageFalseOrderByCreatedAtDesc(Long taskId);

    /**
     * Encontra todos os comentários de um utilizador em todas as tarefas
     */
    List<TaskComment> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Verifica se existe pelo menos um comentário de um utilizador numa tarefa
     */
    boolean existsByTaskIdAndUserId(Long taskId, Long userId);

    /**
     * Encontra o último comentário de uma tarefa
     */
    @Query("SELECT c FROM TaskComment c WHERE c.task.id = :taskId ORDER BY c.createdAt DESC")
    List<TaskComment> findLastCommentByTaskId(@Param("taskId") Long taskId, 
                                              org.springframework.data.domain.Pageable pageable);

    /**
     * Elimina todos os comentários de uma tarefa específica
     */
    void deleteByTaskId(Long taskId);

    /**
     * Elimina comentários antigos de uma tarefa (manter apenas os N mais recentes)
     */
    @Query("DELETE FROM TaskComment c WHERE c.task.id = :taskId AND c.id NOT IN " +
           "(SELECT c2.id FROM TaskComment c2 WHERE c2.task.id = :taskId " +
           "ORDER BY c2.createdAt DESC LIMIT :keepCount)")
    void deleteOldCommentsKeepRecent(@Param("taskId") Long taskId, @Param("keepCount") int keepCount);

    default TaskComment findLastCommentByTask(Long taskId) {
        List<TaskComment> comments = findLastCommentByTaskId(taskId, 
                org.springframework.data.domain.PageRequest.of(0, 1));
        return comments.isEmpty() ? null : comments.get(0);
    }

    default List<TaskComment> findRecentCommentsByTask(Long taskId, int limit) {
        return findRecentCommentsByTaskId(taskId, 
                org.springframework.data.domain.PageRequest.of(0, limit));
    }
}