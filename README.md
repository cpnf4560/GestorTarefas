# 🚀 Gestor de Tarefas - Sistema Híbrido Java

## 📋 Descrição do Projeto

Sistema completo de gestão de tarefas que integra **Spring Boot** (backend) com **Swing** (frontend), oferecendo uma solução híbrida robusta para gestão empresarial de tarefas e equipas.

### 🏗️ Arquitetura do Sistema

```
┌─────────────────┐    HTTP/REST    ┌──────────────────┐
│   Swing GUI     │ ◄──────────────► │   Spring Boot    │
│   (Frontend)    │                  │   (Backend API)  │
├─────────────────┤                  ├──────────────────┤
│ • Login Dialog  │                  │ • REST Controllers│
│ • Main Window   │                  │ • Business Logic │
│ • Task Management│                  │ • Data Validation│
│ • User Profile  │                  │ • Security Config│
└─────────────────┘                  └──────────────────┘
                                               │
                                               ▼
                                    ┌──────────────────┐
                                    │   MySQL Database │
                                    │                  │
                                    │ • Users & Profiles│
                                    │ • Tasks & Comments│
                                    │ • Teams & Members │
                                    └──────────────────┘
```

## 🛠️ Stack Tecnológica

### Backend (API REST)
- **Spring Boot 3.4.1**: Framework principal
- **Spring Data JPA**: Acesso aos dados
- **Spring Security**: Autenticação (modo desenvolvimento)
- **Spring Web**: Controllers REST
- **Spring Actuator**: Monitorização
- **Hibernate**: ORM para persistência
- **MySQL Connector**: Driver de base de dados
- **Bean Validation**: Validação de dados

### Frontend (Interface Gráfica)
- **Java Swing**: Interface gráfica nativa
- **Custom UI Components**: Componentes personalizados
- **Layout Managers**: GridBagLayout, BorderLayout
- **Event Handling**: Padrão Observer

### Base de Dados
- **MySQL**: Base de dados principal (porta 3307 - XAMPP)
- **JPA/Hibernate**: Mapeamento objeto-relacional
- **DDL Auto**: Criação automática de tabelas

### Ferramentas de Desenvolvimento
- **Maven**: Gestão de dependências e build
- **Java 21**: Linguagem (compatível com Java 17+)
- **Git**: Controlo de versões

## 🎯 Funcionalidades Principais

### 🔐 Sistema de Autenticação
- **Login Multi-perfil**: Admin, Gerente, Funcionário
- **Utilizadores Demo**: Pré-configurados para teste
- **Validação de Credenciais**: Email e senha obrigatórios
- **Sessão Persistente**: Manter login durante utilização

### ✅ Gestão Avançada de Tarefas
- **CRUD Completo**: Criar, Ler, Atualizar, Eliminar
- **Estados de Workflow**: 
  - 🟡 PENDENTE → 🔵 EM_ANDAMENTO → 🟢 CONCLUIDA
  - ❌ CANCELADA (estado terminal)
- **Sistema de Prioridades**: BAIXA, NORMAL, ALTA, URGENTE
- **Datas Inteligentes**: Criação, atualização, prazo, conclusão
- **Comentários**: Sistema de notas e observações por tarefa

### 👥 Gestão de Equipas
- **Criação de Equipas**: Organizadas por projeto/departamento
- **Gestão de Membros**: Adicionar/remover utilizadores
- **Hierarquia**: Gerentes podem gerir equipas
- **Atribuição**: Tarefas podem ser atribuídas a equipas

### 👤 Perfis de Utilizador
- **Perfil Detalhado**: Informações pessoais e profissionais
- **Configurações**: Preferências de interface e notificações
- **Histórico**: Registo de atividades e tarefas

### 📊 Dashboard e Relatórios
- Contador de tarefas por status
- Identificação de tarefas em atraso
- Exportação para CSV com múltiplas opções

### 🎨 Interface Gráfica
- Interface intuitiva em Swing
- Tabelas com ordenação e filtros
- Dialogs para criação/edição
- Atualização em tempo real

## Estrutura do Projeto

```
GestorTarefas/
├── src/main/java/com/gestortarefas/
│   ├── GestorTarefasApplication.java    # Classe principal
│   ├── config/
│   │   ├── SecurityConfig.java          # Configuração de segurança
│   │   └── data/DataInitializer.java    # Dados iniciais
│   ├── model/
│   │   ├── User.java                    # Entidade utilizador
│   │   └── Task.java                    # Entidade tarefa
│   ├── repository/
│   │   ├── UserRepository.java          # Repositório utilizadores
│   │   └── TaskRepository.java          # Repositório tarefas
│   ├── service/
│   │   ├── UserService.java             # Serviços utilizadores
│   │   └── TaskService.java             # Serviços tarefas
│   ├── controller/
│   │   ├── UserController.java          # API REST utilizadores
│   │   └── TaskController.java          # API REST tarefas
│   ├── gui/
│   │   ├── LoginFrame.java              # Tela de login
│   │   ├── RegisterDialog.java          # Dialog de registro
│   │   ├── MainFrame.java               # Tela principal
│   │   └── TaskDialog.java              # Dialog de tarefas
│   └── util/
│       ├── HttpUtil.java                # Utilitário HTTP
│       └── CSVExporter.java             # Exportação CSV
├── src/main/resources/
│   └── application.properties           # Configurações
└── pom.xml                             # Dependências Maven
```

