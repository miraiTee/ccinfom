package records.CountPeriod;

import java.time.LocalDateTime;
import java.util.Arrays;

public class CountPeriodFilters {
    private PeriodNo filter_initPeriodNo;
    private PeriodNo filter_finalPeriodNo;
    private String[] filter_pointNos;
    private Direction filter_dirs;
    private LocalDateTime filter_initStartRange;
    private LocalDateTime filter_finalStartRange;
    private Method filter_method;
    private String filter_devices;
    private Status filter_status;

    public String[] getFilter_periodNo() {
        return filter_periodNos;
    }

    public void setFilter_periodNo(String[] filter_periodNo) {
        this.filter_periodNos = filter_periodNo;
    }

    public String[] getFilter_pointNo() {
        return filter_pointNos;
    }

    public void setFilter_pointNo(String[] filter_pointNo) {
        this.filter_pointNos = filter_pointNo;
    }

    public Direction getFilter_dirs() {
        return filter_dirs;
    }

    public void setFilter_dirs(Direction filter_dirs) {
        this.filter_dirs = filter_dirs;
    }

    public LocalDateTime getFilter_initStartRange() {
        return filter_initStartRange;
    }

    public void setFilter_initStartRange(LocalDateTime filter_initStartRange) {
        this.filter_initStartRange = filter_initStartRange;
    }

    public LocalDateTime getFilter_finalStartRange() {
        return filter_finalStartRange;
    }

    public void setFilter_finalStartRange(LocalDateTime filter_finalStartRange) {
        this.filter_finalStartRange = filter_finalStartRange;
    }

    public Method getFilter_method() {
        return filter_method;
    }

    public void setFilter_method(Method filter_method) {
        this.filter_method = filter_method;
    }

    public String getFilter_devices() {
        return filter_devices;
    }

    public void setFilter_devices(String filter_devices) {
        this.filter_devices = filter_devices;
    }

    public Status getFilter_status() {
        return filter_status;
    }

    public void setFilter_status(Status filter_status) {
        this.filter_status = filter_status;
    }

    public void displayAllFilters() {
        System.out.println("--- Current Filters ---");
        displayPeriodNos();
        displayPointNos();
        displayDirection();
        displayStartingRange();
        displayMethod();

        if (filter_method == Method.Device) {
            displayDevices();
        }

        displayStatus();
        System.out.println("-----------------------");
    }

    public void displayPeriodNos() {
        System.out.println("Period No: " + displayArray(filter_periodNos));
    }

    public void displayPointNos() {
        System.out.println("Point No: " + displayArray(filter_pointNos));
    }

    public void displayDirection() {
        System.out.println("Direction: " + displayValue(filter_dirs));
    }

    public void displayStartingRange() {
        System.out.println(
                "Starting Range: "
                        + displayValue(filter_initStartRange)
                        + " - "
                        + displayValue(filter_finalStartRange)
        );
    }

    public void displayMethod() {
        System.out.println("Method: " + displayValue(filter_method));
    }

    public void displayDevices() {
        System.out.println("Devices: " + displayValue(filter_devices));
    }

    public void displayStatus() {
        System.out.println("Status: " + displayValue(filter_status));
    }

    private String displayArray(String[] values) {
        return values == null ? "Not set" : Arrays.toString(values);
    }

    private String displayValue(Object value) {
        return value == null ? "Not set" : value.toString();
    }
}
