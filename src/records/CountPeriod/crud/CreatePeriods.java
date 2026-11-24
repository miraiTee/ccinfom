package records.CountPeriod.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import records.CountPeriod.CountPeriod;
import records.CountPeriod.Direction;
import records.CountPeriod.Method;
import records.CountPeriod.PeriodNo;

public class CreatePeriods {
    private final Connection conn;
    private final Scanner s;

    private final ArrayList<String> activePoints = new ArrayList<>();
    private final ArrayList<String> activeDevices = new ArrayList<>();

    private String pointNo;
    private Direction directn;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDateTime startAt;
    private Method method;
    private String devNo;

    public CreatePeriods(Connection conn, Scanner s) {
        this.conn = conn;
        this.s = s;
    }

    public void run() {
        if (activePoints.isEmpty()){
            System.err.println("No active devices/points for this database");
            return;
        }

        while (true) {

            displayColumns();
            menu();

            if (!s.hasNextInt()) {
                s.nextLine();
                System.out.println("Enter a number.");
                continue;
            }

            int choice = s.nextInt();
            s.nextLine();

            switch (choice) {
                case 1 -> setPoint();
                case 2 -> setDirection();
                case 3 -> setTimestamp();
                case 4 -> setMethod();
                case 5 -> setDevice();
                case 6 -> createPeriod();
                case 0 -> {
                    System.out.println("Exiting Period Creation.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void menu() {
        System.out.println("Creating Period:");
        System.out.println("[ 1 ] Set Point");
        System.out.println("[ 2 ] Set Direction");
        System.out.println("[ 3 ] Set Timestamp");
        System.out.println("[ 4 ] Set Method");
        System.out.println("[ 5 ] Set Device");
        System.out.println("[ 6 ] Create Period");
        System.out.println("[ 0 ] Return to Menu");
    }

    private void displayColumns() {
        System.out.println();
        System.out.println("Point No.  : " + pointNo);
        System.out.println("Direction  : " + directn);
        System.out.println("Starts At  : " + startAt);
        System.out.println("Method     : " + method);
        System.out.println("Device No. : " + devNo);
        System.out.println();
    }

    public void setPoint() {
    
        try {
            getPoints();
        } catch (SQLException e) {
            System.out.println("Could not refresh active points: " + e.getMessage());
            return;
        }

        if (activePoints.isEmpty()) {
            System.out.println("No active points.");
            return;
        }

        while (true) {
            System.out.println("Select Point:");
            for (int i = 0; i < activePoints.size(); i++) {
                System.out.println("[ " + (i + 1) + " ] " + activePoints.get(i));
            }

            System.out.print("Enter choice: ");
            if (!s.hasNextInt()) {
                s.nextLine();
                System.out.println("Enter a number.");
                continue;
            }

            int choice = s.nextInt();
            s.nextLine();

            if (choice >= 1 && choice <= activePoints.size()) {
                pointNo = activePoints.get(choice - 1);
                return;
            }

            System.out.println("Invalid point selection.");
        }
    }

    public void setDirection() {
        while (true) {
            System.out.println("Select Direction:");
            System.out.println("[ 1 ] Northbound");
            System.out.println("[ 2 ] Southbound");
            System.out.print("Enter choice: ");

            if (!s.hasNextInt()) {
                s.nextLine();
                System.out.println("Enter a number.");
                continue;
            }

            int choice = s.nextInt();
            s.nextLine();

            switch (choice) {
                case 1 -> {
                    directn = Direction.Northbound;
                    return;
                }
                case 2 -> {
                    directn = Direction.Southbound;
                    return;
                }
                default -> System.out.println("Invalid direction.");
            }
        }
    }

    public void setTimestamp() {
        while (true) {
            System.out.println("Starts At: " + startAt);
            System.out.println("Timestamp Menu:");
            System.out.println("[ 1 ] Set Date");
            System.out.println("[ 2 ] Set Time");
            System.out.println("[ 3 ] Return");
            System.out.print("Enter choice: ");

            if (!s.hasNextInt()) {
                s.nextLine();
                System.out.println("Enter a number.");
                continue;
            }

            int choice = s.nextInt();
            s.nextLine();

            switch (choice) {
                case 1 -> setDate();
                case 2 -> setTime();
                case 3 -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public void setDate() {
        while (true) {
            System.out.print("Enter date (yyyy-MM-dd): ");
            String input = s.nextLine();

            try {
                startDate = LocalDate.parse(input);
                updateStartAt();
                System.out.println("Date set to: " + startDate);
                return;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use yyyy-MM-dd.");
            }
        }
    }

    public void setTime() {
        int hour;

        while (true) {
            System.out.print("Select hour (0–23): ");

            if (!s.hasNextInt()) {
                s.nextLine();
                System.out.println("Enter a number from 0 to 23.");
                continue;
            }

            hour = s.nextInt();
            s.nextLine();

            if (hour >= 0 && hour <= 23) {
                break;
            }

            System.out.println("Enter an hour from 0 to 23.");
        }

        int choice;
        while (true) {
            System.out.println("Select minute interval:");
            System.out.printf("[ 1 ] %02d:00%n", hour);
            System.out.printf("[ 2 ] %02d:15%n", hour);
            System.out.printf("[ 3 ] %02d:30%n", hour);
            System.out.printf("[ 4 ] %02d:45%n", hour);
            System.out.print("Enter choice: ");

            if (!s.hasNextInt()) {
                s.nextLine();
                System.out.println("Enter a number from 1 to 4.");
                continue;
            }

            choice = s.nextInt();
            s.nextLine();

            if (choice >= 1 && choice <= 4) {
                break;
            }

            System.out.println("Enter a choice from 1 to 4.");
        }

        int minute = (choice - 1) * 15;
        startTime = LocalTime.of(hour, minute);
        updateStartAt();

        System.out.println("Time set to: " + startTime);
    }

    private void updateStartAt() {
        if (startDate != null) {
            LocalTime time = (startTime == null) ? LocalTime.MIDNIGHT : startTime;
            startAt = LocalDateTime.of(startDate, time);
        }
    }

    public void setMethod() {
        Method[] methods = Method.values();

        while (true) {
            System.out.println("Select Method:");
            for (int i = 0; i < methods.length; i++) {
                System.out.println("[ " + (i + 1) + " ] " + methods[i]);
            }

            System.out.print("Enter choice: ");
            if (!s.hasNextInt()) {
                s.nextLine();
                System.out.println("Enter a number.");
                continue;
            }

            int choice = s.nextInt();
            s.nextLine();

            switch (choice) {
                case 1:
                    method = Method.Manual;
                    devNo = null;
                    return;
                case 2:
                    method = Method.Device;
                    return;
                default:
                    System.out.println("Invalid method selection.");
            }

            System.out.println("Invalid method selection.");
        }
    }

    public void setDevice() {

        try {
            getDevices();
        } catch (SQLException e) {
            System.out.println("Could not refresh active devices: " + e.getMessage());
            return;
        }


        if (method == Method.Manual){
            System.out.println("Manual Method is set. Change it first");
            return;
        }
        
        if (activeDevices.isEmpty()) {
            System.out.println("No active devices.");
            return;
        }

        while (true) {
            System.out.println("Select Device:");
            for (int i = 0; i < activeDevices.size(); i++) {
                System.out.println("[ " + (i + 1) + " ] " + activeDevices.get(i));
            }

            System.out.print("Enter choice: ");
            if (!s.hasNextInt()) {
                s.nextLine();
                System.out.println("Enter a number.");
                continue;
            }

            int choice = s.nextInt();
            s.nextLine();

            if (choice >= 1 && choice <= activeDevices.size()) {
                devNo = activeDevices.get(choice - 1);
                return;
            }

            System.out.println("Invalid device selection.");
        }
    }

    public void getPoints() throws SQLException {
        activePoints.clear();

        String sql = "SELECT pointno FROM point WHERE active = 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                activePoints.add(rs.getString("pointno"));
            }
        }
    }

    public void getDevices() throws SQLException {
        activeDevices.clear();

        String sql = "SELECT devno FROM device WHERE active = 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                activeDevices.add(rs.getString("devno"));
            }
        }
    }

    public void createPeriod() {
        if (pointNo == null || directn == null || startDate == null
                || startTime == null || startAt == null || method == null
                || (method == Method.Device && devNo == null)) {
            System.out.println("Set the point, direction, date, time, method, and required device first.");
            return;
        }

        if (method == Method.Manual) {
            devNo = null;
        }

        try {
            LocalDateTime endAt = startAt.plusMinutes(15);

            if (hasConflictingOverlap(pointNo, directn, startAt, endAt, devNo)) {
                return;
            }

            String periodNo = getNextPeriodNo(startDate);

            CountPeriod period = new CountPeriod(
                    periodNo,
                    pointNo,
                    directn,
                    startAt,
                    method,
                    devNo
            );

            String sql = """
                    INSERT INTO countperiod
                        (periodno, pointno, directn, startat, endat,
                        method, devno, status, notes, revno)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, period.getPeriodNo().toString());
                stmt.setString(2, period.getPointNo());
                stmt.setString(3, period.getDirectn().getID());
                stmt.setTimestamp(4, Timestamp.valueOf(period.getStartAt()));
                stmt.setTimestamp(5, Timestamp.valueOf(period.getEndAt()));
                stmt.setString(6, period.getMethod().getID());
                stmt.setString(7, period.getDevNo());
                stmt.setString(8, period.getStatus().getID());
                stmt.setString(9, period.getNotes());
                stmt.setInt(10, period.getRevNo());

                stmt.executeUpdate();
                System.out.println("Period created: " + period.getPeriodNo());
            }
        } catch (SQLException | IllegalArgumentException | NullPointerException e) {
            System.out.println("Could not create period: " + e.getMessage());
        }
    }

    private String getNextPeriodNo(LocalDate date) throws SQLException {
        int year = date.getYear() % 100;
        String prefix = String.format("CP%02d", year);

        String sql = """
                SELECT MAX(periodno)
                FROM countperiod
                WHERE periodno LIKE ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, prefix + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                String greatest = rs.next() ? rs.getString(1) : null;

                int nextNumber = (greatest == null)
                        ? 0
                        : Integer.parseInt(greatest.substring(4)) + 1;

                return new PeriodNo(year, nextNumber).toString();
            }
        }
    }

    private boolean hasConflictingOverlap(
            String pointNo,
            Direction direction,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String devNo
    ) throws SQLException {
        String sql = """
                SELECT periodno, pointno, directn, devno
                FROM countperiod
                WHERE startat < ?
                AND endat > ?
                AND (
                        (pointno = ? AND directn = ?)
                        OR (? IS NOT NULL AND devno = ?)
                    )
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(endAt));
            stmt.setTimestamp(2, Timestamp.valueOf(startAt));
            stmt.setString(3, pointNo);
            stmt.setString(4, direction.name());
            stmt.setString(5, devNo);
            stmt.setString(6, devNo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String existingPeriodNo = rs.getString("periodno");
                    String existingPointNo = rs.getString("pointno");
                    String existingDirection = rs.getString("directn");
                    String existingDevNo = rs.getString("devno");

                    boolean samePointAndDirection =
                            pointNo.equals(existingPointNo)
                            && direction.name().equals(existingDirection);

                    if (samePointAndDirection) {
                        System.out.println(
                                "Overlaps point and direction with: "
                                        + existingPeriodNo);
                        return true;
                    }

                    if (devNo != null && devNo.equals(existingDevNo)) {
                        System.out.println(
                                "Device " + devNo
                                + " is already assigned to an overlapping period: "
                                + existingPeriodNo);
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
