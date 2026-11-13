
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class App {
    public static void main(String[] args) throws Exception {
        String sql = "select name from product where id=8";
        String url = "jdbc:postgresql://localhost:5432/telusko";
        /* jdbc:dbms://networkaddress:port#/dbname */
        String username = "";
        String password = "";

        Connection con = DriverManager.getConnection(url, username, password);
        java.sql.Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        rs.next();
        String name = rs.getString(1);
        System.err.println(name);

        /* SQL INJECTION to study */

        /* PREPARED STATEMENTS */
        /*
        
        Precompiled SQL statements
        - easier to set sql params value
        - prevent against sql dependency injection attack
        - improve app perf   

        - instead of hardcoding, set params placeholders
        - can also be used for insert, updates and deletes
        */

        PreparedStatement myStmt = con.prepareStatement("select * from employees" + " where salary > ? and department=?");
        myStmt.setDouble(1, 80000);
        myStmt.setString(2, "Legal");

        ResultSet myRs = myStmt.executeQuery();

        myStmt.setDouble(1, 25000);
        myStmt.setString(2, "HR");
        
        myRs = myStmt.executeQuery();
    }
}
