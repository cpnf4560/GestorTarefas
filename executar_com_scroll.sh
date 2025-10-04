#!/bin/bash
cd /home/carloscorreia/Secretária/Projetos/UFCD10791/GestorTarefas
echo "====== Recompilando projeto ======"
mvn clean package -DskipTests -q
echo "====== Compilação completa ======"
echo "====== Executando aplicação (versão 1.0.1) ======"
java -jar target/gestor-tarefas-1.0.1.jar
