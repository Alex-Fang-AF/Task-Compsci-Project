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
}
