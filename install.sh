#!/bin/bash

# Script de Instalação - Gestor de Tarefas
# Autor: Carlos Correia
# Versão: 1.0.0

echo "=========================================="
echo "   INSTALADOR - GESTOR DE TAREFAS v1.0   "
echo "=========================================="

# Verificar se Java está instalado
if ! command -v java &> /dev/null; then
    echo "⚠️  Java não encontrado!"
    echo "Por favor, instale Java 17+ antes de continuar:"
    echo "   Ubuntu/Debian: sudo apt install openjdk-17-jre"
    echo "   Windows: Baixe de https://adoptium.net/"
    exit 1
fi

# Verificar versão do Java
JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "⚠️  Java $JAVA_VERSION encontrado. Versão 17+ necessária."
    exit 1
fi

echo "✅ Java $(java -version 2>&1 | head -n1 | cut -d'"' -f2) encontrado"

# Criar diretório de instalação
INSTALL_DIR="$HOME/GestorTarefas"
mkdir -p "$INSTALL_DIR"

# Copiar ficheiros
echo "📂 Criando directório de instalação em: $INSTALL_DIR"
cp target/gestor-tarefas-1.0.0.jar "$INSTALL_DIR/"

# Criar script de execução
cat > "$INSTALL_DIR/run.sh" << 'EOF'
#!/bin/bash
cd "$(dirname "$0")"
java -jar gestor-tarefas-1.0.0.jar
EOF

chmod +x "$INSTALL_DIR/run.sh"

# Criar atalho no desktop (Linux)
if [ -d "$HOME/Desktop" ] || [ -d "$HOME/Área de Trabalho" ]; then
    DESKTOP_DIR="$HOME/Desktop"
    [ -d "$HOME/Área de Trabalho" ] && DESKTOP_DIR="$HOME/Área de Trabalho"
    
    cat > "$DESKTOP_DIR/GestorTarefas.desktop" << EOF
[Desktop Entry]
Version=1.0
Type=Application
Name=Gestor de Tarefas
Comment=Sistema de Gestão de Tarefas
Exec=$INSTALL_DIR/run.sh
Icon=applications-office
Terminal=false
Categories=Office;ProjectManagement;
EOF
    
    chmod +x "$DESKTOP_DIR/GestorTarefas.desktop"
    echo "🖥️  Atalho criado no desktop"
fi

echo ""
echo "✅ Instalação concluída com sucesso!"
echo ""
echo "🚀 Para executar a aplicação:"
echo "   • Use o atalho no desktop, ou"
echo "   • Execute: $INSTALL_DIR/run.sh"
echo ""
echo "📖 Credenciais de demonstração:"
echo "   • Username: demo | Password: demo123"
echo "   • Username: admin | Password: admin123"
echo ""
echo "🌐 Acesso ao sistema:"
echo "   • Interface: Aplicação Desktop"
echo "   • API: http://localhost:8080"
echo "   • Base de Dados: http://localhost:8080/h2-console"