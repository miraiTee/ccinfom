// mago
package records.CountPeriod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/***
 * Count period records.
 * CountPeriod
 */
public class CountPeriodRecords {

    private Connection conn;
    private CountPeriodList countPeriodList;

    public CountPeriodRecords(Connection conn){
        this.conn = conn;
        this.countPeriodList = new CountPeriodList();
    }

    public void changeConn(Connection conn){ this.conn = conn; }
    
    public void createSingleCountPeriod(CountPeriod countPeriod) throws SQLException {
        String sql = "INSERT INTO countPeriod (periodno, pointno, directn, startat, endat, method, devno, status, notes, revno) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, countPeriod.getPeriodNo());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createMultipleCountPeriods(CountPeriodList countPeriodList) throws SQLException {
        String sql = "INSERT INTO countPeriod (periodno, pointno, directn, startat, endat, method, devno, status, notes, revno) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (CountPeriod countPeriod : countPeriodList.getList()) {

                stmt.setString(1, countPeriod.getPeriodNo());
                stmt.setString(2, countPeriod.getPointNo());
                stmt.setString(3, countPeriod.getDirectn().getID());
                stmt.setTimestamp(4, Timestamp.valueOf(countPeriod.getStartAt()));
                stmt.setTimestamp(5, Timestamp.valueOf(countPeriod.getEndAt()));
                stmt.setString(6, countPeriod.getMethod().getID());
                stmt.setString(7, countPeriod.getDevNo());
                stmt.setString(8, countPeriod.getStatus().getID());
                stmt.setString(9, countPeriod.getNotes());
                stmt.setInt(10, countPeriod.getRevNo());
=
                stmt.addBatch(); 
            }

            // 3. Send the entire bucket to the database in ONE network trip
            int[] results = stmt.executeBatch();
            System.out.println(results.length + " records inserted successfully.");
            
        } catch (SQLException e) {
            throw e;
        }
    }

    public void readCountPeriod(CountPeriodList countPeriodList) throws SQLException {
        String sql = "SELECT * FROM countPeriod";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
        
            try (ResultSet rs = stmt.executeQuery()){

                while (rs.next()) {
                    String periodNo = rs.getString("periodno");
                    String pointNo = rs.getString("pointno");

                    Direction directn = Direction.valueOf(rs.getString("directn"));

                    LocalDateTime startAt = rs.getTimestamp("startat").toLocalDateTime();
                    LocalDateTime endAt = rs.getTimestamp("endat").toLocalDateTime();

                    Method method = Method.valueOf(rs.getString("method"));

                    String devNo = rs.getString("devno");
                    
                    Status status = Status.valueOf(rs.getString("status"));

                    String notes = rs.getString("notes");
                    int revNo = rs.getInt("revno");

                    CountPeriod countPeriod = new CountPeriod(periodNo, pointNo, directn, 
                                                                startAt, endAt, method, 
                                                                devNo, status, notes, revNo);

                    countPeriodList.add(countPeriod);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCountPeriod(CountPeriod countPeriod) throws SQLException {
        String sql = "UPDATE students SET email = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newEmail);
            stmt.setInt(2, id);

            int rows = stmt.executeUpdate();
            System.out.println(rows + " student updated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /* TO - DO
        UPDATES
        ORDINARY EDITS:
        CORRECTIONS:
        COMPLETIONS:
        
        DELETES
        DRAFTS
        DEPENDENCY W/ MEMBER 4
        
    */

    public void deleteCountPeriod(CountPeriod countPeriod) throws SQLException {
        String sql = "DELETE FROM students WHERE periodNo = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, countPeriod.getPeriodNo());

            int rows = stmt.executeUpdate();
            System.out.println(rows + " student deleted successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
