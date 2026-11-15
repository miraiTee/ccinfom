// mago
package app;

/***
 * This class acts as the database URL to be passed through the
 * DB Connection. Nothing else except a container for the URL
 */
public class Database {
    private String url;

    public Database(){
        this.url = "jdbc:mysql://localhost:3306/G10_edsatraffic";
    }

    public String getURL() { return url; }
}
