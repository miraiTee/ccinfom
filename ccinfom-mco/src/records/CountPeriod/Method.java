package records.CountPeriod;

public enum Method {

    Manual(1),
    Device(2);

    public final int methodID;

    Method(int methodID){
        this.methodID = methodID;
    }

    public static Method stringToEnum(String text) {
        if (text == null) {
            return null; 
        }

        try {
            return Method.valueOf(text);
        } catch (IllegalArgumentException e) {
            return null; 
        }
    }
}
