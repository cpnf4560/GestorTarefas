# Gestor de Tarefas - Guia de Instalação

## 📦 Opções de Distribuição

### **Opção 1: Pacote de Instalação Simples (Recomendado)**

1. **Compilar a aplicação:**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Criar pacote de distribuição:**
   - Copie o ficheiro `target/gestor-tarefas-1.0.0.jar`
   - Inclua os scripts de instalação (`install.sh` e `install.bat`)
   - Comprima tudo num arquivo ZIP

3. **No PC de destino:**
   - **Linux/Mac**: Execute `./install.sh`
   - **Windows**: Execute `install.bat`

### **Opção 2: Executável Nativo com GraalVM**

Para criar um executável que não precisa de Java:

1. **Instalar GraalVM:**
   ```bash
   # Baixar de https://www.graalvm.org/
   export JAVA_HOME=/path/to/graalvm
   ```

2. **Configurar pom.xml:**
   ```xml
   <plugin>
       <groupId>org.graalvm.buildtools</groupId>
       <artifactId>native-maven-plugin</artifactId>
       <version>0.9.28</version>
   </plugin>
   ```

3. **Gerar executável:**
   ```bash
   mvn clean native:compile -Pnative
   ```

### **Opção 3: Instalador Nativo com jpackage**

Para sistemas com Java 17+:

```bash
# Windows (.msi)
jpackage --input target/ \
         --name "GestorTarefas" \
         --main-jar gestor-tarefas-1.0.0.jar \
         --main-class com.gestortarefas.GestorTarefasApplication \
         --type msi \
         --app-version 1.0.0 \
         --vendor "Carlos Correia"

# Linux (.deb)
jpackage --input target/ \
         --name "gestor-tarefas" \
         --main-jar gestor-tarefas-1.0.0.jar \
         --main-class com.gestortarefas.GestorTarefasApplication \
         --type deb \
         --app-version 1.0.0 \
         --vendor "Carlos Correia"
```

## 🔧 Requisitos do Sistema

- **Java 17+** (se usar JAR)
- **Windows 10+** / **Linux** / **macOS**
- **4GB RAM** (mínimo)
- **50MB espaço em disco**

## 📋 Instruções de Uso

1. **Primeira execução:**
   - A aplicação criará automaticamente a base de dados
   - Utilizadores demo serão criados

2. **Credenciais padrão:**
   - `demo` / `demo123`
   - `admin` / `admin123`

3. **Portas utilizadas:**
   - **8080**: Servidor API
   - **Interface**: Desktop GUI

## 🚀 Distribuição Recomendada

Para máxima compatibilidade, recomendo a **Opção 1** com os scripts de instalação:

1. Crie um ZIP contendo:
   - `gestor-tarefas-1.0.0.jar`
   - `install.sh` (Linux/Mac)
   - `install.bat` (Windows)
   - `README.md` (instruções)

2. O utilizador só precisa:
   - Extrair o ZIP
   - Executar o script de instalação
   - Usar o atalho criado no desktop