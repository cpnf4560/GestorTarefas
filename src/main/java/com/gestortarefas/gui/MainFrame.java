package com.gestortarefas.gui;

import com.gestortarefas.util.HttpUtil;
import com.gestortarefas.util.CSVExporter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Janela principal da aplicação com gestão de tarefas
 */
public class MainFrame extends JFrame {
    
    private Map<String, Object> currentUser;
    private JTable tasksTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    private JTextField searchField;
    private JLabel userLabel;
    private JLabel statsLabel;
    
    // Constantes para colunas da tabela
    private static final String[] COLUMN_NAMES = {
        "ID", "Título", "Status", "Prioridade", "Criado", "Data Limite", "Descrição"
    };

    public MainFrame(Map<String, Object> user) {
        this.currentUser = user;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadTasks();
        updateStats();
    }

    private void initializeComponents() {
        setTitle("Gestor de Tarefas - " + currentUser.get("username"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800); // Definir tamanho padrão adequado
        setLocationRelativeTo(null); // Centralizar na tela
        setMinimumSize(new Dimension(800, 600)); // Tamanho mínimo
        
        // Componentes da interface
        userLabel = new JLabel("Utilizador: " + currentUser.get("fullName"));
        userLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        
        statsLabel = new JLabel("Carregando estatísticas...");
        
        // Tabela de tarefas
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela só de leitura
            }
        };
        
        tasksTable = new JTable(tableModel);
        tasksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tasksTable.setRowHeight(25);
        
        // Configurar larguras das colunas
        tasksTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tasksTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Título
        tasksTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Status
        tasksTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Prioridade
        tasksTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Criado
        tasksTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Data Limite
        tasksTable.getColumnModel().getColumn(6).setPreferredWidth(300); // Descrição
        
        // Ocultar coluna ID
        tasksTable.getColumnModel().getColumn(0).setMinWidth(0);
        tasksTable.getColumnModel().getColumn(0).setMaxWidth(0);
        tasksTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        // Filtros
        statusFilter = new JComboBox<>(new String[]{"Todos", "PENDENTE", "EM_ANDAMENTO", "CONCLUIDA", "CANCELADA"});
        searchField = new JTextField(20);
        
        // Configurar ordenação
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tasksTable.setRowSorter(sorter);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Painel superior - informações do utilizador e filtros
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.add(userLabel);
        userPanel.add(Box.createHorizontalStrut(20));
        userPanel.add(statsLabel);
        
        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filtersPanel.add(new JLabel("Filtrar por status:"));
        filtersPanel.add(statusFilter);
        filtersPanel.add(Box.createHorizontalStrut(10));
        filtersPanel.add(new JLabel("Pesquisar:"));
        filtersPanel.add(searchField);
        
        topPanel.add(userPanel, BorderLayout.WEST);
        topPanel.add(filtersPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Painel central - tabela de tarefas
        JScrollPane tableScrollPane = new JScrollPane(tasksTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Lista de Tarefas", 
            TitledBorder.LEFT, TitledBorder.TOP));
        
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Painel inferior - botões de ação
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        JButton newTaskButton = new JButton("Nova Tarefa");
        JButton editTaskButton = new JButton("Editar Tarefa");
        JButton deleteTaskButton = new JButton("Eliminar Tarefa");
        JButton completeTaskButton = new JButton("Marcar Concluída");
        JButton refreshButton = new JButton("Actualizar");
        JButton exportButton = new JButton("Exportar CSV");
        JButton logoutButton = new JButton("Logout");
        
