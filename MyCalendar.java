import java.time.LocalDate;
import java.util.*;

public class MyCalendar {

    // Instance Variables
    private LocalDate currentDate;
    private final List<Task> tasksList = new ArrayList<>();
    private final List<Event> eventsList = new ArrayList<>();

    // Constructor
    public MyCalendar() {
        currentDate = LocalDate.now();
    }

    // Methods
    public void displayCurrentDate() {
        System.out.println("Current date: " + currentDate);
    }

    public void addTask(Task task) {
        tasksList.add(task);
        System.out.println("Task added: " + task.getTaskName() + " due on " + task.getDueDate());
    }

    public void removeTask(Task task) {
        tasksList.remove(task);
    }

    public List<Task> getTasksList() {
        List<Task> sortedTasks = new ArrayList<>(tasksList);
        sortedTasks.sort((a, b) -> {
            // First sort by due date (earlier dates first)
            int dateComparison = a.getDueDate().compareTo(b.getDueDate());
            if (dateComparison != 0) {
                return dateComparison;
            }
            // Then sort by priority (HIGH > MEDIUM > LOW)
            int aPriority = a.getPriority() != null ? a.getPriority().ordinal() : -1;
            int bPriority = b.getPriority() != null ? b.getPriority().ordinal() : -1;
            return Integer.compare(bPriority, aPriority);
        });
        return Collections.unmodifiableList(sortedTasks);
    }

    public void showTasksOn(LocalDate date) {
        System.out.println("Task(s) on " + date + ":");
        boolean found = false;
        for (Task task : tasksList) {
            if (date.equals(task.getDueDate())) {
                System.out.println("- " + task.getTaskName());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No task(s) due on this date.");
        }
    }

    // Return an immutable list of tasks for a specific date (sorted by priority)
    public List<Task> getTasksOn(LocalDate date) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasksList) {
            if (date.equals(task.getDueDate())) {
                result.add(task);
            }
        }
        // Sort by priority (HIGH > MEDIUM > LOW)
        result.sort((a, b) -> {
            int aPriority = a.getPriority() != null ? a.getPriority().ordinal() : -1;
            int bPriority = b.getPriority() != null ? b.getPriority().ordinal() : -1;
            return Integer.compare(bPriority, aPriority);
        });
        return Collections.unmodifiableList(result);
    }

    // Event management methods
    public void addEvent(Event event) {
        eventsList.add(event);
        System.out.println("Event added: " + event.getEventName() + " from " + event.getStartDate() + " to " + event.getEndDate());
    }

    public void removeEvent(Event event) {
        eventsList.remove(event);
    }

    public List<Event> getEventsList() {
        return Collections.unmodifiableList(eventsList);
    }

    public List<Event> getEventsOn(LocalDate date) {
        List<Event> result = new ArrayList<>();
        for (Event event : eventsList) {
            if (event.occursOn(date)) {
                result.add(event);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public void showEventsOn(LocalDate date) {
        System.out.println("Event(s) on " + date + ":");
        List<Event> events = getEventsOn(date);
        if (events.isEmpty()) {
            System.out.println("No event(s) on this date.");
        } else {
            for (Event event : events) {
                System.out.println("- " + event.getEventName());
            }
        }
    }

    // Accessors for currentDate to support GUI month navigation
    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate date) {
        this.currentDate = date;
    }

    // Main Method (simple demo)
    public static void main(String[] args) {
        MyCalendar calendar = new MyCalendar();
        calendar.displayCurrentDate();
        Task task = new Task("Goon", LocalDate.of(2025, 12, 31));
        calendar.addTask(task);
        calendar.showTasksOn(LocalDate.of(2025, 12, 31));
        calendar.showTasksOn(LocalDate.of(2026, 1, 20));
    }
}
