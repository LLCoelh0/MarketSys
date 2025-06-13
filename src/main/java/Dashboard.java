import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    //UI method
    public Dashboard() {
        //Window setup
        setTitle("MarketSys");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        //Side panel setup
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 40, 40));
        sidebar.setPreferredSize(new Dimension(150, getHeight()));
        //Add side panel buttons
        sidebar.add(createSidebarButton("Dashboard"));
        sidebar.add(createSidebarButton("Stock"));
        sidebar.add(createSidebarButton("Sales"));
        sidebar.add(createSidebarButton("Manager"));
        //Center panels setup
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(createFeaturePanel("Month Balance"));
        mainPanel.add(createFeaturePanel("Stock"));
        mainPanel.add(createFeaturePanel("Graphics"));
        mainPanel.add(createFeaturePanel("Cat Picture"));
        //Add panels
        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }
    //Button method
    private JButton createSidebarButton(String label) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(120, 30));
        return button;
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