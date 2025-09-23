-- =============================================
-- SCRIPTS SQL - UFCD 5425 - GESTOR DE TAREFAS
-- =============================================

-- 1. CRIAR BASE DE DADOS
CREATE DATABASE IF NOT EXISTS gestao 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE gestao;

-- 2. CRIAR UTILIZADOR ESPECÍFICO (OPCIONAL)
-- CREATE USER 'aluno'@'localhost' IDENTIFIED BY 'aluno';
-- GRANT ALL PRIVILEGES ON gestao.* TO 'aluno'@'localhost';
-- FLUSH PRIVILEGES;

-- =============================================
-- 3. ESTRUTURA DAS TABELAS
-- =============================================

-- Tabela de Utilizadores (para login valorizado)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    role ENUM('admin', 'manager', 'user') NOT NULL DEFAULT 'user',
    email VARCHAR(100),
    phone VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabela de Equipas
CREATE TABLE IF NOT EXISTS teams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    manager_id BIGINT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (manager_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Tabela de Tarefas (entidade principal para CRUD)
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status ENUM('PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA') NOT NULL DEFAULT 'PENDENTE',
    priority ENUM('BAIXA', 'MEDIA', 'ALTA') NOT NULL DEFAULT 'MEDIA',
    assigned_user_id BIGINT,
    team_id BIGINT,
    due_date DATE,
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (assigned_user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE SET NULL
);

-- Tabela de Membros das Equipas (relacionamento many-to-many)
CREATE TABLE IF NOT EXISTS team_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_team_member (team_id, user_id),
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabela de Auditoria (Extra E09 - opcional)
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(50) NOT NULL,
    table_name VARCHAR(50) NOT NULL,
    record_id BIGINT,
    old_values JSON,
    new_values JSON,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- =============================================
-- 4. DADOS INICIAIS (SEED DATA)
-- =============================================

-- Utilizadores pré-inseridos (passwords em BCrypt)
INSERT IGNORE INTO users (username, password, name, role, email, active) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye6J6oX7VEr.W0CQ0YJnfNl9FNLqZIIae', 'Administrador', 'admin', 'admin@gestortarefas.com', true),
('manager1', '$2a$10$N9qo8uLOickgx2ZMRZoMye6J6oX7VEr.W0CQ0YJnfNl9FNLqZIIae', 'João Silva', 'manager', 'joao@empresa.com', true),
('user1', '$2a$10$N9qo8uLOickgx2ZMRZoMye6J6oX7VEr.W0CQ0YJnfNl9FNLqZIIae', 'Maria Santos', 'user', 'maria@empresa.com', true),
('user2', '$2a$10$N9qo8uLOickgx2ZMRZoMye6J6oX7VEr.W0CQ0YJnfNl9FNLqZIIae', 'Pedro Costa', 'user', 'pedro@empresa.com', true),
('user3', '$2a$10$N9qo8uLOickgx2ZMRZoMye6J6oX7VEr.W0CQ0YJnfNl9FNLqZIIae', 'Ana Ferreira', 'user', 'ana@empresa.com', true);

-- Equipas de exemplo
INSERT IGNORE INTO teams (name, description, manager_id) VALUES
('Desenvolvimento Web', 'Equipa responsável pelo desenvolvimento de aplicações web', 2),
('Marketing Digital', 'Equipa de marketing e comunicação digital', 2),
('Suporte Técnico', 'Equipa de apoio técnico aos utilizadores', 2);

-- Membros das equipas
INSERT IGNORE INTO team_members (team_id, user_id) VALUES
(1, 3), (1, 4), -- Maria e Pedro na equipa de Desenvolvimento
(2, 5),         -- Ana na equipa de Marketing
(3, 3), (3, 5); -- Maria e Ana na equipa de Suporte

-- Tarefas de exemplo (últimos 5 para o dashboard)
INSERT IGNORE INTO tasks (title, description, status, priority, assigned_user_id, team_id, due_date) VALUES
('Configurar ambiente de desenvolvimento', 'Instalar e configurar ferramentas necessárias', 'CONCLUIDA', 'ALTA', 3, 1, '2024-01-15'),
('Criar mockups da aplicação', 'Desenvolver protótipos das telas principais', 'EM_ANDAMENTO', 'MEDIA', 4, 1, '2024-01-20'),
('Implementar sistema de login', 'Desenvolver autenticação de utilizadores', 'PENDENTE', 'ALTA', 3, 1, '2024-01-25'),
('Testar funcionalidades', 'Realizar testes de integração', 'PENDENTE', 'MEDIA', 4, 1, '2024-01-30'),
('Documentar código', 'Criar documentação técnica', 'PENDENTE', 'BAIXA', 3, 1, '2024-02-05'),
('Campanha redes sociais', 'Criar conteúdo para redes sociais', 'EM_ANDAMENTO', 'MEDIA', 5, 2, '2024-01-18'),
('Atualizar website', 'Renovar conteúdo da página principal', 'PENDENTE', 'BAIXA', 5, 2, '2024-01-22');

-- =============================================
-- 5. ÍNDICES PARA PERFORMANCE
-- =============================================

-- Índices para otimização de consultas
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_assigned_user ON tasks(assigned_user_id);
CREATE INDEX idx_tasks_team ON tasks(team_id);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_tasks_created_at ON tasks(created_at);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(active);

CREATE INDEX idx_teams_manager ON teams(manager_id);
CREATE INDEX idx_teams_active ON teams(active);

-- =============================================
-- 6. VIEWS ÚTEIS PARA RELATÓRIOS
-- =============================================

-- View para estatísticas do dashboard
CREATE OR REPLACE VIEW dashboard_stats AS
SELECT 
    (SELECT COUNT(*) FROM users WHERE active = TRUE) as total_users,
    (SELECT COUNT(*) FROM teams WHERE active = TRUE) as total_teams,
    (SELECT COUNT(*) FROM tasks) as total_tasks,
    (SELECT COUNT(*) FROM tasks WHERE status = 'CONCLUIDA') as completed_tasks,
    (SELECT COUNT(*) FROM tasks WHERE status = 'PENDENTE') as pending_tasks,
    (SELECT COUNT(*) FROM tasks WHERE status = 'EM_ANDAMENTO') as in_progress_tasks;

-- View para últimas tarefas (dashboard requirement)
CREATE OR REPLACE VIEW latest_tasks AS
SELECT 
    t.id,
    t.title,
    t.status,
    t.priority,
    t.created_at,
    u.name as assigned_to,
    tm.name as team_name
FROM tasks t
LEFT JOIN users u ON t.assigned_user_id = u.id
LEFT JOIN teams tm ON t.team_id = tm.id
ORDER BY t.created_at DESC
LIMIT 5;

-- View para exportação completa de tarefas
CREATE OR REPLACE VIEW tasks_export AS
SELECT 
    t.id,
    t.title,
    t.description,
    t.status,
    t.priority,
    u.name as assigned_to,
    tm.name as team_name,
    t.due_date,
    t.created_at,
    t.updated_at
FROM tasks t
LEFT JOIN users u ON t.assigned_user_id = u.id
LEFT JOIN teams tm ON t.team_id = tm.id
ORDER BY t.created_at DESC;

-- =============================================
-- 7. STORED PROCEDURES ÚTEIS (OPCIONAL)
-- =============================================

DELIMITER $$

-- Procedure para obter estatísticas de um utilizador
CREATE PROCEDURE GetUserTaskStats(IN user_id BIGINT)
BEGIN
    SELECT 
        COUNT(*) as total_tasks,
        SUM(CASE WHEN status = 'CONCLUIDA' THEN 1 ELSE 0 END) as completed,
        SUM(CASE WHEN status = 'PENDENTE' THEN 1 ELSE 0 END) as pending,
        SUM(CASE WHEN status = 'EM_ANDAMENTO' THEN 1 ELSE 0 END) as in_progress,
        SUM(CASE WHEN due_date < CURDATE() AND status != 'CONCLUIDA' THEN 1 ELSE 0 END) as overdue
    FROM tasks 
    WHERE assigned_user_id = user_id;
END$$

-- Procedure para obter estatísticas de uma equipa
CREATE PROCEDURE GetTeamTaskStats(IN team_id BIGINT)
BEGIN
    SELECT 
        t.name as team_name,
        COUNT(ta.id) as total_tasks,
        SUM(CASE WHEN ta.status = 'CONCLUIDA' THEN 1 ELSE 0 END) as completed,
        SUM(CASE WHEN ta.status = 'PENDENTE' THEN 1 ELSE 0 END) as pending,
        SUM(CASE WHEN ta.status = 'EM_ANDAMENTO' THEN 1 ELSE 0 END) as in_progress
    FROM teams t
    LEFT JOIN tasks ta ON t.id = ta.team_id
    WHERE t.id = team_id
    GROUP BY t.id, t.name;
END$$

DELIMITER ;

-- =============================================
-- 8. COMANDOS PARA VERIFICAÇÃO
-- =============================================

-- Verificar se as tabelas foram criadas
-- SHOW TABLES;

-- Verificar estrutura das tabelas principais
-- DESCRIBE users;
-- DESCRIBE tasks;
-- DESCRIBE teams;

-- Verificar dados inseridos
-- SELECT COUNT(*) as total_users FROM users;
-- SELECT COUNT(*) as total_tasks FROM tasks;
-- SELECT COUNT(*) as total_teams FROM teams;

-- Testar view do dashboard
-- SELECT * FROM dashboard_stats;
-- SELECT * FROM latest_tasks;

-- =============================================
-- 9. LIMPEZA (PARA DESENVOLVIMENTO)
-- =============================================

-- ATENÇÃO: Usar apenas em ambiente de desenvolvimento
-- DROP DATABASE IF EXISTS gestao;

-- =============================================
-- FIM DOS SCRIPTS SQL
-- =============================================

/*
NOTAS IMPORTANTES:

1. PASSWORDS PRÉ-INSERIDAS:
   - Todas as passwords são: "admin123", "manager123", "user123"
   - Hash BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMye6J6oX7VEr.W0CQ0YJnfNl9FNLqZIIae

2. ESTRUTURA:
   - Tabela 'users' para login valorizado (requirement 1)
   - Tabela 'tasks' como entidade principal para CRUD
   - Views para dashboard e relatórios
   - Índices para performance

3. DADOS DE TESTE:
   - 5 utilizadores (1 admin, 1 manager, 3 users)
   - 3 equipas com membros
   - 7 tarefas para demonstração
   - Dados para dashboard (últimos 5 registos)

4. COMPATIBILIDADE:
   - MySQL 8.0+
   - MariaDB 10.3+
   - Prepared Statements compatíveis
   
5. ENCODING:
   - UTF8MB4 para suporte completo de caracteres
   - Collation unicode para ordenação correta
*/