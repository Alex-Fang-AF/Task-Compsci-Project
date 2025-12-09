# TaskTracker (Task & Event Manager)

Lightweight Java Swing app for creating and viewing tasks and events on a calendar.
## Quick start

1. Compile:

```powershell
javac -d bin -cp . *.java
```
2. Run:

```powershell
java -cp bin Driver
# TaskTracker

Lightweight Java Swing app for creating and viewing tasks and events on a calendar.

## Quick Start

1. Compile:

```powershell
javac -d bin -cp . *.java
```

2. Run:

```powershell
java -cp bin Driver
```

The GUI will open automatically.

## Files and short descriptions

- `Driver.java` — Application entry point; initializes UI defaults and opens the main window.
- `TaskGUI.java` — Main application window: week calendar, top bar, action buttons, theme toggle, and navigation to pages.
- `TaskCreationGUI.java` — Modal dialog to create Tasks or Events; supports 12-hour time + AM/PM alarm options and snooze.
- `MyCalendar.java` — In-memory model managing tasks and events, sorting logic, simple persistence helpers (`saveTasksToFile` / `loadTasksFromFile`) and expired-task cleanup.
- `Task.java` — Task data model (name, due date, priority, description) with helper formatters.
- `Event.java` — Event data model (name, start/end dates, description) and helpers to check occurrence.
- `TasksPage.java` — Full-page UI listing tasks as interactive bubbles; shows bell icon for scheduled alarms.
- `EventsPage.java` — Full-page UI listing events with similar modern styling.
- `CombinedPage.java` — 50/50 split view showing Tasks (left) and Events (right) with clickable rows and alarm indicators.
- `CalendarGUI.java` — Month view calendar UI with rounded day cells, hover effects, right-side details/import panel, and click-to-see-day details.
- `DatePickerDialog.java` — Reusable date picker dialog used when selecting dates in forms.
- `DetailPage.java` — Popup that shows full details for a Task or Event and offers edit/remove actions.
- `ThemeManager.java` — Centralized light/dark theme state, color palette accessors, and listener support for UI updates.
- `UIUtils.java` — Shared UI helpers (fonts, button/textfield styling) and the package-private `RoundedPanel` helper for rounded containers.
- `AlarmManager.java` — Schedules alarms using a `ScheduledExecutorService`, tracks scheduled tasks, and triggers `AlarmDialog` on the EDT.
- `AlarmDialog.java` — Modal reminder dialog presented when an alarm fires; plays a bell loop and offers snooze/cancel choices.
- `AlarmManagementGUI.java` — UI to view and manage scheduled alarms (one-shot scheduling / cancel from UI).
- `SoundPlayer.java` — Small audio utilities for playing tones and looping bell sequences used by alarms/notifications.
- `CalendarImportGUI.java` — (Legacy) an import-oriented calendar view; import functionality has been merged into `CalendarGUI` but this class may still exist.

## Notes

- Uses Java Swing; no external libraries required.
- Tasks are stored in memory; `MyCalendar` provides simple file helpers to persist tasks if desired.
- Tasks with due dates before today are removed from the active list when displayed (automatic cleanup).

If you'd like me to add screenshots, contribution guidelines, or a sample Git workflow to this README, say which and I'll update it.
### Run

