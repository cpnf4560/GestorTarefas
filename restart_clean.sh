#!/bin/bash

echo "🛑 Parando todos os processos..."
pkill -9 -f "GestorTarefas|spring-boot:run|mvn" 2>/dev/null
sleep 3

echo "🧹 Limpando target e recompilando..."
mvn clean compile -q

echo "🚀 Iniciando aplicação..."
mvn spring-boot:run > app.log 2>&1 &

echo "⏱️  Aguardando inicialização (30 segundos)..."
sleep 30

echo "✅ Aplicação deve estar disponível agora!"
echo "📊 Verificando logs:"
tail -20 app.log | grep -E "Started|Interface|Tomcat started"
