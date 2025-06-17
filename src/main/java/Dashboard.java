import javax.swing.*;
import java.awt.*;

public class Dashboard extends BaseWindow {

    //UI method
    public Dashboard() {
        //Center panels setup
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(createFeaturePanel("Month Balance"));
        mainPanel.add(createFeaturePanel("Stock"));
        mainPanel.add(createFeaturePanel("Graphics"));
        mainPanel.add(createFeaturePanel("Cat Picture"));
        //Add panels
        //add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }
    //Panel method
    private JPanel createFeaturePanel(String title) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(new Color(230, 230, 230));
        return panel;
    }
    //Main method *REMOVE LATER*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }
}