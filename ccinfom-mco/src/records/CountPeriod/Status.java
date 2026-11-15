package records.CountPeriod;

public enum Status {

    Completed("Completed"),
    Draft("Draft");

    public final String statusID;

    Status(String statusID){
        this.statusID = statusID;
    }

    public String getID(){
        return statusID;
    }

    public static Status fromString(String text) {
        for (Status s : Status.values()) {
            if (s.statusID.equalsIgnoreCase(text)) {
                return s;
            }
        }

        throw new IllegalArgumentException("No match found");
    }
}
