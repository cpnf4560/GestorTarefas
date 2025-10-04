#!/bin/bash

# Script para criar tarefas da empresa de tubos de aço via API

BASE_URL="http://localhost:8080/api/tasks"

echo "Criando tarefas para empresa de tubos de aço..."

# DIREÇÃO - Mais tarefas
curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Certificação ISO 9001",
    "description": "Coordenar processo de renovação da certificação ISO 9001 para garantir qualidade dos tubos de aço",
    "userId": 67,
    "priority": "ALTA",
    "dueDate": "2025-11-15T23:59:59",
    "tags": "certificacao,qualidade"
  }' > /dev/null

# GESTÃO ADMINISTRATIVA
curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implementação Sistema ERP",
    "description": "Supervisionar implementação do novo sistema ERP para gestão integrada da empresa",
    "userId": 41,
    "priority": "ALTA",
    "dueDate": "2025-12-01T23:59:59",
    "tags": "erp,sistema"
  }' > /dev/null

curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Auditoria Anual Contabilidade",
    "description": "Coordenar auditoria anual dos registos contabilísticos e fiscais da empresa",
    "userId": 41,
    "priority": "NORMAL",
    "dueDate": "2025-11-01T23:59:59",
    "tags": "auditoria,contabilidade"
  }' > /dev/null

# COMERCIAL
curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Prospecção Mercado Construção Civil",
    "description": "Identificar e contactar potenciais clientes no setor da construção civil para tubos estruturais",
    "userId": 53,
    "priority": "ALTA",
    "dueDate": "2025-10-12T23:59:59",
    "tags": "prospeccao,construcao"
  }' > /dev/null

curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Catálogo Produtos 2025",
    "description": "Criar novo catálogo com especificações técnicas de todos os tipos de tubos de aço disponíveis",
    "userId": 71,
    "priority": "NORMAL",
    "dueDate": "2025-10-27T23:59:59",
    "tags": "catalogo,marketing"
  }' > /dev/null

curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Feira Internacional do Aço",
    "description": "Organizar participação na feira internacional do aço em Madrid - stand e apresentações",
    "userId": 71,
    "priority": "NORMAL",
    "dueDate": "2026-01-01T23:59:59",
    "tags": "feira,internacional"
  }' > /dev/null

# COMPRAS
curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Negociação Fornecedores Aço",
    "description": "Renegociar contratos com fornecedores de aço bruto para reduzir custos em 15%",
    "userId": 42,
    "priority": "ALTA",
    "dueDate": "2025-10-16T23:59:59",
    "tags": "fornecedores,negociacao"
  }' > /dev/null

curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Qualificação Novos Fornecedores",
    "description": "Avaliar e qualificar novos fornecedores de matéria-prima na Europa de Leste",
    "userId": 42,
    "priority": "NORMAL",
    "dueDate": "2025-11-11T23:59:59",
    "tags": "fornecedores,qualificacao"
  }' > /dev/null

curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Compra Equipamentos Galvanização",
    "description": "Processar compra de novos equipamentos para linha de galvanização dos tubos",
    "userId": 42,
    "priority": "URGENTE",
    "dueDate": "2025-10-07T23:59:59",
    "tags": "equipamentos,galvanizacao"
  }' > /dev/null

# PRODUÇÃO
curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Manutenção Preventiva Máquinas",
    "description": "Executar manutenção preventiva em todas as máquinas de corte e soldadura",
    "userId": 61,
    "priority": "URGENTE",
    "dueDate": "2025-10-05T23:59:59",
    "tags": "manutencao,maquinas"
  }' > /dev/null

curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Linha Produção Tubos Inox",
    "description": "Configurar nova linha de produção especializada em tubos de aço inoxidável",
    "userId": 63,
    "priority": "ALTA",
    "dueDate": "2025-11-16T23:59:59",
    "tags": "inox,linha-producao"
  }' > /dev/null

curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Controle Qualidade Soldaduras",
    "description": "Implementar novos procedimentos de controle de qualidade para soldaduras dos tubos",
    "userId": 61,
    "priority": "ALTA",
    "dueDate": "2025-10-20T23:59:59",
    "tags": "qualidade,soldadura"
  }' > /dev/null

curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Produção Lote Especial",
    "description": "Produzir lote especial de 200 tubos com especificações personalizadas para cliente VIP",
    "userId": 63,
    "priority": "URGENTE",
    "dueDate": "2025-10-06T23:59:59",
    "tags": "lote-especial,vip"
  }' > /dev/null

# APOIO AO CLIENTE
curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Manual Técnico Instalação",
    "description": "Criar manual técnico detalhado para instalação de tubos estruturais em construções",
    "userId": 46,
    "priority": "NORMAL",
    "dueDate": "2025-11-01T23:59:59",
    "tags": "manual,instalacao"
  }' > /dev/null

curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Formação Equipa Suporte",
    "description": "Formar equipa de suporte técnico sobre novas especificações de tubos galvanizados",
    "userId": 66,
    "priority": "NORMAL",
    "dueDate": "2025-10-16T23:59:59",
    "tags": "formacao,suporte"
  }' > /dev/null

curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Sistema Tickets Online",
    "description": "Implementar sistema online para gestão de tickets de suporte e reclamações",
    "userId": 46,
    "priority": "ALTA",
    "dueDate": "2025-11-11T23:59:59",
    "tags": "sistema,tickets"
  }' > /dev/null

curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Resolução Reclamação Urgente",
    "description": "Resolver reclamação urgente sobre qualidade de tubos fornecidos à empresa XYZ",
    "userId": 66,
    "priority": "URGENTE",
    "dueDate": "2025-10-03T23:59:59",
    "tags": "reclamacao,qualidade"
  }' > /dev/null

# FINANCEIRO
curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Análise Fluxo de Caixa",
    "description": "Analisar projeção de fluxo de caixa para próximos 6 meses considerando novos investimentos",
    "userId": 42,
    "priority": "ALTA",
    "dueDate": "2025-10-14T23:59:59",
    "tags": "fluxo-caixa,analise"
  }' > /dev/null

curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Relatório Rentabilidade Produtos",
    "description": "Gerar relatório detalhado de rentabilidade por tipo de tubo produzido",
    "userId": 42,
    "priority": "NORMAL",
    "dueDate": "2025-10-22T23:59:59",
    "tags": "rentabilidade,relatorio"
  }' > /dev/null

curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Negociação Financiamento",
    "description": "Negociar condições de financiamento bancário para expansão da fábrica",
    "userId": 42,
    "priority": "URGENTE",
    "dueDate": "2025-10-09T23:59:59",
    "tags": "financiamento,banco"
  }' > /dev/null

echo "Tarefas criadas com sucesso!"