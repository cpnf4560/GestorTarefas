#!/bin/bash

echo "=========================================="
echo "  GESTOR DE TAREFAS - Interface Gráfica  "
echo "=========================================="
echo ""

# Verificar se o backend está rodando
echo "Verificando se o backend está ativo..."
if curl -s http://localhost:8080/api/teams/summary > /dev/null 2>&1; then
    echo "✓ Backend está ativo na porta 8080"
else
    echo "✗ Backend não está respondendo na porta 8080"
    echo "Por favor, inicie primeiro o backend usando: ./start_backend.sh"
    exit 1
fi

echo ""
echo "Iniciando interface gráfica..."
echo "Aguarde..."

# Entrar no diretório do projeto
cd "$(dirname "$0")"

# Iniciar a GUI usando Maven
mvn -q exec:java -Dexec.mainClass="com.gestortarefas.GestorTarefasApplication" -Dexec.args="--spring.profiles.active=gui" &

# Aguardar um pouco
sleep 3

echo "Interface gráfica iniciada!"
echo ""
echo "Se a janela não aparecer automaticamente:"
echo "1. Verifique se tem ambiente gráfico (X11/Wayland)"
echo "2. Execute: export DISPLAY=:0"
echo "3. Tente novamente"
echo ""
echo "Para fechar a aplicação, feche a janela ou pressione Ctrl+C aqui."
echo ""

# Manter o script rodando
wait