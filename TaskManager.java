import java.util.List;

public class TaskManager {
	private final MyCalendar calendar = new MyCalendar();

	public void addTask(Task t) {
		calendar.addTask(t);
	}

	public void removeTask(Task t) {
		calendar.removeTask(t);
	}

	public List<Task> getTasks() {
		return calendar.getTasksList();
	}

	// Simple CLI helper for manual testing
	public static void main(String[] args) {
		TaskManager manager = new TaskManager();
		System.out.println("TaskManager helper - use the GUI (Driver) to interact with tasks.");
		manager.calendar.displayCurrentDate();
	}
}
