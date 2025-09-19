package com.gestortarefas.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Utilitário para exportação de dados para CSV
 */
public class CSVExporter {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Exporta lista de tarefas para ficheiro CSV
     */
    public static boolean exportTasks(Component parent, List<Map<String, Object>> tasks, String username) {
        // Escolher localização do ficheiro
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar Tarefas para CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Ficheiros CSV (*.csv)", "csv"));
        
        String defaultFileName = String.format("tarefas_%s_%s.csv", 
            username, 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmm")));
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        int result = fileChooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        
        File file = fileChooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getParentFile(), file.getName() + ".csv");
        }
        
        try {
            exportTasksToFile(tasks, file);
            
            // Mostrar mensagem de sucesso e opção para abrir ficheiro
            int openResult = JOptionPane.showConfirmDialog(parent,
                "Tarefas exportadas com sucesso para:\n" + file.getAbsolutePath() +
                "\n\nDeseja abrir o ficheiro?",
                "Exportação Concluída",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
                
            if (openResult == JOptionPane.YES_OPTION) {
                Desktop.getDesktop().open(file);
            }
            
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                "Erro ao exportar tarefas:\n" + e.getMessage(),
                "Erro na Exportação",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Exporta tarefas para ficheiro específico
     */
    private static void exportTasksToFile(List<Map<String, Object>> tasks, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file, java.nio.charset.StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            
            // Escrever cabeçalho manualmente
            csvPrinter.printRecord("ID", "Título", "Descrição", "Status", "Prioridade", 
                "Data Criação", "Data Limite", "Data Conclusão", "Utilizador", "Atrasada");
            
            for (Map<String, Object> task : tasks) {
                csvPrinter.printRecord(
                    task.get("id"),
                    task.get("title"),
                    task.get("description"),
                    task.get("statusDisplay"),
                    task.get("priorityDisplay"),
                    formatDateTime((String) task.get("createdAt")),
                    formatDateTime((String) task.get("dueDate")),
                    formatDateTime((String) task.get("completedAt")),
                    task.get("username"),
                    task.get("isOverdue") != null && (Boolean) task.get("isOverdue") ? "Sim" : "Não"
                );
            }
        }
    }

    /**
     * Formatar data/hora para exibição no CSV
     */
    private static String formatDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return "";
        }
        
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
            return dateTime.format(formatter);
        } catch (Exception e) {
            return dateTimeStr; // Retornar string original se não conseguir formatar
        }
    }

    /**
     * Exporta estatísticas de um utilizador para CSV
     */
    public static boolean exportUserStats(Component parent, Map<String, Object> stats, String username) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar Estatísticas para CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Ficheiros CSV (*.csv)", "csv"));
        
        String defaultFileName = String.format("estatisticas_%s_%s.csv", 
            username, 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmm")));
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        int result = fileChooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        
        File file = fileChooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getParentFile(), file.getName() + ".csv");
        }
        
        try {
            try (FileWriter writer = new FileWriter(file, java.nio.charset.StandardCharsets.UTF_8);
                 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
                
                // Escrever cabeçalho manualmente
                csvPrinter.printRecord("Status", "Quantidade");
                
                for (Map.Entry<String, Object> entry : stats.entrySet()) {
                    csvPrinter.printRecord(entry.getKey(), entry.getValue());
                }
            }
            
            JOptionPane.showMessageDialog(parent,
                "Estatísticas exportadas com sucesso para:\n" + file.getAbsolutePath(),
                "Exportação Concluída",
                JOptionPane.INFORMATION_MESSAGE);
                
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                "Erro ao exportar estatísticas:\n" + e.getMessage(),
                "Erro na Exportação",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Cria relatório detalhado em CSV
     */
    public static boolean exportDetailedReport(Component parent, List<Map<String, Object>> tasks, 
                                              Map<String, Object> stats, String username) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar Relatório Detalhado para CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Ficheiros CSV (*.csv)", "csv"));
        
        String defaultFileName = String.format("relatorio_%s_%s.csv", 
            username, 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmm")));
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        int result = fileChooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        
        File file = fileChooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getParentFile(), file.getName() + ".csv");
        }
        
        try {
            try (FileWriter writer = new FileWriter(file, java.nio.charset.StandardCharsets.UTF_8);
                 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
                
                // Cabeçalho do relatório
                csvPrinter.printRecord("RELATÓRIO DE TAREFAS - " + username.toUpperCase());
                csvPrinter.printRecord("Gerado em:", LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                csvPrinter.printRecord();
                
                // Estatísticas
                csvPrinter.printRecord("ESTATÍSTICAS:");
                for (Map.Entry<String, Object> entry : stats.entrySet()) {
                    csvPrinter.printRecord(entry.getKey() + ":", entry.getValue());
                }
                csvPrinter.printRecord();
                
                // Cabeçalho das tarefas
                csvPrinter.printRecord("LISTA DETALHADA DE TAREFAS:");
                csvPrinter.printRecord("ID", "Título", "Descrição", "Status", "Prioridade", 
                    "Data Criação", "Data Limite", "Data Conclusão", "Atrasada");
                
                // Tarefas
                for (Map<String, Object> task : tasks) {
                    csvPrinter.printRecord(
                        task.get("id"),
                        task.get("title"),
                        task.get("description"),
                        task.get("statusDisplay"),
                        task.get("priorityDisplay"),
                        formatDateTime((String) task.get("createdAt")),
                        formatDateTime((String) task.get("dueDate")),
                        formatDateTime((String) task.get("completedAt")),
                        task.get("isOverdue") != null && (Boolean) task.get("isOverdue") ? "Sim" : "Não"
                    );
                }
            }
            
            // Mostrar mensagem de sucesso e opção para abrir ficheiro
            int openResult = JOptionPane.showConfirmDialog(parent,
                "Relatório exportado com sucesso para:\n" + file.getAbsolutePath() +
                "\n\nDeseja abrir o ficheiro?",
                "Exportação Concluída",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
                
            if (openResult == JOptionPane.YES_OPTION) {
                Desktop.getDesktop().open(file);
            }
            
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                "Erro ao exportar relatório:\n" + e.getMessage(),
                "Erro na Exportação",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}