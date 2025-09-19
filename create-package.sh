#!/bin/bash

# Script para criar pacote de distribuição
# Autor: Carlos Correia

echo "📦 Criando pacote de distribuição..."

# Verificar se JAR existe
if [ ! -f "target/gestor-tarefas-1.0.0.jar" ]; then
    echo "⚠️  JAR não encontrado. Execute 'mvn clean package -DskipTests' primeiro."
    exit 1
fi

# Criar diretório temporário
TEMP_DIR="gestor-tarefas-v1.0.0"
rm -rf "$TEMP_DIR"
mkdir "$TEMP_DIR"

# Copiar ficheiros necessários
cp target/gestor-tarefas-1.0.0.jar "$TEMP_DIR/"
cp install.sh "$TEMP_DIR/"
cp install.bat "$TEMP_DIR/"
cp INSTALACAO.md "$TEMP_DIR/"

# Criar README simples
cat > "$TEMP_DIR/README.txt" << 'EOF'
GESTOR DE TAREFAS v1.0.0
========================

INSTALAÇÃO:
-----------
Windows: Execute install.bat
Linux/Mac: Execute ./install.sh

REQUISITOS:
-----------
- Java 17 ou superior

CREDENCIAIS DEMO:
-----------------
Username: demo | Password: demo123
Username: admin | Password: admin123

SUPORTE:
--------
Para dúvidas, contacte: carlos@example.com
EOF

# Criar arquivo ZIP
echo "📂 Comprimindo ficheiros..."
zip -r "${TEMP_DIR}.zip" "$TEMP_DIR/"

# Limpeza
rm -rf "$TEMP_DIR"

echo "✅ Pacote criado: ${TEMP_DIR}.zip"
echo ""
echo "🚀 Para distribuir:"
echo "   1. Envie o arquivo ${TEMP_DIR}.zip"
echo "   2. O utilizador extrai e executa o script de instalação"
echo "   3. A aplicação fica instalada com atalho no desktop"