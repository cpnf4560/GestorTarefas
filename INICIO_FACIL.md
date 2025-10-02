# 🚀 GESTOR DE TAREFAS - Guia de Inicialização Rápida

## 📋 Pré-requisitos
- **Java 21+** instalado e configurado
- **Maven 3.8+** instalado e configurado  
- **MySQL 8.0+** rodando na porta 3306
- Base de dados `gestor_tarefas` criada

## ⚡ Inicialização Super Fácil

### 🐧 **Linux/macOS**
```bash
# Dar permissão de execução (apenas na primeira vez)
chmod +x iniciar_app.sh

# Iniciar automaticamente (recomendado)
./iniciar_app.sh

# OU usar o menu interativo
./iniciar_app.sh menu
```

### 🪟 **Windows**
```cmd
# Duplo-clique no arquivo OU via terminal
iniciar_app.bat
```

## 🎯 **Funcionalidades dos Scripts**

### **Inicialização Automática** (Opção padrão)
- ✅ Compila automaticamente
- ✅ Para processos existentes  
- ✅ Inicia backend Spring Boot
- ✅ Aguarda backend estar pronto
- ✅ Inicia interface gráfica
- ✅ Mostra informações de login

### **Menu Interativo** (`./iniciar_app.sh menu`)
1. 🚀 **Iniciar tudo** - Backend + Interface
2. 🖥️ **Iniciar apenas Backend** - Para desenvolvimento
3. 🖼️ **Iniciar apenas Interface** - Se backend já estiver rodando
4. 🛑 **Parar tudo** - Para todos os processos
5. 📊 **Ver status** - Verifica o que está rodando
6. 👤 **Ver informações de login** - Credenciais de acesso
7. 🔄 **Recompilar e reiniciar** - Após mudanças no código
8. ❌ **Sair** - Fecha o menu

### **Comandos Rápidos**
```bash
./iniciar_app.sh status    # Ver status
./iniciar_app.sh stop      # Parar tudo
./iniciar_app.sh menu      # Menu interativo
```

## 👤 **Informações de Login**

### **👑 Administradores**
- `admin.correia` / `senha123`
- `martim.sottomayor` / `senha123`

### **👨‍💼 Gerentes** 
- `lucile.almeida` / `senha123`
- `diana.brochado` / `senha123`
- `paulo.bessa` / `senha123`

### **👥 Funcionários**
- `monica.lewinsky` / `senha123`
- `rita.almeida` / `senha123`
- `ricardo.leal` / `senha123`
- `carla.silva` / `senha123`

## 🌐 **URLs Importantes**
- **Backend API**: http://localhost:8080
- **Documentação API**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

## 🔧 **Resolução de Problemas**

### **Porta 8080 ocupada**
```bash
# Verificar o que está usando a porta
lsof -i :8080

# Parar processo específico
kill -9 <PID>

# OU usar o script
./iniciar_app.sh stop
```

### **Erro de compilação**
```bash
# Limpar e recompilar
mvn clean package -DskipTests

# OU usar o menu
./iniciar_app.sh menu
# Opção 7: Recompilar e reiniciar
```

### **Base de dados não conecta**
1. Verificar se MySQL está rodando
2. Verificar credenciais em `application.properties`
3. Confirmar que base `gestor_tarefas` existe

### **Interface não abre**
- Verificar se backend está rodando primeiro
- Verificar logs em `gui.log`
- Tentar reiniciar apenas a interface (opção 3 do menu)

## 📁 **Estrutura de Logs**
- `backend.log` - Logs do servidor Spring Boot
- `gui.log` - Logs da interface gráfica
- `app.log` - Logs gerais da aplicação

## 🎨 **Características da Interface**
- **Login** automático com validação
- **Dashboard** específico por perfil (Admin/Gerente/Funcionário)
- **Gestão de utilizadores** completa (Admin)
- **Gestão de equipas** e atribuições
- **Gestão de tarefas** com comentários
- **Relatórios** e estatísticas

## 💡 **Dicas**
- Use sempre `./iniciar_app.sh` para facilidade máxima
- O menu interativo é perfeito para desenvolvimento
- Backend pode rodar independentemente para testes API
- Verifique sempre o status antes de reportar problemas

---
**🎉 Pronto! Sua aplicação Gestor de Tarefas está configurada para iniciar facilmente!**