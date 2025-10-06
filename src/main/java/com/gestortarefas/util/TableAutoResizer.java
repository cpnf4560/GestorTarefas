package com.gestortarefas.util;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Utilitário para auto-redimensionamento de colunas de tabela
 */
public class TableAutoResizer {
    
    /**
     * Cria um botão "quadradinho" para auto-redimensionar colunas da tabela
     */
    public static JButton createAutoResizeButton(JTable table) {
        JButton autoResizeButton = new JButton("⚏");
        autoResizeButton.setFont(new Font("Dialog", Font.BOLD, 14));
        autoResizeButton.setPreferredSize(new Dimension(25, 25));
        autoResizeButton.setToolTipText("Auto-redimensionar colunas");
        autoResizeButton.setFocusPainted(false);
        autoResizeButton.setBorderPainted(true);
        autoResizeButton.setBackground(new Color(240, 240, 240));
        
        autoResizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autoResizeColumns(table);
            }
        });
        
        return autoResizeButton;
    }
    
    /**
     * Auto-redimensiona as colunas da tabela baseado no conteúdo
     */
    public static void autoResizeColumns(JTable table) {
        if (table == null) return;
        
        TableColumnModel columnModel = table.getColumnModel();
        TableModel tableModel = table.getModel();
        
        for (int column = 0; column < columnModel.getColumnCount(); column++) {
            TableColumn tableColumn = columnModel.getColumn(column);
            
            // Largura do cabeçalho
            int headerWidth = 0;
            Object headerValue = tableColumn.getHeaderValue();
            if (headerValue != null) {
                FontMetrics headerFontMetrics = table.getFontMetrics(table.getTableHeader().getFont());
                headerWidth = headerFontMetrics.stringWidth(headerValue.toString()) + 20; // padding
            }
            
            // Largura do conteúdo das células
            int cellWidth = 0;
            for (int row = 0; row < Math.min(tableModel.getRowCount(), 50); row++) { // Limita a 50 linhas para performance
                Object cellValue = tableModel.getValueAt(row, column);
                if (cellValue != null) {
                    String cellText = cellValue.toString();
                    FontMetrics cellFontMetrics = table.getFontMetrics(table.getFont());
                    int textWidth = cellFontMetrics.stringWidth(cellText) + 20; // padding
                    cellWidth = Math.max(cellWidth, textWidth);
                }
            }
            
            // Define a largura preferida como o máximo entre cabeçalho e conteúdo
            int preferredWidth = Math.max(headerWidth, cellWidth);
            
            // Define limites mínimos e máximos
            preferredWidth = Math.max(preferredWidth, 80);  // Mínimo 80px
            preferredWidth = Math.min(preferredWidth, 300); // Máximo 300px
            
            tableColumn.setPreferredWidth(preferredWidth);
        }
        
        // Força o repaint da tabela
        table.revalidate();
        table.repaint();
    }
    
    /**
     * Cria um painel com a tabela e o botão de auto-redimensionamento
     */
    public static JPanel createTablePanelWithAutoResize(JTable table, JScrollPane scrollPane) {
        JPanel tablePanel = new JPanel(new BorderLayout());
        
        // Painel superior com o botão
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
        topPanel.setBackground(new Color(248, 248, 248));
        topPanel.add(new JLabel("Colunas:"));
        topPanel.add(createAutoResizeButton(table));
        
        tablePanel.add(topPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
}