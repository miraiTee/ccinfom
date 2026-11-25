package reports.CompletedPoint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import records.CountPeriod.Direction;
import records.CountPeriod.Method;

/***
 * Member 3 owns this report. 
 * It summarizes the extent of recorded completed counting during inclusive user selected dates. 
 * Each row represents 
 * - one point, 
 * - direction, 
 * - and method, 
 * - and displays the point reference,  
 * - current name, 
 * - direction, 
 * - method, 
 * - completed-period count, 
 * - and the earliest and latest qualifying interval starts. 
 * 
 * - Optional filters are point, 
 * - direction, 
 * - method, 
 * - and a nonnegative minimum completed-period count. 
 * 
 * - Blank point  and All category choices impose no corresponding restriction.
 * - The threshold applies to the complete group,  
 * - not individual period rows. 
 * 
 * Only groups with qualifying "Completed" periods appear. 
 * Order is period count  descending, then point reference, direction, and method. 
 * CICReport
 */

public class CICReport {
    private Scanner s = new Scanner(System.in);

    private ArrayList <CompletedPoint> completedPoints = new ArrayList<>();
    private Connection conn;

    // Filters
    private ArrayList <String> distinctPoints = new ArrayList<>();
    private ArrayList <String> filterDistinctPoints = new ArrayList<>();
    private ArrayList <String> filterDirection = new ArrayList<>();
    private ArrayList <String> filterMethod = new ArrayList<>();
    // if 0, do not filter
    private int filterMinCount = -1;

    /* Filters to add:
        point
        directn
        method
        nonnegative min completed period count
    */

    public CICReport(Connection conn){
        this.conn = conn;
    }

    public void run(){
        try{
            generateReport();
            printReport();
        } catch (SQLException e){
            e.printStackTrace();
            throw new IllegalArgumentException("Failed generating report.");
        }
    }

