import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Sales extends JFrame{
    //UI fields
    private final JTextField codeField;
    private final JTextField quantityField;
    private final DefaultTableModel saleModel;
    private final JLabel totalLabel;
    private double total = 0.0;
    //Class to represent a single item
    private static class SaleItem {
        int productId;
        int quantity;
        SaleItem(int productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }
    //List of products in a sale
    private final List<SaleItem> saleItems = new ArrayList<>();
    //UI method
    public Sales() {
        //Window setup
        setTitle("MarketSys");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //LEFT PANEL
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, 0));
        //Buttons
        JPanel topButtonsPanel = new JPanel(new GridLayout(1, 3));
        topButtonsPanel.add(new JButton("Closing"));
        topButtonsPanel.add(new JButton("Remove item"));
        topButtonsPanel.add(new JButton("Lore Ipsum"));
        leftPanel.add(topButtonsPanel, BorderLayout.NORTH);
        //Keypad setup
        JPanel keypadPanel = new JPanel(new GridLayout(4, 3));
        codeField = new JTextField();
        leftPanel.add(codeField, BorderLayout.CENTER);
        for (int i = 1; i <= 9; i++ ) {
            int digit = i;
            JButton btn = new JButton(String.valueOf(digit));
            btn.addActionListener(_ -> codeField.setText(codeField.getText() + digit));
            keypadPanel.add(btn);
        }
        JButton zeroBtn = new JButton("0");
        zeroBtn.addActionListener(_ -> codeField.setText(codeField.getText() + "0"));
        keypadPanel.add(new JButton("."));
        keypadPanel.add(zeroBtn);
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(_ -> codeField.setText(""));
        keypadPanel.add(clearBtn);
        //Add keypad
        leftPanel.add(keypadPanel, BorderLayout.CENTER);
        //Input fields
        JPanel inputPanel = new JPanel(new GridLayout(2,2));
        inputPanel.add(new JLabel("Item Code"));
        inputPanel.add(codeField);
        inputPanel.add(new JLabel("Quantity"));
        quantityField = new JTextField();
        inputPanel.add(quantityField);
        leftPanel.add(inputPanel, BorderLayout.SOUTH);
        //Add left panel
        add(leftPanel, BorderLayout.WEST);

        //RIGHT PANEL
        JPanel rightPanel = new JPanel(new BorderLayout());
        //Sale Table
        saleModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Qty", "Subtotal"}, 0);
        JTable saleTable = new JTable(saleModel);
        rightPanel.add(new JScrollPane(saleTable), BorderLayout.CENTER);
        //Total label
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 40));
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(totalLabel);
        //Action buttons Panel
        JPanel actionPanel = new JPanel();
        JButton addItemBtn = new JButton("Add item");
        JButton finishBtn = new JButton("Finish Purchase");
        JButton cancelBtn = new JButton("Cancel Purchase");
        actionPanel.add(addItemBtn);
        actionPanel.add(finishBtn);
        actionPanel.add(cancelBtn);
        rightPanel.add(actionPanel, BorderLayout.SOUTH);
        //Bottom buttons panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalPanel, BorderLayout.NORTH);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);
        //Add bottom right panel
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);
        //Buttons actions
        addItemBtn.addActionListener(_ -> addItem());
        finishBtn.addActionListener(_ -> finishPurchase());
        cancelBtn.addActionListener(_ -> cancelPurchase());
        //Add right panel
        add(rightPanel, BorderLayout.CENTER);
    }
    //Add item method
    private void addItem() {
        String code = codeField.getText().trim();
        String qtyStr = quantityField.getText().trim();
        //Fail proof
        if (code.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, " Fill in both code and quantity.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int quantity;
        try {
            quantity = Integer.parseInt(qtyStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            //DB connection
            Properties props = new Properties();
            props.load(new FileReader("src\\main\\java\\db.properties"));
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String pass =props.getProperty("db.password");
            //DB data extraction
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                String selectSql = "SELECT * FROM stock WHERE product_id = ?";
                PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                selectStmt.setInt(1, Integer.parseInt(code));
                ResultSet rs = selectStmt.executeQuery();
                //Fail proof
                if (rs.next()) {
                    int stockQty = rs.getInt("Quantity");
                    if (stockQty < quantity) {
                        JOptionPane.showMessageDialog(this, "Not enough stock available", "Stock error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    //Get data
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    double subtotal = price * quantity;
                    //Total price
                    total+= subtotal;
                    //String format
                    String formattedPrice = String.format("$ %.2f", price);
                    String formattedSubtotal = String.format("$ %.2f", subtotal);
                    //Put data on sale table
                    saleModel.addRow(new Object[]{code, name, formattedPrice, quantity, formattedSubtotal});
                    totalLabel.setText(String.format("Total: $%.2f", total));
                    saleItems.add(new SaleItem(Integer.parseInt(code), quantity));
                    //Clear input
                    codeField.setText("");
                    quantityField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Item not found.", "NotFound", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error accessing database: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //Finish sale and update the DB method
    private void finishPurchase() {
        try {
            //DB connection
            Properties props = new Properties();
            props.load(new FileReader("src\\main\\java\\db.properties"));
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");
            //DB Update
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                for (SaleItem item : saleItems) {
                    String updateSql = "UPDATE stock SET quantity = quantity - ? WHERE product_id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    updateStmt.setInt(1, item.quantity);
                    updateStmt.setInt(2, item.productId);
                    updateStmt.executeUpdate();
                }
            }
            JOptionPane.showMessageDialog(this, "Purchase completed successfully.");
            cancelPurchase();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating stock: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //Clear or cancel current sale method
    private void cancelPurchase() {
        saleModel.setRowCount(0);
        total = 0.0;
        totalLabel.setText("Total: $0.00");
        saleItems.clear();
        codeField.setText("");
        quantityField.setText("");
    }
    //Main method *REMOVE LATER*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Sales().setVisible(true));
        }
}
