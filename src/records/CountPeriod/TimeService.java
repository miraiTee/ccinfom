package records.CountPeriod;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

/* HAndles the timestamp for count period records.
 */

public class TimeService {

    public int selectYear(){
        while (true) {
            System.out.println("Select year from 2026~2036:");
            int year = readInt(s);
            if (year >= 2026 && year <= 2036) {
                return year;
            } else {
                System.out.println("Invalid year.");
            }
        }
    }

    public int selectMonth(){
        while (true) {
            System.out.println("Select month (1-12):");
            int month = readInt(s);
            if (month >= 1 && month <= 12) {
                return  month;
            } else {
                System.out.println("Invalid month. Enter a value from 1 to 12.");
            }
        }
    }

    public int selectDay(int year, int month){
        while (true) {
            System.out.println("Select day:");
            int day = readInt(s);
            try {
                LocalDate.of(year, month, day);
                return day;
            } catch (DateTimeException e) {
                System.out.println("Invalid day for " + year + "-" + String.format("%02d", month) + ".");
            }
        }
    }

    public int selectHour(){
        while (true) {
            System.out.println("Select hour (0-23):");
            int hour = readInt(s);
            if (hour >= 0 && hour <= 23) {
                return hour;
            } else {
                System.out.println("Invalid hour.");
            }
        }
    }

    public int selectMin(){
        while (true) {
            System.out.println("Select minute interval:");
            System.out.println("[0]  :00");
            System.out.println("[15] :15");
            System.out.println("[30] :30");
            System.out.println("[45] :45");
            int minute = readInt(s);

            if (minute == 0 || minute == 15 || minute == 30 || minute == 45) {
                return minute;
            } else {
                System.out.println("Invalid minute interval.");
            }
        }

    }
    
    public int readInt(Scanner s) {
        while (true) {
            try {
                return s.nextInt();
            } catch (InputMismatchException e) {
                s.nextLine();
                System.out.println("Please enter a valid whole number.");
            }
        }
    }
}
