import javax.swing.*;
import java.awt.*;

public abstract class BaseWindow extends JFrame {

    protected JPanel sidebar;

    public BaseWindow() {
        //Setup window
        setTitle("MarketSys");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //Sidebar setup
        createSidebar();
        add(sidebar, BorderLayout.WEST);
    }

    //Sidebar method
    private void createSidebar() {
        //Sidebar setup
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 40, 40) );
        sidebar.setPreferredSize(new Dimension(150, getHeight()));
        //Add buttons to the panel
        sidebar.add(createSidebarButton("Dashboard", Dashboard.class));
        sidebar.add(createSidebarButton("Stock", StockViewer.class));
        sidebar.add(createSidebarButton("Sales", Sales.class));
        sidebar.add(createSidebarButton("Personnel", PersonnelManagement.class));
    }

    //Button method
    protected JButton createSidebarButton(String label, Class<? extends JFrame> targetClass) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(120, 30));

        //On click action
        button.addActionListener(_ ->{
            try {
                JFrame nextPage = targetClass.getDeclaredConstructor().newInstance();
                nextPage.setVisible(true);
                this.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error opening" + label + "page " + ex.getMessage(), "Navigation Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return button;
    }

}
