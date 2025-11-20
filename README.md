# Task-Compsci-Project

A comprehensive Task and Event Manager with a calendar-backed GUI built in Java 21.

## Features

- **Task Management**: Create and manage tasks with specific due dates
- **Multi-Day Events**: Create events that span multiple days
  - Event duration is displayed in days
  - Events automatically appear on all days they span
- **Toggle Mode**: Switch between Task and Event creation modes with a single button
- **Interactive Calendar**: Month-view calendar with navigation
  - Click on dates to view tasks/events for that day
  - Highlight shows today's date
- **Beautiful UI**: Enhanced styling with:
  - Custom colored buttons with hover effects
  - Styled input fields with borders and padding
  - Improved layout and spacing
  - Professional color scheme

## Java 21 (LTS) Setup

### Prerequisites
Install a Java 21 JDK and set up your environment:
- Set JAVA_HOME to your JDK 21 installation directory
- Add %JAVA_HOME%\bin to your PATH

You can download Java 21 from:
- [Eclipse Temurin / Adoptium](https://adoptium.net/temurin/releases/?version=21)
- [Oracle Java SE](https://www.oracle.com/java/technologies/downloads/#java21)

### Compile

\\\powershell
cd 'C:\Users\800025210\Downloads\Task-Compsci-Project'
javac -Xlint:unchecked *.java
\\\

### Run (launches the Swing GUI)

\\\powershell
java -cp . Driver
\\\

## How to Use

### Adding a Task
1. Click "Switch to Task Mode" if you're in Event Mode
2. Enter a task name in the "Name" field
3. Enter the due date in "yyyy-MM-dd" format
4. Click "Add Task"

### Adding a Multi-Day Event
1. Click "Switch to Event Mode" if you're in Task Mode
2. Enter an event name in the "Name" field
3. Enter the start date in "yyyy-MM-dd" format
4. Enter the end date in "yyyy-MM-dd" format
5. Click "Add Event"
   - The event will appear on every day from start to end date (inclusive)

### Viewing Items
- **Show All Tasks**: View all tasks with their due dates
- **Show All Events**: View all events with their duration
- Click a calendar date to view tasks/events for that specific day

### Removing Items
- Click "Remove Task" to remove a task by name
- Click "Remove Event" to remove an event by name

## Files

- Driver.java - Entry point that launches the GUI
- TaskGUI.java - Main GUI application with toggle mode
- MyCalendar.java - Calendar data management for both tasks and events
- Task.java - Task class (single date)
- Event.java - Event class (multi-day support)
- TaskManager.java - Task management utilities
- DSSave.java - Data structure/save utilities
- CalendarImportGUI.java - Calendar import functionality

## Notes

- Tasks are due on a specific single date
- Events span one or more consecutive days
- Use the toggle button to switch between creating tasks and events
- The GUI entry point is Driver (which launches TaskGUI)
- The project compiles and runs under Java 21 LTS
- All input fields have enhanced styling for better UX
