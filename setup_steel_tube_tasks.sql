-- ========================================
-- SETUP TAREFAS PARA EMPRESA DE TUBOS DE AÇO
-- ========================================

-- Primeiro, eliminar todas as tarefas existentes
DELETE FROM task_comments WHERE task_id IN (SELECT id FROM tasks);
DELETE FROM tasks;

-- Reiniciar o AUTO_INCREMENT 
ALTER TABLE tasks AUTO_INCREMENT = 1;

-- ========================================
-- TAREFAS POR EQUIPA
-- ========================================

-- ============ DIREÇÃO (ID: 18) ============
INSERT INTO tasks (title, description, status, priority, user_id, created_at, updated_at, due_date, tags, estimated_hours) VALUES
('Planeamento Estratégico 2025', 'Definir objetivos estratégicos da empresa para o próximo ano, incluindo metas de vendas e expansão de mercado', 'PENDENTE', 'ALTA', 67, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 15 DAY), 'estrategia,planeamento', 40),
('Reunião com Investidores', 'Apresentar resultados do trimestre e discutir planos de investimento em novas linhas de produção', 'EM_ANDAMENTO', 'URGENTE', 68, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'investidores,financeiro', 8),
('Certificação ISO 9001', 'Coordenar processo de renovação da certificação ISO 9001 para garantir qualidade dos tubos de aço', 'PENDENTE', 'ALTA', 67, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 45 DAY), 'certificacao,qualidade', 60);

-- ============ GESTÃO ADMINISTRATIVA (ID: 19) ============
INSERT INTO tasks (title, description, status, priority, user_id, created_at, updated_at, due_date, tags, estimated_hours) VALUES
('Implementação Sistema ERP', 'Supervisionar implementação do novo sistema ERP para gestão integrada da empresa', 'EM_ANDAMENTO', 'ALTA', 41, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), 'erp,sistema,gestao', 120),
('Auditoria Anual Contabilidade', 'Coordenar auditoria anual dos registos contabilísticos e fiscais da empresa', 'PENDENTE', 'NORMAL', 41, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 'auditoria,contabilidade', 24),
('Políticas de Recursos Humanos', 'Atualizar manual de políticas de RH incluindo procedimentos de segurança na produção', 'PENDENTE', 'NORMAL', 41, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 20 DAY), 'rh,politicas,seguranca', 16);

-- ============ COMERCIAL (ID: 20) ============
INSERT INTO tasks (title, description, status, priority, user_id, created_at, updated_at, due_date, tags, estimated_hours) VALUES
('Prospecção Mercado Construção Civil', 'Identificar e contactar potenciais clientes no setor da construção civil para tubos estruturais', 'EM_ANDAMENTO', 'ALTA', 53, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 10 DAY), 'prospeccao,construcao', 20),
('Catálogo Produtos 2025', 'Criar novo catálogo com especificações técnicas de todos os tipos de tubos de aço disponíveis', 'PENDENTE', 'NORMAL', 71, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 25 DAY), 'catalogo,marketing', 30),
('Proposta Cliente ABC Construções', 'Elaborar proposta comercial detalhada para fornecimento de 500 toneladas de tubos galvanizados', 'URGENTE', 'URGENTE', 53, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 2 DAY), 'proposta,cliente', 4),
('Feira Internacional do Aço', 'Organizar participação na feira internacional do aço em Madrid - stand e apresentações', 'PENDENTE', 'NORMAL', 71, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), 'feira,internacional', 50);

-- ============ COMPRAS (ID: 21) ============
INSERT INTO tasks (title, description, status, priority, user_id, created_at, updated_at, due_date, tags, estimated_hours) VALUES
('Negociação Fornecedores Aço', 'Renegociar contratos com fornecedores de aço bruto para reduzir custos em 15%', 'EM_ANDAMENTO', 'ALTA', 42, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), 'fornecedores,negociacao', 25),
('Qualificação Novos Fornecedores', 'Avaliar e qualificar novos fornecedores de matéria-prima na Europa de Leste', 'PENDENTE', 'NORMAL', 42, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 40 DAY), 'fornecedores,qualificacao', 35),
('Compra Equipamentos Galvanização', 'Processar compra de novos equipamentos para linha de galvanização dos tubos', 'URGENTE', 'URGENTE', 42, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 5 DAY), 'equipamentos,galvanizacao', 8);

