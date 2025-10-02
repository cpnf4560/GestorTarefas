#!/bin/bash

# Script corrigido para iniciar aplicação sem conflitos
cd /home/carloscorreia/Secretária/Projetos/UFCD10791/GestorTarefas

echo "🛑 Parando processos existentes..."
pkill -f java 2>/dev/null
sleep 3

echo "🖥️ Iniciando apenas backend..."
# Usar aplicação principal com profile backend
java -jar target/gestor-tarefas-1.0.0.jar --spring.profiles.active=backend --spring.main.web-application-type=servlet > backend.log 2>&1 &
BACKEND_PID=$!

echo "⏱️ Aguardando backend inicializar..."
for i in {1..30}; do
    if curl -s http://localhost:8080/api/users >/dev/null 2>&1; then
        echo "✅ Backend iniciado em ${i}s"
        break
    fi
    sleep 1
    echo -n "."
done

echo ""
echo "🖼️ Iniciando interface..."
# Iniciar GUI usando profile específico
java -jar target/gestor-tarefas-1.0.0.jar --spring.profiles.active=gui > gui.log 2>&1 &
GUI_PID=$!

sleep 3

echo "✅ Aplicação iniciada!"
echo "Backend PID: $BACKEND_PID"
echo "GUI PID: $GUI_PID"
echo ""
echo "👤 Login como administrador:"
echo "  Usuário: admin.correia"
echo "  Senha: senha123"
echo ""
echo "🌐 API: http://localhost:8080"