#!/bin/bash

# Script simples para iniciar a aplicação
cd /home/carloscorreia/Secretária/Projetos/UFCD10791/GestorTarefas

echo "🛑 Parando processos existentes..."
pkill -f "spring.profiles.active=backend" 2>/dev/null
pkill -f "spring.profiles.active=gui" 2>/dev/null
sleep 2

echo "🖥️ Iniciando backend..."
java -jar target/gestor-tarefas-1.0.0.jar --spring.profiles.active=backend > backend.log 2>&1 &
BACKEND_PID=$!

echo "⏱️ Aguardando backend inicializar (30s)..."
sleep 30

echo "🖼️ Iniciando interface..."
java -Dspring.profiles.active=gui -jar target/gestor-tarefas-1.0.0.jar > gui.log 2>&1 &
GUI_PID=$!

echo "✅ Aplicação iniciada!"
echo "Backend PID: $BACKEND_PID"
echo "GUI PID: $GUI_PID"
echo ""
echo "👤 Login como administrador:"
echo "  Usuário: admin.correia"
echo "  Senha: senha123"
echo ""
echo "🌐 API: http://localhost:8080"