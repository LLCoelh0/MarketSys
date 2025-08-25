import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PersonnelManagement extends BaseWindow {
    private final DefaultTableModel employeeModel;

    //UI method
    public PersonnelManagement() {
        //Setup Personnel panel
        employeeModel = new DefaultTableModel(new String[]{"User ID", "Name", "Password", "Role"}, 0);
        JTable employeeTable = new JTable(employeeModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        //Add Personnel table
        add(scrollPane, BorderLayout.CENTER);

        //CRUD setup panel
        JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        crudPanel.setBorder(BorderFactory.createTitledBorder("Manage Employees"));
        JButton create = new JButton("Create");
        JButton readBtn = new JButton("Refresh");
        JButton updateBtn = new JButton("Button");
        JButton deleteBtn = new JButton("Delete");

        crudPanel.add(create);
        crudPanel.add(readBtn);
        crudPanel.add(updateBtn);
        crudPanel.add(deleteBtn);

        create.addActionListener(_ -> new CrudPopup(this, "Create", "employees"));
        updateBtn.addActionListener(_ -> new CrudPopup(this, "Update", "employees"));
        deleteBtn.addActionListener(_ -> new CrudPopup(this, "Delete", "employees"));
        readBtn.addActionListener(_ -> loadEmployeesFromDB());


        //Add CRUD panel
        add(crudPanel, BorderLayout.SOUTH);


        //Refresh table
        readBtn.addActionListener(_ -> loadEmployeesFromDB());
        //Refresh table on start
        loadEmployeesFromDB();
    }

    private void loadEmployeesFromDB() {
        employeeModel.setNumRows(0);
        try {
            //DB Connection and query
            try (
                Connection conn = DatabaseConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM employees")
            ){
                while (rs.next()) {
                    int userID = rs.getInt("user_id");
                    String name = rs.getString("name");
                    String passwd = rs.getString("password");
                    String role = rs.getString("role");
                    employeeModel.addRow(new Object[]{userID, name, passwd, role});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading employees:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PersonnelManagement().setVisible(true));
    }

}
