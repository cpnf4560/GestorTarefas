package com.gestortarefas.gui;

import java.awt.Color;

/**
 * Constantes de cores para o design da aplicação
 */
public class Colors {
    
    // Cores principais
    public static final Color MAGASTEEL_BLUE = new Color(70, 130, 180);     // Azul magasteel
    public static final Color MAGASTEEL_BLUE_DARK = new Color(60, 110, 160); // Azul magasteel escuro
    public static final Color MAGASTEEL_BLUE_LIGHT = new Color(135, 175, 215); // Azul magasteel claro
    
    // Cores de apoio
    public static final Color SOFT_WHITE = new Color(248, 248, 248);         // Branco suave
    public static final Color LIGHT_GRAY = new Color(240, 240, 240);         // Cinza claro
    public static final Color MEDIUM_GRAY = new Color(200, 200, 200);        // Cinza médio
    public static final Color DARK_GRAY = new Color(80, 80, 80);             // Cinza escuro
    
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
}