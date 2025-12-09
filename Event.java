/**
 * Event.java
 *
 * Data model for multi-day events.
 */
import java.time.LocalDate;

public class Event {
    private String eventName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    // Constructor
    public Event(String eventName, LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }
        this.eventName = eventName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = "";
    }

    // Constructor with description
    public Event(String eventName, LocalDate startDate, LocalDate endDate, String description) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }
        this.eventName = eventName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description != null ? description : "";
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

    public String getDescription() {
        return description;
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

    public void setDescription(String description) {
        this.description = description != null ? description : "";
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
        String base;
        if (isMultiDay()) {
            base = eventName + " (" + startDate + " to " + endDate + ") [" + getDurationDays() + " days]";
        } else {
            base = eventName + " (" + startDate + ")";
        }
        if (description != null && !description.isEmpty()) {
            base += " - " + description;
        }
        return base;
    }
}
