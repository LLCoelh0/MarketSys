import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConfig {
    //DB properties path string
    private static final String CONFIG_PATH = "src\\main\\java\\db.properties";
    //DB login method
    public static Connection getConnection() throws Exception {
        Properties props = new Properties();
        props.load(new FileReader(CONFIG_PATH));

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String dbPass = props.getProperty("db.password");

        return DriverManager.getConnection(url, user, dbPass);
    }
}
