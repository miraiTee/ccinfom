package reports.CompletedPoint;

import java.time.LocalDateTime;
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

public class CompletedPoint {
    private String pointNo;
    private Direction directn;
    private Method method;
    private int completedPeriod;
    private LocalDateTime startAt;

    public CompletedPoint(String pointNo, Direction directn, Method method, 
        int completedPeriod, LocalDateTime startAt){

        this.pointNo = pointNo;
        this.directn = directn;
        this.method = method;
        this.completedPeriod = completedPeriod;
        this.startAt = startAt;

    }

    public String getPointNo() { return pointNo; }

    @Override
    public String toString() {
        return "Point: " + pointNo + 
               " | Direction: " + directn + 
               " | Method: " + method + 
               " | Completed Periods: " + completedPeriod + 
               " | Earliest Interval: " + startAt;
    }
}
