// mago
package records.CountPeriod;

import java.time.*;

/***
 * Count period object.
 * CountPeriod
 */
public class CountPeriod implements Comparable<CountPeriod> {
    private PeriodNo periodNo;

    // expect a class from point as pointno
    private String pointNo;

    private Direction directn;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private Method method;

    // expect a class from device as devno
    private String devNo;

    private Status status;

    private String notes;

    private int revno;

    // Existing Count Period
    public CountPeriod(PeriodNo periodNo, String pointNo, Direction directn, 
        LocalDateTime startAt, LocalDateTime endAt, Method method,
        String devNo, Status status, String notes, int revno) {

        // validateFieldLength(periodNo, 16);
        // validateFieldLength(pointNo, 12);
        // validateFieldLength(devNo, 12);
        validateFieldLength(notes, 255);

        validateStartToEnd(startAt, endAt);
        validateMethod(method, devNo);

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

    // New Count Period (from app)
    public CountPeriod(String periodNo, String pointNo, Direction directn, 
        LocalDateTime startAt, Method method, String devNo){

        // validateFieldLength(periodNo, 16);
        // validateFieldLength(pointNo, 12);
        // validateFieldLength(devNo, 12);
        validateFieldLength(notes, 255);
        
        this.periodNo = new PeriodNo(periodNo);
        this.pointNo = pointNo;

        this.directn = directn;

        this.startAt = startAt;
        this.endAt = startAt.plusMinutes(15);

        this.method = method;
        this.devNo = devNo;

        this.status = Status.Draft;
        this.notes = null;
        this.revno = 1;
    
        validateStartToEnd(this.startAt, this.endAt);
        validateMethod(this.method, this.devNo);
    }

    private void validateFieldLength(String string, int maxlength){
        if (string != null && string.length() > maxlength) 
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

        if (method == Method.Manual) {
            if (devNo != null)
                throw new IllegalArgumentException("Manual must not have a device number.");
        }
        else {
            if (devNo == null)
                throw new NullPointerException("Device must have a device number.");
        }

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

        if (!prefix.equals("DV"))
            throw new IllegalArgumentException("Device Prefix does not match.");

        if (!no.matches("\\d{3}") == true)
            throw new IllegalArgumentException("Device No contains a non-numeric character.");
    }

    public PeriodNo getPeriodNo() { return periodNo; }
    public String getPointNo() { return pointNo; }

    public Direction getDirectn() { return directn; };

    public LocalDateTime getStartAt() { return startAt; };
    public LocalDateTime getEndAt() { return endAt; };

    public Method getMethod() { return method; };

    public String getDevNo() { return devNo; };

    public Status getStatus() { return status; };

    public String getNotes() { return notes; };

    public int getRevNo() { return revno; };

    public boolean compare(CountPeriod countPeriod){
        return periodNo.equals(countPeriod.getPeriodNo());
    }

    @Override
    public int compareTo(CountPeriod other) {
        return this.getPeriodNo().toString().compareTo(other.getPeriodNo().toString());
    }

    @Override
    public String toString() {
        return periodNo + 
               " | " + pointNo + 
               " | " + directn.getID() + 
               " | " + startAt + 
               " | " + endAt +
               " | " + method.getID() +
               " | " + devNo +
               " | " + status.getID() +
               " | " + notes +
               " | " + revno;
    }



}
