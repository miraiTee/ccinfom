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
    private static final String CATEGORY_TABLE = "periodcategory";

    public DeletePeriods(Connection conn, Scanner s) {
        this.conn = conn;
        this.s = s;
    }

    public void run() {
        System.out.print("Enter the period reference to delete: ");
        String periodNo = s.nextLine().trim();

        if (periodNo.isEmpty()) {
            System.out.println("Period reference cannot be empty.");
            return;
        }

        try {
            deleteDraftPeriod(periodNo);
        } catch (SQLException e) {
            System.out.println("Could not delete period: " + e.getMessage());
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
                System.out.println("This Draft period has category rows.");
                conn.rollback();
                return;
            }

            System.out.printf(
                    "Delete Draft period %s? Type DELETE to confirm: ",
                    periodNo
            );

            if (!"DELETE".equals(s.nextLine().trim())) {
                System.out.println("Deletion cancelled.");
                conn.rollback();
                return;
            }

            String sql = "DELETE FROM periodcount WHERE periodno = ? AND status = ?";
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
                FROM periodcount
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
        String sql = "SELECT 1 FROM " + CATEGORY_TABLE + " WHERE periodno = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, periodNo);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
