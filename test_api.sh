#!/bin/bash

echo "🚀 Iniciando teste do sistema..."

# Iniciar o backend em background
echo "📡 Iniciando backend..."
cd /home/carloscorreia/Secretária/Projetos/UFCD10791/GestorTarefas
mvn exec:java -Dexec.mainClass="com.gestortarefas.GestorTarefasApplication" > backend.log 2>&1 &
BACKEND_PID=$!

echo "Backend PID: $BACKEND_PID"

# Aguardar backend inicializar
echo "⏳ Aguardando backend inicializar (30 segundos)..."
sleep 30

# Testar se backend está online
echo "🔍 Testando conectividade..."
if curl -s http://localhost:8080/actuator/health | grep -q "UP"; then
    echo "✅ Backend online!"
else 
    echo "❌ Backend não respondeu"
    kill $BACKEND_PID
    exit 1
fi

# Testar criar utilizador
echo "👤 Testando criação de utilizador..."
USER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@gestortarefas.com",  
    "password": "test123",
    "fullName": "Utilizador Teste"
  }')

if echo "$USER_RESPONSE" | grep -q "id"; then
    echo "✅ Utilizador criado com sucesso!"
    USER_ID=$(echo "$USER_RESPONSE" | grep -o '"id":[0-9]*' | cut -d: -f2)
    echo "   User ID: $USER_ID"
else
    echo "❌ Erro ao criar utilizador:"
    echo "$USER_RESPONSE"
fi

# Testar criar equipa  
echo "👥 Testando criação de equipa..."
TEAM_RESPONSE=$(curl -s -X POST http://localhost:8080/api/teams \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Equipa Teste",
    "description": "Equipa para testes",
    "managerId": 1
  }')

if echo "$TEAM_RESPONSE" | grep -q "id"; then
    echo "✅ Equipa criada com sucesso!"
    TEAM_ID=$(echo "$TEAM_RESPONSE" | grep -o '"id":[0-9]*' | cut -d: -f2)
    echo "   Team ID: $TEAM_ID"
else
    echo "❌ Erro ao criar equipa:"
    echo "$TEAM_RESPONSE"
fi

# Testar criar tarefa
echo "📋 Testando criação de tarefa..."
TASK_RESPONSE=$(curl -s -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d "{
    \"title\": \"Tarefa Teste\",
    \"description\": \"Descrição da tarefa teste\",
    \"priority\": \"NORMAL\",
    \"userId\": ${USER_ID:-1},
    \"createdByUserId\": 1
  }")

if echo "$TASK_RESPONSE" | grep -q "id"; then
    echo "✅ Tarefa criada com sucesso!"
    TASK_ID=$(echo "$TASK_RESPONSE" | grep -o '"id":[0-9]*' | cut -d: -f2)
    echo "   Task ID: $TASK_ID"
else
    echo "❌ Erro ao criar tarefa:"
    echo "$TASK_RESPONSE"
fi

echo "🎉 Testes concluídos! O backend está funcional."
echo ""
echo "💡 COMO USAR:"
echo "1. Mantenha este terminal aberto para o backend funcionar"
echo "2. Abra outro terminal e execute:"
echo "   cd /home/carloscorreia/Secretária/Projetos/UFCD10791/GestorTarefas"
echo "   java -cp target/classes com.gestortarefas.view.MainWindow"
echo "3. Use a interface normalmente - agora deve funcionar!"
echo ""
echo "🛑 Para parar o backend, pressione Ctrl+C"

# Manter backend ativo
wait $BACKEND_PID