package com.gestortarefas.gui;

import com.gestortarefas.util.HttpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;

/**
 * Dialog para criar/editar tarefas
 */
public class TaskDialog extends JDialog {
    
    private Long taskId; // null para nova tarefa
    private Map<String, Object> currentUser;
    
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> priorityCombo;
    private JComboBox<String> statusCombo;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    private JButton saveButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    
    // Campos de atribuição
    private JRadioButton assignToUserRadio;
    private JRadioButton assignToTeamRadio;
    private JComboBox<UserItem> userCombo;
    private JComboBox<TeamItem> teamCombo;
    private ButtonGroup assignmentGroup;
    
    private boolean isEditMode;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Constructor para EDITAR tarefa existente
    public TaskDialog(Window parent, Long taskId, Map<String, Object> user) {
        super(parent, "Editar Tarefa", ModalityType.APPLICATION_MODAL);
        System.out.println("\n\n=== TaskDialog EDIT CONSTRUCTOR CALLED ===");
        System.out.println("TaskDialog: Creating EDIT task dialog for taskId: " + taskId);
        System.out.println("TaskDialog: User info: " + user);
        System.out.println("=== TaskDialog EDIT CONSTRUCTOR DONE ===\n");
        this.taskId = taskId;
        this.currentUser = user;
        this.isEditMode = true;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        System.out.println("TaskDialog: Loading task data for edit mode...");
        loadTaskData();
        System.out.println("TaskDialog: Dialog fully initialized with assignment components");
    }
    
    // Constructor para CRIAR nova tarefa
    public TaskDialog(Window parent, Map<String, Object> user) {
        super(parent, "Nova Tarefa", ModalityType.APPLICATION_MODAL);
        System.out.println("\n\n=== TaskDialog NEW CONSTRUCTOR CALLED ===");
        System.out.println("TaskDialog: Creating NEW task dialog");
        System.out.println("TaskDialog: User info: " + user);
        System.out.println("=== TaskDialog NEW CONSTRUCTOR DONE ===\n");
        this.taskId = null;
        this.currentUser = user;
        this.isEditMode = false;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        System.out.println("TaskDialog: Dialog fully initialized with assignment components for NEW task");
    }

    private void initializeComponents() {
        // Configurar estilo moderno para componentes
        titleField = new JTextField(30);
        titleField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        descriptionArea = new JTextArea(4, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        priorityCombo = new JComboBox<>(new String[]{"BAIXA", "NORMAL", "ALTA", "URGENTE"});
        priorityCombo.setSelectedItem("NORMAL");
        priorityCombo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        statusCombo = new JComboBox<>(new String[]{"PENDENTE", "EM_ANDAMENTO", "CONCLUIDA", "CANCELADA"});
        statusCombo.setSelectedItem("PENDENTE");
        statusCombo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        // Spinner para data
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        
        // Spinner para hora
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        SpinnerDateModel timeModel = new SpinnerDateModel(cal.getTime(), null, null, Calendar.MINUTE);
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        
        saveButton = new JButton(isEditMode ? "✓ Atualizar" : "✓ Criar");
        saveButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        saveButton.setBackground(new Color(34, 139, 34));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        cancelButton = new JButton("✕ Cancelar");
        cancelButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(220, 53, 69));
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        // Se for nova tarefa, desabilitar campo status
        if (!isEditMode) {
            statusCombo.setEnabled(false);
        }
        
        // Componentes de atribuição com estilo moderno
        System.out.println("TaskDialog: Inicializando componentes de atribuição...");
        assignToUserRadio = new JRadioButton("👤 Atribuir a utilizador específico"); 
        assignToUserRadio.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        assignToUserRadio.setFocusPainted(false);
        
        assignToTeamRadio = new JRadioButton("👥 Atribuir a equipa (tarefa coletiva)");
        assignToTeamRadio.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        assignToTeamRadio.setFocusPainted(false);
        
        assignmentGroup = new ButtonGroup();
        assignmentGroup.add(assignToUserRadio);
        assignmentGroup.add(assignToTeamRadio);
        
        userCombo = new JComboBox<>();
        userCombo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        
        teamCombo = new JComboBox<>();
        teamCombo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        
        // Por padrão, selecionar atribuição individual
        assignToUserRadio.setSelected(true);
        System.out.println("TaskDialog: Radio button 'Atribuir a utilizador específico' selecionado por padrão");
        
        // Carregar dados
        loadUsersAndTeams();
        updateAssignmentControls();
        System.out.println("TaskDialog: Componentes de atribuição inicializados");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Painel principal com cor de fundo suave
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(248, 249, 250));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Cabeçalho da seção principal
        JLabel titleSectionLabel = new JLabel("📋 Informações da Tarefa");
        titleSectionLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        titleSectionLabel.setForeground(new Color(52, 58, 64));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        mainPanel.add(titleSectionLabel, gbc);
        
