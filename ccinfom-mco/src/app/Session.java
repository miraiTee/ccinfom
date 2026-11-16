// mago
package app;



import java.sql.Connection;
import java.sql.SQLException;

/***
 * Singleton class used to define a user's session in the app.
 * If the user's profile is changed, soo too does the session's instance is.
 * 
 * Contains a profile and valid connection with it.
 * 
 * Session
 */
public class Session {
    private static Session instance;
    private Profile userProfile;
    private Connection connection;

    private Session(Profile profile, Connection conn) {
        this.userProfile = profile;
        this.connection = conn;
    }

    public static Session startSession(Profile profile, Connection conn) {
        instance = new Session(profile, conn);
        return instance;
    }

    public static Session getInstance() {
        return instance;
    }

    public void invalidate() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
        instance = null;
    }

    public Connection getConnection() {
        return connection;
    }

    public Profile getUserProfile() {
        return userProfile;
    }
    
    public static boolean isAuthenticated() {
        return instance != null;
    }
}