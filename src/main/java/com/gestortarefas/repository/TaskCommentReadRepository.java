package com.gestortarefas.repository;

import com.gestortarefas.model.TaskCommentRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações relacionadas ao rastreamento de leitura de comentários
 */
@Repository
public interface TaskCommentReadRepository extends JpaRepository<TaskCommentRead, Long> {

    /**
     * Encontra o registro de leitura para um usuário específico e uma tarefa específica
     */
    Optional<TaskCommentRead> findByTaskIdAndUserId(Long taskId, Long userId);

    /**
     * Encontra todos os registros de leitura de um usuário
     */
    List<TaskCommentRead> findByUserId(Long userId);

    /**
     * Encontra todos os registros de leitura de uma tarefa
     */
    List<TaskCommentRead> findByTaskId(Long taskId);

    /**
     * Verifica se existe um registro de leitura para um usuário e tarefa
     */
    boolean existsByTaskIdAndUserId(Long taskId, Long userId);

    /**
     * Conta comentários não lidos de uma tarefa para um usuário específico
     * Retorna o número de comentários criados após a última leitura do usuário
     */
    @Query("SELECT COUNT(c) FROM TaskComment c " +
           "WHERE c.task.id = :taskId " +
           "AND c.createdAt > COALESCE(" +
           "    (SELECT tcr.lastReadAt FROM TaskCommentRead tcr " +
           "     WHERE tcr.task.id = :taskId AND tcr.user.id = :userId), " +
           "    '1900-01-01 00:00:00'" +
           ")")
    long countUnreadCommentsByTaskAndUser(@Param("taskId") Long taskId, @Param("userId") Long userId);

    /**
     * Obtém contagem de comentários não lidos para múltiplas tarefas de um usuário
     */
    @Query("SELECT c.task.id, COUNT(c) FROM TaskComment c " +
           "WHERE c.task.id IN :taskIds " +
           "AND c.createdAt > COALESCE(" +
           "    (SELECT tcr.lastReadAt FROM TaskCommentRead tcr " +
           "     WHERE tcr.task.id = c.task.id AND tcr.user.id = :userId), " +
           "    '1900-01-01 00:00:00'" +
           ") " +
           "GROUP BY c.task.id")
    List<Object[]> countUnreadCommentsByTasksAndUser(@Param("taskIds") List<Long> taskIds, @Param("userId") Long userId);

    /**
     * Remove registros de leitura antigos (mais de X dias)
     */
    @Query("DELETE FROM TaskCommentRead tcr WHERE tcr.lastReadAt < :cutoffDate")
    void deleteOldReadRecords(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
}