import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydb";
        String user = "javauser";
        String password = "choose_a_password";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected: " + !conn.isClosed());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}