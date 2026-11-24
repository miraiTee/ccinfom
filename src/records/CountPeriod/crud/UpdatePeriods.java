package records.CountPeriod.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import records.CountPeriod.CountPeriod;
import records.CountPeriod.Direction;
import records.CountPeriod.Method;
import records.CountPeriod.PeriodNo;
import records.CountPeriod.Status;

public class UpdatePeriods {
    private static final String COMPLETE_STATUS = "Complete";

    private final Connection conn;
    private final Scanner s;

    public UpdatePeriods(Connection conn, Scanner s) {
        this.conn = conn;
        this.s = s;
    }

    public void run() {
        while (true) {
            System.out.println("\nPeriod management");
            System.out.println("[1] View and manage drafts");
            System.out.println("[2] Override a completed period");
            System.out.println("[0] Exit");

            int choice = readInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1 -> manageDrafts();
                    case 2 -> overrideCompletedPeriod();
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Invalid selection.");
                }
            } catch (SQLException | IllegalArgumentException e) {
                System.out.println("Could not complete operation: " + e.getMessage());
            }
        }
    }

    private void manageDrafts() throws SQLException {
        List<CountPeriod> drafts = getPeriodsByStatus(Status.Draft.name());

        if (drafts.isEmpty()) {
            System.out.println("There are no draft periods.");
            return;
        }

        System.out.println("\nDraft periods:");
        for (int i = 0; i < drafts.size(); i++) {
            CountPeriod period = drafts.get(i);
            System.out.printf(
                    "[%d] %s | %s | %s | Revision %d%n",
                    i + 1,
                    period.getPeriodNo(),
                    period.getPointNo(),
                    period.getStartAt(),
                    period.getRevNo()
            );
        }

        int selected = readInt("Select a period (0 to cancel): ");
        if (selected == 0) {
            return;
        }
        if (selected < 1 || selected > drafts.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        CountPeriod selectedPeriod = drafts.get(selected - 1);
        String periodNo = selectedPeriod.getPeriodNo().toString();

        System.out.println("\nSelected period:");
        displayPeriod(selectedPeriod);
        System.out.println("[1] Edit method/device");
        System.out.println("[2] Lock in as Complete");
        System.out.println("[0] Leave as Draft");

        int action = readInt("Choose an action: ");

        switch (action) {
            case 1 -> editDraft(periodNo);
            case 2 -> lockInDraft(periodNo);
            case 0 -> System.out.println("Period left as Draft.");
            default -> System.out.println("Invalid selection; period left unchanged.");
        }
    }

    private void editDraft(String periodNo) throws SQLException {
        boolean originalAutoCommit = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);
            CountPeriod period = getAndLockPeriod(periodNo);

            if (period == null || period.getStatus() != Status.Draft) {
                conn.rollback();
                System.out.println("The period is no longer a Draft.");
                return;
            }

            if (hasVehicleRows(periodNo)) {
                conn.rollback();
                System.out.println(
                        "This period has vehicle-count rows and cannot be edited "
                                + "through this workflow.");
                return;
            }

            Method newMethod = chooseMethod();
            String newDeviceNo = null;

            if (newMethod == Method.Device) {
                newDeviceNo = chooseActiveDevice();
                if (newDeviceNo == null) {
                    conn.rollback();
                    return;
                }
            }

            System.out.print("Save changes? Type SAVE to confirm: ");
            if (!"SAVE".equals(s.nextLine().trim())) {
                conn.rollback();
                System.out.println("Update cancelled.");
                return;
            }

            String sql = """
                    UPDATE countperiod
                    SET method = ?, devno = ?, revno = revno + 1
                    WHERE periodno = ? AND status = ?
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newMethod.name());
                stmt.setString(2, newDeviceNo);
                stmt.setString(3, periodNo);
                stmt.setString(4, Status.Draft.name());

                if (stmt.executeUpdate() != 1) {
                    conn.rollback();
                    System.out.println("Period was not updated.");
                    return;
                }
            }

            conn.commit();
            System.out.println("Draft updated; revision number increased.");

        } catch (SQLException | RuntimeException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    private void lockInDraft(String periodNo) throws SQLException {
        boolean originalAutoCommit = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);
            CountPeriod period = getAndLockPeriod(periodNo);

            if (period == null || period.getStatus() != Status.Draft) {
                conn.rollback();
                System.out.println("The period is no longer a Draft.");
                return;
            }

            System.out.print("Lock this period in as Complete? Type LOCK to confirm: ");
            if (!"LOCK".equals(s.nextLine().trim())) {
                conn.rollback();
                System.out.println("Period left as Draft.");
                return;
            }

            String sql = """
                    UPDATE countperiod
                    SET status = ?, revno = revno + 1
                    WHERE periodno = ? AND status = ?
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, COMPLETE_STATUS);
                stmt.setString(2, periodNo);
                stmt.setString(3, Status.Draft.name());

                if (stmt.executeUpdate() != 1) {
                    conn.rollback();
                    System.out.println("Period was not locked in.");
                    return;
                }
            }

            conn.commit();
            System.out.println("Period locked in as Complete.");

        } catch (SQLException | RuntimeException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    private void overrideCompletedPeriod() throws SQLException {
        System.out.print("Enter the completed period reference to override: ");
        String periodNo = s.nextLine().trim();

        if (periodNo.isEmpty()) {
            System.out.println("Period reference cannot be empty.");
            return;
        }

        System.out.print("Enter the reason for this override: ");
        String reason = s.nextLine().trim();

        if (reason.isEmpty()) {
            System.out.println("An override reason is required.");
            return;
        }
        if (reason.length() > 255) {
            System.out.println("The reason must be 255 characters or fewer.");
            return;
        }

        boolean originalAutoCommit = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);
            CountPeriod period = getAndLockPeriod(periodNo);

            if (period == null || !COMPLETE_STATUS.equals(period.getStatus().name())) {
                conn.rollback();
                System.out.println("Completed period not found.");
                return;
            }

            displayPeriod(period);
            Method newMethod = chooseMethod();
            String newDeviceNo = null;

            if (newMethod == Method.Device) {
                newDeviceNo = chooseActiveDevice();
                if (newDeviceNo == null) {
                    conn.rollback();
                    return;
                }
            }

            System.out.print("Apply override? Type OVERRIDE to confirm: ");
            if (!"OVERRIDE".equals(s.nextLine().trim())) {
                conn.rollback();
                System.out.println("Override cancelled.");
                return;
            }

            String sql = """
                    UPDATE countperiod
                    SET method = ?, devno = ?, notes = ?, revno = revno + 1
                    WHERE periodno = ? AND status = ?
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newMethod.name());
                stmt.setString(2, newDeviceNo);
                stmt.setString(3, reason);
                stmt.setString(4, periodNo);
                stmt.setString(5, COMPLETE_STATUS);

                if (stmt.executeUpdate() != 1) {
                    conn.rollback();
                    System.out.println("Override was not applied.");
                    return;
                }
            }

            conn.commit();
            System.out.println("Override applied; reason saved in notes.");

        } catch (SQLException | RuntimeException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    private List<CountPeriod> getPeriodsByStatus(String status) throws SQLException {
        List<CountPeriod> periods = new ArrayList<>();
        String sql = """
                SELECT periodno, pointno, directn, startat, endat,
                       method, devno, status, notes, revno
                FROM countperiod
                WHERE status = ?
                ORDER BY startat
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    periods.add(readPeriod(rs));
                }
            }
        }

        return periods;
    }

    private CountPeriod getAndLockPeriod(String periodNo) throws SQLException {
        String sql = """
                SELECT periodno, pointno, directn, startat, endat,
                       method, devno, status, notes, revno
                FROM countperiod
                WHERE periodno = ?
                FOR UPDATE
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, periodNo);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? readPeriod(rs) : null;
            }
        }
    }

    private CountPeriod readPeriod(ResultSet rs) throws SQLException {
        return new CountPeriod(
                new PeriodNo(rs.getString("periodno")),
                rs.getString("pointno"),
                Direction.valueOf(rs.getString("directn")),
                rs.getTimestamp("startat").toLocalDateTime(),
                rs.getTimestamp("endat").toLocalDateTime(),
                Method.valueOf(rs.getString("method")),
                rs.getString("devno"),
                Status.valueOf(rs.getString("status")),
                rs.getString("notes"),
                rs.getInt("revno")
        );
    }

    private boolean hasVehicleRows(String periodNo) throws SQLException {
        String sql = "SELECT 1 FROM vehcount WHERE periodno = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, periodNo);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Method chooseMethod() {
        Method[] methods = Method.values();

        while (true) {
            System.out.println("Select counting method:");
            for (int i = 0; i < methods.length; i++) {
                System.out.println("[" + (i + 1) + "] " + methods[i]);
            }

            int choice = readInt("Enter choice: ");
            if (choice >= 1 && choice <= methods.length) {
                return methods[choice - 1];
            }

            System.out.println("Invalid method selection.");
        }
    }

    private String chooseActiveDevice() throws SQLException {
        List<String> devices = new ArrayList<>();
        String sql = "SELECT devno FROM device WHERE active = 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                devices.add(rs.getString("devno"));
            }
        }

        if (devices.isEmpty()) {
            System.out.println("No active devices are available.");
            return null;
        }

        while (true) {
            System.out.println("Select active device:");
            for (int i = 0; i < devices.size(); i++) {
                System.out.println("[" + (i + 1) + "] " + devices.get(i));
            }

            int choice = readInt("Enter choice: ");
            if (choice >= 1 && choice <= devices.size()) {
                return devices.get(choice - 1);
            }

            System.out.println("Invalid device selection.");
        }
    }

    private void displayPeriod(CountPeriod period) {
        System.out.println("Period: " + period.getPeriodNo());
        System.out.println("Point: " + period.getPointNo());
        System.out.println("Direction: " + period.getDirectn());
        System.out.println("Start: " + period.getStartAt());
        System.out.println("End: " + period.getEndAt());
        System.out.println("Method: " + period.getMethod());
        System.out.println("Device: " + period.getDevNo());
        System.out.println("Status: " + period.getStatus());
        System.out.println("Revision: " + period.getRevNo());
        System.out.println("Notes: " + period.getNotes());
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
}