        // Linha separadora
        JSeparator separator1 = new JSeparator();
        separator1.setForeground(new Color(200, 200, 200));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 0, 15, 0);
        mainPanel.add(separator1, gbc);
        
        // Título com validação visual
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel titleLabel = new JLabel("*Título:");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        titleLabel.setForeground(new Color(73, 80, 87));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        mainPanel.add(titleLabel, gbc);
        
        // Adicionar listener para validação em tempo real
        titleField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateTitle(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateTitle(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateTitle(); }
            
            private void validateTitle() {
                String title = titleField.getText().trim();
                if (title.isEmpty()) {
                    titleField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 53, 69), 2),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                    ));
                } else if (title.length() < 3) {
                    titleField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 193, 7), 2),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                    ));
                } else {
                    titleField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(34, 139, 34), 2),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                    ));
                }
            }
        });
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        mainPanel.add(titleField, gbc);
        gbc.weightx = 0.0;
        
        // Descrição
        JLabel descLabel = new JLabel("Descrição:");
        descLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        descLabel.setForeground(new Color(73, 80, 87));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(descLabel, gbc);
        
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        descScrollPane.setPreferredSize(new Dimension(400, 100));
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        mainPanel.add(descScrollPane, gbc);
        gbc.weightx = 0.0;
        
        // Painel para prioridade e status
        JPanel priorityStatusPanel = new JPanel(new GridBagLayout());
        priorityStatusPanel.setOpaque(false);
        GridBagConstraints psgbc = new GridBagConstraints();
        psgbc.insets = new Insets(5, 5, 5, 15);
        psgbc.anchor = GridBagConstraints.WEST;
        
        // Prioridade
        JLabel priorityLabel = new JLabel("⚠ Prioridade:");
        priorityLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        priorityLabel.setForeground(new Color(73, 80, 87));
        psgbc.gridx = 0; psgbc.gridy = 0;
        priorityStatusPanel.add(priorityLabel, psgbc);
        
        psgbc.gridx = 1;
        priorityStatusPanel.add(priorityCombo, psgbc);
        
        // Status (apenas para edição)
        if (isEditMode) {
            JLabel statusLabel = new JLabel("📊 Status:");
            statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            statusLabel.setForeground(new Color(73, 80, 87));
            psgbc.gridx = 2;
            priorityStatusPanel.add(statusLabel, psgbc);
            
            psgbc.gridx = 3;
            priorityStatusPanel.add(statusCombo, psgbc);
        }
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(priorityStatusPanel, gbc);
        
        // Seção de Atribuição
        int currentRow = 5;
        
        // Cabeçalho da seção de atribuição
        JLabel assignmentTitle = new JLabel("🎯 Atribuição");
        assignmentTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        assignmentTitle.setForeground(new Color(52, 58, 64));
        gbc.gridx = 0; gbc.gridy = currentRow; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 10, 5, 10);
        mainPanel.add(assignmentTitle, gbc);
        
        // Linha separadora
        JSeparator separator2 = new JSeparator();
        separator2.setForeground(new Color(200, 200, 200));
        gbc.gridx = 0; gbc.gridy = ++currentRow; gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 0, 15, 0);
        mainPanel.add(separator2, gbc);
        
        // Painel de atribuição organizado
        JPanel assignmentPanel = new JPanel(new GridBagLayout());
        assignmentPanel.setOpaque(false);
        assignmentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints agbc = new GridBagConstraints();
        agbc.insets = new Insets(8, 8, 8, 8);
        agbc.anchor = GridBagConstraints.WEST;
        agbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Radio buttons de atribuição
        agbc.gridx = 0; agbc.gridy = 0; agbc.gridwidth = 2;
        assignmentPanel.add(assignToUserRadio, agbc);
        
        agbc.gridx = 0; agbc.gridy = 1; agbc.gridwidth = 2; agbc.weightx = 1.0;
        agbc.insets = new Insets(5, 25, 10, 8);
        assignmentPanel.add(userCombo, agbc);
        
        agbc.gridx = 0; agbc.gridy = 2; agbc.gridwidth = 2; agbc.weightx = 0.0;
        agbc.insets = new Insets(15, 8, 8, 8);
        assignmentPanel.add(assignToTeamRadio, agbc);
        
        agbc.gridx = 0; agbc.gridy = 3; agbc.gridwidth = 2; agbc.weightx = 1.0;
        agbc.insets = new Insets(5, 25, 8, 8);
        assignmentPanel.add(teamCombo, agbc);
        
        gbc.gridx = 0; gbc.gridy = ++currentRow; gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10); gbc.weightx = 1.0;
        mainPanel.add(assignmentPanel, gbc);
        gbc.weightx = 0.0;
        
        // Data limite
        JLabel dateLabel = new JLabel("📅 Data Limite:");
        dateLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        dateLabel.setForeground(new Color(73, 80, 87));
        gbc.gridx = 0; gbc.gridy = ++currentRow; gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 10, 10, 10);
        mainPanel.add(dateLabel, gbc);
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        datePanel.setOpaque(false);
        
        // Estilizar spinners
        dateSpinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        timeSpinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        
        datePanel.add(dateSpinner);
        JLabel asLabel = new JLabel("às");
        asLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        asLabel.setForeground(new Color(108, 117, 125));
        datePanel.add(asLabel);
        datePanel.add(timeSpinner);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        mainPanel.add(datePanel, gbc);
        
        // Botões com estilo moderno
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        saveButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.setPreferredSize(new Dimension(120, 40));
        
        // Efeitos hover nos botões
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(new Color(25, 135, 84));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(new Color(34, 139, 34));
            }
        });
        
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cancelButton.setBackground(new Color(200, 35, 51));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cancelButton.setBackground(new Color(220, 53, 69));
            }
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = ++currentRow; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = ++currentRow; gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 10, 10, 10);
        mainPanel.add(statusLabel, gbc);
        
        System.out.println("========================================");
        System.out.println("TaskDialog: TOTAL DE LINHAS NO LAYOUT: " + (currentRow + 1));
        System.out.println("TaskDialog: Layout moderno aplicado com sucesso");
        System.out.println("========================================");
        
        // ScrollPane com estilo moderno
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Painel de notas com estilo melhorado
        JPanel notePanel = new JPanel(new BorderLayout());
        notePanel.setBackground(new Color(233, 236, 239));
        notePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        
        JLabel noteLabel = new JLabel(
            "<html><div style='text-align: center;'>" +
            "<span style='color: #dc3545; font-weight: bold;'>*</span> Campos obrigatórios " +
            "<span style='color: #6c757d;'>•</span> " +
            "Data formato: <span style='font-family: monospace;'>dd/MM/yyyy HH:mm</span>" +
            "</div></html>", 
            JLabel.CENTER
        );
        noteLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        noteLabel.setForeground(new Color(108, 117, 125));
        
        notePanel.add(noteLabel, BorderLayout.CENTER);
        add(notePanel, BorderLayout.SOUTH);
        
        // Configuração da janela otimizada
        setSize(650, 750);
        setMinimumSize(new Dimension(550, 600));
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Ícone da janela (opcional)
        // setIconImage(...);
        
        System.out.println("TaskDialog: Janela configurada com layout moderno 650x750px");
    }

    private void setupEventListeners() {
        saveButton.addActionListener(e -> saveTask());
        cancelButton.addActionListener(e -> dispose());
        
        // Listeners para os radio buttons de atribuição
        assignToUserRadio.addActionListener(e -> updateAssignmentControls());
        assignToTeamRadio.addActionListener(e -> updateAssignmentControls());
    }

    private void loadTaskData() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = HttpUtil.get("/tasks/" + taskId);
                Map<String, Object> result = HttpUtil.parseResponse(response.body());

                if ((Boolean) result.get("success")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> task = (Map<String, Object>) result.get("task");
                    
                    SwingUtilities.invokeLater(() -> {
                        populateFields(task);
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        showStatus("Erro ao carregar tarefa: " + result.get("message"), Color.RED);
                    });
                }

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    showStatus("Erro de conexão: " + e.getMessage(), Color.RED);
                });
            }
        }).start();
    }

    private void populateFields(Map<String, Object> task) {
        titleField.setText((String) task.get("title"));
        descriptionArea.setText((String) task.get("description"));
        priorityCombo.setSelectedItem((String) task.get("priority"));
        statusCombo.setSelectedItem((String) task.get("status"));
        
        // Formatar data limite
        String dueDate = (String) task.get("dueDate");
        if (dueDate != null && !dueDate.isEmpty()) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(dueDate);
                
                // Configurar data no dateSpinner
                Calendar dateCal = Calendar.getInstance();
                dateCal.set(dateTime.getYear(), dateTime.getMonthValue() - 1, dateTime.getDayOfMonth());
                dateSpinner.setValue(dateCal.getTime());
                
                // Configurar hora no timeSpinner
                Calendar timeCal = Calendar.getInstance();
                timeCal.set(Calendar.HOUR_OF_DAY, dateTime.getHour());
                timeCal.set(Calendar.MINUTE, dateTime.getMinute());
                timeSpinner.setValue(timeCal.getTime());
                
            } catch (Exception e) {
                // Se não conseguir parsear, mantém valores padrão
                System.out.println("Erro ao parsear data: " + e.getMessage());
            }
        }
    }

    private void saveTask() {
        // Validar campos
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showStatus("Título é obrigatório", new Color(220, 53, 69));
            titleField.requestFocus();
            return;
        }
        
        if (title.length() < 3) {
            showStatus("Título deve ter pelo menos 3 caracteres", new Color(220, 53, 69));
            titleField.requestFocus();
            return;
        }

        String description = descriptionArea.getText().trim();
        String priority = (String) priorityCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();
        
        // Obter data e hora dos spinners
        String dueDateFormatted = null;
        try {
            Calendar dateCal = Calendar.getInstance();
            dateCal.setTime((java.util.Date) dateSpinner.getValue());
            
            Calendar timeCal = Calendar.getInstance();
            timeCal.setTime((java.util.Date) timeSpinner.getValue());
            
            // Combinar data e hora
            LocalDateTime dueDate = LocalDateTime.of(
                dateCal.get(Calendar.YEAR),
                dateCal.get(Calendar.MONTH) + 1, // Calendar.MONTH é 0-indexed
                dateCal.get(Calendar.DAY_OF_MONTH),
                timeCal.get(Calendar.HOUR_OF_DAY),
                timeCal.get(Calendar.MINUTE)
            );
            
            dueDateFormatted = dueDate.toString();
        } catch (Exception e) {
            showStatus("Formato de data/hora inválido", new Color(220, 53, 69));
            return;
        }

        final String finalDueDateFormatted = dueDateFormatted;

        saveButton.setEnabled(false);
        showStatus(isEditMode ? "Atualizando tarefa..." : "Criando tarefa...", new Color(255, 193, 7));

        // Executar operação numa thread separada
        new Thread(() -> {
            try {
                Map<String, Object> taskData = new HashMap<>();
                taskData.put("title", title);
                taskData.put("description", description);
                taskData.put("priority", priority);
                taskData.put("userId", ((Number) currentUser.get("id")).longValue());
                
                // Adicionar dados de atribuição com debug
                System.out.println("TaskDialog: Processando atribuição...");
                System.out.println("TaskDialog: assignToUserRadio.isSelected() = " + assignToUserRadio.isSelected());
                System.out.println("TaskDialog: assignToTeamRadio.isSelected() = " + assignToTeamRadio.isSelected());
                System.out.println("TaskDialog: userCombo.getSelectedItem() = " + userCombo.getSelectedItem());
                System.out.println("TaskDialog: teamCombo.getSelectedItem() = " + teamCombo.getSelectedItem());
                
                if (assignToUserRadio.isSelected() && userCombo.getSelectedItem() != null) {
                    UserItem selectedUser = (UserItem) userCombo.getSelectedItem();
                    taskData.put("assignedUserId", selectedUser.getId());
                    taskData.put("isAssignedToTeam", false);
                    System.out.println("TaskDialog: Atribuindo a utilizador: " + selectedUser.getId());
                } else if (assignToTeamRadio.isSelected() && teamCombo.getSelectedItem() != null) {
                    TeamItem selectedTeam = (TeamItem) teamCombo.getSelectedItem();
                    taskData.put("assignedTeamId", selectedTeam.getId());
                    taskData.put("isAssignedToTeam", true);
                    System.out.println("TaskDialog: Atribuindo a equipa: " + selectedTeam.getId());
                } else {
                    System.out.println("TaskDialog: Nenhuma atribuição válida encontrada - criando tarefa sem atribuição");
                }
                
                if (finalDueDateFormatted != null) {
                    taskData.put("dueDate", finalDueDateFormatted);
                }
                
                HttpResponse<String> response;
                if (isEditMode) {
                    taskData.put("status", status);
                    response = HttpUtil.put("/tasks/" + taskId, taskData);
                } else {
                    response = HttpUtil.post("/tasks", taskData);
                }
                
                Map<String, Object> result = HttpUtil.parseResponse(response.body());

                SwingUtilities.invokeLater(() -> {
                    if ((Boolean) result.get("success")) {
                        showStatus(isEditMode ? "Tarefa atualizada com sucesso!" : "Tarefa criada com sucesso!", 
                                 new Color(0, 120, 0));
                        
                        // Fechar dialog após 1.5 segundos
                        Timer timer = new Timer(1500, e -> dispose());
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        showStatus((String) result.get("message"), new Color(220, 53, 69));
                        saveButton.setEnabled(true);
                    }
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    showStatus("Erro de conexão: " + e.getMessage(), new Color(220, 53, 69));
                    saveButton.setEnabled(true);
                });
            }
        }).start();
    }

    private void showStatus(String message, Color color) {
        String icon = "";
        if (color.equals(Color.RED) || color.equals(new Color(220, 53, 69))) {
            icon = "❌ ";
        } else if (color.equals(Color.GREEN) || color.equals(new Color(34, 139, 34))) {
            icon = "✓ ";
        } else if (color.equals(Color.ORANGE) || color.equals(new Color(255, 193, 7))) {
            icon = "⏳ ";
        } else if (color.equals(Color.BLUE) || color.equals(new Color(0, 123, 255))) {
            icon = "ℹ ";
        }
        
        statusLabel.setText(icon + message);
        statusLabel.setForeground(color);
        
        // Animação simples de fade-in
        statusLabel.setVisible(false);
        Timer timer = new Timer(50, e -> statusLabel.setVisible(true));
        timer.setRepeats(false);
        timer.start();
        
        // Auto-hide para mensagens de sucesso após 3 segundos
        if (color.equals(Color.GREEN) || color.equals(new Color(34, 139, 34))) {
            Timer hideTimer = new Timer(3000, e -> {
                statusLabel.setText(" ");
                statusLabel.setVisible(true);
            });
            hideTimer.setRepeats(false);
            hideTimer.start();
        }
    }
    
    /**
     * Carregar utilizadores e equipas do servidor
     */
    private void loadUsersAndTeams() {
        new Thread(() -> {
            try {
                System.out.println("TaskDialog: Carregando utilizadores e equipas...");
                
                // Carregar utilizadores
                HttpResponse<String> usersResponse = HttpUtil.get("/users");
                Map<String, Object> usersResult = HttpUtil.parseResponse(usersResponse.body());
                
                if ((Boolean) usersResult.get("success")) {
                    @SuppressWarnings("unchecked")
                    java.util.List<Map<String, Object>> users = (java.util.List<Map<String, Object>>) usersResult.get("users");
                    
                    System.out.println("TaskDialog: Carregados " + users.size() + " utilizadores");
                    
                    SwingUtilities.invokeLater(() -> {
                        userCombo.removeAllItems();
                        int activeUsers = 0;
                        for (Map<String, Object> user : users) {
                            if ((Boolean) user.get("active")) {
                                userCombo.addItem(new UserItem(user));
                                activeUsers++;
                            }
                        }
                        System.out.println("TaskDialog: Adicionados " + activeUsers + " utilizadores ativos ao combo");
                        
                        // Se não há itens, garantir que o radio button seja desabilitado
                        if (userCombo.getItemCount() == 0) {
                            assignToUserRadio.setEnabled(false);
                            System.out.println("TaskDialog: Desabilitando radio button de utilizador (sem utilizadores)");
                        } else {
                            // Por padrão, selecionar o primeiro utilizador se atribuição individual estiver selecionada
                            if (assignToUserRadio.isSelected() && userCombo.getSelectedItem() == null) {
                                userCombo.setSelectedIndex(0);
                                System.out.println("TaskDialog: Selecionado primeiro utilizador por padrão");
                            }
                        }
                    });
                } else {
                    System.out.println("TaskDialog: Erro ao carregar utilizadores: " + usersResult.get("message"));
                }
                
                // Carregar equipas
                HttpResponse<String> teamsResponse = HttpUtil.get("/teams/summary");
                @SuppressWarnings("unchecked")
                java.util.List<Map<String, Object>> teams = objectMapper.readValue(teamsResponse.body(), 
                    objectMapper.getTypeFactory().constructCollectionType(java.util.List.class, Map.class));
                
                System.out.println("TaskDialog: Carregadas " + teams.size() + " equipas");
                
                SwingUtilities.invokeLater(() -> {
                    teamCombo.removeAllItems();
                    int activeTeams = 0;
                    for (Map<String, Object> team : teams) {
                        if ((Boolean) team.get("active")) {
                            teamCombo.addItem(new TeamItem(team));
                            activeTeams++;
                        }
                    }
                    System.out.println("TaskDialog: Adicionadas " + activeTeams + " equipas ativas ao combo");
                    
                    // Se não há itens, garantir que o radio button seja desabilitado
                    if (teamCombo.getItemCount() == 0) {
                        assignToTeamRadio.setEnabled(false);
                        System.out.println("TaskDialog: Desabilitando radio button de equipa (sem equipas)");
                    } else {
                        // Por padrão, selecionar a primeira equipa se atribuição de equipa estiver selecionada
                        if (assignToTeamRadio.isSelected() && teamCombo.getSelectedItem() == null) {
                            teamCombo.setSelectedIndex(0);
                            System.out.println("TaskDialog: Selecionada primeira equipa por padrão");
                        }
                    }
                    
                    // Atualizar controles após carregar todos os dados
                    updateAssignmentControls();
                });
                
            } catch (Exception e) {
                System.out.println("TaskDialog: Erro ao carregar dados: " + e.getMessage());
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    showStatus("Erro ao carregar dados: " + e.getMessage(), Color.RED);
                });
            }
        }).start();
    }
    
    /**
     * Atualizar controles de atribuição baseado na seleção
     */
    private void updateAssignmentControls() {
        userCombo.setEnabled(assignToUserRadio.isSelected());
        teamCombo.setEnabled(assignToTeamRadio.isSelected());
    }
    
    /**
     * Classe para representar um utilizador no ComboBox
     */
    private static class UserItem {
        private final Long id;
        private final String fullName;
        private final String teamName;
        
        public UserItem(Map<String, Object> userData) {
            this.id = ((Number) userData.get("id")).longValue();
            this.fullName = (String) userData.get("fullName");
            this.teamName = (String) userData.get("teamName");
        }
        
        public Long getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return fullName + (teamName != null ? " (" + teamName + ")" : "");
        }
    }
    
    /**
     * Classe para representar uma equipa no ComboBox
     */
    private static class TeamItem {
        private final Long id;
        private final String name;
        private final String description;
        
        public TeamItem(Map<String, Object> teamData) {
            this.id = ((Number) teamData.get("id")).longValue();
            this.name = (String) teamData.get("name");
            this.description = (String) teamData.get("description");
        }
        
        public Long getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return name + (description != null ? " - " + description : "");
        }
    }
}