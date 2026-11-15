package reports;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import records.CountPeriod.*;

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
    
    public static ResultSet generateReport(){
        StringBuilder query =   new StringBuilder();
        query.append("SELECT pointno, directn, method,");
        query.append("");
        query.append("");
        query.append("");
        query.append("");
        query.append("");
    }
}
