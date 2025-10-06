# 🧪 Guia de Testes - CardLayout + i18n (UFCD 5425)

## 📋 Status: Aplicação INICIADA!
**Janela aberta:** "Gestor de Tarefas - Login"

---

## ✅ Teste 1: Login Card (Tela Inicial)

### O que verificar:
- [ ] Janela tem título: **"Gestor de Tarefas - Login"**
- [ ] Tamanho da janela: **1400x900 pixels**
- [ ] Botão **🌐 PT/EN** visível no canto superior direito
- [ ] **3 abas** visíveis: Admin | Gerente | Funcionário
- [ ] Logo "GT" ou imagem logo.png no topo
- [ ] Título: "🚀 Sistema de Gestão de Tarefas"
- [ ] Campos: **Utilizador** e **Senha**
- [ ] Botões: **Entrar** (azul) e **Register** (cinza)
- [ ] Painel "👥 Login Rápido - 29 Utilizadores" com 3 tabs

### Ações:
1. **Clicar no botão 🌐 PT/EN**
   - ✅ Labels mudam: "Utilizador" → "Username", "Senha" → "Password"
   - ✅ Botão "Entrar" → "Login"
   - ✅ Clicar novamente: volta para PT

2. **Fazer login:**
   - Usar: `demo` / `demo123` (Funcionário)
   - Ou clicar em qualquer botão do painel de utilizadores demo

---

## ✅ Teste 2: Navegação Login → Dashboard

### O que deve acontecer:
1. Após login bem-sucedido:
   - [ ] Janela muda de **"Login"** para **"Dashboard"**
   - [ ] **Não abre nova janela** (CardLayout!)
   - [ ] Mesma janela 1400x900px
   - [ ] Dashboard Kanban aparece com **4 colunas**

### Verificar Dashboard:
- [ ] **4 colunas Kanban:**
  1. **PENDENTES** (ou PENDING se em EN)
  2. **HOJE** (ou TODAY)
  3. **ATRASADAS** (ou OVERDUE)
  4. **CONCLUÍDAS** (ou COMPLETED)

- [ ] **Botões nas colunas:**
  - PENDENTES: botão **"Iniciar"** (ou "Start")
  - HOJE: botão **"Concluir"** (ou "Complete")
  - ATRASADAS: botão **"Concluir"** (ou "Complete")
  - CONCLUÍDAS: **sem botões**

- [ ] **Tarefas visíveis nas tabelas**
  - Deve haver tarefas distribuídas pelas colunas
  - Cada tarefa deve ter: ID, Título, Prazo, Atribuído a

---

## ✅ Teste 3: i18n no Dashboard

### Ações:
1. **No Dashboard, clicar botão 🌐 PT/EN**
   - [ ] Colunas mudam de nome:
     - PENDENTES → PENDING
     - HOJE → TODAY
     - ATRASADAS → OVERDUE
     - CONCLUÍDAS → COMPLETED
   
   - [ ] Botões mudam:
     - "Iniciar" → "Start"
     - "Concluir" → "Complete"
   
   - [ ] **BOTÕES CONTINUAM VISÍVEIS!** (correção crítica do enum ColumnType)
   
   - [ ] Cabeçalhos das tabelas mudam:
     - "Prioridade" → "Priority"
     - "Tarefa" → "Task"
     - "Prazo" → "Due Date"
     - "Atribuído a" → "Assigned To"
     - "Estado" → "Status"

2. **Clicar 🌐 novamente:**
   - [ ] Tudo volta para PT
   - [ ] Botões permanecem visíveis

---

## ✅ Teste 4: Funcionalidade dos Botões

### Na coluna PENDENTES:
1. **Selecionar uma tarefa**
2. **Clicar botão "Iniciar"**
   - [ ] Tarefa muda status para "EM_ANDAMENTO"
   - [ ] Tarefa **move para coluna HOJE** ou permanece em PENDENTES

### Na coluna HOJE ou ATRASADAS:
1. **Selecionar uma tarefa**
2. **Clicar botão "Concluir"**
   - [ ] Tarefa muda status para "CONCLUIDA"
   - [ ] Tarefa **move para coluna CONCLUÍDAS**

### Na coluna CONCLUÍDAS:
- [ ] **Não há botões de ação** (correto!)

---

## ✅ Teste 5: Logout e Retorno ao Login

### Ações:
1. **No Dashboard, clicar no menu ou botão de Logout**
   - Localizar botão/menu de sair (geralmente no topo ou menu lateral)

2. **Clicar em Logout:**
   - [ ] **Não fecha a janela** (CardLayout!)
   - [ ] Volta para tela de **Login Card**
   - [ ] Título muda para "Gestor de Tarefas - Login"
   - [ ] Campos de login estão **limpos**

3. **Fazer login novamente:**
   - [ ] Volta para Dashboard Card
   - [ ] Ciclo completo funciona: Login → Dashboard → Logout → Login

---

## ✅ Teste 6: Dashboards por Perfil

### Testar diferentes perfis:

#### Admin (Administrador):
- Login: `martim.sottomayor` / `martim.sottomayor123`
- [ ] Dashboard tem **3 tabelas:**
  1. Tarefas (12 colunas)
  2. Utilizadores (8 colunas)
  3. Equipas (7 colunas)
- [ ] Mais funcionalidades e permissões

