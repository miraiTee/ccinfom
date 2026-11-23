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
}
