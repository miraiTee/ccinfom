// mago
package records.CountPeriod;

import java.sql.Connection;
import java.util.Scanner;
import records.CountPeriod.crud.*;

/***
 * Count period records.
 * CountPeriod
 */
public class CountPeriodRecords {

    private Connection conn;
    private Scanner s = new Scanner(System.in);

    private CreatePeriods create;
    private ReadPeriods read;
    private UpdatePeriods update;
    private DeletePeriods delete;

    public CountPeriodRecords(Connection conn){
        this.conn = conn;
        create = new CreatePeriods(conn, s);
        read = new ReadPeriods(conn, s);
        update = new UpdatePeriods(conn, s);
        delete = new DeletePeriods(conn, s);
    }

    public void run(){

        int choice = -1;
        while (choice != 0){
            displayMenu();
            choice = s.nextInt();
            s.nextLine();

            switch(choice){
                case 1 -> create.run();
                case 2 -> read.run();
                case 3 -> update.run();
                case 4 -> delete.run();
                case 0 -> System.out.print("bye");
                default -> System.out.print("Invalid choice");
            }
        }
    }

    public void displayMenu(){
        System.out.println("Count Period Records");
        System.out.println("[ 1 ] Create New Periods.");
        System.out.println("[ 2 ] Search for Periods.");
        System.out.println("[ 3 ] Edit Periods.");
        System.out.println("[ 4 ] Delete Periods.");
        System.out.println("[ 0 ] Return to main.");
    }
}
