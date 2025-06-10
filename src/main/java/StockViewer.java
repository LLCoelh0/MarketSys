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
        setTitle("MarketSys");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        //Define column names in table
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Quantity"}, 0);
        //Create JTable using table model
        //Table to display stock data
        JTable productTable = new JTable(tableModel);
        //Add table inside scroll pane
        add(new JScrollPane(productTable), BorderLayout.CENTER);
        //Load data from database
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        Properties props = new Properties();
        try {
            props.load(new FileReader("src\\main\\java\\db.properties"));
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            try (
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM stock")
            ) {
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
}