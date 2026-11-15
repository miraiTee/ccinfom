package reports.CompletedPoint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    ArrayList <CompletedPoint> completedPoints = new ArrayList<>();
    Connection conn;
    
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
            generateReport(conn);
            printReport();
        } catch (SQLException e){
            e.printStackTrace();
            throw new IllegalArgumentException("Skibidi");
        }
    }

    public void generateReport(Connection conn) throws SQLException {
        completedPoints.clear();
        StringBuilder query =   new StringBuilder();
        query.append("SELECT pointno, directn, method, ");
        query.append("COUNT(periodno) AS CompletedPeriods, ");
        query.append("MIN(startat) AS EarliestInterval ");
        query.append("FROM   countperiod ");
        query.append("WHERE status = 'Completed' ");
        
        query.append("GROUP BY pointno, directn, method ");
        query.append("ORDER BY CompletedPeriods DESC, pointNo, directn, method; ");

        try (PreparedStatement stmt = conn.prepareStatement(query.toString())){
            
            try (ResultSet rs = stmt.executeQuery()){
                while (rs.next()){
                    completedPoints.add(new CompletedPoint(
                    rs.getString("pointno"),
                    Direction.fromString(rs.getString("directn")),
                    Method.fromString(rs.getString("method")),
                    rs.getInt("CompletedPeriods"),
                    rs.getTimestamp("EarliestInterval").toLocalDateTime()
                    ));
                }
            }

        }

        System.out.println("Report generated successfully. Total rows found: " + completedPoints.size());
    }

    public void printReport() {
        System.out.println("Completed Interval Coverage:");
        System.out.println("Member: Mirai Dela Cruz");

        for (CompletedPoint completedPoint : completedPoints){
            System.out.println(completedPoint);
        }
    }

}
