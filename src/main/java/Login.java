import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;


public class Login extends JFrame{
    //UI fields
    private final JTextField userField;
    private final JPasswordField passField;

    //UI method
    public Login() {
        //Window setup
        setTitle("MarketSys");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        //Setup login fields panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10,10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20,10, 10));
        //ID field
        formPanel.add(new JLabel("Username or id"));
        userField = new JTextField();
        formPanel.add(userField);
        //Password field
        formPanel.add(new JLabel("Password"));
        passField = new JPasswordField();
        formPanel.add(passField);
        //Add panel
        add(formPanel, BorderLayout.CENTER);
        //Login button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener((this::handleLogin));
        //Setup and button creation
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        getRootPane().setDefaultButton(loginButton);
        //Add login button
        add(buttonPanel, BorderLayout.SOUTH);
    }
    //Login process
    private void handleLogin(ActionEvent e) {
        String usernameOrId = userField.getText().trim();
        String password = new String(passField.getPassword());
        //Validation fields
        if (usernameOrId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill in all fields", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            //Database connection
            try (Connection conn = DatabaseConfig.getConnection()) {
                //Check login info in db
                String sql = "SELECT * FROM employees WHERE (name = ? or user_id = ?) AND password = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, usernameOrId);
                    stmt.setString(2, usernameOrId);
                    stmt.setString(3, password);
                    //Check credentials
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Login successful!", "Welcome", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new Dashboard().setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid username or password", "Access Denied", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error during login:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}