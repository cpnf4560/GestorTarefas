@echo off
REM Script para iniciar facilmente a aplicação Gestor de Tarefas no Windows
REM Autor: Sistema Gestor de Tarefas

title Gestor de Tarefas - Inicializador

echo.
echo ===============================================
echo    GESTOR DE TAREFAS - Inicializador Windows
echo ===============================================
echo.

REM Verificar se Java está instalado
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java nao encontrado. Por favor, instale o Java primeiro.
    pause
    exit /b 1
)

REM Verificar se Maven está instalado  
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Maven nao encontrado. Por favor, instale o Maven primeiro.
    pause
    exit /b 1
)

REM Verificar se estamos no diretório correto
if not exist "pom.xml" (
    echo ❌ Arquivo pom.xml nao encontrado. Execute este script do diretorio raiz do projeto.
    pause
    exit /b 1
)

echo 🔨 Compilando aplicacao...
call mvn clean package -DskipTests -q
if errorlevel 1 (
    echo ❌ Erro na compilacao
    pause
    exit /b 1
)
echo ✅ Compilacao concluida

echo.
echo 🛑 Parando processos existentes...
taskkill /f /im java.exe >nul 2>&1

echo.
echo 🖥️ Iniciando backend Spring Boot...
start "Backend-GestorTarefas" /min java -jar target/gestor-tarefas-1.0.0.jar --spring.profiles.active=backend

echo ⏱️ Aguardando backend inicializar...
timeout /t 10 /nobreak >nul

echo.
echo 🖼️ Iniciando interface grafica...

REM Construir classpath para Windows
for /f "delims=" %%i in ('mvn dependency:build-classpath -q -Dmdep.outputFile=con 2^>nul') do set CLASSPATH=%%i
set FULL_CLASSPATH=target\classes;%CLASSPATH%

start "GUI-GestorTarefas" java -cp "%FULL_CLASSPATH%" com.gestortarefas.gui.LoginFrame

echo.
echo 🎉 Aplicacao iniciada com sucesso!
echo.
echo 📊 INFORMACOES DE LOGIN:
echo ========================
echo Administradores:
echo   • admin.correia / senha123
echo   • martim.sottomayor / senha123
echo.
echo Gerentes:
echo   • lucile.almeida / senha123
echo   • diana.brochado / senha123
echo.
echo Funcionarios:
echo   • monica.lewinsky / senha123
echo   • rita.almeida / senha123
echo.
echo 🌐 Backend API: http://localhost:8080
echo.
echo Pressione qualquer tecla para fechar...
pause >nul