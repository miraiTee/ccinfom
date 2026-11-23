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
            if (countPeriod.getPointNo().equals(newCountPeriod.getPointNo()) &&
                countPeriod.getDirectn() == newCountPeriod.getDirectn() &&
                overlaps(countPeriod, newCountPeriod))
                throw new IllegalArgumentException(
                    "Count period overlaps an existing period at the same point and direction.");
        }
    }

    private void validatePeriodNo(CountPeriod newCountPeriod){
        for (CountPeriod countPeriod : countPeriods){
            if (countPeriod.compare(newCountPeriod))
                throw new IllegalArgumentException(
                    "A count period with reference " + newCountPeriod.getPeriodNo() + " already exists.");
        }
    }

    private boolean overlaps(CountPeriod a, CountPeriod b){
        return a.getStartAt().isBefore(b.getEndAt()) &&
               b.getStartAt().isBefore(a.getEndAt());
    }

    public void add(CountPeriod countPeriod){
        validatePeriodNo(countPeriod);
        validatePointDirection(countPeriod);
        countPeriods.add(countPeriod);
        sort();
    }

    public void remove(CountPeriod countPeriod){
        countPeriods.remove(countPeriod);
        sort();
    }

    public void sort() {
        countPeriods.sort(null);
    }
}
