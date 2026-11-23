// mago
package records.CountPeriod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/***
 * Count period records.
 * CountPeriod
 */
public class CountPeriodRecords {

    private Connection conn;
    private CountPeriodList countPeriodList = new CountPeriodList();
    private Scanner s = new Scanner(System.in);

    private ArrayList <String> availablePoints = new ArrayList<>();
    private ArrayList <String> availableDevices = new ArrayList<>();

    public CountPeriodRecords(Connection conn){
        this.conn = conn;
    }

    public void run(){
        try {
            checkAvailablePeriods();
            checkAvailableDevices();
            recordMenu();
        } catch (Exception e){

        }
    }

    private void recordMenu() throws Exception {
        boolean lifetime = true;

        // try {
        //     initPeriodList();
        // } catch (Exception e) {
        //     throw new IllegalArgumentException("Failed generating report.");
        // }

        while(lifetime){
            try{
                System.out.println("Count Period Records");
                System.out.println("Select what to do in the records:");
                System.out.println("[ 1 ] : Create New Count Period");
                System.out.println("[ 2 ] : Read Period Listing");
                System.out.println("[ 3 ] : Update Count Period");
                System.out.println("[ 4 ] : Delete Count Period");
                System.out.println("[ 5 ] : Return to Main Menu");

                int input = s.nextInt();

                switch(input){
                    case 1: createNewPeriod(); break;
                    case 2: readPeriod();
                    case 3: updatePeriod();
                    case 4: deletePeriod();
                    case 5: lifetime = false; break;
                    default: System.out.println("Incorrect option");
                }
            } catch (Exception e){

            }
        }
    }

    // private void initPeriodList() throws SQLException {
    //     String sql = "SELECT * from countperiod";

    //     try (PreparedStatement stmt = conn.prepareStatement(sql);
    //         ResultSet rs = stmt.executeQuery()){

    //         while (rs.next()) {                    
                
    //             String periodNo = rs.getString("periodno");
    //             String pointNo = rs.getString("pointno");
    //             Direction directn = Direction.valueOf(rs.getString("directn"));

    //             LocalDateTime startAt = rs.getTimestamp("startat").toLocalDateTime();
    //             LocalDateTime endAt = rs.getTimestamp("endat").toLocalDateTime();

    //             Method method = Method.valueOf(rs.getString("method"));

    //             String devNo = rs.getString("devno");
                
    //             Status status = Status.valueOf(rs.getString("status"));

    //             String notes = rs.getString("notes");
    //             int revNo = rs.getInt("revno");

    //             try {
    //                 CountPeriod countPeriod = new CountPeriod(periodNo, pointNo, directn, 
    //                                                             startAt, endAt, method, 
    //                                                             devNo, status, notes, revNo);
    //                 countPeriodList.add(countPeriod);
    //             } catch (IllegalArgumentException | NullPointerException e) {    
    //                 System.err.println("Skipped period " + periodNo + ": " + e.getMessage());
    //             }
    //         }
    //     }
    // }

    /***
     * 
     * CREATION
     * 
     */

