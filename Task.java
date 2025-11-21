import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Task {
    private String taskName;
    private LocalDate dueDate;
    private boolean isCompleted;
    private LocalDate completionDate;
    private TaskPriority priority;
    private String description;

    // Priority enum
    public enum TaskPriority {
        LOW("Low"), MEDIUM("Medium"), HIGH("High");
        
        private final String displayName;
        
        TaskPriority(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructor with basic info
    public Task(String taskName, LocalDate dueDate) {
        this.taskName = taskName;
        this.dueDate = dueDate;
        this.isCompleted = false;
        this.completionDate = null;
        this.priority = TaskPriority.MEDIUM;
        this.description = "";
    }

    // Constructor with all info
    public Task(String taskName, LocalDate dueDate, TaskPriority priority, String description) {
        this.taskName = taskName;
        this.dueDate = dueDate;
        this.isCompleted = false;
        this.completionDate = null;
        this.priority = priority;
        this.description = description;
    }

    // Getters
    public String getTaskName() {
        return taskName;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setTaskName(String n) {
        this.taskName = n;
    }

    public void setdueDate(LocalDate d) {
        this.dueDate = d;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Mark task as completed
    public void markCompleted() {
        this.isCompleted = true;
        this.completionDate = LocalDate.now();
    }

    // Mark task as incomplete
    public void markIncomplete() {
        this.isCompleted = false;
        this.completionDate = null;
    }

    // Check if task is overdue
    public boolean isOverdue() {
        if (isCompleted) {
            return false; // Completed tasks are not overdue
        }
        return LocalDate.now().isAfter(dueDate);
    }

    // Check if task is due today
    public boolean isDueToday() {
        return dueDate.isEqual(LocalDate.now());
    }

    // Get days until due
    public long daysUntilDue() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    // Get formatted due date string
    public String getFormattedDueDate() {
        return dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    // Enhanced toString method for better display
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Status indicator
        if (isCompleted) {
            sb.append("✓ ");
        } else if (isOverdue()) {
            sb.append("⚠ ");
        } else {
            sb.append("• ");
        }
        
        // Task name
        sb.append(taskName);
        
        // Priority indicator
        if (priority == TaskPriority.HIGH) {
            sb.append(" [HIGH]");
        } else if (priority == TaskPriority.LOW) {
            sb.append(" [LOW]");
        }
        
        // Due date
        sb.append(" - Due: ").append(getFormattedDueDate());
        
        // Completion status
        if (isCompleted && completionDate != null) {
            sb.append(" (Completed: ").append(completionDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append(")");
        }
        
        return sb.toString();
    }

    // Edit task details
    public void editTask(String newName, LocalDate newDueDate, TaskPriority newPriority, String newDescription) {
        if (newName != null && !newName.isEmpty()) {
            this.taskName = newName;
        }
        if (newDueDate != null) {
            this.dueDate = newDueDate;
        }
        if (newPriority != null) {
            this.priority = newPriority;
        }
        if (newDescription != null) {
            this.description = newDescription;
        }
    }
}
