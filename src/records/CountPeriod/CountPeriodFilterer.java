package records.CountPeriod;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class CountPeriodFilterer {
    private final Scanner s = new Scanner(System.in);
    private final CountPeriodFilters filters = new CountPeriodFilters();

    public void filterSearch() {
        while (true) {
            displayFilters();

            System.out.println("[1] Period Number");
            System.out.println("[2] Point Number");
            System.out.println("[3] Direction");
            System.out.println("[4] Starting Range");
            System.out.println("[5] Method");
            System.out.println("[6] State");
            System.out.println("[7] Apply filters");
            System.out.println("[0] Return to Main Menu");

            switch (s.nextLine()) {
                case "1" -> setPeriodNumber();
                case "2" -> setPointNumber();
                case "3" -> setDirection();
                case "4" -> setStartingRange();
                case "5" -> setMethodMenu();
                case "6" -> setStatus();
                case "7" -> applyFilters();
                case "0" -> { return; }
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void setPeriodNumber() {
        System.out.print("Enter period number(s), separated by commas: ");
        filters.setFilter_periodNo(readStringArray());
    }

    private void setPointNumber() {
        System.out.print("Enter point number(s), separated by commas: ");
        filters.setFilter_pointNo(readStringArray());
    }

    private String[] readStringArray() {
        String input = s.nextLine().trim();

        if (input.isEmpty()) {
            return new String[0];
        }

        return input.split("\\s*,\\s*");
    }

    private void setDirection() {
        while (true) {
            filters.displayDirection();
            System.out.println("[1] Northbound");
            System.out.println("[2] Southbound");
            System.out.println("[3] No Filter");
            System.out.println("[0] Return");

            switch (s.nextLine()) {
                case "1" -> {
                    filters.setFilter_dirs(Direction.Northbound);
                    return;
                }
                case "2" -> {
                    filters.setFilter_dirs(Direction.Southbound);
                    return;
                }
                case "3" -> {
                    filters.setFilter_dirs(null);
                    return;
                }
                case "0" -> { return; }
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void setStartingRange() {
        try {
            System.out.print("Enter initial start timestamp (YYYY-MM-DDTHH:MM): ");
            LocalDateTime initial = LocalDateTime.parse(s.nextLine());

            System.out.print("Enter final start timestamp (YYYY-MM-DDTHH:MM): ");
            LocalDateTime end = LocalDateTime.parse(s.nextLine());

            if (end.isBefore(initial)) {
                System.out.println("The final timestamp must be after the initial timestamp.");
                return;
            }

            filters.setFilter_initStartRange(initial);
            filters.setFilter_finalStartRange(end);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid timestamp. Example: 2026-11-24T09:30");
        }
    }

    private void setMethodMenu() {
        while (true) {
            System.out.println("Method selection:");
            System.out.println("[1] Choose method");
            System.out.println("[2] Set list of devices");
            System.out.println("[0] Return");

            switch (s.nextLine()) {
                case "1" -> setMethod();
                case "2" -> setDevices();
                case "0" -> { return; }
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void setMethod() {
        System.out.println("Enter the Method");
        System.out.println("[1] Manual");
        System.out.println("[2] Device");
        System.out.println("[3] No Filter");

        switch (s.nextLine()) {
            case "1" -> filters.setFilter_method(Method.Manual);
            case "2" -> filters.setFilter_method(Method.Device);
            case "3" -> {
                filters.setFilter_method(null);
                filters.setFilter_devices(null);
            }
            default -> System.out.println("Invalid option");
        }
    }

    private void setDevices() {
        System.out.print("Enter device(s), separated by commas: ");
        filters.setFilter_devices(String.join(", ", readStringArray()));
    }

    private void setStatus() {
        System.out.println("Enter the Status");
        System.out.println("[1] Manual");
        System.out.println("[2] Device");
        System.out.println("[3] No Filter");
        String input = s.nextLine().trim();

        if (input.isEmpty()) {
            filters.setFilter_status(null);
            return;
        }

        try {
            filters.setFilter_status(Status.valueOf(input));
        } catch (IllegalArgumentException e) {
            System.out.println("Incorrect status..");
        }
    }

    private void applyFilters() {
        // Pass filters to your search/service code here.
        System.out.println("Applying filters...");
    }

    private void displayFilters() {
        filters.displayAllFilters();
    }
}
