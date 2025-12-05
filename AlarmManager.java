import java.time.*;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Simple alarm manager that schedules one-shot alarms and lets the UI reschedule next reminders.
 */
public class AlarmManager {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final Map<Task, ScheduledFuture<?>> scheduled = new ConcurrentHashMap<>();

    // Schedule an alarm for the task at the task's due date at given timeOfDay (if null, use start-of-day).
    // repeatMinutes is the default snooze length used when the user selects "snooze to repeat".
    public static void scheduleAlarm(Task task, long repeatMinutes, java.time.LocalTime timeOfDay) {
        cancelAlarm(task);
        long delayMs = computeDelayToDueDateMillis(task, timeOfDay);
        if (delayMs < 0) delayMs = 0; // if due date/time is in the past, trigger immediately
        ScheduledFuture<?> f = scheduler.schedule(() -> triggerAlarm(task, repeatMinutes), delayMs, TimeUnit.MILLISECONDS);
        scheduled.put(task, f);
    }

    // Backwards-compatible overload: schedule at start-of-day
    public static void scheduleAlarm(Task task, long repeatMinutes) {
        scheduleAlarm(task, repeatMinutes, null);
    }

    // Schedule a one-shot alarm after minutesFromNow minutes
    public static void scheduleOneShot(Task task, long minutesFromNow) {
        cancelAlarm(task);
        long delayMs = Math.max(0, minutesFromNow * 60L * 1000L);
        ScheduledFuture<?> f = scheduler.schedule(() -> triggerAlarm(task, minutesFromNow), delayMs, TimeUnit.MILLISECONDS);
        scheduled.put(task, f);
    }

    public static void cancelAlarm(Task task) {
        ScheduledFuture<?> f = scheduled.remove(task);
        if (f != null) f.cancel(false);
    }

    // Return a snapshot list of tasks that currently have scheduled alarms
    public static java.util.List<Task> getScheduledTasks() {
        return new java.util.ArrayList<>(scheduled.keySet());
    }

    private static void triggerAlarm(Task task, long repeatMinutes) {
        // When alarm fires, open the AlarmDialog on the EDT
        javax.swing.SwingUtilities.invokeLater(() -> {
            AlarmDialog.showAlarm(task, repeatMinutes);
        });
    }

    private static long computeDelayToDueDateMillis(Task task, java.time.LocalTime timeOfDay) {
        try {
            java.time.LocalDate due = task.getDueDate();
            java.time.ZonedDateTime dueZdt;
            if (timeOfDay == null) {
                dueZdt = due.atStartOfDay(ZoneId.systemDefault());
            } else {
                dueZdt = java.time.ZonedDateTime.of(due, timeOfDay, ZoneId.systemDefault());
            }
            Instant now = Instant.now();
            Instant then = dueZdt.toInstant();
            return java.time.Duration.between(now, then).toMillis();
        } catch (Exception e) {
            return 0L;
        }
    }
}
