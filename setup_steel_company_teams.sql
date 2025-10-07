-- ======================================================================
-- SCRIPT DE CONFIGURAÇÃO COMPLETA DAS EQUIPAS DA EMPRESA DE TUBOS DE AÇO  
-- ======================================================================
-- Este script define a estrutura organizacional completa com:
-- - 8 equipas especializadas
-- - Gestores corretos para cada equipa  
-- - Atribuição de todos os 30 utilizadores às equipas
-- ======================================================================

USE gestortarefas;

-- ======================================================================
-- PASSO 1: DEFINIR GESTORES DAS EQUIPAS
-- ======================================================================
UPDATE teams SET manager_id = (SELECT id FROM users WHERE username = 'martim.sottomayor') WHERE name = 'Direção';
UPDATE teams SET manager_id = (SELECT id FROM users WHERE username = 'lucile.almeida') WHERE name = 'Gestão Administrativa';
UPDATE teams SET manager_id = (SELECT id FROM users WHERE username = 'bessa.ribeiro') WHERE name = 'Comercial';
UPDATE teams SET manager_id = (SELECT id FROM users WHERE username = 'diana.brochado') WHERE name = 'Compras';
UPDATE teams SET manager_id = (SELECT id FROM users WHERE username = 'antonio.nolasco') WHERE name = 'Logística';
UPDATE teams SET manager_id = (SELECT id FROM users WHERE username = 'paulo.bessa') WHERE name = 'Produção';
UPDATE teams SET manager_id = (SELECT id FROM users WHERE username = 'pedro.lopes') WHERE name = 'Apoio ao Cliente';
UPDATE teams SET manager_id = (SELECT id FROM users WHERE username = 'rui.goncalves') WHERE name = 'Financeiro';

-- ======================================================================
-- PASSO 2: LIMPAR ATRIBUIÇÕES EXISTENTES E CRIAR AS CORRETAS
-- ======================================================================
DELETE FROM user_teams;

-- DIREÇÃO (2 membros)
-- Gestor: Martim Sottomayor + Catarina Balsemão
INSERT INTO user_teams (user_id, team_id) VALUES 
((SELECT id FROM users WHERE username = 'martim.sottomayor'), (SELECT id FROM teams WHERE name = 'Direção')),
((SELECT id FROM users WHERE username = 'catarina.balsemao'), (SELECT id FROM teams WHERE name = 'Direção'));

-- GESTÃO ADMINISTRATIVA (4 membros)
-- Gerente: Lucile Almeida + Rita Almeida + Sandra Rocha + Ricardo Leal
INSERT INTO user_teams (user_id, team_id) VALUES 
((SELECT id FROM users WHERE username = 'lucile.almeida'), (SELECT id FROM teams WHERE name = 'Gestão Administrativa')),
((SELECT id FROM users WHERE username = 'rita.almeida'), (SELECT id FROM teams WHERE name = 'Gestão Administrativa')),
((SELECT id FROM users WHERE username = 'sandra.rocha'), (SELECT id FROM teams WHERE name = 'Gestão Administrativa')),
((SELECT id FROM users WHERE username = 'ricardo.leal'), (SELECT id FROM teams WHERE name = 'Gestão Administrativa'));

-- COMERCIAL (5 membros)
-- Gerente: Bessa Ribeiro + João Couto + Ana Íris Reis + Inês Rodrigues + Teresa Correia
INSERT INTO user_teams (user_id, team_id) VALUES 
((SELECT id FROM users WHERE username = 'bessa.ribeiro'), (SELECT id FROM teams WHERE name = 'Comercial')),
((SELECT id FROM users WHERE username = 'joao.couto'), (SELECT id FROM teams WHERE name = 'Comercial')),
((SELECT id FROM users WHERE username = 'ana.reis'), (SELECT id FROM teams WHERE name = 'Comercial')),
((SELECT id FROM users WHERE username = 'ines.rodrigues'), (SELECT id FROM teams WHERE name = 'Comercial')),
((SELECT id FROM users WHERE username = 'teresa.correia'), (SELECT id FROM teams WHERE name = 'Comercial'));

