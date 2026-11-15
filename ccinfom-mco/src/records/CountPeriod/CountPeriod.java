// mago
package records.CountPeriod;

import java.time.*;

/***
 * Count period object.
 * CountPeriod
 */
public class CountPeriod implements Comparable<CountPeriod> {
    private String periodNo;
    private String pointNo;

    private Direction directn;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private Method method;

    private String devNo;

    private Status status;

    private String notes;

    private int revno;

    // Existing Count Period
    public CountPeriod(String periodNo, String pointNo, Direction directn, 
        LocalDateTime startAt, LocalDateTime endAt, Method method,
        String devNo, Status status, String notes, int revno){

        validateFieldLength(periodNo, 16);
        validateFieldLength(pointNo, 12);
        validateFieldLength(devNo, 12);
        validateFieldLength(notes, 255);

        validateStartToEnd(startAt, endAt);
        validateMethod(method, devNo);
        validateStatus(status, notes, revno);

        this.periodNo = periodNo;
        this.pointNo = pointNo;

        this.directn = directn;

        this.startAt = startAt;
        this.endAt = endAt;

        this.method = method;
        this.devNo = devNo;

        this.status = status;
        this.notes = notes;
        this.revno = revno;
    }

    // New Count Period (if method == DEVICE)
    public CountPeriod(String periodNo, String pointNo, Direction directn, 
        LocalDateTime startAt, String devNo, String notes){

        validateFieldLength(periodNo, 16);
        validateFieldLength(pointNo, 12);
        validateFieldLength(devNo, 12);
        validateFieldLength(notes, 255);
        
        validateStartToEnd(startAt, endAt);
        validateMethod(method, devNo);
        validateStatus(status, notes, 1);

        this.periodNo = periodNo;
        this.pointNo = pointNo;

        this.directn = directn;

        this.startAt = startAt;
        this.endAt = startAt.plusMinutes(15) ;

        this.method = Method.Device;
        this.devNo = devNo;

        this.status = Status.Draft;
        this.notes = notes;
        this.revno = 1;
    
        validateStartToEnd(startAt, endAt);
        validateMethod(method, null);
    }

    // New Count Period (if method == Manual)
    public CountPeriod(String periodNo, String pointNo, Direction directn, 
        LocalDateTime startAt, String notes){

        validateFieldLength(periodNo, 16);
        validateFieldLength(pointNo, 12);
        validateFieldLength(notes, 255);

        validateStatus(status, notes, 1);

        this.periodNo = periodNo;
        this.pointNo = pointNo;

        this.directn = directn;

        this.startAt = startAt;
        this.endAt = startAt.plusMinutes(15) ;

        this.method = Method.Manual;
        this.devNo = null;

        this.status = Status.Draft;
        this.notes = notes;
        this.revno = 1;

        validateStartToEnd(startAt, endAt);
        validateMethod(method, null);
    }

    private void validateDeviceInVicinity(String pointNo, String devNo){

    }

    private void validateFieldLength(String periodNo, String pointNo, String devNo, String notes){
        if (periodNo.length() > 16) 
            throw new IllegalArgumentException("Period No must not be > 16 chars");
        if (pointNo.length() > 12) 
            throw new IllegalArgumentException("Point No must not be > 12 chars");
        if (devNo.length() > 12) 
            throw new IllegalArgumentException("Device No must not be > 16 chars");
        if (notes.length() > 255) 
            throw new IllegalArgumentException("Notes must not be > 16 chars");
    }

    private void validateFieldLength(String string, int maxlength){
        if (string.length() > maxlength) 
            throw new IllegalArgumentException(string + " must not be > " + Integer.toString(maxlength) + " chars");
    }

    /***
     * Guarantee that a count period is between 15 minutes before~after.
     * 
     * @param startAt
     * @param endAt
     */
    private void validateStartToEnd(LocalDateTime startAt, LocalDateTime endAt){
        // Null validation
        if (startAt == null || endAt == null) {
            throw new NullPointerException("Start and end times cannot be null");
        }

        // Start must be in 15 mins intervals e.g. (7:00, 7:15, 7:30, 7:45)
        if (startAt.getMinute() % 15 != 0)
            throw new IllegalArgumentException("Duration must be during quarters");

        // Start ~ End must be between 15 mins
        Duration duration =  Duration.between(startAt, endAt);

        if (duration.toMinutes() != 15)
            throw new IllegalArgumentException("Duration must be exactly 15 minutes differently");
    }

    private void validateMethod(Method method, String devNo){
        if (method == null && devNo == null) {
            throw new NullPointerException("Method and Device Number cannot be both null");
        }

        if (method == Method.Manual)
            if (devNo != null)
                throw new IllegalArgumentException("Manual must not have a device number.");
        else
            if (devNo == null)
                throw new NullPointerException("Device must have a device number.");

        if (devNo != null)
            validateDeviceNumber(devNo);
    }

    /***
     * Validate the device number if matching format.
     * @param devNo
     */
    private void validateDeviceNumber(String devNo){
        if (devNo.length() != 5)
            throw new IllegalArgumentException("Device Number is not length of 5.");

        String prefix = devNo.substring(0, 2);
        String no = devNo.substring(2, 5);

        if (prefix.equals("DV"))
            throw new IllegalArgumentException("Device Prefix does not match.");

        if (no.contains("[a-zA-Z]+") == true)
            throw new IllegalArgumentException("Device No contains a non-numeric character.");
    }

    /***
     * Validate status
     * 
     * @param status
     * @param notes
     * @param revNo
     */
    private void validateStatus(Status status, String notes, int revNo){
        if (status == null)
            throw new IllegalArgumentException("No status found.");

        if (status == Status.Draft && notes == null)
            throw new IllegalArgumentException("Draft must have notes.");

        if (revNo < 1)
            throw new IllegalArgumentException("RevNo must not be 0 or negative.");
    }

    public String getPeriodNo() { return periodNo; }
    public String getPointNo() { return pointNo; }

    public Direction getDirectn() { return directn; };

    public LocalDateTime getStartAt() { return startAt; };
    public LocalDateTime getEndAt() { return endAt; };

    public Method getMethod() { return method; };

    public String getDevNo() { return devNo; };

    public Status getStatus() { return status; };

    public String getNotes() { return notes; };

    public int getRevNo() { return revno; };

    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }

    public boolean compare(CountPeriod countPeriod){
        return periodNo.equals(countPeriod.getPeriodNo());
    }

    @Override
    public int compareTo(CountPeriod other) {
        return this.periodNo.compareTo(other.periodNo);
    }
}
