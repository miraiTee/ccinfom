package app;

public class Database {
    private String url;

    public Database(){
        this.url = "jdbc:mysql://localhost:3306/G10_edsatraffic";
    }

    public String getURL() { return url; }
}
