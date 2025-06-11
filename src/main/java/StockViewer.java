import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileReader;
import java.sql.*;
import java.util.Properties;

public class StockViewer extends JFrame{
    //Table to manage row and column
    private final DefaultTableModel tableModel;

    //Constructor to create stock viewer window
    public StockViewer() {
        //Window initial setup
        setTitle("MarketSys");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //Side panel
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 40,40));
        sidebar.setPreferredSize(new Dimension(150, getHeight()));

        //Menu options
        sidebar.add(createSidebarButton("Dashboard"));
        sidebar.add(createSidebarButton("Stock"));
        sidebar.add(createSidebarButton("Sales"));
        sidebar.add(createSidebarButton("Manager"));

        //Central pane table
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Quantity"}, 0);
        JTable productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);

        //CRUD panel
        JPanel crudPanel = new JPanel();
        crudPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        crudPanel.setBorder(BorderFactory.createTitledBorder("CRUD actions"));

        JButton createButton = new JButton("Create");
        JButton readButton = new JButton("Read");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        crudPanel.add(createButton);
        crudPanel.add(readButton);
        crudPanel.add(updateButton);
        crudPanel.add(deleteButton);

        //Refresh button
        readButton.addActionListener(e -> refreshData());

        //Add components to the window
        add(sidebar, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(crudPanel, BorderLayout.SOUTH);

        //Load data from database
        loadDataFromDatabase();
    }

    private JButton createSidebarButton(String label) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(120,30));
        return button;
    }

    private void loadDataFromDatabase() {
        tableModel.setRowCount(0);
        Properties props = new Properties();
        try {
            //Login in database
            props.load(new FileReader("src\\main\\java\\db.properties"));
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            try (
                //Extract data
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM stock")
            ) {
                //Show data
                while (rs.next()) {
                int id = rs.getInt("product_id");
                String name = rs.getNString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                tableModel.addRow(new Object[]{id, name, price, quantity});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading DB config or connection:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void refreshData(){
        loadDataFromDatabase();
    }
}