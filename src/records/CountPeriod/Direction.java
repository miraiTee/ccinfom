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
}
