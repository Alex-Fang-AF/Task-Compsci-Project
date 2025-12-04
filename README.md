# Task Manager

A simple task and event manager with a calendar GUI built in Java.

## Features

- **Create Tasks** — Add tasks with due dates and priority levels (High, Moderate, Low)
- **Create Events** — Add multi-day events with start and end dates
- **Calendar View** — See tasks and events on a weekly calendar
- **Month View** — Browse an entire month with task/event counts
- **Dark/Light Mode** — Toggle between themes with the circular button in the top-left
- **Full-Page Views** — Dedicated pages for viewing all tasks and events

## Quick Start

### Compile

```powershell
javac *.java
```

### Run

```powershell
java Driver
```

The GUI will open automatically.

## How to Use

### Adding a Task
1. Click **"Create Item"**
2. Enter a task name
3. Click **"Pick"** to select a due date from the calendar
4. Choose a priority level
5. Click **"Create"**

### Adding an Event
1. Click **"Create Item"**
2. Click **"Switch to Event Mode"**
3. Enter an event name
4. Pick start and end dates
5. Click **"Create"**

### Viewing Items
- **Show All Tasks** — View all tasks in the tasks page
- **Open Events Page** — View all events
- **Open Month View** — Browse the calendar by month
- Click on task/event bubbles to see full details

### Removing Items
1. Click **"Remove Item"**
2. Select Task or Event
3. Choose which item to delete
4. Click **"Remove"**

### Dark/Light Mode
Click the circular **"Dark/Light"** button in the top-left corner to toggle between light and dark themes.

## Files

- `Driver.java` — Entry point that launches the GUI
- `TaskGUI.java` — Main application window with calendar and controls
- `TaskCreationGUI.java` — Dialog for creating tasks and events
- `Task.java` — Task data class
- `Event.java` — Event data class
- `MyCalendar.java` — Calendar data management
- `ThemeManager.java` — Dark/light mode support
- `TasksPage.java` — Full-page task list view
- `EventsPage.java` — Full-page event list view
- `CalendarGUI.java` — Detailed month view
- `DetailPage.java` — Task/event detail popup
- `DatePickerDialog.java` — Calendar picker for date selection
- `SoundManager.java` — Origin for creating sound effects on program
- Other supporting classes

## Requirements

- Java 8 or higher
- No external dependencies — uses only Java Swing

## Notes

- Tasks are due on a specific single date
- Events can span multiple consecutive days
- Dates use `dd/MM/yyyy` format (e.g., 25/12/2025)
- All data is stored in memory (no persistence between sessions)
-Sounds are played after each successful creation of tasks

