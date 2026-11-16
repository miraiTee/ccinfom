import app.Database;
import app.Profile;
import app.Session;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import reports.CompletedPoint.CICReport;

public class App {
    
    public static Scanner s = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("EDSA Traffic Monitoring.");
        boolean running = true;

        while (running) {
            if (!Session.isAuthenticated()) {
                handleLogin();
            } else {
                int choice = mainMenu();
                switch (choice) {
                    case 1: handleRecords(); break;
                    case 2: handleReports(); break;
                    case 3: Session.getInstance().invalidate(); break;
                    case 4: 
                        if (Session.isAuthenticated()) {
                            Session.getInstance().invalidate();
                        }
                        running = false; 
                        break;
                    default: System.out.println("Invalid choice");
                }
            }
        }
    }


    private static void handleLogin() {


        Profile profile = enterProfile();
        Database database = new Database();
        try {
            Connection conn = DriverManager.getConnection(database.getURL(), profile.getUsername(), profile.getPassword());
            Session.startSession(profile, conn);
            System.out.println("Logged in successfully.");
        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private static void handleRecords() {
        int choice = recordMenu();
        Connection conn = Session.getInstance().getConnection();

        try {
            switch (choice) {
                case 1: 
                    // points
                    break;
                case 2: 
                    // devices
                    break;
                case 3:
                    // countperiods
                    break;
                case 4:
                    // vehcount
                    break;
                case 5:
                    //radarcal
                    break;
                default: System.out.println("Invalid choice");
            }
        } catch (Exception e) {
            System.out.println("Error retrieving records: " + e.getMessage());
        }
    }

    private static void handleReports() {
        int choice = reportMenu();
        Connection conn = Session.getInstance().getConnection();

        try {
            switch (choice) {
                case 1: 
                    // Completed Traffic Counts by Monitoring Point
                    break;
                case 2: 
                    // Device Counting Participation
                    break;
                case 3:
                    CICReport CICreport = new CICReport(conn);
                    CICreport.run();
                    break;
                case 4:
                    // Classified Vehicle Volumes
                    break;
                default: System.out.println("Invalid choice");
            }
        } catch (Exception e) {
            System.out.println("Error retrieving reports: " + e.getMessage());
        }
    }

    public static int mainMenu(){
        System.out.println("");
        System.out.println("Choose ff: ");
        System.out.println("[ 1 ] : Check Records");
        System.out.println("[ 2 ] : Check Reports");
        System.out.println("[ 3 ] : Change Profile");
        System.out.println("[ 4 ] : Quit App");
        
        int choice = s.nextInt();
        s.nextLine();
        return choice;
    }

    public static int recordMenu(){
        System.out.println("");
        System.out.println("Choose records to check: ");
        System.out.println("[ 1 ] : Points");
        System.out.println("[ 2 ] : Device");
        System.out.println("[ 3 ] : CountPeriod");
        System.out.println("[ 4 ] : VehCount");
        System.out.println("[ 5 ] : RadarCal");

        int choice = s.nextInt();
        s.nextLine();
        return choice;
    }

    public static int reportMenu(){
        System.out.println("");
        System.out.println("Choose report to check: ");
        System.out.println("[ 1 ] : Completed Traffic Counts by Monitoring Point");
        System.out.println("[ 2 ] : Device Counting Participation");
        System.out.println("[ 3 ] : Completed Interval Coverage");
        System.out.println("[ 4 ] : Classified Vehicle Volumes");
        
        int choice = s.nextInt();
        s.nextLine();
        return choice;
    }

    public static Profile enterProfile(){
        System.out.println("");
        System.out.print("Enter username: ");
        String username = s.nextLine();
        System.out.print("Enter password: ");
        String password = s.nextLine();

        return new Profile(username, password);
    }

    public static Connection connectToDatabase(Profile profile, Database database) throws SQLException {
        return DriverManager.getConnection(database.getURL(), profile.getUsername(), profile.getPassword());
    }
}
