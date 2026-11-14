package records.CountPeriod;

public enum Direction {

    Northbound(1), 
    Southbound(2);

    public final int dirID;

    Direction(int dirID){        
        this.dirID = dirID;
    }

    public static Direction stringToEnum(String text) {
        if (text == null) {
            return null; 
        }

        try {
            return Direction.valueOf(text);
        } catch (IllegalArgumentException e) {
            return null; 
        }
    }
}
