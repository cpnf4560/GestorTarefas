-- Inserir apenas algumas tarefas de teste primeiro
INSERT INTO tasks (title, description, status, priority, user_id, created_at, updated_at, due_date, tags, estimated_hours) VALUES
('Planeamento Estratégico 2025', 'Definir objetivos estratégicos da empresa para o próximo ano', 'PENDENTE', 'ALTA', 67, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 15 DAY), 'estrategia', 40),
('Reunião Investidores', 'Apresentar resultados do trimestre', 'EM_ANDAMENTO', 'URGENTE', 68, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'investidores', 8),
('Proposta Cliente ABC', 'Elaborar proposta comercial para 500 toneladas de tubos', 'PENDENTE', 'URGENTE', 53, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 2 DAY), 'proposta', 4);