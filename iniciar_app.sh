#!/bin/bash

# Script para iniciar facilmente a aplicação Gestor de Tarefas
# Autor: Sistema Gestor de Tarefas
# Data: $(date +"%d/%m/%Y")

echo "🚀 GESTOR DE TAREFAS - Inicializador Automático"
echo "=============================================="

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Diretório da aplicação
APP_DIR="/home/carloscorreia/Secretária/Projetos/UFCD10791/GestorTarefas"
cd "$APP_DIR"

# Função para verificar se um processo está rodando
check_process() {
    if pgrep -f "$1" > /dev/null; then
        return 0
    else
        return 1
    fi
}

# Função para parar processos existentes
stop_existing_processes() {
    echo -e "${YELLOW}🛑 Parando processos existentes...${NC}"
    
    # Parar backend
    if check_process "spring.profiles.active=backend"; then
        pkill -f "spring.profiles.active=backend"
        echo -e "${GREEN}   ✓ Backend parado${NC}"
        sleep 2
    fi
    
    # Parar GUI
    if check_process "spring.profiles.active=gui"; then
        pkill -f "spring.profiles.active=gui"
        echo -e "${GREEN}   ✓ Interface GUI parada${NC}"
        sleep 1
    fi
}

# Função para compilar a aplicação
compile_app() {
    echo -e "${BLUE}🔨 Compilando aplicação...${NC}"
    if mvn clean package -DskipTests -q; then
        echo -e "${GREEN}   ✓ Compilação concluída com sucesso${NC}"
        return 0
    else
        echo -e "${RED}   ✗ Erro na compilação${NC}"
        return 1
    fi
}

# Função para iniciar o backend
start_backend() {
    echo -e "${BLUE}🖥️  Iniciando backend Spring Boot...${NC}"
    
    # Verificar se já está rodando
    if check_process "spring.profiles.active=backend"; then
        echo -e "${YELLOW}   ⚠️  Backend já está rodando${NC}"
        return 0
    fi
    
    # Iniciar backend em background
    nohup java -jar target/gestor-tarefas-1.0.0.jar --spring.profiles.active=backend > backend.log 2>&1 &
    
    # Aguardar inicialização
    echo -e "${YELLOW}   ⏱️  Aguardando inicialização...${NC}"
    
    for i in {1..25}; do
        if curl -s http://localhost:8080/api/users > /dev/null 2>&1; then
            echo -e "${GREEN}   ✓ Backend iniciado com sucesso (${i}s)${NC}"
            return 0
        fi
        sleep 1
        echo -n "."
    done
    
    echo -e "${RED}   ✗ Backend não iniciou dentro do tempo esperado${NC}"
    return 1
}

# Função para iniciar a GUI
start_gui() {
    echo -e "${BLUE}🖼️  Iniciando interface gráfica...${NC}"
    
    # Verificar se já está rodando
    if check_process "spring.profiles.active=gui"; then
        echo -e "${YELLOW}   ⚠️  Interface já está rodando${NC}"
        return 0
    fi
    
    # Verificar se JAR existe
    if [ ! -f "target/gestor-tarefas-1.0.0.jar" ]; then
        echo -e "${RED}   ✗ JAR não encontrado. Execute compilação primeiro.${NC}"
        return 1
    fi
    
    # Iniciar GUI em background
    java -Dspring.profiles.active=gui -jar target/gestor-tarefas-1.0.0.jar > gui.log 2>&1 &
    
    sleep 2
    
    if check_process "spring.profiles.active=gui"; then
        echo -e "${GREEN}   ✓ Interface gráfica iniciada${NC}"
        return 0
    else
        echo -e "${RED}   ✗ Erro ao iniciar interface gráfica${NC}"
        return 1
    fi
}

# Função para mostrar status
show_status() {
    echo ""
    echo -e "${BLUE}📊 STATUS DA APLICAÇÃO:${NC}"
    echo "========================"
    
    if check_process "spring.profiles.active=backend"; then
        echo -e "${GREEN}🟢 Backend: ATIVO (http://localhost:8080)${NC}"
    else
        echo -e "${RED}🔴 Backend: INATIVO${NC}"
    fi
    
    if check_process "spring.profiles.active=gui"; then
        echo -e "${GREEN}🟢 Interface: ATIVA${NC}"
    else
        echo -e "${RED}🔴 Interface: INATIVA${NC}"
    fi
    
    echo ""
}

