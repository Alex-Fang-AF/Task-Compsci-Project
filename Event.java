import java.time.LocalDate;

public class Event {
    private String eventName;
    private LocalDate startDate;
    private LocalDate endDate;

    // Constructor
    public Event(String eventName, LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }
        this.eventName = eventName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters
    public String getEventName() {
        return eventName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    // Setters
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setStartDate(LocalDate startDate) {
        if (this.endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        if (endDate.isBefore(this.startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }
        this.endDate = endDate;
    }

    // Check if event spans multiple days
    public boolean isMultiDay() {
        return !startDate.equals(endDate);
    }

    // Check if event occurs on a specific date
    public boolean occursOn(LocalDate date) {
        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
               (date.isEqual(endDate) || date.isBefore(endDate));
    }

    // Get duration in days
    public int getDurationDays() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    @Override
    public String toString() {
        if (isMultiDay()) {
            return eventName + " (" + startDate + " to " + endDate + ") [" + getDurationDays() + " days]";
        } else {
            return eventName + " (" + startDate + ")";
        }
    }
}
