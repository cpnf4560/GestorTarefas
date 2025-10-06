package com.gestortarefas.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Constantes de cores para o design da aplicação
 */
public class Colors {
    
    // Cores principais
    public static final Color MAGASTEEL_BLUE = new Color(70, 130, 180);     // Azul magasteel
    public static final Color MAGASTEEL_BLUE_DARK = new Color(60, 110, 160); // Azul magasteel escuro
    public static final Color MAGASTEEL_BLUE_LIGHT = new Color(135, 175, 215); // Azul magasteel claro
    
    // Cores de apoio modernas
    public static final Color SOFT_WHITE = new Color(252, 252, 252);         // Branco suave moderno
    public static final Color LIGHT_GRAY = new Color(245, 247, 250);         // Cinza claro moderno
    public static final Color MEDIUM_GRAY = new Color(220, 225, 230);        // Cinza médio moderno
    public static final Color DARK_GRAY = new Color(55, 65, 75);             // Cinza escuro moderno
    public static final Color CARD_BACKGROUND = new Color(255, 255, 255);    // Fundo dos cards
    public static final Color PANEL_BACKGROUND = new Color(248, 250, 252);   // Fundo dos painéis
    public static final Color BORDER_COLOR = new Color(225, 232, 240);       // Cor das bordas
    
    // Cores de estado
    public static final Color SUCCESS_GREEN = new Color(46, 125, 50);        // Verde sucesso
    public static final Color WARNING_ORANGE = new Color(245, 124, 0);       // Laranja aviso
    public static final Color ERROR_RED = new Color(211, 47, 47);            // Vermelho erro
    public static final Color INFO_BLUE = new Color(25, 118, 210);           // Azul informação
    
    // Cores para prioridades
    public static final Color PRIORITY_LOW = new Color(76, 175, 80);         // Verde para BAIXA
    public static final Color PRIORITY_NORMAL = new Color(33, 150, 243);     // Azul para NORMAL
    public static final Color PRIORITY_HIGH = new Color(255, 152, 0);        // Laranja para ALTA
    public static final Color PRIORITY_URGENT = new Color(244, 67, 54);      // Vermelho para URGENTE
    
    // Cores para status
    public static final Color STATUS_PENDING = new Color(158, 158, 158);     // Cinza para PENDENTE
    public static final Color STATUS_IN_PROGRESS = new Color(33, 150, 243);  // Azul para EM_ANDAMENTO
    public static final Color STATUS_COMPLETED = new Color(76, 175, 80);     // Verde para CONCLUIDA
    public static final Color STATUS_CANCELLED = new Color(244, 67, 54);     // Vermelho para CANCELADA
    
    /**
     * Retorna a cor baseada na prioridade
     */
    public static Color getPriorityColor(String priority) {
        if (priority == null) return MEDIUM_GRAY;
        
        switch (priority.toUpperCase()) {
            case "BAIXA": return PRIORITY_LOW;
            case "NORMAL": return PRIORITY_NORMAL;
            case "ALTA": return PRIORITY_HIGH;
            case "URGENTE": return PRIORITY_URGENT;
            default: return MEDIUM_GRAY;
        }
    }
    
    /**
     * Retorna a cor baseada no status
     */
    public static Color getStatusColor(String status) {
        if (status == null) return MEDIUM_GRAY;
        
        switch (status.toUpperCase()) {
            case "PENDENTE": return STATUS_PENDING;
            case "EM_ANDAMENTO": return STATUS_IN_PROGRESS;
            case "CONCLUIDA": return STATUS_COMPLETED;
            case "CANCELADA": return STATUS_CANCELLED;
            default: return MEDIUM_GRAY;
        }
    }
    
    /**
     * Aplica tema moderno a um painel
     */
    public static void applyModernTheme(JPanel panel) {
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }
    
    /**
     * Aplica tema moderno a um botão
     */
    public static void applyModernButton(JButton button) {
        button.setBackground(MAGASTEEL_BLUE);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAGASTEEL_BLUE_DARK, 1),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    /**
     * Aplica tema moderno a uma tabela
     */
    public static void applyModernTable(JTable table) {
        table.setBackground(CARD_BACKGROUND);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(MAGASTEEL_BLUE_LIGHT);
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setBackground(LIGHT_GRAY);
        table.getTableHeader().setForeground(DARK_GRAY);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
    }
    
    /**
     * Configura alinhamento central para todas as colunas exceto as especificadas
     */
    public static void applyCenterAlignment(JTable table, int... excludeColumns) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            boolean exclude = false;
            for (int excludeCol : excludeColumns) {
                if (i == excludeCol) {
                    exclude = true;
                    break;
                }
            }
            
            if (!exclude) {
                table.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, 
                            boolean isSelected, boolean hasFocus, int row, int column) {
                        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                        return c;
                    }
                });
            }
        }
    }
}