-- ============ LOGÍSTICA (ID: 22) ============
INSERT INTO tasks (title, description, status, priority, user_id, created_at, updated_at, due_date, tags, estimated_hours) VALUES
('Otimização Rotas Distribuição', 'Analisar e otimizar rotas de distribuição para reduzir custos de transporte em 20%', 'PENDENTE', 'ALTA', NULL, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 21 DAY), 'distribuicao,otimizacao', 28),
('Sistema Rastreamento Cargas', 'Implementar sistema de rastreamento GPS para todos os camiões de distribuição', 'EM_ANDAMENTO', 'NORMAL', NULL, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 35 DAY), 'rastreamento,tecnologia', 40),
('Ampliação Armazém Principal', 'Coordenar obras de ampliação do armazém para acomodar aumento de 40% no stock', 'PENDENTE', 'ALTA', NULL, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 120 DAY), 'armazem,ampliacao', 80);

-- ============ PRODUÇÃO (ID: 23) ============
INSERT INTO tasks (title, description, status, priority, user_id, created_at, updated_at, due_date, tags, estimated_hours) VALUES
('Manutenção Preventiva Máquinas', 'Executar manutenção preventiva em todas as máquinas de corte e soldadura', 'PENDENTE', 'URGENTE', 61, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY), 'manutencao,maquinas', 16),
('Linha Produção Tubos Inox', 'Configurar nova linha de produção especializada em tubos de aço inoxidável', 'EM_ANDAMENTO', 'ALTA', 63, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 45 DAY), 'inox,linha-producao', 60),
('Controle Qualidade Soldaduras', 'Implementar novos procedimentos de controle de qualidade para soldaduras dos tubos', 'PENDENTE', 'ALTA', 61, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 18 DAY), 'qualidade,soldadura', 24),
('Produção Lote Especial', 'Produzir lote especial de 200 tubos com especificações personalizadas para cliente VIP', 'URGENTE', 'URGENTE', 63, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 4 DAY), 'lote-especial,vip', 12);

-- ============ APOIO AO CLIENTE (ID: 24) ============
INSERT INTO tasks (title, description, status, priority, user_id, created_at, updated_at, due_date, tags, estimated_hours) VALUES
('Manual Técnico Instalação', 'Criar manual técnico detalhado para instalação de tubos estruturais em construções', 'PENDENTE', 'NORMAL', 46, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 'manual,instalacao', 20),
('Formação Equipa Suporte', 'Formar equipa de suporte técnico sobre novas especificações de tubos galvanizados', 'EM_ANDAMENTO', 'NORMAL', 66, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), 'formacao,suporte', 16),
('Sistema Tickets Online', 'Implementar sistema online para gestão de tickets de suporte e reclamações', 'PENDENTE', 'ALTA', 46, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 40 DAY), 'sistema,tickets', 35),
('Resolução Reclamação Urgente', 'Resolver reclamação urgente sobre qualidade de tubos fornecidos à empresa XYZ', 'URGENTE', 'URGENTE', 66, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY), 'reclamacao,qualidade', 6);

-- ============ FINANCEIRO (ID: 25) ============
INSERT INTO tasks (title, description, status, priority, user_id, created_at, updated_at, due_date, tags, estimated_hours) VALUES
('Análise Fluxo de Caixa', 'Analisar projeção de fluxo de caixa para próximos 6 meses considerando novos investimentos', 'EM_ANDAMENTO', 'ALTA', 42, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 12 DAY), 'fluxo-caixa,analise', 18),
('Relatório Rentabilidade Produtos', 'Gerar relatório detalhado de rentabilidade por tipo de tubo produzido', 'PENDENTE', 'NORMAL', 42, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 20 DAY), 'rentabilidade,relatorio', 12),
('Negociação Financiamento', 'Negociar condições de financiamento bancário para expansão da fábrica', 'URGENTE', 'URGENTE', 42, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'financiamento,banco', 10);

-- ========================================
-- INFORMAÇÕES ADICIONAIS
-- ========================================
-- Total de tarefas criadas: ~30 tarefas
-- Distribuição por prioridade:
--   - URGENTE: 8 tarefas (destaque VERMELHO)
--   - ALTA: 11 tarefas (destaque AMARELO) 
--   - NORMAL: 11 tarefas
-- 
-- Distribuição por status:
--   - PENDENTE: ~18 tarefas
--   - EM_ANDAMENTO: ~12 tarefas
--   - CONCLUIDA: 0 tarefas
-- ========================================

SELECT 'TAREFAS CRIADAS PARA EMPRESA DE TUBOS DE AÇO!' as STATUS;