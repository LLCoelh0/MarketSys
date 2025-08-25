import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class CrudPopup extends JDialog {

    private final String table;
    private final String action;

    // Employee fields
    private JTextField userIdField;
    private JTextField nameField;
    private JTextField passwordField;
    private JTextField roleField;

    // Stock fields
    private JTextField productIdField;
    private JTextField productNameField;
    private JTextField quantityField;
    private JTextField priceField;
    //Crud popup setup
    public CrudPopup(JFrame parent, String table, String action) {
        super(parent, action.toUpperCase() + " - " + table.toUpperCase(), true);
        this.table = table;
        this.action = action;

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        if (table.equals("employees")) {
            // Initialize fields
            userIdField = new JTextField();
            nameField = new JTextField();
            passwordField = new JTextField();
            roleField = new JTextField();

            formPanel.add(new JLabel("User ID:"));
            formPanel.add(userIdField);
            formPanel.add(new JLabel("Name:"));
            formPanel.add(nameField);
            formPanel.add(new JLabel("Password:"));
            formPanel.add(passwordField);
            formPanel.add(new JLabel("Role:"));
            formPanel.add(roleField);

        } else if (table.equals("stock")) {
            // Initialize fields
            productIdField = new JTextField();
            productNameField = new JTextField();
            quantityField = new JTextField();
            priceField = new JTextField();

            formPanel.add(new JLabel("Product ID:"));
            formPanel.add(productIdField);
            formPanel.add(new JLabel("Name:"));
            formPanel.add(productNameField);
            formPanel.add(new JLabel("Quantity:"));
            formPanel.add(quantityField);
            formPanel.add(new JLabel("Price:"));
            formPanel.add(priceField);
        }

        add(formPanel, BorderLayout.CENTER);

        JButton executeButton = new JButton(action.toUpperCase());
        executeButton.addActionListener(this::handleCrudAction);
        add(executeButton, BorderLayout.SOUTH);

        setVisible(true);
    }
    //Employee crud method
    private void handleCrudAction(ActionEvent e) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            switch (table) {
                case "employees" -> handleEmployeeCrud(conn);
                case "stock" -> handleStockCrud(conn);
            }
            JOptionPane.showMessageDialog(this, action + " successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEmployeeCrud(Connection conn) throws SQLException {
        String sql;
        //Switch case for each option of the crud operations
        switch (action.toLowerCase()) {
            case "create" -> {
                sql = "INSERT INTO employees (user_id, name, password, role) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, userIdField.getText());
                    stmt.setString(2, nameField.getText());
                    stmt.setString(3, passwordField.getText());
                    stmt.setString(4, roleField.getText());
                    stmt.executeUpdate();
                }
            }
            case "update" -> {
                sql = "UPDATE employees SET name=?, password=?, role=? WHERE user_id=?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, nameField.getText());
                    stmt.setString(2, passwordField.getText());
                    stmt.setString(3, roleField.getText());
                    stmt.setString(4, userIdField.getText());
                    stmt.executeUpdate();
                }
            }
            case "delete" -> {
                sql = "DELETE FROM employees WHERE user_id=?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, userIdField.getText());
                    stmt.executeUpdate();
                }
            }
            case "read" -> {
                sql = "SELECT * FROM employees WHERE user_id=?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, userIdField.getText());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        nameField.setText(rs.getString("name"));
                        passwordField.setText(rs.getString("password"));
                        roleField.setText(rs.getString("role"));
                    } else {
                        JOptionPane.showMessageDialog(this, "Employee not found");
                    }
                }
            }
        }
    }
    //Stock crud method
    private void handleStockCrud(Connection conn) throws SQLException {
        String sql;
        //Switch case for each option of the crud operations
        switch (action.toLowerCase()) {
            case "create" -> {
                sql = "INSERT INTO stock (product_id, name, quantity, price) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, productIdField.getText());
                    stmt.setString(2, productNameField.getText());
                    stmt.setInt(3, Integer.parseInt(quantityField.getText()));
                    stmt.setDouble(4, Double.parseDouble(priceField.getText()));
                    stmt.executeUpdate();
                }
            }
            case "update" -> {
                sql = "UPDATE stock SET name=?, quantity=?, price=? WHERE product_id=?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, productNameField.getText());
                    stmt.setInt(2, Integer.parseInt(quantityField.getText()));
                    stmt.setDouble(3, Double.parseDouble(priceField.getText()));
                    stmt.setString(4, productIdField.getText());
                    stmt.executeUpdate();
                }
            }
            case "delete" -> {
                sql = "DELETE FROM stock WHERE product_id=?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, productIdField.getText());
                    stmt.executeUpdate();
                }
            }
            case "read" -> {
                sql = "SELECT * FROM stock WHERE product_id=?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, productIdField.getText());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        productNameField.setText(rs.getString("name"));
                        quantityField.setText(String.valueOf(rs.getInt("quantity")));
                        priceField.setText(String.valueOf(rs.getDouble("price")));
                    } else {
                        JOptionPane.showMessageDialog(this, "Product not found!");
                    }
                }
            }
        }
    }
}
