import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TaskGUI extends JFrame {
    final private MyCalendar calendar;
    private JPanel mainPanel;
    private JPanel buttonPanel;
    private JPanel displayPanel;
    
    private JTextArea taskListArea;
    private JTextArea eventListArea;
    private JButton addTaskButton;
    private JButton showTasksButton;
    private JButton removeTaskButton;
    private JButton toggleCalendarButton;
    private CalendarGUI calendarWindow;
    
    // Calendar UI fields
    private JPanel calendarPanel;
    private JLabel monthLabel;
    private final List<JButton> dayButtons = new ArrayList<>();
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
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        setBackground(new Color(245, 250, 255));
    }

    // Create and organize all panels
    private void createPanels() {
        // Main panel with border layout
        mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 250, 255));

        // Calendar panel for month view
        createCalendarPanel();

        // Button panel for actions
        createButtonPanel();

        // Display panel for showing tasks and events
        createDisplayPanel();

        // Add sections to main panel
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(calendarPanel, BorderLayout.CENTER);
        mainPanel.add(displayPanel, BorderLayout.EAST);

        // Add main panel to frame
        add(mainPanel);
    }

    // Create button panel for actions
    private void createButtonPanel() {
        buttonPanel = new JPanel(new GridLayout(2, 2, 15, 12));
        buttonPanel.setBackground(new Color(245, 250, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Creation button
        JButton createButton = new JButton("Create Item");

        // Task buttons
        addTaskButton = new JButton("Show All Tasks");
        showTasksButton = new JButton("Show Tasks");
        removeTaskButton = new JButton("Remove Item");

        // Add action listeners
        createButton.addActionListener(e -> openItemCreation());
        addTaskButton.addActionListener(e -> showAllItems());
        showTasksButton.addActionListener(e -> showAllItems());
        removeTaskButton.addActionListener(e -> removeItem());

        // Style buttons
        styleButton(createButton, new Color(156, 39, 176));
        styleButton(addTaskButton, new Color(33, 150, 243));
        styleButton(showTasksButton, new Color(33, 150, 243));
        styleButton(removeTaskButton, new Color(244, 67, 54));

        // Toggle external week view window
        toggleCalendarButton = new JButton("Open Week View");
        toggleCalendarButton.addActionListener(e -> {
            if (calendarWindow == null) {
                calendarWindow = new CalendarGUI(calendar);
            }
            boolean nowVisible = !calendarWindow.isVisible();
            calendarWindow.setVisible(nowVisible);
            toggleCalendarButton.setText(nowVisible ? "Hide Week View" : "Open Week View");
        });

        buttonPanel.add(createButton);
        buttonPanel.add(addTaskButton);
        buttonPanel.add(removeTaskButton);
        buttonPanel.add(toggleCalendarButton);
    }

    // Open item creation dialog (task or event)
    private void openItemCreation() {
        TaskCreationGUI dialog = new TaskCreationGUI(this, calendar);
        dialog.setVisible(true);
        if (dialog.isItemCreated()) {
            // Refresh both displays and calendar
            showAllItems();
            refreshCalendar();
        }
    }

    // Style buttons with colors
    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
    }

    // Create display panel for tasks and events
    private void createDisplayPanel() {
        displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(new Color(245, 250, 255));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Top section: Tasks (50% of space)
        JPanel tasksPanel = new JPanel(new BorderLayout());
        tasksPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            "Tasks",
            0, 0,
            new Font("Arial", Font.BOLD, 12),
            new Color(50, 100, 150)
        ));
        tasksPanel.setBackground(new Color(245, 250, 255));

        taskListArea = new JTextArea();
        taskListArea.setEditable(false);
        taskListArea.setLineWrap(true);
        taskListArea.setWrapStyleWord(true);
        taskListArea.setFont(new Font("Arial", Font.PLAIN, 12));
        taskListArea.setBackground(new Color(255, 255, 255));
        taskListArea.setForeground(new Color(50, 50, 50));
        taskListArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        taskListArea.setMargin(new Insets(5, 5, 5, 5));

        JScrollPane taskScrollPane = new JScrollPane(taskListArea);
        taskScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        taskScrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(150, 150, 200);
                this.trackColor = new Color(240, 240, 240);
            }
        });
        tasksPanel.add(taskScrollPane, BorderLayout.CENTER);

        // Bottom section: Events (50% of space)
        JPanel eventsPanel = new JPanel(new BorderLayout());
        eventsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            "Events",
            0, 0,
            new Font("Arial", Font.BOLD, 12),
            new Color(50, 100, 150)
        ));
        eventsPanel.setBackground(new Color(245, 250, 255));

        eventListArea = new JTextArea();
        eventListArea.setEditable(false);
        eventListArea.setLineWrap(true);
        eventListArea.setWrapStyleWord(true);
        eventListArea.setFont(new Font("Arial", Font.PLAIN, 12));
        eventListArea.setBackground(new Color(255, 255, 255));
        eventListArea.setForeground(new Color(50, 50, 50));
        eventListArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        eventListArea.setMargin(new Insets(5, 5, 5, 5));

        JScrollPane eventScrollPane = new JScrollPane(eventListArea);
        eventScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        eventScrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(150, 150, 200);
                this.trackColor = new Color(240, 240, 240);
            }
        });
        eventsPanel.add(eventScrollPane, BorderLayout.CENTER);

        // Create a container for both panels with equal sizing
        JPanel containerPanel = new JPanel(new GridLayout(2, 1, 0, 12));
        containerPanel.setBackground(new Color(245, 250, 255));
        containerPanel.add(tasksPanel);
        containerPanel.add(eventsPanel);

        // Add both panels to display panel
        displayPanel.add(containerPanel, BorderLayout.CENTER);
    }

    // Display all tasks and events
    private void showAllItems() {
        showAllTasks();
        showAllEvents();
    }

    // Display all tasks
    private void showAllTasks() {
        taskListArea.setText("");
        System.out.println("DEBUG: showAllTasks called. Tasks count: " + calendar.getTasksList().size());
        if (calendar.getTasksList().isEmpty()) {
            taskListArea.setText("No tasks added yet.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Task task : calendar.getTasksList()) {
                System.out.println("DEBUG: Task - Name: '" + task.getTaskName() + "', Due: " + task.getDueDate());
                sb.append("• ").append(task.getTaskName()).append(" - Due: ").append(task.getDueDate()).append("\n");
            }
            taskListArea.setText(sb.toString());
        }
    }

    // Remove a task or event by name
    private void removeItem() {
        // Create a dialog with dropdown list for removal
        JDialog removeDialog = new JDialog(this, "Remove Item", true);
        removeDialog.setSize(400, 300);
        removeDialog.setLocationRelativeTo(this);
        removeDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel dialogMainPanel = new JPanel(new BorderLayout(10, 10));
        dialogMainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        dialogMainPanel.setBackground(new Color(245, 250, 255));
        
        // Type selection
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.setBackground(new Color(245, 250, 255));
        JLabel typeLabel = new JLabel("Select type:");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        String[] types = {"Task", "Event"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setBackground(new Color(255, 255, 255));
        typeCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        
        typePanel.add(typeLabel);
        typePanel.add(typeCombo);
        
        // Item list
        JPanel listPanel = new JPanel(new BorderLayout(5, 5));
        listPanel.setBackground(new Color(245, 250, 255));
        JLabel listLabel = new JLabel("Select item to remove:");
        listLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> itemList = new JList<>(listModel);
        itemList.setFont(new Font("Arial", Font.PLAIN, 12));
        itemList.setBackground(new Color(255, 255, 255));
        itemList.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200), 1));
        
        JScrollPane scrollPane = new JScrollPane(itemList);
        scrollPane.getVerticalScrollBar().setBackground(new Color(200, 220, 240));
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI());
        
        listPanel.add(listLabel, BorderLayout.NORTH);
        listPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Update list based on type selection
        typeCombo.addActionListener(evt -> {
            listModel.clear();
            if (typeCombo.getSelectedItem().equals("Task")) {
                for (Task task : calendar.getTasksList()) {
                    listModel.addElement(task.getTaskName());
                }
            } else {
                for (Event event : calendar.getEventsList()) {
                    listModel.addElement(event.getEventName());
                }
            }
        });
        
        // Button panel
        JPanel dialogButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        dialogButtonPanel.setBackground(new Color(245, 250, 255));
        
        JButton removeBtn = new JButton("Remove");
        removeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        removeBtn.setBackground(new Color(220, 80, 80));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFocusPainted(false);
        removeBtn.setBorder(BorderFactory.createLineBorder(new Color(150, 50, 50), 1));
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 12));
        cancelBtn.setBackground(new Color(150, 150, 150));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        
        removeBtn.addActionListener(evt -> {
            if (itemList.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(removeDialog, "Please select an item to remove.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String selectedItem = itemList.getSelectedValue();
            if (typeCombo.getSelectedItem().equals("Task")) {
                for (Task task : calendar.getTasksList()) {
                    if (task.getTaskName().equals(selectedItem)) {
                        calendar.removeTask(task);
                        break;
                    }
                }
            } else {
                for (Event event : calendar.getEventsList()) {
                    if (event.getEventName().equals(selectedItem)) {
                        calendar.removeEvent(event);
                        break;
                    }
                }
            }
            
            JOptionPane.showMessageDialog(removeDialog, selectedItem + " removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            showAllItems();
            refreshCalendar();
            removeDialog.dispose();
        });
        
        cancelBtn.addActionListener(evt -> removeDialog.dispose());
        
        dialogButtonPanel.add(removeBtn);
        dialogButtonPanel.add(cancelBtn);
        
        // Add panels to dialog
        dialogMainPanel.add(typePanel, BorderLayout.NORTH);
        dialogMainPanel.add(listPanel, BorderLayout.CENTER);
        dialogMainPanel.add(dialogButtonPanel, BorderLayout.SOUTH);
        
        removeDialog.add(dialogMainPanel);
        
        // Populate initial list with tasks
        for (Task task : calendar.getTasksList()) {
            listModel.addElement(task.getTaskName());
        }
        
        removeDialog.setVisible(true);
    }

    // Create a month-view calendar panel with day buttons
    private void createCalendarPanel() {
        calendarPanel = new JPanel(new BorderLayout(8, 8));
        calendarPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            "Calendar",
            0, 0,
            new Font("Arial", Font.BOLD, 12),
            new Color(50, 100, 150)
        ));
        calendarPanel.setBackground(new Color(245, 250, 255));

        JPanel nav = new JPanel(new BorderLayout(5, 0));
        nav.setBackground(new Color(245, 250, 255));
        JButton prev = new JButton("<");
        JButton next = new JButton(">");
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 13));
        monthLabel.setForeground(new Color(50, 100, 150));
        
        nav.add(prev, BorderLayout.WEST);
        nav.add(monthLabel, BorderLayout.CENTER);
        nav.add(next, BorderLayout.EAST);

        // Style navigation buttons
        styleButton(prev, new Color(100, 150, 200));
        styleButton(next, new Color(100, 150, 200));

        prev.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            refreshCalendar();
        });
        next.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            refreshCalendar();
        });

        calendarPanel.add(nav, BorderLayout.NORTH);

        // Panel to hold the dynamically sized grid
        JPanel gridContainer = new JPanel(new BorderLayout());
        gridContainer.setBackground(new Color(245, 250, 255));
        dayButtons.clear(); // Clear any previous buttons
        createCalendarGrid(gridContainer);

        calendarPanel.add(gridContainer, BorderLayout.CENTER);
    }

    // Create calendar grid with dynamic sizing based on the current month
    private void createCalendarGrid(JPanel gridContainer) {
        if (currentYearMonth == null) {
            currentYearMonth = YearMonth.from(LocalDate.now());
        }

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int startIndex = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday -> 0
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int totalCells = startIndex + daysInMonth;
        int rows = (int) Math.ceil((double) totalCells / 7);
        int totalGridCells = rows * 7; // Fill complete rows

        JPanel grid = new JPanel(new GridLayout(rows + 1, 7, 2, 2));
        grid.setBackground(new Color(245, 250, 255));

        // Add day name headers
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String dn : dayNames) {
            JLabel lbl = new JLabel(dn, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 10));
            lbl.setForeground(new Color(50, 100, 150));
            grid.add(lbl);
        }

        // Add day buttons, filling the last row with blanks
        for (int i = 0; i < totalGridCells; i++) {
            JButton dayBtn = new JButton();
            dayBtn.setMargin(new Insets(4, 4, 4, 4));
            dayBtn.setFocusable(false);
            dayBtn.setFont(new Font("Arial", Font.BOLD, 10));
            dayBtn.setBackground(new Color(255, 255, 255));
            dayBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

            int dayNumber = i - startIndex + 1;
            if (dayNumber >= 1 && dayNumber <= daysInMonth) {
                final int day = dayNumber;
                dayBtn.setText(String.valueOf(day));
                dayBtn.setEnabled(true);
                
                LocalDate d = currentYearMonth.atDay(day);
                // Highlight today
                if (d.equals(LocalDate.now())) {
                    dayBtn.setBackground(new Color(200, 230, 255));
                }

                dayBtn.addActionListener(e -> {
                    selectedDate = currentYearMonth.atDay(day);
                    showTasksForDate(selectedDate);
                });
            } else {
                dayBtn.setEnabled(false);
            }

            // Add hover effect for day buttons
            dayBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (dayBtn.isEnabled() && !dayBtn.getText().isEmpty()) {
                        dayBtn.setBackground(new Color(220, 240, 255));
                    }
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (dayBtn.isEnabled() && !dayBtn.getText().isEmpty()) {
                        try {
                            LocalDate d = currentYearMonth.atDay(Integer.parseInt(dayBtn.getText()));
                            if (d.equals(LocalDate.now())) {
                                dayBtn.setBackground(new Color(200, 230, 255));
                            } else {
                                dayBtn.setBackground(new Color(255, 255, 255));
                            }
                        } catch (NumberFormatException ex) {
                            dayBtn.setBackground(new Color(255, 255, 255));
                        }
                    }
                }
            });
            dayButtons.add(dayBtn);
            grid.add(dayBtn);
        }

        gridContainer.removeAll();
        gridContainer.add(grid, BorderLayout.CENTER);
    }

    // Refresh the calendar UI for the currentYearMonth
    private void refreshCalendar() {
        if (currentYearMonth == null) {
            currentYearMonth = YearMonth.from(LocalDate.now());
        }
        monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
        
        // Recreate the calendar grid with the new month's layout
        createCalendarGrid((JPanel) calendarPanel.getComponent(1)); // Get the grid container
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

    // Display all events
    // Display all events
    private void showAllEvents() {
        eventListArea.setText("");
        List<Event> events = calendar.getEventsList();
        if (events.isEmpty()) {
            eventListArea.setText("No events added yet.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Event event : events) {
                sb.append("• ").append(event).append("\n");
            }
            eventListArea.setText(sb.toString());
        }
    }


    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TaskGUI());
    }
}
