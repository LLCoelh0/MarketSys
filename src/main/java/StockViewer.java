import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StockViewer extends BaseWindow{
    //Table to manage row and column
    private final DefaultTableModel tableModel;

    //Constructor to create stock viewer window
    public StockViewer() {
        //Central pane table
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Quantity"}, 0);
        JTable productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        //CRUD panel
        JPanel crudPanel = new JPanel();
        crudPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        crudPanel.setBorder(BorderFactory.createTitledBorder("CRUD actions"));
        //CRUD buttons
        JButton createButton = new JButton("Create");
        JButton readButton = new JButton("Read");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        crudPanel.add(createButton);
        crudPanel.add(readButton);
        crudPanel.add(updateButton);
        crudPanel.add(deleteButton);
        //Read button action
        readButton.addActionListener(_ -> refreshData());
        //Add components to the window

        add(scrollPane, BorderLayout.CENTER);
        add(crudPanel, BorderLayout.SOUTH);
        //Load data from database
        loadDataFromDatabase();
    }

    //Load DB data
    private void loadDataFromDatabase() {
        tableModel.setRowCount(0);
        try {
            try (
                //Extract data
                Connection conn = DatabaseConfig.getConnection();
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
    //Read button method **provisional**
    private void refreshData(){
        loadDataFromDatabase();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StockViewer().setVisible(true));
    }
}