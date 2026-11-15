// mago
package records.CountPeriod;

/***
 * Enum
 * Direction
 */
public enum Direction {

    Northbound("Northbound"), 
    Southbound("Southbound");

    public final String dirID;

    Direction(String dirID){        
        this.dirID = dirID;
    }

    public String getID(){
        return dirID;
    }

    public static Direction fromString(String text) {
        for (Direction d : Direction.values()) {
            if (d.dirID.equalsIgnoreCase(text)) {
                return d;
            }
        }

        throw new IllegalArgumentException("No match found");
    }
}
