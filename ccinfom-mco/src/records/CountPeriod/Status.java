package records.CountPeriod;

public enum Status {

    Completed(1),
    Draft(2);

    public final int statusID;

    Status(int statusID){
        this.statusID = statusID;
    }

    public static Status stringToEnum(String text) {
        if (text == null) {
            return null; 
        }

        try {
            return Status.valueOf(text);
        } catch (IllegalArgumentException e) {
            return null; 
        }
    }
}
