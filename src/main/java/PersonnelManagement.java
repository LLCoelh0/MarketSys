import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileReader;
import java.sql.*;
import java.util.Properties;

public class PersonnelManagement extends JFrame {
    private final DefaultTableModel employeeModel;

    //UI method
    public PersonnelManagement() {
        setTitle("MarketSys");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
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

        //Center panel
        employeeModel = new DefaultTableModel(new String[]{"User ID", "Name", "Password", "Role"}, 0);
        JTable employeeTable = new JTable(employeeModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);

        //CRUD panel
        JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        crudPanel.setBorder(BorderFactory.createTitledBorder("Manage Employees"));

        JButton creteBtn = new JButton("Create");
        JButton readBtn = new JButton("Refresh");
        JButton updateBtn = new JButton("Button");
        JButton deleteBtn = new JButton("Delete");

        crudPanel.add(creteBtn);
        crudPanel.add(readBtn);
        crudPanel.add(updateBtn);
        crudPanel.add(deleteBtn);
        //Refresh table
        readBtn.addActionListener(_ -> loadEmployeesFromDB());

        //Add components in panel
        add(sidebar, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(crudPanel, BorderLayout.SOUTH);

        //Refresh table on start
        loadEmployeesFromDB();
    }
    //Side panel button method
    private JButton createSidebarButton(String label) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(1209, 30));
        return button;
    }

    private void loadEmployeesFromDB() {
        employeeModel.setNumRows(0);
        Properties props = new Properties();
        try {
            //DB credentials
            props.load(new FileReader("src\\main\\java\\db.properties"));
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password =props.getProperty("db.password");

            //DB Connection and query
            try (
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM employees")
            ) {
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