## Tecnologias Utilizadas

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| Java | 17 | Linguagem de programação |
| Spring Boot | 3.1.5 | Framework backend |
| Spring Data JPA | - | Persistência de dados |
| Spring Security | - | Autenticação e segurança |
| H2 Database | - | Base de dados em memória |
| Swing | - | Interface gráfica |
| Maven | - | Gestão de dependências |
| Apache Commons CSV | 1.10.0 | Exportação CSV |
| Jackson | - | Serialização JSON |

## Como Executar

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6 ou superior

### Passos para execução

1. **Clonar/Navegar para o diretório do projeto**
   ```bash
   cd /home/carloscorreia/Secretária/Projetos/UFCD10791/GestorTarefas
   ```

2. **Compilar o projeto**
   ```bash
   mvn clean compile
   ```

3. **Executar a aplicação**
   ```bash
   mvn spring-boot:run
   ```

4. **Alternativa: Gerar JAR executável**
   ```bash
   mvn clean package
   java -jar target/gestor-tarefas-1.0.0.jar
   ```

### Primeiro Acesso

Ao executar a aplicação pela primeira vez, são criados utilizadores de demonstração:

| Username | Senha | Tipo |
|----------|-------|------|
| `demo` | `demo123` | Utilizador normal |
| `admin` | `admin123` | Administrador |

## APIs REST Disponíveis

### Utilizadores
- `POST /api/users/auth/login` - Autenticar utilizador
- `POST /api/users/auth/register` - Registar novo utilizador
- `GET /api/users/{id}` - Obter utilizador por ID
- `PUT /api/users/{id}` - Atualizar utilizador

### Tarefas  
- `GET /api/tasks/user/{userId}` - Listar tarefas do utilizador
- `POST /api/tasks` - Criar nova tarefa
- `PUT /api/tasks/{id}` - Atualizar tarefa
- `DELETE /api/tasks/{id}` - Eliminar tarefa
- `PUT /api/tasks/{id}/complete` - Marcar como concluída
- `GET /api/tasks/user/{userId}/stats` - Estatísticas do utilizador

## Console H2 Database

Durante o desenvolvimento, pode aceder ao console da base de dados H2:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:gestortarefas`
- Username: `sa`
- Password: (vazio)

## Funcionalidades Implementadas

### ✅ Recursos Principais
- [x] Autenticação segura com Spring Security
- [x] CRUD completo de tarefas
- [x] Interface gráfica responsiva
- [x] Base de dados H2 com JPA
- [x] API REST documentada
- [x] Validações server-side
- [x] Exportação CSV
- [x] Filtros e pesquisa
- [x] Estatísticas em tempo real

### ✅ Recursos Extras
- [x] Dados iniciais de demonstração
- [x] Criptografia de senhas
- [x] Tratamento de erros
- [x] Interface multi-threaded
- [x] Relatórios detalhados
- [x] Validação de datas
- [x] Estados e prioridades

## Arquitetura

### Padrões Utilizados
- **MVC (Model-View-Controller)**: Separação clara de responsabilidades
- **Repository Pattern**: Abstração do acesso a dados
- **Service Layer**: Lógica de negócio centralizada
- **DTO Pattern**: Transferência segura de dados via API

### Comunicação Frontend-Backend
- Interface Swing comunica com Spring Boot via HTTP/REST
- JSON para serialização de dados
- Validações tanto no cliente quanto no servidor
- Tratamento assíncrono de requisições

## Extensibilidade

O projeto foi desenvolvido com foco na extensibilidade:

1. **Novos Campos**: Fácil adição de campos às entidades
2. **Novos Status**: Enum extensível para status de tarefas  
3. **Novas APIs**: Arquitetura REST facilita novos endpoints
4. **Novos Relatórios**: Sistema de exportação modular
5. **Outras Bases de Dados**: JPA permite migração fácil

## Possíveis Melhorias Futuras

- Notificações por email para tarefas próximas do prazo
- Interface web complementar
- Autenticação JWT para sessões mais seguras
- Upload de ficheiros anexos às tarefas
- Partilha de tarefas entre utilizadores
- Integração com calendários externos
- Tema escuro na interface
- Internacionalização (i18n)

## Conclusão

Este projeto demonstra a integração efectiva de múltiplas tecnologias Java numa aplicação real, seguindo boas práticas de desenvolvimento e padrões de arquitetura consagrados.

---
**Desenvolvido por**: Carlos Correia  
**Data**: Setembro 2025  
**Curso**: UFCD10791 - Programação em Java  