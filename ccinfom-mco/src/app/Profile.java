// mago
package app;


/***
 * Class that represents a user in the DB, determines which permissions
 * they are allowed in the database.
 * 
 * Grants the ability to get/set the uesr's name and password.
 * 
 * Profile
 */
public class Profile {
    private String username;
    private String password;

    public Profile(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername(){ return username;}
    public String getPassword(){ return password;}

    public void changeProfile(String username, String password) { 
        this.username = username;
        this.password = password;
    }
}
