package app;

import java.sql.Connection;

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