    public void checkDistinctPoints() throws SQLException {
        String sql = """ 
        SELECT DISTINCT pointno 
        FROM countperiod 
        WHERE status = 'Completed' 
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                distinctPoints.add(rs.getString("pointno"));
            }
        }
    }

    public void generateReport() throws SQLException {
        checkDistinctPoints();
        filterMenu();


        StringBuilder query =   new StringBuilder();
        query.append("SELECT pointno, directn, method, ");
        query.append("COUNT(periodno) AS CompletedPeriods, ");
        query.append("MIN(startat) AS EarliestStart, ");
        query.append("MAX(startat) AS LatestStart ");
        query.append("FROM countperiod ");
        query.append("WHERE status = 'Completed' ");

        query.append(filterTemplate());
        
        query.append("GROUP BY pointno, directn, method ");

        if (filterMinCount > 0) {
            query.append(" HAVING CompletedPeriods >= ? ");
        }

        query.append("ORDER BY CompletedPeriods DESC, pointNo, directn, method; ");

        try (PreparedStatement stmt = conn.prepareStatement(query.toString())){
            
            int paramIndex = 1;
            if (!filterDistinctPoints.isEmpty()) {
                for (String p : filterDistinctPoints) stmt.setString(paramIndex++, p);
            }
            if (!filterDirection.isEmpty()) {
                for (String d : filterDirection) stmt.setString(paramIndex++, d);
            }
            if (!filterMethod.isEmpty()) {
                for (String m : filterMethod) stmt.setString(paramIndex++, m);
            }
            if (filterMinCount > 0) {
                stmt.setInt(paramIndex++, filterMinCount);
            }
            
            try (ResultSet rs = stmt.executeQuery()){
                while (rs.next()){
                    completedPoints.add(new CompletedPoint(
                    rs.getString("pointno"),
                    Direction.valueOf(rs.getString("directn")),
                    Method.valueOf(rs.getString("method")),
                    rs.getInt("CompletedPeriods"),
                    rs.getTimestamp("EarliestStart").toLocalDateTime(),
                    rs.getTimestamp("LatestStart").toLocalDateTime()
                    ));
                }
            }

        }

        System.out.println("Report generated successfully.");
    }

    /*
        Filters:
        - points
        - direction
        - method
        - min completed-period count
    */
    public void filterMenu(){

        // Distinct Pts
        while (true){
        System.out.println("Choose distinct points to analyze. [ Leave Blank for all pts ]");
        int i = 1;
        for (String distinctPoint : distinctPoints){
            System.out.println("[ " + Integer.toString(i) + " ] " + distinctPoint);
            i += 1;
        }
            try{
                String input = s.nextLine();

                if (input.isEmpty()){
                    for (String distinctPoint : distinctPoints){
                        filterDistinctPoints.add(distinctPoint);
                    }
                }

                else {
                    String[] distPoints = input.split(" ");
                    for (String distinctPoint : distPoints){
                        if (distinctPoints.contains(distinctPoint))
                            filterDistinctPoints.add(distinctPoint);
                        else
                            throw new Exception("Invalid Input");
                    }
                }
            } catch(Exception e){
                filterDistinctPoints.clear();
            }

            if (!filterDistinctPoints.isEmpty())
                break;
        }

        // Direction
        while (true) { 
            System.out.println("Choose direction/s to analyze.");
            System.out.println("[ 1 ]: Northbound");
            System.out.println("[ 2 ]: Southbound");   
            System.out.println("[ 3 ]: All");        

            try {
                String input = s.nextLine();
            
                if (input.equals("Northbound"))
                    filterDirection.add(input);
                else if (input.equals("Southbound"))
                    filterDirection.add(input);
                else if (input.equals("All")){
                    filterDirection.add("Northbound");
                    filterDirection.add("Southbound");
                }
                else
                    throw new Exception("Invalid Input.");

            } catch (Exception e){
                filterDirection.clear();
            }

            if (!filterDirection.isEmpty())
                break;
        }

        // Method
        while (true) { 
            System.out.println("Choose method/s to analyze.");
            System.out.println("[ 1 ]: Manual");
            System.out.println("[ 2 ]: Device");
            System.out.println("[ 3 ]: All");

            try {
                String method = s.nextLine();

                    if (method.equals("Manual"))
                        filterMethod.add(method);
                    else if (method.equals("Device"))
                        filterMethod.add(method);
                    else if (method.equals("All")){
                        filterMethod.add("Manual");
                        filterMethod.add("Device");
                    }
                    else
                        throw new Exception("Invalid Input.");

            } catch (Exception e){
                filterMethod.clear();
            }

            if (!filterMethod.isEmpty())
                break;
        }

        // Min Count
        while (true) { 
            System.out.println("Choose minimum count to analyze.");
            boolean pass = true;

            try {
                filterMinCount = s.nextInt();
                if (filterMinCount < 0)
                    throw new Exception("Only positive/0 min counts.");
            } catch (Exception e){
                filterMinCount = -1;
                pass = false;
                System.err.println("Invalid Input.");
            }
            
            if (pass)
                break;
        }

    }

    public String filterTemplate() {
        StringBuilder filter = new StringBuilder();

        if (!filterDistinctPoints.isEmpty()) {
            filter.append(" AND pointno IN (");
            for (int i = 0; i < filterDistinctPoints.size(); i++) {
                filter.append(i == filterDistinctPoints.size() - 1 ? "?" : "?,");
            }
            filter.append(") ");
        }

        if (!filterDirection.isEmpty()) {
            filter.append(" AND directn IN (");
            for (int i = 0; i < filterDirection.size(); i++) {
                filter.append(i == filterDirection.size() - 1 ? "?" : "?,");
            }
            filter.append(") ");
        }

        if (!filterMethod.isEmpty()) {
            filter.append(" AND method IN (");
            for (int i = 0; i < filterMethod.size(); i++) {
                filter.append(i == filterMethod.size() - 1 ? "?" : "?,");
            }
            filter.append(") ");
        }

        return filter.toString();
    }

    public void printReport() {
        System.out.println("Completed Interval Coverage:");
        System.out.println("Member: Mirai Dela Cruz");

        System.out.println("");
        System.out.println("Point # | Direction | Method | CompletedPeriods | EarliestStart | LatestStart");


        for (CompletedPoint completedPoint : completedPoints){
            System.out.println(completedPoint);
        }

        System.out.println("Total rows found: " + completedPoints.size());
    }

}
