package com.gestortarefas.gui;

import com.gestortarefas.util.HttpUtil;
import com.gestortarefas.util.CSVExporter;
import com.gestortarefas.util.I18nManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
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
    private JButton languageButton;
    private I18nManager i18n;
    
    // Constantes para colunas da tabela (serão traduzidas dinamicamente)
    private String[] columnNames;

    public MainFrame(Map<String, Object> user) {
        this.currentUser = user;
        this.i18n = I18nManager.getInstance();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadTasks();
        updateStats();
        updateLanguage(); // Atualizar textos iniciais
    }

    private void initializeComponents() {
        // Inicializar colunas da tabela
        updateColumnNames();
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
        
        // Componentes da interface
        userLabel = new JLabel();
        userLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        
        statsLabel = new JLabel();
        
        // Botão de idioma
        languageButton = new JButton("PT/EN");
        languageButton.setPreferredSize(new Dimension(60, 25));
        languageButton.addActionListener(e -> toggleLanguage());
        
        // Tabela de tarefas
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tasksTable = new JTable(tableModel);
        tasksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tasksTable.setRowHeight(25);
        
        // Configurar larguras das colunas
        setupColumnWidths();
        
        // Ocultar coluna ID
        tasksTable.getColumnModel().getColumn(0).setMinWidth(0);
        tasksTable.getColumnModel().getColumn(0).setMaxWidth(0);
        tasksTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        // Filtros
        statusFilter = new JComboBox<>();
        updateStatusFilter();
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
        // Adicionar logo
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(createLogoIcon());
        userPanel.add(logoLabel);
        userPanel.add(Box.createHorizontalStrut(10));
        userPanel.add(userLabel);
        userPanel.add(Box.createHorizontalStrut(20));
        userPanel.add(statsLabel);
        
        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filtersPanel.add(languageButton); // Adicionar botão de idioma
        filtersPanel.add(Box.createHorizontalStrut(10));
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
        // Listener para fechar aplicação com confirmação
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                confirmExit();
            }
        });
        
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

    // Métodos para internacionalização PT/EN
    private void updateColumnNames() {
        columnNames = new String[] {
            "ID",
            i18n.getText("name"),
            i18n.getText("status"),
            i18n.getText("priority"),
            i18n.getText("created_at"),
            i18n.getText("due_date"),
            i18n.getText("description")
        };
    }
    
    private void setupColumnWidths() {
        if (tasksTable.getColumnCount() >= 7) {
            tasksTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
            tasksTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Nome
            tasksTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Status
            tasksTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Prioridade
            tasksTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Criado
            tasksTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Data Limite
            tasksTable.getColumnModel().getColumn(6).setPreferredWidth(300); // Descrição
        }
    }
    
    private void updateStatusFilter() {
        statusFilter.removeAllItems();
        statusFilter.addItem("Todos");
        statusFilter.addItem(i18n.getText("pending"));
        statusFilter.addItem(i18n.getText("in_progress"));
        statusFilter.addItem(i18n.getText("completed"));
        statusFilter.addItem(i18n.getText("cancelled"));
    }
    
    private void toggleLanguage() {
        i18n.toggleLanguage();
        updateLanguage();
    }
    
    private void updateLanguage() {
        // Atualizar título da janela
        setTitle(i18n.getText("title") + " - " + currentUser.get("username"));
        
        // Atualizar label do utilizador
        userLabel.setText(i18n.getText("user") + ": " + currentUser.get("fullName"));
        
        // Atualizar nomes das colunas da tabela
        updateColumnNames();
        for (int i = 0; i < columnNames.length && i < tableModel.getColumnCount(); i++) {
            tasksTable.getColumnModel().getColumn(i).setHeaderValue(columnNames[i]);
        }
        tasksTable.getTableHeader().repaint();
        
        // Atualizar filtros
        String selectedStatus = (String) statusFilter.getSelectedItem();
        updateStatusFilter();
        statusFilter.setSelectedItem(selectedStatus);
        
        // Atualizar todos os componentes visuais
        updateAllComponents();
        
        // Forçar repaint da interface
        revalidate();
        repaint();
    }
    
    private void updateAllComponents() {
        // Encontrar e atualizar todos os botões e labels na interface
        updateComponentsRecursively(this);
    }
    
    private void updateComponentsRecursively(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                String text = button.getText();
                
                // Mapear textos dos botões
                switch (text) {
                    case "Nova Tarefa":
                    case "New Task":
                        button.setText(i18n.getText("add") + " " + i18n.getText("tasks"));
                        break;
                    case "Editar Tarefa":
                    case "Edit Task":
                        button.setText(i18n.getText("edit") + " " + i18n.getText("tasks"));
                        break;
                    case "Eliminar Tarefa":
                    case "Delete Task":
                        button.setText(i18n.getText("delete") + " " + i18n.getText("tasks"));
                        break;
                    case "Marcar Concluída":
                    case "Mark Completed":
                        button.setText(i18n.getText("completed"));
                        break;
                    case "Actualizar":
                    case "Refresh":
                        button.setText(i18n.getText("refresh"));
                        break;
                    case "Exportar CSV":
                    case "Export CSV":
                        button.setText(i18n.getText("export_csv"));
                        break;
                    case "Logout":
                        button.setText(i18n.getText("logout"));
                        break;
                }
            } else if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                String text = label.getText();
                
                if (text.contains("Filtrar por status") || text.contains("Filter by status")) {
                    label.setText(i18n.getText("filter") + " por " + i18n.getText("status") + ":");
                } else if (text.contains("Pesquisar") || text.contains("Search")) {
                    label.setText(i18n.getText("search") + ":");
                }
            } else if (component instanceof Container) {
                updateComponentsRecursively((Container) component);
            }
        }
    }

    private void confirmExit() {
        int result = JOptionPane.showConfirmDialog(this,
            i18n.getText("confirm_exit"),
            i18n.getText("exit"), JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(this,
            i18n.getText("confirm_exit"),
            i18n.getText("logout"), JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    /**
     * Carrega o logo do ficheiro ou cria um ícone programático como backup
     */
    private ImageIcon createLogoIcon() {
        try {
            // Tentar carregar o logo do ficheiro
            java.net.URL logoUrl = getClass().getClassLoader().getResource("images/logo.png");
            if (logoUrl != null) {
                ImageIcon originalIcon = new ImageIcon(logoUrl);
                // Redimensionar para 48x48 pixels
                Image img = originalIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar logo: " + e.getMessage());
        }
        
        // Backup: criar logo programático se não conseguir carregar o ficheiro
        int size = 48;
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Ativar antialiasing para melhor qualidade
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Desenhar fundo circular azul
        g2d.setColor(Colors.MAGASTEEL_BLUE);
        g2d.fillRoundRect(2, 2, size-4, size-4, 12, 12);
        
        // Desenhar borda
        g2d.setColor(Colors.DARK_GRAY);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(2, 2, size-4, size-4, 12, 12);
        
        // Desenhar texto "GT" no centro
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "GT";
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int x = (size - textWidth) / 2;
        int y = (size + textHeight) / 2 - 2;
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        return new ImageIcon(image);
    }
    
    public void refreshTasks() {
        loadTasks();
        updateStats();
    }
}