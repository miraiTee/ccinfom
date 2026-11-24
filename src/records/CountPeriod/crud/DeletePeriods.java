package records.CountPeriod.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DeletePeriods {
    private final Connection conn;
    private final Scanner s;

    // Change this to the actual child/category table name.

    public DeletePeriods(Connection conn, Scanner s) {
        this.conn = conn;
        this.s = s;
    }

    public void run() {
        try {
            if (!showPeriods()) {
                return;
            }

            System.out.print("Enter the period reference to delete: ");
            String periodNo = s.nextLine().trim();

            if (periodNo.isEmpty()) {
                System.out.println("Period reference cannot be empty.");
                return;
            }

            deleteDraftPeriod(periodNo);
        } catch (SQLException e) {
            System.out.println("Could not delete period: " + e.getMessage());
        }
    }

    private boolean showPeriods() throws SQLException {
        String sql = """
                SELECT p.periodno,
                    p.status,
                    0 AS categoryrows
                FROM countperiod p
                WHERE LOWER(p.status) = 'draft'
                AND NOT EXISTS (
                    SELECT 1
                    FROM vehcount v
                    WHERE v.periodno = p.periodno
                )
                ORDER BY p.periodno
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            System.out.printf("%-20s | %-12s | %s%n",
                    "periodno", "status", "categoryrows");
            System.out.println("---------------------|--------------|-------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-20s | %-12s | %d%n",
                        rs.getString("periodno"),
                        rs.getString("status"),
                        rs.getInt("categoryrows"));
            }

            if (!found) {
                System.out.println("No Draft periods without category rows found.");
            }

            return found;
        }
    }


    private void deleteDraftPeriod(String periodNo) throws SQLException {
        boolean originalAutoCommit = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);

            String status = getAndLockStatus(periodNo);

            if (status == null) {
                System.out.println("Period not found: " + periodNo);
                conn.rollback();
                return;
            }

            if (!"Draft".equalsIgnoreCase(status)) {
                System.out.println(
                        "Only Draft periods can be deleted. Current status: " + status);
                conn.rollback();
                return;
            }

            if (hasCategoryRows(periodNo)) {
                System.out.println("This period has category rows.");
                conn.rollback();
                return;
            }

            System.out.printf("Delete period %s? Type DELETE to confirm: ", periodNo);

            if (!"DELETE".equals(s.nextLine().trim())) {
                System.out.println("Deletion cancelled.");
                conn.rollback();
                return;
            }

            String sql = "DELETE FROM countperiod WHERE periodno = ? AND status = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, periodNo);
                stmt.setString(2, "Draft");

                if (stmt.executeUpdate() != 1) {
                    conn.rollback();
                    System.out.println("Period was not deleted.");
                    return;
                }
            }

            conn.commit();
            System.out.println("Draft period deleted: " + periodNo);

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    private String getAndLockStatus(String periodNo) throws SQLException {
        String sql = """
                SELECT status
                FROM countperiod
                WHERE periodno = ?
                FOR UPDATE
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, periodNo);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("status") : null;
            }
        }
    }

    private boolean hasCategoryRows(String periodNo) throws SQLException {
        String sql = "SELECT 1 FROM vehcount WHERE periodno = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, periodNo);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
