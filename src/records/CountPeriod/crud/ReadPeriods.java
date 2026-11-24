package records.CountPeriod.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import records.CountPeriod.Direction;
import records.CountPeriod.Method;
import records.CountPeriod.PeriodNo;
import records.CountPeriod.Status;

public class ReadPeriods {
    private final Connection conn;
    private final Scanner s;

    private String initPeriodNo;
    private String finalPeriodNo;
    private final List<String> points = new ArrayList<>();
    private Direction direction;
    private LocalDateTime initStartRange;
    private LocalDateTime finalStartRange;
    private Method method;
    private final List<String> devices = new ArrayList<>();
    private Status status;

    public ReadPeriods(Connection conn, Scanner s) {
        this.conn = conn;
        this.s = s;
    }

    public void run() {
        while (true) {
            menu();
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> searchPeriod();
                case 2 -> searchPeriods();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public void menu() {
        System.out.println("\nReading Periods:");
        System.out.println("[ 1 ] Search a Period");
        System.out.println("[ 2 ] Search Multiple Periods");
        System.out.println("[ 0 ] Return to Menu");
    }

    private void searchPeriod() {
        System.out.print("Enter Period No. ");
        String input = s.nextLine().trim();

        try {
            PeriodNo periodNo = new PeriodNo(input);
            String sql = "SELECT * FROM countperiod WHERE periodno = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, periodNo.toString());
                displayResults(stmt);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid period number: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Could not search periods: " + e.getMessage());
        }
    }

    private void searchPeriods() {
        clearFilters();

        while (true) {
            displayFilters();
            filterMenu();

            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1 -> setPeriodRange();
                case 2 -> setPoints();
                case 3 -> setDirection();
                case 4 -> setStartRange();
                case 5 -> setMethod();
                case 6 -> setDevices();
                case 7 -> setStatus();
                case 8 -> {
                    executeSearch();
                    return;
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void filterMenu() {
        System.out.println("Filters Menu");
        System.out.println("[ 1 ] Filter Range of Periods");
        System.out.println("[ 2 ] Filter Points");
        System.out.println("[ 3 ] Filter Direction");
        System.out.println("[ 4 ] Filter Starting Timestamp");
        System.out.println("[ 5 ] Filter Method");
        System.out.println("[ 6 ] Filter Devices");
        System.out.println("[ 7 ] Filter Status");
        System.out.println("[ 8 ] Search");
        System.out.println("[ 0 ] Cancel");
    }

    private void displayFilters() {
        System.out.println("\nCurrent filters:");
        System.out.println("Period range: " + valueOrAny(initPeriodNo) + " to "
                + valueOrAny(finalPeriodNo));
        System.out.println("Points: " + (points.isEmpty() ? "Any" : points));
        System.out.println("Direction: " + valueOrAny(direction));
        System.out.println("Start range: " + valueOrAny(initStartRange) + " to "
                + valueOrAny(finalStartRange));
        System.out.println("Method: " + valueOrAny(method));
        System.out.println("Devices: " + (devices.isEmpty() ? "Any" : devices));
        System.out.println("Status: " + valueOrAny(status));
    }

    private void setPeriodRange() {
        System.out.print("First Period No. (blank for no lower limit): ");
        initPeriodNo = blankToNull(s.nextLine());

        System.out.print("Last Period No. (blank for no upper limit): ");
        finalPeriodNo = blankToNull(s.nextLine());
    }

    private void setPoints() {
        try {
            List<String> activePoints = getActiveValues(
                    "SELECT pointno FROM point WHERE active = 1",
                    "pointno"
            );

            if (activePoints.isEmpty()) {
                System.out.println("No active points found.");
            } else {
                System.out.println("Available active points: " + activePoints);
            }

            System.out.print("Enter point numbers separated by commas (blank to clear): ");
            replaceList(points, s.nextLine());
        } catch (SQLException e) {
            System.out.println("Could not load points: " + e.getMessage());
        }
    }

    private void setDevices() {
        try {
            List<String> activeDevices = getActiveValues(
                    "SELECT devno FROM device WHERE active = 1",
                    "devno"
            );

            if (activeDevices.isEmpty()) {
                System.out.println("No active devices found.");
            } else {
                System.out.println("Available active devices: " + activeDevices);
            }

            System.out.print("Enter device numbers separated by commas (blank to clear): ");
            replaceList(devices, s.nextLine());
        } catch (SQLException e) {
            System.out.println("Could not load devices: " + e.getMessage());
        }
    }

    private List<String> getActiveValues(String sql, String column) throws SQLException {
        List<String> values = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                values.add(rs.getString(column));
            }
        }

        return values;
    }

    private void setDirection() {
        Direction[] values = Direction.values();
        direction = chooseEnum("direction", values);
    }

    private void setStartRange() {
        initStartRange = readOptionalDateTime(
                "Start at or after (yyyy-MM-ddTHH:mm, blank for none): ");
        finalStartRange = readOptionalDateTime(
                "Start at or before (yyyy-MM-ddTHH:mm, blank for none): ");
    }

    private void setMethod() {
        method = chooseEnum("method", Method.values());
    }

    private void setStatus() {
        status = chooseEnum("status", Status.values());
    }

    private void executeSearch() {
        StringBuilder sql = new StringBuilder("SELECT * FROM countperiod WHERE 1 = 1");
        List<Object> params = new ArrayList<>();

        if (initPeriodNo != null) {
            sql.append(" AND periodno >= ?");
            params.add(initPeriodNo);
        }
        if (finalPeriodNo != null) {
            sql.append(" AND periodno <= ?");
            params.add(finalPeriodNo);
        }
        appendInFilter(sql, params, "pointno", points);
        if (direction != null) {
            sql.append(" AND directn = ?");
            params.add(direction.name());
        }
        if (initStartRange != null) {
            sql.append(" AND startat >= ?");
            params.add(Timestamp.valueOf(initStartRange));
        }
        if (finalStartRange != null) {
            sql.append(" AND startat <= ?");
            params.add(Timestamp.valueOf(finalStartRange));
        }
        if (method != null) {
            sql.append(" AND method = ?");
            params.add(method.name());
        }
        appendInFilter(sql, params, "devno", devices);
        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status.name());
        }

        sql.append(" ORDER BY startat, periodno");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object value = params.get(i);
                if (value instanceof Timestamp timestamp) {
                    stmt.setTimestamp(i + 1, timestamp);
                } else {
                    stmt.setObject(i + 1, value);
                }
            }
            displayResults(stmt);
        } catch (SQLException e) {
            System.out.println("Could not search periods: " + e.getMessage());
        }
    }

    private void appendInFilter(
            StringBuilder sql,
            List<Object> params,
            String column,
            List<String> values
    ) {
        if (values.isEmpty()) {
            return;
        }

        sql.append(" AND ").append(column).append(" IN (");
        sql.append(String.join(", ", java.util.Collections.nCopies(values.size(), "?")));
        sql.append(")");
        params.addAll(values);
    }

    private void displayResults(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            System.out.printf(
                    "%-12s | %-8s | %-12s | %-23s | %-23s | %-10s | %-10s | %-10s | %-55s | %s%n",
                    "periodno", "pointno", "directn", "startat", "endat",
                    "method", "devno", "status", "notes", "revno");

            System.out.println(
                    "-------------|----------|--------------|-------------------------|"
                + "-------------------------|------------|------------|------------|"
                + "---------------------------------------------------------|---------");

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.printf(
                        "%-12s | %-8s | %-12s | %-23s | %-23s | %-10s | %-10s | %-10s | %-55s | %s%n",
                        valueOrDash(rs.getString("periodno")),
                        valueOrDash(rs.getString("pointno")),
                        valueOrDash(rs.getString("directn")),
                        valueOrDash(rs.getString("startat")),
                        valueOrDash(rs.getString("endat")),
                        valueOrDash(rs.getString("method")),
                        valueOrDash(rs.getString("devno")),
                        valueOrDash(rs.getString("status")),
                        valueOrDash(rs.getString("notes")),
                        valueOrDash(rs.getString("revno")));
            }

            if (!found) {
                System.out.println("No matching period found.");
            }
        }
    }

    private String valueOrDash(String value) {
        return value == null ? "-" : value;
    }

    private LocalDateTime readOptionalDateTime(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = s.nextLine().trim();

            if (input.isEmpty()) {
                return null;
            }

            try {
                return LocalDateTime.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid timestamp. Example: 2026-03-15T09:30");
            }
        }
    }

    private <E extends Enum<E>> E chooseEnum(String label, E[] values) {
        while (true) {
            System.out.println("Select " + label + " (0 to clear):");
            for (int i = 0; i < values.length; i++) {
                System.out.println("[ " + (i + 1) + " ] " + values[i]);
            }

            int choice = readInt("Enter choice: ");
            if (choice == 0) {
                return null;
            }
            if (choice >= 1 && choice <= values.length) {
                return values[choice - 1];
            }
            System.out.println("Invalid selection.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = s.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Enter a number.");
            }
        }
    }

    private void replaceList(List<String> target, String input) {
        target.clear();
        for (String value : input.split(",")) {
            String trimmed = value.trim();
            if (!trimmed.isEmpty()) {
                target.add(trimmed);
            }
        }
    }

    private String blankToNull(String input) {
        String trimmed = input.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String valueOrAny(Object value) {
        return value == null ? "Any" : value.toString();
    }

    private void clearFilters() {
        initPeriodNo = null;
        finalPeriodNo = null;
        points.clear();
        direction = null;
        initStartRange = null;
        finalStartRange = null;
        method = null;
        devices.clear();
        status = null;
    }
}
