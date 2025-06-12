import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileReader;
import java.sql.*;
import java.util.Properties;

public class Sales extends JFrame{
    private final JTextField codeField;
    private final JTextField quantityField;
    private final DefaultTableModel saleModel;
    private final JLabel totalLabel;
    private double total = 0.0;

    public Sales() {
        setTitle("Sales Window");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //Left panel
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
            btn.addActionListener(e -> codeField.setText(codeField.getText() + digit));
            keypadPanel.add(btn);
        }
        JButton zeroBtn = new JButton("0");
        zeroBtn.addActionListener(e -> codeField.setText(codeField.getText() + "0"));
        keypadPanel.add(zeroBtn);
        keypadPanel.add(new JButton("."));
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> codeField.setText(""));
        keypadPanel.add(clearBtn);
        //Add keypad
        leftPanel.add(keypadPanel, BorderLayout.CENTER);

        //Input fields
        JPanel inputPanel = new JPanel(new GridLayout(2,2));
        inputPanel.add(new JLabel("Item Code:"));
        inputPanel.add(codeField);
        inputPanel.add(new JLabel("Quantity"));
        quantityField = new JTextField();
        inputPanel.add(quantityField);
        leftPanel.add(inputPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);

        //Right panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        //Sale Table
        saleModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Qty", "Subtotal"}, 0);
        JTable saleTable = new JTable(saleModel);
        rightPanel.add(new JScrollPane(saleTable));
        //Total label
        totalLabel = new JLabel("Total; $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(totalLabel);
        rightPanel.add(totalPanel, BorderLayout.NORTH);
        //Action Panel
        JPanel actionPanel = new JPanel();
        JButton addItemBtn = new JButton("Add item");
        JButton finishBtn = new JButton("Finish Purchase");
        JButton cancelBtn = new JButton("Cancel Purchase");
        actionPanel.add(addItemBtn);
        actionPanel.add(finishBtn);
        actionPanel.add(cancelBtn);
        rightPanel.add(actionPanel, BorderLayout.SOUTH);

        //Add item
        addItemBtn.addActionListener(e -> addItem());

        //Cancel purchase logic
        cancelBtn.addActionListener(e -> {
            saleModel.setRowCount(0);
            total = 0.0;
            totalLabel.setText("Total: $0.00");
        });
        add(rightPanel, BorderLayout.CENTER);
    }
    private void addItem() {
        String code = codeField.getText().trim();
        String qtyStr = quantityField.getText().trim();

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
            Properties props = new Properties();
            props.load(new FileReader("src\\main\\java\\db.properties"));
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String pass =props.getProperty("db.password");

            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                String selectSql = "SELECT * FROM stock WHERE product_id = ?";
                PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                selectStmt.setInt(1, Integer.parseInt(code));
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    int stockQty = rs.getInt("Quantity");
                    if (stockQty < quantity) {
                        JOptionPane.showMessageDialog(this, "Not enough stock available", "Stock error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    double subtotal = price * quantity;
                    total+= subtotal;
                    saleModel.addRow(new Object[]{code, name, price, quantity, subtotal});
                    totalLabel.setText(String.format("Total: $%.2f", total));

                    //Update stock
                    String updateSql = "UPDATE stock SET quantity = quantity - ? WHERE product_id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    updateStmt.setInt(1, quantity);
                    updateStmt.setInt(2, Integer.parseInt(code));
                    updateStmt.executeUpdate();

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
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Sales().setVisible(true));
        }
}