-- COMPRAS (3 membros)
-- Gerente: Diana Brochado + Tatiana Albuquerque + Rita Oliveira  
INSERT INTO user_teams (user_id, team_id) VALUES 
((SELECT id FROM users WHERE username = 'diana.brochado'), (SELECT id FROM teams WHERE name = 'Compras')),
((SELECT id FROM users WHERE username = 'tatiana.albuquerque'), (SELECT id FROM teams WHERE name = 'Compras')),
((SELECT id FROM users WHERE username = 'rita.oliveira'), (SELECT id FROM teams WHERE name = 'Compras'));

-- LOGÍSTICA (3 membros)
-- Gerente: António Nolasco + Vânia Lourença + Anca Tusa
INSERT INTO user_teams (user_id, team_id) VALUES 
((SELECT id FROM users WHERE username = 'antonio.nolasco'), (SELECT id FROM teams WHERE name = 'Logística')),
((SELECT id FROM users WHERE username = 'vania.lourenco'), (SELECT id FROM teams WHERE name = 'Logística')),
((SELECT id FROM users WHERE username = 'anca.tusa'), (SELECT id FROM teams WHERE name = 'Logística'));

-- PRODUÇÃO (6 membros)
-- Gerente: Paulo Bessa + Rogério Silva + Tiago Rodrigues + Muhammad Al-Dossari + Vijay Kumar + Sanita Rahman
INSERT INTO user_teams (user_id, team_id) VALUES 
((SELECT id FROM users WHERE username = 'paulo.bessa'), (SELECT id FROM teams WHERE name = 'Produção')),
((SELECT id FROM users WHERE username = 'rogerio.silva'), (SELECT id FROM teams WHERE name = 'Produção')),
((SELECT id FROM users WHERE username = 'tiago.rodrigues'), (SELECT id FROM teams WHERE name = 'Produção')),
((SELECT id FROM users WHERE username = 'mohammad.aldossari'), (SELECT id FROM teams WHERE name = 'Produção')),
((SELECT id FROM users WHERE username = 'vijay.kumar'), (SELECT id FROM teams WHERE name = 'Produção')),
((SELECT id FROM users WHERE username = 'sanita.rahman'), (SELECT id FROM teams WHERE name = 'Produção'));

-- APOIO AO CLIENTE (3 membros)
-- Gerente: Pedro Lopes + Monica Lewinsky + Cristiana Oliveira
INSERT INTO user_teams (user_id, team_id) VALUES 
((SELECT id FROM users WHERE username = 'pedro.lopes'), (SELECT id FROM teams WHERE name = 'Apoio ao Cliente')),
((SELECT id FROM users WHERE username = 'monica.lewinsky'), (SELECT id FROM teams WHERE name = 'Apoio ao Cliente')),
((SELECT id FROM users WHERE username = 'cristiana.oliveira'), (SELECT id FROM teams WHERE name = 'Apoio ao Cliente'));

-- FINANCEIRO (3 membros)  
-- Gerente: Rui Gonçalves + Carla Silva + Melinda Szekely
INSERT INTO user_teams (user_id, team_id) VALUES 
((SELECT id FROM users WHERE username = 'rui.goncalves'), (SELECT id FROM teams WHERE name = 'Financeiro')),
((SELECT id FROM users WHERE username = 'carla.silva'), (SELECT id FROM teams WHERE name = 'Financeiro')),
((SELECT id FROM users WHERE username = 'melinda.szekely'), (SELECT id FROM teams WHERE name = 'Financeiro'));

-- ======================================================================
-- PASSO 3: VERIFICAÇÃO DOS RESULTADOS
-- ======================================================================
SELECT '=== EQUIPAS COM GESTORES E MEMBROS ===' as info;
SELECT 
    t.name as equipa,
    u.full_name as membro,
    CASE WHEN t.manager_id = u.id THEN '(GERENTE)' ELSE '' END as papel
FROM teams t 
JOIN user_teams ut ON t.id = ut.team_id 
JOIN users u ON ut.user_id = u.id 
ORDER BY t.id, CASE WHEN t.manager_id = u.id THEN 0 ELSE 1 END, u.full_name;

SELECT '=== RESUMO POR EQUIPA ===' as info;
SELECT 
    t.name as equipa,
    (SELECT u.full_name FROM users u WHERE u.id = t.manager_id) as gestor,
    COUNT(ut.user_id) as total_membros
FROM teams t 
LEFT JOIN user_teams ut ON t.id = ut.team_id 
GROUP BY t.id, t.name 
ORDER BY t.id;