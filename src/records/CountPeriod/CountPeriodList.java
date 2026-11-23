// mago
package records.CountPeriod;

import java.util.ArrayList;

/***
 * Count period list.
 * CountPeriod
 */
public class CountPeriodList {
    
    private ArrayList<CountPeriod> countPeriods;

    public CountPeriodList(){
        this.countPeriods = new ArrayList<>();
    }

    public ArrayList<CountPeriod> getList(){
        return countPeriods;
    }
    
    /***
     * Periods at the same point and direction cannot overlap,  
     * while "Northbound" and "Southbound" can be counted simultaneously.
     */
    private void validatePointDirection(CountPeriod newCountPeriod){
        for (CountPeriod countPeriod : countPeriods){
            if ((countPeriod.getPointNo()).equals(newCountPeriod.getPointNo()) &&
                (countPeriod.getDirectn()).equals(newCountPeriod.getDirectn()))
            throw new IllegalArgumentException("Cannot add the exact same countPeriod twice");
        }
    }

    public void add(CountPeriod countPeriod){
        if (countPeriods.contains(countPeriod))
            throw new IllegalArgumentException("Cannot add the exact same countPeriod twice");

        

        validatePointDirection(countPeriod);
        countPeriods.add(countPeriod);
        sort();
    }

    public void remove(CountPeriod countPeriod){
        if (countPeriods.contains(countPeriod))
            countPeriods.remove(countPeriod);

        sort();
    }

    public void sort() {
        countPeriods.sort(null); 
    }
}
