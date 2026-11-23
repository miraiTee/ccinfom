// mago
package records.CountPeriod;

/***
 * Enum
 * Method
 */
public enum Method {

    Manual("Manual"),
    Device("Device");

    public final String methodID;

    Method(String methodID){
        this.methodID = methodID;
    }

    public String getID(){
        return methodID;
    }
}
