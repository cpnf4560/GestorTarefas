-- Script para atribuir utilizadores às equipas corretas
USE gestortarefas;

-- Primeiro, limpar atribuições existentes
DELETE FROM user_teams;

-- DIREÇÃO (ID: 1)
-- Martim Sottomayor (gestor já está definido) e Catarina Balsemão
INSERT INTO user_teams (user_id, team_id) VALUES 
((SELECT id FROM users WHERE username = 'martim.sottomayor'), 1),
((SELECT id FROM users WHERE username = 'catarina.balsemao'), 1);

-- GESTÃO ADMINISTRATIVA (ID: 2) 
-- Lucile Almeida (gerente); Rita Almeida; Sandra Rocha; Ricardo Leal
INSERT INTO user_teams (user_id, team_id) VALUES 
((SELECT id FROM users WHERE username = 'lucile.almeida'), 2),
((SELECT id FROM users WHERE username = 'rita.almeida'), 2),
((SELECT id FROM users WHERE username = 'sandra.rocha'), 2),
((SELECT id FROM users WHERE username = 'ricardo.leal'), 2);

-- COMERCIAL (ID: 3)
-- Rui Gonçalves (gerente); João Couto; Ana Íris Reis; Inês Rodrigues; Teresa Correia
INSERT INTO user_teams (user_id, team_id) VALUES 
((SELECT id FROM users WHERE username = 'rui.goncalves'), 3),
((SELECT id FROM users WHERE username = 'joao.couto'), 3),
((SELECT id FROM users WHERE username = 'ana.reis'), 3),
((SELECT id FROM users WHERE username = 'ines.rodrigues'), 3),
((SELECT id FROM users WHERE username = 'teresa.correia'), 3);

-- COMPRAS (ID: 4)
-- Diana Brochado (gerente); Tatiana Albuquerque; Rita Oliveira
INSERT INTO user_teams (user_id, team_id) VALUES 
((SELECT id FROM users WHERE username = 'diana.brochado'), 4),
((SELECT id FROM users WHERE username = 'tatiana.albuquerque'), 4),
((SELECT id FROM users WHERE username = 'rita.oliveira'), 4);

-- LOGÍSTICA (ID: 5)
-- António Nolasco (gerente); Vânia Lourença; Anca Tusa
INSERT INTO user_teams (user_id, team_id) VALUES 
((SELECT id FROM users WHERE username = 'antonio.nolasco'), 5),
((SELECT id FROM users WHERE username = 'vania.lourenco'), 5),
((SELECT id FROM users WHERE username = 'anca.tusa'), 5);

-- PRODUÇÃO (ID: 6)
-- Paulo Bessa (gerente); Rogério Silva; Tiago Rodrigues; Muhammad Al-Dossari; Vijay Kumar; Sanita Rahman
INSERT INTO user_teams (user_id, team_id) VALUES 
((SELECT id FROM users WHERE username = 'paulo.bessa'), 6),
((SELECT id FROM users WHERE username = 'rogerio.silva'), 6),
((SELECT id FROM users WHERE username = 'tiago.rodrigues'), 6),
((SELECT id FROM users WHERE username = 'mohammad.aldossari'), 6),
((SELECT id FROM users WHERE username = 'vijay.kumar'), 6),
((SELECT id FROM users WHERE username = 'sanita.rahman'), 6);

-- APOIO AO CLIENTE (ID: 7)
-- Pedro Lopes (gerente); Monica Lewinsky; Cristiana Oliveira
INSERT INTO user_teams (user_id, team_id) VALUES 
((SELECT id FROM users WHERE username = 'pedro.lopes'), 7),
((SELECT id FROM users WHERE username = 'monica.lewinsky'), 7),
((SELECT id FROM users WHERE username = 'cristiana.oliveira'), 7);

-- FINANCEIRO (ID: 8)
-- Bessa Ribeiro (gerente); Carla Silva; Melinda Szekely
INSERT INTO user_teams (user_id, team_id) VALUES 
((SELECT id FROM users WHERE username = 'bessa.ribeiro'), 8),
((SELECT id FROM users WHERE username = 'carla.silva'), 8),
((SELECT id FROM users WHERE username = 'melinda.szekely'), 8);

-- Verificar resultados
SELECT 
    t.name as equipa,
    u.full_name as membro,
    CASE WHEN t.manager_id = u.id THEN '(GERENTE)' ELSE '' END as papel
FROM teams t 
JOIN user_teams ut ON t.id = ut.team_id 
JOIN users u ON ut.user_id = u.id 
ORDER BY t.id, u.full_name;

-- Contagem por equipa
SELECT 
    t.name as equipa,
    COUNT(ut.user_id) as total_membros
FROM teams t 
LEFT JOIN user_teams ut ON t.id = ut.team_id 
GROUP BY t.id, t.name 
ORDER BY t.id;