# Função para mostrar informações de login
show_login_info() {
    echo -e "${BLUE}👤 INFORMAÇÕES DE LOGIN:${NC}"
    echo "========================="
    echo -e "${GREEN}Administradores:${NC}"
    echo "  • admin.correia / senha123"
    echo "  • martim.sottomayor / senha123"
    echo ""
    echo -e "${GREEN}Gerentes:${NC}"
    echo "  • lucile.almeida / senha123"
    echo "  • diana.brochado / senha123"
    echo ""
    echo -e "${GREEN}Funcionários:${NC}"
    echo "  • monica.lewinsky / senha123"
    echo "  • rita.almeida / senha123"
    echo ""
}

# Menu principal
main_menu() {
    while true; do
        echo ""
        echo -e "${BLUE}🎯 MENU PRINCIPAL:${NC}"
        echo "=================="
        echo "1) 🚀 Iniciar tudo (Backend + Interface)"
        echo "2) 🖥️  Iniciar apenas Backend"
        echo "3) 🖼️  Iniciar apenas Interface"
        echo "4) 🛑 Parar tudo"
        echo "5) 📊 Ver status"
        echo "6) 👤 Ver informações de login"
        echo "7) 🔄 Recompilar e reiniciar"
        echo "8) ❌ Sair"
        echo ""
        read -p "Escolha uma opção (1-8): " choice
        
        case $choice in
            1)
                stop_existing_processes
                if start_backend && start_gui; then
                    show_status
                    show_login_info
                fi
                ;;
            2)
                start_backend
                show_status
                ;;
            3)
                start_gui
                show_status
                ;;
            4)
                stop_existing_processes
                show_status
                ;;
            5)
                show_status
                ;;
            6)
                show_login_info
                ;;
            7)
                stop_existing_processes
                compile_app
                if start_backend && start_gui; then
                    show_status
                    show_login_info
                fi
                ;;
            8)
                echo -e "${GREEN}👋 Até breve!${NC}"
                exit 0
                ;;
            *)
                echo -e "${RED}❌ Opção inválida${NC}"
                ;;
        esac
    done
}

# Verificar se o Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven não encontrado. Por favor, instale o Maven primeiro.${NC}"
    exit 1
fi

# Verificar se o Java está instalado
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java não encontrado. Por favor, instale o Java primeiro.${NC}"
    exit 1
fi

# Verificar se estamos no diretório correto
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}❌ Arquivo pom.xml não encontrado. Execute este script do diretório raiz do projeto.${NC}"
    exit 1
fi

# Início automático se não houver argumentos
if [ $# -eq 0 ]; then
    echo -e "${GREEN}🎯 Iniciando aplicação automaticamente...${NC}"
    echo ""
    
    # Compilar primeiro
    if ! compile_app; then
        exit 1
    fi
    
    # Parar processos existentes
    stop_existing_processes
    
    # Iniciar backend e GUI
    if start_backend && start_gui; then
        show_status
        show_login_info
        echo ""
        echo -e "${GREEN}🎉 Aplicação iniciada com sucesso!${NC}"
        echo -e "${YELLOW}💡 Para gerenciar a aplicação, execute novamente: ./iniciar_app.sh menu${NC}"
    else
        echo -e "${RED}❌ Erro ao iniciar aplicação${NC}"
        exit 1
    fi
elif [ "$1" = "menu" ]; then
    main_menu
elif [ "$1" = "stop" ]; then
    stop_existing_processes
elif [ "$1" = "status" ]; then
    show_status
else
    echo "Uso: $0 [menu|stop|status]"
    echo "  (sem argumentos): Inicia a aplicação automaticamente"
    echo "  menu: Abre o menu interativo"
    echo "  stop: Para todos os processos"
    echo "  status: Mostra o status atual"
fi