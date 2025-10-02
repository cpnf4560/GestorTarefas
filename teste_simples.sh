#!/bin/bash

echo "=========================================="
echo "     TESTE SIMPLES DE ATRIBUIÇÃO        "
echo "=========================================="
echo ""

echo "1. Verificando backend..."
curl -s http://localhost:8080/api/teams/18 > /dev/null && echo "✓ Backend ativo" || echo "✗ Backend inativo"

echo ""
echo "2. Membros atuais da equipa Direção:"
curl -s http://localhost:8080/api/teams/18/members

echo ""
echo "3. Tentando adicionar membro via API..."
echo "   Executando: POST /api/teams/18/members/41"
echo "   User-Id: 67"

# Usar timeout para evitar travamento
timeout 10s curl -X POST "http://localhost:8080/api/teams/18/members/41" \
    -H "User-Id: 67" \
    -H "Content-Type: application/json" \
    --connect-timeout 5 \
    --max-time 10 \
    -v 2>&1 | head -20

echo ""
echo "4. Membros após tentativa:"
curl -s http://localhost:8080/api/teams/18/members

echo ""
echo "=========================================="