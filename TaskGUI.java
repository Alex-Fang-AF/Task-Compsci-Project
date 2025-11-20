import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TaskGUI extends JFrame {
    final private MyCalendar calendar;
    private JPanel mainPanel;
    private JPanel inputPanel;
    private JPanel buttonPanel;
    private JPanel displayPanel;
    
    private JTextField taskNameField;
    private JTextField dueDateField;
    private JTextArea taskListArea;
    private JButton addTaskButton;
    private JButton showTasksButton;
    private JButton removeTaskButton;
    private JButton toggleCalendarButton;
    private CalendarGUI calendarWindow;
    // Calendar UI fields
    private JPanel calendarPanel;
    private JLabel monthLabel;
    private List<JButton> dayButtons = new ArrayList<>();
    private YearMonth currentYearMonth;
    private LocalDate selectedDate;

    // Constructor
    public TaskGUI() {
        calendar = new MyCalendar();
        currentYearMonth = YearMonth.from(calendar.getCurrentDate());
        initializeFrame();
        createPanels();
        refreshCalendar();
        setVisible(true);
    }

    // Initialize the main frame
    private void initializeFrame() {
        setTitle("Task Manager GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    // Create and organize all panels
    private void createPanels() {
        // Main panel with border layout
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Input panel for task creation
        createInputPanel();

        // Calendar panel for month view
        createCalendarPanel();

        // Button panel for actions
        createButtonPanel();

        // Display panel for showing tasks
        createDisplayPanel();

        // Combine input and button panels
        JPanel topSection = new JPanel(new BorderLayout(10, 10));
        topSection.add(inputPanel, BorderLayout.NORTH);
        topSection.add(buttonPanel, BorderLayout.SOUTH);

        // Add sections to main panel
        mainPanel.add(topSection, BorderLayout.NORTH);
        mainPanel.add(displayPanel, BorderLayout.CENTER);
        mainPanel.add(calendarPanel, BorderLayout.WEST);

        // Add main panel to frame
        add(mainPanel);
    }

    // Create input panel for task name and due date
    private void createInputPanel() {
        inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add New Task"));

        // Task name
        JLabel nameLabel = new JLabel("Task Name:");
        taskNameField = new JTextField();
        inputPanel.add(nameLabel);
        inputPanel.add(taskNameField);

        // Due date
        JLabel dateLabel = new JLabel("Due Date (yyyy-MM-dd):");
        dueDateField = new JTextField();
        inputPanel.add(dateLabel);
        inputPanel.add(dueDateField);
    }

    // Create button panel for actions
    private void createButtonPanel() {
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        addTaskButton = new JButton("Add Task");
        showTasksButton = new JButton("Show All Tasks");
        removeTaskButton = new JButton("Remove Task");

        // Add action listeners
        addTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });

        showTasksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllTasks();
            }
        });

        removeTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeTask();
            }
        });

        // Toggle external month-details window
        toggleCalendarButton = new JButton("Open Month Details");
        toggleCalendarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (calendarWindow == null) {
                    calendarWindow = new CalendarGUI(calendar);
                }
                boolean nowVisible = !calendarWindow.isVisible();
                calendarWindow.setVisible(nowVisible);
                toggleCalendarButton.setText(nowVisible ? "Hide Month Details" : "Open Month Details");
            }
        });

        buttonPanel.add(addTaskButton);
        buttonPanel.add(showTasksButton);
        buttonPanel.add(removeTaskButton);
        buttonPanel.add(toggleCalendarButton);
    }

    // Create display panel for tasks
    private void createDisplayPanel() {
        displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBorder(BorderFactory.createTitledBorder("Task List"));

        taskListArea = new JTextArea();
        taskListArea.setEditable(false);
        taskListArea.setLineWrap(true);
        taskListArea.setWrapStyleWord(true);
        taskListArea.setFont(new Font("Arial", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(taskListArea);
        displayPanel.add(scrollPane, BorderLayout.CENTER);
    }

    // Add a new task
    private void addTask() {
        String taskName = taskNameField.getText().trim();
        String dueDateStr = dueDateField.getText().trim();

        if (taskName.isEmpty() || dueDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate dueDate = LocalDate.parse(dueDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            Task task = new Task(taskName, dueDate);
            calendar.addTask(task);
            taskNameField.setText("");
            dueDateField.setText("");
            JOptionPane.showMessageDialog(this, "Task added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            showAllTasks();
            refreshCalendar();
            if (calendarWindow != null) calendarWindow.refreshCalendar();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd.", "Date Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Display all tasks
    private void showAllTasks() {
        taskListArea.setText("");
        if (calendar.getTasksList().isEmpty()) {
            taskListArea.setText("No tasks added yet.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Task task : calendar.getTasksList()) {
                sb.append("• ").append(task.getTaskName()).append(" - Due: ").append(task.getDueDate()).append("\n");
            }
            taskListArea.setText(sb.toString());
        }
    }

    // Remove a task by name
    private void removeTask() {
        if (calendar.getTasksList().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tasks to remove.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String taskName = JOptionPane.showInputDialog(this, "Enter the task name to remove:");
        if (taskName != null && !taskName.trim().isEmpty()) {
            boolean found = false;
            for (Task task : calendar.getTasksList()) {
                if (task.getTaskName().equalsIgnoreCase(taskName.trim())) {
                    calendar.removeTask(task);
                    found = true;
                    break;
                }
            }
            if (found) {
                JOptionPane.showMessageDialog(this, "Task removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                showAllTasks();
                refreshCalendar();
                if (calendarWindow != null) calendarWindow.refreshCalendar();
            } else {
                JOptionPane.showMessageDialog(this, "Task not found.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // Create a month-view calendar panel with day buttons
    private void createCalendarPanel() {
        calendarPanel = new JPanel(new BorderLayout(5,5));
        calendarPanel.setBorder(BorderFactory.createTitledBorder("Calendar"));

        JPanel nav = new JPanel(new BorderLayout());
        JButton prev = new JButton("<");
        JButton next = new JButton(">");
        monthLabel = new JLabel("", SwingConstants.CENTER);
        nav.add(prev, BorderLayout.WEST);
        nav.add(monthLabel, BorderLayout.CENTER);
        nav.add(next, BorderLayout.EAST);

        prev.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            refreshCalendar();
        });
        next.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            refreshCalendar();
        });

        calendarPanel.add(nav, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(7, 7));
        String[] dayNames = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        for (String dn : dayNames) {
            JLabel lbl = new JLabel(dn, SwingConstants.CENTER);
            grid.add(lbl);
        }
        for (int i = 0; i < 42; i++) {
            JButton dayBtn = new JButton();
            dayBtn.setMargin(new Insets(2,2,2,2));
            dayBtn.setFocusable(false);
            dayBtn.addActionListener(e -> {
                String txt = ((JButton)e.getSource()).getText();
                if (txt != null && !txt.isEmpty()) {
                    int day = Integer.parseInt(txt);
                    selectedDate = currentYearMonth.atDay(day);
                    dueDateField.setText(selectedDate.toString());
                    showTasksForDate(selectedDate);
                }
            });
            dayButtons.add(dayBtn);
            grid.add(dayBtn);
        }

        calendarPanel.add(grid, BorderLayout.CENTER);
    }

    // Refresh the calendar UI for the currentYearMonth
    private void refreshCalendar() {
        if (currentYearMonth == null) {
            currentYearMonth = YearMonth.from(LocalDate.now());
        }
        monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int startIndex = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday -> 0
        int daysInMonth = currentYearMonth.lengthOfMonth();

        for (int i = 0; i < 42; i++) {
            JButton btn = dayButtons.get(i);
            int dayNumber = i - startIndex + 1;
            if (dayNumber >= 1 && dayNumber <= daysInMonth) {
                btn.setText(String.valueOf(dayNumber));
                btn.setEnabled(true);
                LocalDate d = currentYearMonth.atDay(dayNumber);
                // Highlight today
                if (d.equals(LocalDate.now())) {
                    btn.setBackground(new Color(200, 230, 255));
                } else {
                    btn.setBackground(null);
                }
            } else {
                btn.setText("");
                btn.setEnabled(false);
                btn.setBackground(null);
            }
        }
    }

    // Show tasks for a specific date in the task list area
    private void showTasksForDate(LocalDate date) {
        List<Task> tasks = calendar.getTasksOn(date);
        StringBuilder sb = new StringBuilder();
        sb.append("Tasks for ").append(date).append(":\n");
        if (tasks.isEmpty()) {
            sb.append("No tasks for this date.");
        } else {
            for (Task t : tasks) {
                sb.append("• ").append(t.getTaskName()).append("\n");
            }
        }
        taskListArea.setText(sb.toString());
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TaskGUI();
            }
        });
    }
}
