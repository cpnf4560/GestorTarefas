import com.gestortarefas.gui.SimpleTaskDialog;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class test_gui {
    public static void main(String[] args) {
        try {
            // Mock user data
            Map<String, Object> user = new HashMap<>();
            user.put("id", 1L);
            user.put("fullName", "Carlos Teste");
            user.put("role", "ADMIN");
            
            // Test the SimpleTaskDialog
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(200, 100);
                frame.setVisible(true);
                
                SimpleTaskDialog dialog = new SimpleTaskDialog((javax.swing.JFrame) frame, user);
                dialog.setVisible(true);
            });
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}