    private void createNewPeriod() throws SQLException{
        CountPeriod countPeriod = createNewPeriodMenu();

        String sql = """
        INSERT INTO countperiod
            (periodno, pointno, directn, startat, endat,
             method, devno, status, notes, revno)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;


        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, countPeriod.getPeriodNo().toString());
            stmt.setString(2, countPeriod.getPointNo());
            stmt.setString(3, countPeriod.getDirectn().getID());
            stmt.setTimestamp(4, Timestamp.valueOf(countPeriod.getStartAt()));
            stmt.setTimestamp(5, Timestamp.valueOf(countPeriod.getEndAt()));
            stmt.setString(6, countPeriod.getMethod().getID());
            stmt.setString(7, countPeriod.getDevNo());

            stmt.setString(8, countPeriod.getStatus().getID());
            stmt.setString(9, countPeriod.getNotes());
            stmt.setInt(10, countPeriod.getRevNo());

            stmt.executeUpdate();
        } 
    }

    private CountPeriod createNewPeriodMenu() {
        // select existing point
        String point = selectPoint();
        Direction dir = selectDirection();

        LocalDateTime startat = selectTime();
        // select counting method
        Method method = selectMethod();
        // if method == device
        String device = null;

        String latestPeriod = null;
        CountPeriod countPeriod = null;

        try {
            latestPeriod = incrementPeriod(checkLatestPeriod());
        } catch (Exception e){
            throw new IllegalArgumentException("rip");
        }

        if (method == Method.Device)
            device = selectDevice();

        try {
           countPeriod = new CountPeriod(latestPeriod, point, dir, startat, device);
        } catch (Exception e) {
            throw new IllegalArgumentException("rip");
        }

        return countPeriod;
    }

    private String selectPoint(){
        while(true){

            System.out.println("Select Available Points");

            int i = 1;
            for (String point : availablePoints){
                System.out.println("[" + i + "] " + point);
                i += 1;
            }

            String input = s.nextLine();

            if (availablePoints.contains(input))
                return input;
            else
                System.out.println("Point selection not found");
        }
    }

    private Method selectMethod(){
        while (true) { 
            System.out.println("Select Method");
            System.out.println("[1]: Manual");
            System.out.println("[2]: Device (must input device later)");

            String direction = s.nextLine();

            try {
                Method m = Method.valueOf(direction);
                return m;
            } catch (Exception e){
                System.out.println("Direction not found");
            }
        }
    }

    private String selectDevice(){
        while(true){

            System.out.println("Select Available Devices");

            int i = 1;
            for (String device : availableDevices){
                System.out.println("[" + i + "] " + device);
                i += 1;
            }

            String input = s.nextLine();

            if (availableDevices.contains(input))
                return input;
            else
                System.out.println("Device selection not found");
        }
    }

    private LocalDateTime selectTime(){
        TimeService ts = new TimeService();

        int year = ts.selectYear();

        int month = ts.selectMonth();

        int day = ts.selectDay(year, month);

        int hour = ts.selectHour();

        int minute = ts.selectHour();

        LocalDate date = LocalDate.of(year, month, day);
        LocalTime time = LocalTime.of(hour, minute);
        return LocalDateTime.of(date, time);
    }

    private Direction selectDirection(){
        while (true) { 
            System.out.println("Select Directions");
            System.out.println("[1]: Northbound");
            System.out.println("[2]: Southbound");

            String direction = s.nextLine();

            try {
                Direction dir = Direction.valueOf(direction);
                return dir;
            } catch (Exception e){
                System.out.println("Direction not found");
            }
        }
    }

    /***
     * 
     * READING
     * 
     */
    private void readPeriod(){
        while (true) { 
            System.out.println("Search Options");
            System.out.println("[1]: Single Period");
            System.out.println("[2]: Multiple Periods (w/ Filter)");

            String direction = s.nextLine();

        }
        // search for reference
        // point
        // direction
        // start range
        // method
        // device
        // state

        // selection loads saved metadata
        // all current category rows (incl. null)
    }

    private void searchSinglePeriod(PeriodNo periodNo) throws SQLException {
        String sql = """
            SELECT *
            FROM countperiod
            WHERE periodno = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, periodNo.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PeriodNo pNum = new PeriodNo(rs.getString("periodno"));
                    String pointNo = rs.getString("pointno");
                    Direction direction = Direction.valueOf(rs.getString("directn"));
                    LocalDateTime startAt = rs.getTimestamp("startat").toLocalDateTime();
                    LocalDateTime endAt = rs.getTimestamp("endat").toLocalDateTime();
                    Method method = Method.valueOf(rs.getString("method"));
                    String devNo = rs.getString("devno");
                    Status status = Status.valueOf(rs.getString("status"));
                    String notes = rs.getString("notes");
                    int revNo = rs.getInt("revno");

                    CountPeriod countPeriod = new CountPeriod(
                        pNum, pointNo, direction, startAt, endAt, 
                        method, devNo, status, notes, revNo
                    );

                    ArrayList<CountPeriod> countPeriods = new ArrayList<>();
                    countPeriods.add(countPeriod);
                    printPeriods(countPeriods);

                } else {
                    System.out.println("No period found.");
                }
            }
        }
    }

    private void printPeriods(ArrayList<CountPeriod> countPeriods){
        System.out.println("Completed Interval Coverage:");
        System.out.println("Member: Mirai Dela Cruz");
        System.out.println("");

        System.out.println("Period # | Point # | Direction | StartAt | EndAt | Method | DevNo | Status | Notes | RevNo");
        System.out.println("Found" + countPeriods.size() + "period:");
        System.out.println("--------------------------------------------------------------");
        for (CountPeriod countPeriod : countPeriods){
            System.out.println(countPeriod);
        }
        System.out.println("--------------------------------------------------------------");
    }

    private void readPeriodListings() throws SQLException{
        for (CountPeriod countPeriod : countPeriodList.getList()){
            System.out.println(countPeriod);
        }
    }


    private void editPeriodMenu() {
        // no edit to reference, point/dir/startat
        // ONCE COUNT depends on period

        // corrections cannot create overlaps or change meaning of a saved counting boundary

        // completion checks all six rows and coord change through the period

        // if period = completed
        // authorized correction and explanation
        // invalidated set == completed activity
    }

    private void deletePeriod() {
        // limited to erroneous "Draft" period w/o category rows
        // m 4 resolve any erroneous child records before deleting parent
        // Completed periods and legitimate zero-count periods NOT removed
    }

    private String checkLatestPeriod() throws SQLException {
        /* Integrate point's necessary details once point record is done */
        String sql = "SELECT periodno FROM countperiod ORDER BY pointno DESC LIMIT 1";
        String s = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery(); 
            while (rs.next()) {
                s = rs.getString("periodno");
            }
        } 

        return s;
    }

    private String incrementPeriod(String periodNo){
        //split two 
        String periodPrefix = periodNo.substring(0,1);
        int periodNum = Integer.parseInt(periodNo.substring(2)) + 1;

        return (periodPrefix + Integer.toString(periodNum));
    }

    private void checkAvailablePeriods() throws SQLException {
        /* Integrate point's necessary details once point record is done */
        String sql = "SELECT pointno FROM point WHERE active = 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery(); 
            while (rs.next()) {
                availablePoints.add(rs.getString("pointno"));
            }
        }
    }

    private void checkAvailableDevices() throws SQLException {
        /* Integrate device's necessary details once point record is done */
        String sql = "SELECT deviceno FROM device WHERE active = 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                availableDevices.add(rs.getString("deviceno"));
            }
        }
    }
}
