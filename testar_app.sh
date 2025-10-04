#!/bin/bash

# Script para testar a aplicação
echo "=== INICIANDO APLICAÇÃO ==="

cd /home/carloscorreia/Secretária/Projetos/UFCD10791/GestorTarefas

# Parar processos existentes
echo "Parando processos Java existentes..."
pkill -f java 2>/dev/null || true
sleep 2

# Verificar se porta 8080 está livre
if lsof -i :8080 >/dev/null 2>&1; then
    echo "AVISO: Porta 8080 ainda ocupada!"
    lsof -i :8080
fi

# Iniciar backend
echo "Iniciando backend..."
java -jar target/gestor-tarefas-1.0.0.jar spring.profiles.active=backend >/dev/null 2>&1 &
BACKEND_PID=$!

echo "Backend PID: $BACKEND_PID"

# Aguardar backend
echo "Aguardando backend inicializar..."
sleep 8

# Verificar se backend está ativo
if curl -s http://localhost:8080/api/users >/dev/null 2>&1; then
    echo "✅ Backend ativo!"
else
    echo "❌ Backend não respode!"
    exit 1
fi

# Iniciar GUI
echo "Iniciando GUI..."
java -jar target/gestor-tarefas-1.0.0.jar --gui

echo "=== FIM ==="