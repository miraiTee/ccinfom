package records.CountPeriod;

public class PeriodNo {
    private static final String PREFIX = "CP";
    private static final int YEAR_WIDTH = 2;
    private static final int NUMBER_WIDTH = 7;

    private int year;
    private int number;

    // Period No from db
    public PeriodNo(String periodNo) {
        validateFieldLength(periodNo, 11);

        this.year = Integer.parseInt(periodNo.substring(2, 4));
        this.number = Integer.parseInt(periodNo.substring(4));
    }

    // New period no
    public PeriodNo(int year, int number) {
        if (year < 0 || year > 99) {
            throw new IllegalArgumentException("Year must be between 0 and 99");
        }
        if (number < 0 || number >= 100_000_000) {
            throw new IllegalArgumentException("Number must be between 0 and 99999999");
        }

        this.year = year;
        this.number = number;
    }

    public void incrementYear() {
        year = (year + 1) % 100;
        number = 0;
    }

    public void incrementPeriodNo() {
        if (number == 99_999_999) {
            throw new IllegalStateException("Period number limit reached");
        }
        number++;
    }

    private void validateFieldLength(String value, int length) {
        if (value == null || value.length() != length) {
            throw new IllegalArgumentException(
                    "Period number must be exactly " + length + " characters"
            );
        }
    }

    @Override
    public String toString() {
        return PREFIX
                + String.format("%0" + YEAR_WIDTH + "d", year)
                + String.format("%0" + NUMBER_WIDTH + "d", number);
    }
}

