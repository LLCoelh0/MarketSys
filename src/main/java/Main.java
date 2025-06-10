import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        //Catch exception from the ui start
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        }catch (Exception ex) {
            System.err.println("Failed to initialize FlatLAf");
        }
        //Start window
        SwingUtilities.invokeLater(() -> {
            StockViewer viewer = new StockViewer();
            viewer.setVisible(true);
        });
        }
    }