        buttonPanel.add(newTaskButton);
        buttonPanel.add(editTaskButton);
        buttonPanel.add(deleteTaskButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(completeTaskButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(logoutButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Event listeners dos botões
        newTaskButton.addActionListener(e -> openNewTaskDialog());
        editTaskButton.addActionListener(e -> editSelectedTask());
        deleteTaskButton.addActionListener(e -> deleteSelectedTask());
        completeTaskButton.addActionListener(e -> completeSelectedTask());
        refreshButton.addActionListener(e -> {
            loadTasks();
            updateStats();
        });
        exportButton.addActionListener(e -> exportTasksToCSV());
        logoutButton.addActionListener(e -> logout());
    }

    private void setupEventListeners() {
        // Filtro por status
        statusFilter.addActionListener(e -> filterTasks());
        
        // Pesquisa por título
        searchField.addActionListener(e -> searchTasks());
        
        // Double-click na tabela para editar
        tasksTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedTask();
                }
            }
        });
    }

    private void loadTasks() {
        // Carregar tarefas numa thread separada
        new Thread(() -> {
            try {
                Long userId = ((Number) currentUser.get("id")).longValue();
                HttpResponse<String> response = HttpUtil.get("/tasks/user/" + userId);
                Map<String, Object> result = HttpUtil.parseResponse(response.body());

                if ((Boolean) result.get("success")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> tasks = (List<Map<String, Object>>) result.get("tasks");
                    
                    SwingUtilities.invokeLater(() -> {
                        updateTasksTable(tasks);
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, 
                            "Erro ao carregar tarefas: " + result.get("message"),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    });
                }

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, 
                        "Erro de conexão: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void updateTasksTable(List<Map<String, Object>> tasks) {
        tableModel.setRowCount(0);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Map<String, Object> task : tasks) {
            Object[] rowData = new Object[7];
            rowData[0] = task.get("id");
            rowData[1] = task.get("title");
            rowData[2] = task.get("statusDisplay");
            rowData[3] = task.get("priorityDisplay");
            
            // Formatar data de criação
            String createdAt = (String) task.get("createdAt");
            if (createdAt != null) {
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(createdAt);
                    rowData[4] = dateTime.format(formatter);
                } catch (Exception e) {
                    rowData[4] = createdAt;
                }
            } else {
                rowData[4] = "";
            }
            
            // Formatar data limite
            String dueDate = (String) task.get("dueDate");
            if (dueDate != null) {
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(dueDate);
                    rowData[5] = dateTime.format(formatter);
                } catch (Exception e) {
                    rowData[5] = dueDate;
                }
            } else {
                rowData[5] = "";
            }
            
            rowData[6] = task.get("description");
            
            tableModel.addRow(rowData);
        }
    }

    private void updateStats() {
        new Thread(() -> {
            try {
                Long userId = ((Number) currentUser.get("id")).longValue();
                HttpResponse<String> response = HttpUtil.get("/tasks/user/" + userId + "/stats");
                Map<String, Object> result = HttpUtil.parseResponse(response.body());

                if ((Boolean) result.get("success")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> stats = (Map<String, Object>) result.get("stats");
                    
                    SwingUtilities.invokeLater(() -> {
                        int total = stats.values().stream().mapToInt(v -> ((Number) v).intValue()).sum();
                        int pending = ((Number) stats.getOrDefault("Pendente", 0)).intValue();
                        int completed = ((Number) stats.getOrDefault("Concluída", 0)).intValue();
                        
                        statsLabel.setText(String.format("Total: %d | Pendentes: %d | Concluídas: %d", 
                            total, pending, completed));
                    });
                }

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    statsLabel.setText("Erro ao carregar estatísticas");
                });
            }
        }).start();
    }

    private void filterTasks() {
        // Implementar filtro por status
        // Por simplicidade, recarregar todas as tarefas
        loadTasks();
    }

    private void searchTasks() {
        // Implementar pesquisa
        // Por simplicidade, recarregar todas as tarefas
        loadTasks();
    }

    private void openNewTaskDialog() {
        TaskDialog dialog = new TaskDialog(this, null, currentUser);
        dialog.setVisible(true);
    }

    private void editSelectedTask() {
        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma tarefa para editar.", 
                "Nenhuma tarefa selecionada", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Converter índice da linha filtrada/ordenada para o índice original
        selectedRow = tasksTable.convertRowIndexToModel(selectedRow);
        
        Long taskId = (Long) tableModel.getValueAt(selectedRow, 0);
        
        // Criar dialog de edição
        TaskDialog dialog = new TaskDialog(this, taskId, currentUser);
        dialog.setVisible(true);
    }

    private void deleteSelectedTask() {
        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma tarefa para eliminar.", 
                "Nenhuma tarefa selecionada", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja eliminar esta tarefa?",
            "Confirmar eliminação", JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            selectedRow = tasksTable.convertRowIndexToModel(selectedRow);
            Long taskId = (Long) tableModel.getValueAt(selectedRow, 0);
            
            // Eliminar tarefa numa thread separada
            new Thread(() -> {
                try {
                    HttpResponse<String> response = HttpUtil.delete("/tasks/" + taskId);
                    Map<String, Object> deleteResult = HttpUtil.parseResponse(response.body());

                    SwingUtilities.invokeLater(() -> {
                        if ((Boolean) deleteResult.get("success")) {
                            JOptionPane.showMessageDialog(this, "Tarefa eliminada com sucesso!");
                            loadTasks();
                            updateStats();
                        } else {
                            JOptionPane.showMessageDialog(this, 
                                "Erro ao eliminar tarefa: " + deleteResult.get("message"),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    });

                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, 
                            "Erro de conexão: " + e.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        }
    }

    private void completeSelectedTask() {
        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma tarefa para marcar como concluída.", 
                "Nenhuma tarefa selecionada", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        selectedRow = tasksTable.convertRowIndexToModel(selectedRow);
        Long taskId = (Long) tableModel.getValueAt(selectedRow, 0);
        
        // Marcar como concluída numa thread separada
        new Thread(() -> {
            try {
                HttpResponse<String> response = HttpUtil.put("/tasks/" + taskId + "/complete", new HashMap<>());
                Map<String, Object> result = HttpUtil.parseResponse(response.body());

                SwingUtilities.invokeLater(() -> {
                    if ((Boolean) result.get("success")) {
                        JOptionPane.showMessageDialog(this, "Tarefa marcada como concluída!");
                        loadTasks();
                        updateStats();
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Erro ao completar tarefa: " + result.get("message"),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, 
                        "Erro de conexão: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void exportTasksToCSV() {
        // Obter todas as tarefas do utilizador atual
        new Thread(() -> {
            try {
                Long userId = ((Number) currentUser.get("id")).longValue();
                HttpResponse<String> tasksResponse = HttpUtil.get("/tasks/user/" + userId);
                Map<String, Object> tasksResult = HttpUtil.parseResponse(tasksResponse.body());

                HttpResponse<String> statsResponse = HttpUtil.get("/tasks/user/" + userId + "/stats");
                Map<String, Object> statsResult = HttpUtil.parseResponse(statsResponse.body());

                if ((Boolean) tasksResult.get("success") && (Boolean) statsResult.get("success")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> tasks = (List<Map<String, Object>>) tasksResult.get("tasks");
                    
                    @SuppressWarnings("unchecked")
                    Map<String, Object> stats = (Map<String, Object>) statsResult.get("stats");
                    
                    SwingUtilities.invokeLater(() -> {
                        // Mostrar opções de exportação
                        String[] options = {"Apenas Tarefas", "Relatório Detalhado", "Cancelar"};
                        int choice = JOptionPane.showOptionDialog(this,
                            "Escolha o tipo de exportação:",
                            "Exportar para CSV",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null, options, options[0]);
                            
                        String username = (String) currentUser.get("username");
                        
                        if (choice == 0) {
                            CSVExporter.exportTasks(this, tasks, username);
                        } else if (choice == 1) {
                            CSVExporter.exportDetailedReport(this, tasks, stats, username);
                        }
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, 
                            "Erro ao obter dados para exportação",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    });
                }

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, 
                        "Erro ao exportar: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja sair?",
            "Confirmar logout", JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    public void refreshTasks() {
        loadTasks();
        updateStats();
    }
}