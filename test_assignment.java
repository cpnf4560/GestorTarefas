import com.gestortarefas.gui.TaskDialog;
import com.gestortarefas.gui.MainFrame;
import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

/**
 * Teste específico para verificar se a funcionalidade de atribuição está a funcionar
 */
public class test_assignment {
    public static void main(String[] args) {
        System.out.println("🧪 TESTE DA FUNCIONALIDADE DE ATRIBUIÇÃO");
        
        // Configurar ambiente gráfico
        System.setProperty("java.awt.headless", "false");
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Criar utilizador de teste
                Map<String, Object> testUser = new HashMap<>();
                testUser.put("id", 1L);
                testUser.put("username", "admin.correia");
                testUser.put("fullName", "Admin Carlos Correia");
                testUser.put("role", "ADMIN");
                
                // Criar MainFrame simulado
                JFrame testFrame = new JFrame("Teste");
                testFrame.setSize(400, 300);
                testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                testFrame.setVisible(true);
                
                // **TESTE CRÍTICO**: Criar TaskDialog e verificar se tem componentes de atribuição
                System.out.println("🔍 Criando TaskDialog para verificar componentes de atribuição...");
                
                // Simular MainFrame
                MainFrame mockMainFrame = new MainFrame(testUser);
                
                // Criar TaskDialog com construtor para nova tarefa
                TaskDialog dialog = new TaskDialog(mockMainFrame, testUser);
                
                System.out.println("✅ TaskDialog criado com sucesso!");
                System.out.println("📋 Verificar se o diálogo contém:");
                System.out.println("   - Radio buttons de atribuição (Utilizador/Equipa)");
                System.out.println("   - Combo boxes para seleção");
                System.out.println("   - Campos de título, descrição, prioridade");
                System.out.println("   - Data e hora limite");
                
                // Mostrar diálogo
                dialog.setVisible(true);
                
                testFrame.dispose(); // Fechar frame de teste
                
            } catch (Exception e) {
                System.err.println("❌ ERRO NO TESTE: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}