package records.CountPeriod;

import java.time.*;

public class CountPeriod {
    private String periodNo;
    private String pointNo;

    private Direction directn;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private Method method;

    private String devno;

    private Status status;

    private String notes;

    private int revno;

    public CountPeriod(String periodNo, String pointNo, Direction directn, 
        LocalDateTime startAt, LocalDateTime endAt, Method method,
        String devno, Status status, String notes, int revno){

        validateStartToEnd(startAt, endAt);
        validateMethod(method, devno);
        validateStatus(status, notes, revno);

        this.periodNo = periodNo;
        this.pointNo = pointNo;

        this.directn = directn;

        this.startAt = startAt;
        this.endAt = endAt;

        this.method = method;
        this.devno = devno;

        this.status = status;
        this.notes = notes;
        this.revno = revno;
    }

    /***
     * Periods at the same point and direction cannot overlap,  
     * while "Northbound" and "Southbound" can be counted simultaneously.
     */
    private void validatePointDirection(){

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
            throw new IllegalArgumentException("Start and end times cannot be null");
        }

        // Start must be in 15 mins intervals e.g. (7:00, 7:15, 7:30, 7:45)
        if (startAt.getMinute() % 15 != 0)
            throw new IllegalArgumentException("Duration must be during quarters");

        // Start ~ End must be between 15 mins
        Duration duration =  Duration.between(startAt, endAt);

        if (duration.toMinutes() != 15)
            throw new IllegalArgumentException("Duration must be exactly 15 minutes differently");
    }

    private void validateMethod(Method method, String devno){
        if (method == null && devno == null) {
            throw new IllegalArgumentException("Method and Device Number cannot be both null");
        }

        if (method == Method.Manual)
            if (devno != null)
                throw new IllegalArgumentException("Manual must not have a device number.");
        else
            if (devno == null)
                throw new IllegalArgumentException("Device must have a device number.");

        if (devno != null)
            validateDeviceNumber(devno);
    }

    /***
     * Validate the device number if matching format.
     * @param devno
     */
    private void validateDeviceNumber(String devno){
        if (devno.length() != 5)
            throw new IllegalArgumentException("Device Number is not length of 5.");

        String prefix = devno.substring(0, 2);
        String no = devno.substring(2, 5);

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
}
