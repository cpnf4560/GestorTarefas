package com.gestortarefas.gui;

import com.gestortarefas.util.HttpUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private MainFrame parentFrame;
    
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> priorityCombo;
    private JComboBox<String> statusCombo;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    private JButton saveButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    
    private boolean isEditMode;

    public TaskDialog(MainFrame parent, Long taskId, Map<String, Object> user) {
        super(parent, taskId == null ? "Nova Tarefa" : "Editar Tarefa", true);
        this.parentFrame = parent;
        this.taskId = taskId;
        this.currentUser = user;
        this.isEditMode = taskId != null;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        if (isEditMode) {
            loadTaskData();
        }
    }

    private void initializeComponents() {
        titleField = new JTextField(30);
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        priorityCombo = new JComboBox<>(new String[]{"BAIXA", "NORMAL", "ALTA", "URGENTE"});
        priorityCombo.setSelectedItem("NORMAL");
        
        statusCombo = new JComboBox<>(new String[]{"PENDENTE", "EM_ANDAMENTO", "CONCLUIDA", "CANCELADA"});
        statusCombo.setSelectedItem("PENDENTE");
        
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
        
        saveButton = new JButton(isEditMode ? "Atualizar" : "Criar");
        cancelButton = new JButton("Cancelar");
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        
        // Se for nova tarefa, desabilitar campo status
        if (!isEditMode) {
            statusCombo.setEnabled(false);
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Título
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("*Título:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        mainPanel.add(titleField, gbc);
        
        // Descrição
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JScrollPane(descriptionArea), gbc);
        
        // Prioridade
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Prioridade:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 1;
        mainPanel.add(priorityCombo, gbc);
        
        // Status (apenas para edição)
        if (isEditMode) {
            gbc.gridx = 2;
            mainPanel.add(new JLabel("Status:"), gbc);
            gbc.gridy = 3; gbc.gridx = 2;
            mainPanel.add(statusCombo, gbc);
        }
        
        // Data limite
        gbc.gridx = 0; gbc.gridy = isEditMode ? 4 : 3; gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Data Limite:"), gbc);
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.add(dateSpinner);
        datePanel.add(new JLabel("às"));
        datePanel.add(timeSpinner);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        mainPanel.add(datePanel, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        saveButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = isEditMode ? 5 : 4; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = isEditMode ? 6 : 5; gbc.gridwidth = 3;
        mainPanel.add(statusLabel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Nota
        JLabel noteLabel = new JLabel("<html><center>* Campos obrigatórios<br/>Data formato: dd/MM/yyyy HH:mm</center></html>", JLabel.CENTER);
        noteLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));
        noteLabel.setForeground(Color.GRAY);
        noteLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        add(noteLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setupEventListeners() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTask();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
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
            showStatus("Título é obrigatório", Color.RED);
            titleField.requestFocus();
            return;
        }
        
        if (title.length() < 3) {
            showStatus("Título deve ter pelo menos 3 caracteres", Color.RED);
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
            showStatus("Erro ao processar data e hora", Color.RED);
            return;
        }

        final String finalDueDateFormatted = dueDateFormatted;

        saveButton.setEnabled(false);
        showStatus(isEditMode ? "Atualizando tarefa..." : "Criando tarefa...", Color.ORANGE);

        // Executar operação numa thread separada
        new Thread(() -> {
            try {
                Map<String, Object> taskData = new HashMap<>();
                taskData.put("title", title);
                taskData.put("description", description);
                taskData.put("priority", priority);
                taskData.put("userId", ((Number) currentUser.get("id")).longValue());
                
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
                        
                        // Atualizar tabela principal
                        parentFrame.refreshTasks();
                        
                        // Fechar dialog após 1.5 segundos
                        Timer timer = new Timer(1500, e -> dispose());
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        showStatus((String) result.get("message"), Color.RED);
                        saveButton.setEnabled(true);
                    }
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    showStatus("Erro de conexão: " + e.getMessage(), Color.RED);
                    saveButton.setEnabled(true);
                });
            }
        }).start();
    }

    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
}