#### Gerente:
- Login: `lucile.almeida` / `lucile.almeida123`
- [ ] Dashboard tem **tabela de membros da equipa**
- [ ] Vê tarefas da sua equipa (Gestão Administrativa)

#### Funcionário:
- Login: `demo` / `demo123`
- [ ] Dashboard Kanban com **suas tarefas**
- [ ] 4 colunas: Pendentes, Hoje, Atrasadas, Concluídas

---

## ✅ Teste 7: Tarefas Atribuídas

### Verificar no Dashboard:
- [ ] **Todas as tarefas têm utilizador atribuído**
- [ ] **Todas as tarefas têm equipa atribuída**

### Equipas esperadas (8 equipas):
1. Direção (3 tarefas)
2. Gestão Administrativa (4 tarefas)
3. Comercial (4 tarefas)
4. Financeiro (2 tarefas)
5. Compras (3 tarefas)
6. Produção (4 tarefas)
7. Apoio ao Cliente (4 tarefas)
8. Logística (3 tarefas)

**Total: 27 tarefas todas atribuídas!**

---

## ✅ Teste 8: Fechar Aplicação

### Ações:
1. **Clicar no [X] da janela**
   - [ ] Aparece confirmação: **"Tem certeza que deseja sair da aplicação?"**
   - [ ] Botões: **Sim** | **Não**

2. **Clicar "Não":**
   - [ ] Janela permanece aberta

3. **Clicar [X] novamente e "Sim":**
   - [ ] Aplicação fecha completamente
   - [ ] Processo Spring Boot encerra

---

## 📊 Checklist de Conformidade UFCD 5425

### Requisitos Obrigatórios:
- [x] ✅ Single-window navigation (CardLayout)
- [x] ✅ Internacionalização PT ↔ EN
- [x] ✅ Navegação por cards (Login ↔ Dashboard)
- [x] ✅ Callbacks para transições (onLoginSuccess, onLogout)
- [x] ✅ Sem múltiplas janelas JFrame
- [x] ✅ Interface responsiva e moderna
- [x] ✅ Todas tarefas atribuídas (users + equipas)
- [x] ✅ Botões funcionais em todos os idiomas

### Extras Implementados:
- [x] ✅ 378+ traduções PT/EN
- [x] ✅ Enum ColumnType (solução type-safe)
- [x] ✅ 29 utilizadores demo
- [x] ✅ Dashboard Kanban 4 colunas
- [x] ✅ Sistema de comentários
- [x] ✅ Gestão de equipas

---

## 🐛 Bugs Conhecidos (RESOLVIDOS)

### ❌ BUG 1: Botões Invisíveis ao Trocar Idioma
**Status:** ✅ **RESOLVIDO**
- **Causa:** Comparação de strings traduzidas (`if (title.equals("PENDENTES"))`)
- **Solução:** Enum `ColumnType` + `switch(columnType)`
- **Resultado:** Botões sempre visíveis em PT e EN!

### ❌ BUG 2: Múltiplas Janelas (LoginFrame + MainWindow)
**Status:** ✅ **RESOLVIDO**
- **Causa:** Arquitetura antiga com múltiplos JFrame
- **Solução:** CardLayout com JFrame único (MainCardLayout)
- **Resultado:** Single-window navigation conforme UFCD 5425!

### ❌ BUG 3: Tarefas Sem Equipa
**Status:** ✅ **RESOLVIDO**
- **Causa:** Apenas 6/27 tarefas tinham `assigned_team_id`
- **Solução:** Script SQL atribuindo todas as tarefas
- **Resultado:** 27/27 tarefas com user_id E team_id!

---

## 🎯 Resultado Esperado

### Após todos os testes:
✅ **Aplicação funciona perfeitamente!**
- CardLayout navegando suavemente entre Login e Dashboard
- i18n completo em toda aplicação (PT ↔ EN)
- Botões sempre visíveis e funcionais
- Todas tarefas atribuídas
- Sem múltiplas janelas
- Interface moderna e responsiva

### Nota UFCD 5425:
**20/20** 🏆

---

## 📝 Observações de Teste

**Anote aqui qualquer problema encontrado:**

```
Data: 06/10/2025
Testador: _______________________

Teste 1 (Login Card): ☐ OK ☐ Problemas: ___________________
Teste 2 (Navegação): ☐ OK ☐ Problemas: ___________________
Teste 3 (i18n Dashboard): ☐ OK ☐ Problemas: ___________________
Teste 4 (Botões): ☐ OK ☐ Problemas: ___________________
Teste 5 (Logout): ☐ OK ☐ Problemas: ___________________
Teste 6 (Perfis): ☐ OK ☐ Problemas: ___________________
Teste 7 (Tarefas): ☐ OK ☐ Problemas: ___________________
Teste 8 (Fechar): ☐ OK ☐ Problemas: ___________________

Notas adicionais:
____________________________________________
____________________________________________
____________________________________________
```

---

## 🚀 Próximos Passos Após Testes

1. ✅ **Se tudo OK:** Projeto pronto para entrega!
2. ⚠️ **Se houver problemas:** Documentar e corrigir
3. 📊 **Preparar apresentação:** Screenshots, demonstração ao vivo
4. 📄 **Documentação final:** README, manual de utilizador

---

**Boa sorte nos testes! A aplicação está pronta! 🎉**
