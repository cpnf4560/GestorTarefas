#!/bin/bash

echo "=========================================="
echo "  GESTOR DE TAREFAS - Iniciando App     "
echo "=========================================="
echo ""

# Diretório do projeto
PROJECT_DIR="/home/carloscorreia/Secretária/Projetos/UFCD10791/GestorTarefas"
cd "$PROJECT_DIR" || exit 1

echo "✓ Diretório do projeto: $PROJECT_DIR"

# Verificar se o backend está rodando
echo "Verificando backend..."
if curl -s http://localhost:8080/api/teams/summary > /dev/null 2>&1; then
    echo "✓ Backend está ativo na porta 8080"
else
    echo "✗ Backend não está respondendo. Iniciando backend..."
    
    # Iniciar backend em background
    nohup java -jar target/gestor-tarefas-1.0.0.jar \
        --spring.profiles.active=backend \
        --spring.main.web-application-type=servlet > backend.log 2>&1 &
    
    echo "Aguardando backend inicializar..."
    sleep 10
    
    if curl -s http://localhost:8080/api/teams/summary > /dev/null 2>&1; then
        echo "✓ Backend iniciado com sucesso"
    else
        echo "✗ Erro ao iniciar backend"
        exit 1
    fi
fi

echo ""
echo "Iniciando interface gráfica..."
echo "Se a janela não aparecer, verifique se tem ambiente gráfico (X11/Wayland)"
echo ""

# Configurar display
export DISPLAY=${DISPLAY:-:0}

# Iniciar GUI
java -jar target/gestor-tarefas-1.0.0.jar --spring.profiles.active=gui

echo ""
echo "Interface gráfica encerrada."