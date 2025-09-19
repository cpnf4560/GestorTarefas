@echo off
REM Script de Instalação - Gestor de Tarefas
REM Autor: Carlos Correia
REM Versão: 1.0.0

echo ==========================================
echo    INSTALADOR - GESTOR DE TAREFAS v1.0   
echo ==========================================

REM Verificar se Java está instalado
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  Java não encontrado!
    echo Por favor, instale Java 17+ antes de continuar:
    echo    Baixe de https://adoptium.net/
    pause
    exit /b 1
)

echo ✅ Java encontrado

REM Criar directório de instalação
set "INSTALL_DIR=%USERPROFILE%\GestorTarefas"
mkdir "%INSTALL_DIR%" 2>nul

REM Copiar ficheiros
echo 📂 Criando directório de instalação em: %INSTALL_DIR%
copy "target\gestor-tarefas-1.0.0.jar" "%INSTALL_DIR%\" >nul

REM Criar script de execução
echo @echo off > "%INSTALL_DIR%\run.bat"
echo cd /d "%%~dp0" >> "%INSTALL_DIR%\run.bat"
echo java -jar gestor-tarefas-1.0.0.jar >> "%INSTALL_DIR%\run.bat"
echo pause >> "%INSTALL_DIR%\run.bat"

REM Criar atalho no desktop
set "DESKTOP_DIR=%USERPROFILE%\Desktop"
echo Set oWS = WScript.CreateObject("WScript.Shell") > "%TEMP%\CreateShortcut.vbs"
echo sLinkFile = "%DESKTOP_DIR%\Gestor de Tarefas.lnk" >> "%TEMP%\CreateShortcut.vbs"
echo Set oLink = oWS.CreateShortcut(sLinkFile) >> "%TEMP%\CreateShortcut.vbs"
echo oLink.TargetPath = "%INSTALL_DIR%\run.bat" >> "%TEMP%\CreateShortcut.vbs"
echo oLink.WorkingDirectory = "%INSTALL_DIR%" >> "%TEMP%\CreateShortcut.vbs"
echo oLink.Description = "Sistema de Gestão de Tarefas" >> "%TEMP%\CreateShortcut.vbs"
echo oLink.Save >> "%TEMP%\CreateShortcut.vbs"

cscript "%TEMP%\CreateShortcut.vbs" >nul
del "%TEMP%\CreateShortcut.vbs" >nul

echo 🖥️  Atalho criado no desktop

echo.
echo ✅ Instalação concluída com sucesso!
echo.
echo 🚀 Para executar a aplicação:
echo    • Use o atalho no desktop, ou
echo    • Execute: %INSTALL_DIR%\run.bat
echo.
echo 📖 Credenciais de demonstração:
echo    • Username: demo ^| Password: demo123
echo    • Username: admin ^| Password: admin123
echo.
echo 🌐 Acesso ao sistema:
echo    • Interface: Aplicação Desktop
echo    • API: http://localhost:8080
echo    • Base de Dados: http://localhost:8080/h2-console
echo.
pause