-- Script SQL para adicionar funcionalidades de comentários e atribuição específica
-- Execute na base de dados gestortarefas

-- 1. Criar tabela para comentários das tarefas (sistema de chat)
CREATE TABLE IF NOT EXISTS task_comments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    comment_text TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    is_system_message BOOLEAN NOT NULL DEFAULT FALSE,
    
    PRIMARY KEY (id),
    
    -- Chaves estrangeiras
    CONSTRAINT fk_task_comment_task 
        FOREIGN KEY (task_id) REFERENCES tasks(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT fk_task_comment_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE,
    
    -- Índices para performance
    INDEX idx_task_comments_task_id (task_id),
    INDEX idx_task_comments_user_id (user_id),
    INDEX idx_task_comments_created_at (created_at),
    INDEX idx_task_comments_task_created (task_id, created_at)
);

-- 2. Adicionar coluna assigned_by_user_id à tabela tasks para rastrear quem atribuiu
ALTER TABLE tasks 
ADD COLUMN assigned_by_user_id BIGINT NULL AFTER created_by_user_id,
ADD CONSTRAINT fk_task_assigned_by_user 
    FOREIGN KEY (assigned_by_user_id) REFERENCES users(id) 
    ON DELETE SET NULL;

-- 3. Adicionar índice para o novo campo
ALTER TABLE tasks 
ADD INDEX idx_tasks_assigned_by_user (assigned_by_user_id);

-- 4. Comentários iniciais do sistema para tarefas existentes (opcional)
INSERT INTO task_comments (task_id, user_id, comment_text, is_system_message, created_at)
SELECT 
    t.id,
    t.created_by_user_id,
    CONCAT('Tarefa criada por ', u.full_name),
    TRUE,
    t.created_at
FROM tasks t
INNER JOIN users u ON t.created_by_user_id = u.id
WHERE t.created_by_user_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM task_comments tc 
      WHERE tc.task_id = t.id AND tc.is_system_message = TRUE
  );

-- 5. Verificar estrutura das tabelas criadas
DESCRIBE task_comments;
DESCRIBE tasks;

-- 6. Verificar dados de exemplo
SELECT 
    tc.id,
    tc.task_id,
    t.title as task_title,
    u.full_name as user_name,
    tc.comment_text,
    tc.created_at,
    tc.is_system_message
FROM task_comments tc
INNER JOIN tasks t ON tc.task_id = t.id
INNER JOIN users u ON tc.user_id = u.id
ORDER BY tc.created_at DESC
LIMIT 10;