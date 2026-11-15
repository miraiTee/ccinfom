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

    public static Method fromString(String text) {
        for (Method m : Method.values()) {
            if (m.methodID.equalsIgnoreCase(text)) {
                return m;
            }
        }

        throw new IllegalArgumentException("No match found");
    }
}
