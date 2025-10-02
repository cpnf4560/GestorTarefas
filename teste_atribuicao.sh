#!/bin/bash

echo "=========================================="
echo "     TESTE DE ATRIBUIÇÃO DE EQUIPAS     "
echo "=========================================="
echo ""

# Verificar se backend está ativo
echo "1. Verificando backend..."
if curl -s http://localhost:8080/api/teams/18 > /dev/null 2>&1; then
    echo "✓ Backend ativo"
else
    echo "✗ Backend não está respondendo"
    exit 1
fi

echo ""
echo "2. Estado atual das equipas (membros):"
echo "   Direção: $(curl -s http://localhost:8080/api/teams/18/members | jq length) membros"
echo "   Logística: $(curl -s http://localhost:8080/api/teams/22/members | jq length) membros"

echo ""
echo "3. Tentando adicionar Lucile Almeida (ID 41) à equipa Direção (ID 18)..."

# Testar adicionar membro
RESPONSE=$(curl -s -w "%{http_code}" -X POST "http://localhost:8080/api/teams/18/members/41" -H "User-Id: 67" -o /tmp/add_member_response.json)

if [ "$RESPONSE" = "200" ]; then
    echo "✓ Membro adicionado com sucesso!"
    echo "   Resposta: $(cat /tmp/add_member_response.json)"
else
    echo "✗ Erro ao adicionar membro (HTTP $RESPONSE)"
    echo "   Resposta: $(cat /tmp/add_member_response.json)"
fi

echo ""
echo "4. Verificando membros após adição:"
echo "   Direção agora tem: $(curl -s http://localhost:8080/api/teams/18/members | jq length) membros"

echo ""
echo "5. Listando membros da equipa Direção:"
curl -s http://localhost:8080/api/teams/18/members | jq -r '.[] | "   - \(.name // .username) (\(.role))"'

echo ""
echo "6. Tentando remover o membro..."
RESPONSE2=$(curl -s -w "%{http_code}" -X DELETE "http://localhost:8080/api/teams/18/members/41" -H "User-Id: 67" -o /tmp/remove_member_response.json)

if [ "$RESPONSE2" = "200" ]; then
    echo "✓ Membro removido com sucesso!"
else
    echo "✗ Erro ao remover membro (HTTP $RESPONSE2)"
    echo "   Resposta: $(cat /tmp/remove_member_response.json)"
fi

echo ""
echo "7. Estado final:"
echo "   Direção agora tem: $(curl -s http://localhost:8080/api/teams/18/members | jq length) membros"

echo ""
echo "=========================================="
echo "          TESTE CONCLUÍDO               "
echo "=========================================="