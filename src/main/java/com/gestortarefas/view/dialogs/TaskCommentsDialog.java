package com.gestortarefas.view.dialogs;

import com.gestortarefas.util.RestApiClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Diálogo para visualização e adição de comentários nas tarefas (sistema de chat)
 */
public class TaskCommentsDialog extends JDialog {
    
    private static final long serialVersionUID = 1L;
    
    private final Long taskId;
    private final Long currentUserId;
    private final RestApiClient apiClient;
    private final String taskTitle;
    
    // Componentes da interface
    private JTextArea commentsArea;
    private JScrollPane commentsScrollPane;
    private JTextArea newCommentArea;
    private JScrollPane newCommentScrollPane;
    private JButton sendButton;
    private JButton refreshButton;
    private JButton closeButton;
    private JLabel statusLabel;
    private JLabel commentCountLabel;
    
    private Timer refreshTimer;
    
    public TaskCommentsDialog(Window parent, Long taskId, String taskTitle, Long currentUserId, RestApiClient apiClient) {
        super(parent, "Comentários - " + taskTitle, ModalityType.APPLICATION_MODAL);
        this.taskId = taskId;
        this.currentUserId = currentUserId;
        this.apiClient = apiClient;
        this.taskTitle = taskTitle;
        
        initializeComponents();
        setupDialog();
        loadComments();
        startAutoRefresh();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Painel superior com título e contador
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 10, 5, 10));
        
        JLabel titleLabel = new JLabel("💬 Chat da Tarefa: " + taskTitle);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        
        commentCountLabel = new JLabel("0 comentários");
        commentCountLabel.setFont(commentCountLabel.getFont().deriveFont(12f));
        commentCountLabel.setForeground(Color.GRAY);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(commentCountLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Painel central com área de comentários
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        // Área de visualização dos comentários
        commentsArea = new JTextArea();
        commentsArea.setEditable(false);
        commentsArea.setBackground(new Color(248, 248, 248));
        commentsArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);
        commentsArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        commentsScrollPane = new JScrollPane(commentsArea);
        commentsScrollPane.setPreferredSize(new Dimension(500, 300));
        commentsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        commentsScrollPane.setBorder(BorderFactory.createTitledBorder("Histórico de Comentários"));
        
        centerPanel.add(commentsScrollPane, BorderLayout.CENTER);
        
        // Painel para novo comentário
        JPanel newCommentPanel = new JPanel(new BorderLayout());
        newCommentPanel.setBorder(BorderFactory.createTitledBorder("Novo Comentário"));
        
        newCommentArea = new JTextArea(3, 40);
        newCommentArea.setLineWrap(true);
        newCommentArea.setWrapStyleWord(true);
        newCommentArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        newCommentArea.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        newCommentScrollPane = new JScrollPane(newCommentArea);
        newCommentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        newCommentPanel.add(newCommentScrollPane, BorderLayout.CENTER);
        
        // Botão enviar comentário
        JPanel sendPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sendButton = new JButton("📤 Enviar Comentário");
        sendButton.setBackground(new Color(70, 130, 180));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(sendButton.getFont().deriveFont(Font.BOLD));
        sendButton.addActionListener(this::sendComment);
        
        // Botão refresh
        refreshButton = new JButton("🔄 Atualizar");
        refreshButton.addActionListener(e -> loadComments());
        
        sendPanel.add(refreshButton);
        sendPanel.add(sendButton);
        newCommentPanel.add(sendPanel, BorderLayout.SOUTH);
        
        centerPanel.add(newCommentPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        
        // Painel inferior com status e botões
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        
        statusLabel = new JLabel("Carregando comentários...");
        statusLabel.setFont(statusLabel.getFont().deriveFont(11f));
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closeButton = new JButton("Fechar");
        closeButton.addActionListener(e -> {
            if (refreshTimer != null) {
                refreshTimer.stop();
            }
            dispose();
        });
        buttonPanel.add(closeButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Configurações do campo de novo comentário
        setupNewCommentField();
    }
    
    private void setupNewCommentField() {
        // Placeholder text
        newCommentArea.setText("Digite seu comentário aqui...");
        newCommentArea.setForeground(Color.GRAY);
        
        newCommentArea.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (newCommentArea.getText().equals("Digite seu comentário aqui...")) {
                    newCommentArea.setText("");
                    newCommentArea.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (newCommentArea.getText().trim().isEmpty()) {
                    newCommentArea.setText("Digite seu comentário aqui...");
                    newCommentArea.setForeground(Color.GRAY);
                }
            }
        });
        
        // Enter para enviar (Ctrl+Enter para quebra de linha)
        newCommentArea.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && !evt.isControlDown()) {
                    evt.consume();
                    if (!newCommentArea.getText().trim().isEmpty() && 
                        !newCommentArea.getText().equals("Digite seu comentário aqui...")) {
                        sendComment(null);
                    }
                }
            }
        });
    }
    
    private void setupDialog() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        setResizable(true);
        
        // Fechar timer ao fechar janela
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (refreshTimer != null) {
                    refreshTimer.stop();
                }
            }
        });
    }
    
    private void loadComments() {
        SwingUtilities.invokeLater(() -> {
            try {
                statusLabel.setText("Carregando comentários...");
                statusLabel.setForeground(Color.BLACK);
                
                // Chamar API para obter comentários
                Map<String, Object> response = apiClient.getTaskComments(taskId);
                
                if (response != null && response.containsKey("comments")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> comments = (List<Map<String, Object>>) response.get("comments");
                    
                    displayComments(comments);
                    
                    // Atualizar contador
                    int total = comments.size();
                    commentCountLabel.setText(total + (total == 1 ? " comentário" : " comentários"));
                    
                    statusLabel.setText("Comentários atualizados (" + 
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ")");
                    statusLabel.setForeground(new Color(34, 139, 34));
                } else {
                    commentsArea.setText("Nenhum comentário ainda.\nSeja o primeiro a comentar!");
                    commentCountLabel.setText("0 comentários");
                    statusLabel.setText("Nenhum comentário encontrado");
                    statusLabel.setForeground(Color.GRAY);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                commentsArea.setText("❌ Erro ao carregar comentários: " + e.getMessage());
                statusLabel.setText("Erro ao carregar comentários");
                statusLabel.setForeground(Color.RED);
            }
        });
    }
    
    private void displayComments(List<Map<String, Object>> comments) {
        StringBuilder sb = new StringBuilder();
        
        if (comments.isEmpty()) {
            sb.append("Nenhum comentário ainda.\nSeja o primeiro a comentar!");
        } else {
            for (int i = 0; i < comments.size(); i++) {
                Map<String, Object> comment = comments.get(i);
                
                // Extrair dados do comentário
                String userName = extractUserName(comment);
                String commentText = getStringValue(comment, "commentText", "");
                String createdAt = getStringValue(comment, "createdAt", "");
                Boolean isSystemMessage = (Boolean) comment.get("isSystemMessage");
                
                // Debug - imprimir dados do comentário
                System.out.println("DEBUG - Comentário " + i + ":");
                System.out.println("  commentText bruto: [" + comment.get("commentText") + "]");
                System.out.println("  commentText extraído: [" + commentText + "]");
                System.out.println("  commentText length: " + (commentText != null ? commentText.length() : "null"));
                System.out.println("  userName: [" + userName + "]");
                System.out.println("  Todas as chaves: " + comment.keySet());
                
                // Verificar se comentText está vazio e forçar um valor para debug
                if (commentText == null || commentText.trim().isEmpty()) {
                    Object rawText = comment.get("commentText");
                    commentText = "[DEBUG] Texto vazio - Raw: [" + rawText + "] - Tipo: " + 
                                 (rawText != null ? rawText.getClass().getSimpleName() : "null");
                } else {
                    // Garantir que o texto não seja perdido
                    commentText = commentText.trim();
                }
                
                // Formatar data e hora
                String formattedDate = formatDateTime(createdAt);
                
                if (Boolean.TRUE.equals(isSystemMessage)) {
                    // Mensagem do sistema
                    sb.append("🔔 ").append(formattedDate).append(" - SISTEMA\n");
                    sb.append("   ").append(commentText).append("\n");
                } else {
                    // Comentário normal do usuário
                    sb.append("💬 ").append(formattedDate).append(" - ").append(userName).append("\n");
                    sb.append("   ").append(commentText).append("\n");
                }
                
                if (i < comments.size() - 1) {
                    sb.append("\n");
                }
            }
        }
        
        commentsArea.setText(sb.toString());
        commentsArea.setCaretPosition(commentsArea.getDocument().getLength());
    }
    
    /**
     * Extrai o nome do utilizador do comentário, tentando várias estruturas possíveis
     */
    private String extractUserName(Map<String, Object> comment) {
        // Tentar obter de várias formas possíveis
        
        // 1. Objeto user completo
        Object userObj = comment.get("user");
        if (userObj instanceof Map) {
            Map<?, ?> userMap = (Map<?, ?>) userObj;
            
            // Tentar fullName primeiro
            Object fullName = userMap.get("fullName");
            if (fullName != null && !fullName.toString().trim().isEmpty()) {
                return fullName.toString();
            }
            
            // Tentar username
            Object username = userMap.get("username");
            if (username != null && !username.toString().trim().isEmpty()) {
                return username.toString();
            }
            
            // Tentar email
            Object email = userMap.get("email");
            if (email != null && !email.toString().trim().isEmpty()) {
                return email.toString();
            }
        }
        
        // 2. Campos diretos no comentário
        Object userName = comment.get("userName");
        if (userName != null && !userName.toString().trim().isEmpty()) {
            return userName.toString();
        }
        
        Object userFullName = comment.get("userFullName");
        if (userFullName != null && !userFullName.toString().trim().isEmpty()) {
            return userFullName.toString();
        }
        
        Object username = comment.get("username");
        if (username != null && !username.toString().trim().isEmpty()) {
            return username.toString();
        }
        
        // 3. Fallback para userId
        Object userId = comment.get("userId");
        if (userId != null) {
            return "Utilizador #" + userId.toString();
        }
        
        // 4. Último recurso
        return "Utilizador Desconhecido";
    }
    
    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    private String formatDateTime(String dateTimeStr) {
        try {
            if (dateTimeStr != null && !dateTimeStr.trim().isEmpty()) {
                // Tentar parse do formato ISO 8601 do backend
                if (dateTimeStr.contains("T")) {
                    LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr.substring(0, 19));
                    return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                } else if (dateTimeStr.length() >= 16) {
                    // Formato alternativo
                    return dateTimeStr.substring(0, 16).replace("T", " ");
                }
            }
        } catch (Exception e) {
            // Em caso de erro, usar formato simples
            try {
                if (dateTimeStr != null && dateTimeStr.length() >= 16) {
                    return dateTimeStr.substring(0, 16).replace("T", " ");
                }
            } catch (Exception ex) {
                // Ignorar
            }
        }
        return dateTimeStr != null ? dateTimeStr : "Data desconhecida";
    }
    
    private void sendComment(ActionEvent e) {
        String commentText = newCommentArea.getText().trim();
        
        if (commentText.isEmpty() || commentText.equals("Digite seu comentário aqui...")) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, digite um comentário antes de enviar.", 
                "Comentário Vazio", 
                JOptionPane.WARNING_MESSAGE);
            newCommentArea.requestFocus();
            return;
        }
        
        if (commentText.length() > 1000) {
            JOptionPane.showMessageDialog(this, 
                "O comentário não pode exceder 1000 caracteres.\nAtual: " + commentText.length() + " caracteres.", 
                "Comentário Muito Longo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Desabilitar botão durante envio
        sendButton.setEnabled(false);
        sendButton.setText("Enviando...");
        statusLabel.setText("Enviando comentário...");
        statusLabel.setForeground(Color.BLUE);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    // Preparar dados do comentário
                    Map<String, Object> commentData = new HashMap<>();
                    commentData.put("userId", currentUserId);
                    commentData.put("commentText", commentText);
                    commentData.put("isSystemMessage", false);
                    
                    // Enviar comentário via API
                    Map<String, Object> response = apiClient.addTaskComment(taskId, commentData);
                    
                    return response != null && Boolean.TRUE.equals(response.get("success"));
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    
                    if (success) {
                        // Limpar campo de comentário
                        newCommentArea.setText("Digite seu comentário aqui...");
                        newCommentArea.setForeground(Color.GRAY);
                        
                        // Recarregar comentários
                        loadComments();
                        
                        statusLabel.setText("Comentário enviado com sucesso!");
                        statusLabel.setForeground(new Color(34, 139, 34));
                    } else {
                        statusLabel.setText("Erro ao enviar comentário");
                        statusLabel.setForeground(Color.RED);
                        JOptionPane.showMessageDialog(TaskCommentsDialog.this, 
                            "Erro ao enviar comentário. Tente novamente.", 
                            "Erro", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    statusLabel.setText("Erro ao enviar comentário");
                    statusLabel.setForeground(Color.RED);
                } finally {
                    // Reabilitar botão
                    sendButton.setEnabled(true);
                    sendButton.setText("📤 Enviar Comentário");
                }
            }
        };
        
        worker.execute();
    }
    
    private void startAutoRefresh() {
        // Auto-refresh a cada 30 segundos
        refreshTimer = new Timer(30000, e -> {
            if (isDisplayable() && isVisible()) {
                loadComments();
            }
        });
        refreshTimer.start();
    }
}