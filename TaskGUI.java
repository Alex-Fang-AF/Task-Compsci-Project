import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TaskGUI extends JFrame {
    final private MyCalendar calendar;
    private JPanel mainPanel;
    private JPanel buttonPanel;
    // right-side display panel removed (we now use separate full-page windows)
    private JPanel mainContentPanel; // center content area which can show calendar or large task/event pages
    private JPanel calendarCard;
    private JPanel tasksCard;
    private JPanel eventsCard;
    private TasksPage tasksPageWindow;
    private EventsPage eventsPageWindow;
    
    // Small inline area fields removed in favor of full page windows
    // Larger dedicated areas for full-sized pages
    private JTextArea largeTaskArea;
    private JTextArea largeEventArea;
    private JButton addTaskButton;
    private JButton showTasksButton;
    private JButton removeTaskButton;
    private JButton toggleCalendarButton;
    private CalendarGUI calendarWindow;
    
    // Calendar UI fields
    private JPanel calendarPanel;
    private JLabel weekLabel;
    private LocalDate currentWeekStart;

    // Constructor
    public TaskGUI() {
        calendar = new MyCalendar();
        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        initializeFrame();
        createPanels();
        refreshCalendar();
        setVisible(true);
    }

    // (removed priority badge) tasks will be ordered by priority when displayed

    // Initialize the main frame
    private void initializeFrame() {
        setTitle("Task Manager GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        setBackground(ThemeManager.getBackgroundColor());
    }

    // Create and organize all panels
    private void createPanels() {
        // Top panel with theme toggle button on the left
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ThemeManager.getBackgroundColor());
        topPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        // Create circular theme toggle button
        // Button text should show the mode it will switch to if dark show light, if light show dark button
        String btnText = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK ? "Light" : "Dark";
        JButton themeToggleButton = createCircularButton(btnText, 55);
        // When clicked, toggle the theme. A listener below will update UI everywhere.
        themeToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                ThemeManager.toggleTheme();
            }
        });
        // Register a listener so when theme changes we update text and apply theme across windows
        ThemeManager.addListener(new ThemeManager.ThemeChangeListener() {
            public void onThemeChanged(ThemeManager.Theme newTheme) {
                themeToggleButton.setText(newTheme == ThemeManager.Theme.DARK ? "Light" : "Dark");
                applyTheme();
                if (tasksPageWindow != null) tasksPageWindow.applyTheme();
                if (eventsPageWindow != null) eventsPageWindow.applyTheme();
                if (calendarWindow != null) calendarWindow.applyTheme();
            }
        });
        styleButton(themeToggleButton, new Color(100, 100, 100));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(ThemeManager.getBackgroundColor());
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.add(themeToggleButton);
        topPanel.add(leftPanel, BorderLayout.WEST);

        // Main panel with border layout
        mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(ThemeManager.getBackgroundColor());

        // Button panel for actions
        createButtonPanel();

        // Create new Panel for calendar
        createCalendarPanel();
        // Main center content area
        mainContentPanel = new JPanel(new java.awt.CardLayout());
        calendarCard = new JPanel(new BorderLayout());
        calendarCard.setBackground(ThemeManager.getBackgroundColor());
        calendarCard.add(calendarPanel, BorderLayout.CENTER);
        // Large task and event cards
        tasksCard = new JPanel(new BorderLayout());
        largeTaskArea = new JTextArea();
        largeTaskArea.setEditable(false);
        largeTaskArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane largeTaskScroll = new JScrollPane(largeTaskArea);
        tasksCard.add(largeTaskScroll, BorderLayout.CENTER);

        eventsCard = new JPanel(new BorderLayout());
        largeEventArea = new JTextArea();
        largeEventArea.setEditable(false);
        largeEventArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane largeEventScroll = new JScrollPane(largeEventArea);
        eventsCard.add(largeEventScroll, BorderLayout.CENTER);

        mainContentPanel.add(calendarCard, "CALENDAR");
        mainContentPanel.add(tasksCard, "TASKS");
        mainContentPanel.add(eventsCard, "EVENTS");

        // Add sections to main panel
        add(topPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(mainContentPanel, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);
    }

    // Create button panel for actions
    private void createButtonPanel() {
        buttonPanel = new JPanel(new GridLayout(2, 4, 15, 12));
        buttonPanel.setBackground(ThemeManager.getBackgroundColor());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Creation button
        JButton createButton = new JButton("Create Item");

        // Task buttons
        addTaskButton = new JButton("Show All Tasks/Events");
        showTasksButton = new JButton("Show Tasks");
        removeTaskButton = new JButton("Remove Item");

        // Add action listeners
        createButton.addActionListener(e -> openItemCreation());
        addTaskButton.addActionListener(e -> showAllItems());
        showTasksButton.addActionListener(e -> showAllItems());
        removeTaskButton.addActionListener(e -> removeItem());

        // Style buttons
        styleButton(createButton, new Color(76, 175, 80));
        styleButton(addTaskButton, new Color(33, 150, 243));
        styleButton(showTasksButton, new Color(33, 150, 243));
        styleButton(removeTaskButton, new Color(244, 67, 54));

        // Toggle external month view window
        toggleCalendarButton = new JButton("Open Month View");
        toggleCalendarButton.addActionListener(e -> {
            if (calendarWindow == null) {
                calendarWindow = new CalendarGUI(calendar);
            }
            boolean nowVisible = !calendarWindow.isVisible();
            calendarWindow.setVisible(nowVisible);
            toggleCalendarButton.setText(nowVisible ? "Hide Month View" : "Open Month View");
        });
        styleButton(toggleCalendarButton, new Color(255, 152, 0));

        // Button for showing large Tasks page
        JButton showTasksPageButton = new JButton("Open Tasks Page");
        showTasksPageButton.addActionListener(e -> {
            // Open the separate TasksPage window
            if (tasksPageWindow == null) {
                tasksPageWindow = new TasksPage(calendar);
            }
            tasksPageWindow.refresh();
            tasksPageWindow.setVisible(true);
        });
        styleButton(showTasksPageButton, new Color(33, 150, 243));

        // Button for showing large Events page
        JButton showEventsPageButton = new JButton("Open Events Page");
        showEventsPageButton.addActionListener(e -> {
            // Open the separate EventsPage window
            if (eventsPageWindow == null) {
                eventsPageWindow = new EventsPage(calendar);
            }
            eventsPageWindow.refresh();
            eventsPageWindow.setVisible(true);
        });
        styleButton(showEventsPageButton, new Color(33, 150, 243));

        buttonPanel.add(createButton);
        buttonPanel.add(removeTaskButton);
        buttonPanel.add(toggleCalendarButton);
        buttonPanel.add(addTaskButton);
        buttonPanel.add(showTasksPageButton);
        buttonPanel.add(showEventsPageButton);
    }

    // Open item creation dialog (task or event)
    private void openItemCreation() {
        TaskCreationGUI dialog = new TaskCreationGUI(this, calendar);
        dialog.setVisible(true);
        if (dialog.isItemCreated()) {
            // Refresh calendar only (do not auto-open the Tasks/Events page)
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

    // Create a circular button
    private JButton createCircularButton(String text, int diameter) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isArmed()) {
                    g.setColor(getBackground().darker());
                } else {
                    g.setColor(getBackground());
                }
                g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
                super.paintComponent(g);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(diameter, diameter);
            }
        };
        button.setPreferredSize(new Dimension(diameter, diameter));
        button.setMaximumSize(new Dimension(diameter, diameter));
        button.setMinimumSize(new Dimension(diameter, diameter));
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBackground(new Color(100, 100, 100));
        return button;
    }

        
    // Display all tasks and events
    private void showAllItems() {
        showAllTasks();
        showAllEvents();
        // default to open Tasks page when requested
        if (tasksPageWindow == null) tasksPageWindow = new TasksPage(calendar);
        tasksPageWindow.refresh();
        tasksPageWindow.setVisible(true);
        // Also refresh any open full-page windows
        if (tasksPageWindow != null) tasksPageWindow.refresh();
        if (eventsPageWindow != null) eventsPageWindow.refresh();
    }

    // Display all tasks
    private void showAllTasks() {
        StringBuilder sb = new StringBuilder();
        System.out.println("DEBUG: showAllTasks called. Tasks count: " + calendar.getTasksList().size());
        if (calendar.getTasksList().isEmpty()) {
            sb.append("No tasks added yet.");
        } else {
            
            for (Task task : calendar.getTasksList()) {
                System.out.println("DEBUG: Task - Name: '" + task.getTaskName() + "', Due: " + task.getDueDate());
                sb.append("- ").append(task.getTaskName()).append(" - Due: ").append(task.getDueDate()).append("\n");
            }
        }
        // Open and refresh the dedicated Tasks full-page view
        if (tasksPageWindow == null) tasksPageWindow = new TasksPage(calendar);
        tasksPageWindow.refresh();
        tasksPageWindow.setVisible(true);
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
        dialogMainPanel.setBackground(ThemeManager.getBackgroundColor());
        
        // Type selection
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.setBackground(ThemeManager.getPanelBackground());
        JLabel typeLabel = new JLabel("Select type:");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        typeLabel.setForeground(ThemeManager.getTextColor());
        
        String[] types = {"Task", "Event"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setBackground(ThemeManager.getPanelBackground());
        typeCombo.setForeground(ThemeManager.getTextColor());
        typeCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        // renderer to keep combo readable in dark mode
        typeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setBackground(isSelected ? ThemeManager.getHeaderBackground() : ThemeManager.getPanelBackground());
                c.setForeground(ThemeManager.getTextColor());
                return c;
            }
        });
        
        typePanel.add(typeLabel);
        typePanel.add(typeCombo);
        
        // Item list
        JPanel listPanel = new JPanel(new BorderLayout(5, 5));
        listPanel.setBackground(ThemeManager.getPanelBackground());
        JLabel listLabel = new JLabel("Select item to remove:");
        listLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        listLabel.setForeground(ThemeManager.getTextColor());
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> itemList = new JList<>(listModel);
        itemList.setFont(new Font("Arial", Font.PLAIN, 12));
        itemList.setBackground(ThemeManager.getPanelBackground());
        itemList.setForeground(ThemeManager.getTextColor());
        itemList.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));
        itemList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setBackground(isSelected ? ThemeManager.getHeaderBackground() : ThemeManager.getPanelBackground());
                c.setForeground(ThemeManager.getTextColor());
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(itemList);
        scrollPane.getViewport().setBackground(ThemeManager.getPanelBackground());
        scrollPane.getVerticalScrollBar().setBackground(ThemeManager.getPanelBackground());
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
        dialogButtonPanel.setBackground(ThemeManager.getPanelBackground());
        
        JButton removeBtn = new JButton("Remove");
        removeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        removeBtn.setBackground(new Color(220, 80, 80));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFocusPainted(false);
        removeBtn.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 12));
        cancelBtn.setBackground(new Color(150, 150, 150));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));
        
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
            
            // No sound: removed per user request

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

        // Apply initial theme to the dialog and register a listener to update dynamically
        SwingUtilities.invokeLater(() -> {
            removeDialog.getContentPane().setBackground(ThemeManager.getBackgroundColor());
            typePanel.setBackground(ThemeManager.getPanelBackground());
            listPanel.setBackground(ThemeManager.getPanelBackground());
            listLabel.setForeground(ThemeManager.getTextColor());
            itemList.setBackground(ThemeManager.getPanelBackground());
            itemList.setForeground(ThemeManager.getTextColor());
            dialogButtonPanel.setBackground(ThemeManager.getPanelBackground());
        });

        final ThemeManager.ThemeChangeListener _removeDialogThemeListener = new ThemeManager.ThemeChangeListener() {
            public void onThemeChanged(ThemeManager.Theme newTheme) {
                SwingUtilities.invokeLater(() -> {
                    removeDialog.getContentPane().setBackground(ThemeManager.getBackgroundColor());
                    typePanel.setBackground(ThemeManager.getPanelBackground());
                    listPanel.setBackground(ThemeManager.getPanelBackground());
                    listLabel.setForeground(ThemeManager.getTextColor());
                    itemList.setBackground(ThemeManager.getPanelBackground());
                    itemList.setForeground(ThemeManager.getTextColor());
                    dialogButtonPanel.setBackground(ThemeManager.getPanelBackground());
                    removeDialog.repaint();
                });
            }
        };
        ThemeManager.addListener(_removeDialogThemeListener);
        removeDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                ThemeManager.removeListener(_removeDialogThemeListener);
            }
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                ThemeManager.removeListener(_removeDialogThemeListener);
            }
        });
        
        // Populate initial list with tasks
        for (Task task : calendar.getTasksList()) {
            listModel.addElement(task.getTaskName());
        }
        
        removeDialog.setVisible(true);
    }

    // Create a month-view calendar panel
    private void createCalendarPanel() {
        calendarPanel = new JPanel(new BorderLayout(8, 8));
        calendarPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            "Month View",
            0, 0,
            new Font("Arial", Font.BOLD, 12),
            new Color(50, 100, 150)
        ));
        calendarPanel.setBackground(new Color(245, 250, 255));

        JPanel nav = new JPanel(new BorderLayout(5, 0));
        nav.setBackground(new Color(245, 250, 255));
        JButton prev = new JButton("Back");
        JButton next = new JButton("Next");
        weekLabel = new JLabel("", SwingConstants.CENTER);
        weekLabel.setFont(new Font("Arial", Font.BOLD, 13));
        weekLabel.setForeground(new Color(50, 100, 150));
        
        nav.add(prev, BorderLayout.WEST);
        nav.add(weekLabel, BorderLayout.CENTER);
        nav.add(next, BorderLayout.EAST);

        // Style navigation buttons
        styleButton(prev, new Color(100, 150, 200));
        styleButton(next, new Color(100, 150, 200));

        prev.addActionListener(e -> {
            currentWeekStart = currentWeekStart.minusWeeks(1);
            refreshCalendar();
        });
        next.addActionListener(e -> {
            currentWeekStart = currentWeekStart.plusWeeks(1);
            refreshCalendar();
        });

        calendarPanel.add(nav, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 7, 5, 5));
        grid.setBackground(new Color(245, 250, 255));
        grid.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Add day panels with expanded content
        String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = currentWeekStart.plusDays(i);
            
            JPanel dayPanel = new JPanel(new BorderLayout());
            dayPanel.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            dayPanel.setBackground(Color.WHITE);

            // Date header
            JPanel datePanel = new JPanel();
            datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));
            datePanel.setOpaque(true);
            
                    JLabel dayNameLabel = new JLabel(dayNames[i], SwingConstants.CENTER);
                    dayNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            dayNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel dateLabel = new JLabel(String.valueOf(currentDate.getDayOfMonth()), SwingConstants.CENTER);
            // Make the date number larger for better readability
            dateLabel.setFont(new Font("Arial", Font.BOLD, 16));
            dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Highlight today
            if (currentDate.equals(LocalDate.now())) {
                datePanel.setBackground(new Color(220, 240, 255));
            } else {
                datePanel.setBackground(new Color(240, 240, 240));
            }
            
            datePanel.add(dayNameLabel);
            datePanel.add(dateLabel);
            datePanel.setBorder(BorderFactory.createEmptyBorder(3, 2, 3, 2));
            dayPanel.add(datePanel, BorderLayout.NORTH);

            // Content area for tasks and events
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(4, 3, 4, 3));
            
            List<Task> tasks = new ArrayList<>(calendar.getTasksOn(currentDate));
            // Tasks are already sorted by due date, then by priority in MyCalendar.getTasksOn()
            List<Event> events = calendar.getEventsOn(currentDate);
            
            // Display tasks
            if (!tasks.isEmpty()) {
                for (Task t : tasks) {
                    final Task taskRef = t;
                    String name = t.getTaskName();
                    if (name.length() > 12) {
                        name = name.substring(0, 10) + "...";
                    }
                    JLabel taskLabel = new JLabel("- " + name);
                    taskLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                    taskLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    taskLabel.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
                    taskLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    taskLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            DetailPage dp = new DetailPage(taskRef);
                            dp.setVisible(true);
                        }
                    });
                    contentPanel.add(taskLabel);
                }
            }
            
            // Display events
            if (!events.isEmpty()) {
                if (!tasks.isEmpty()) {
                    contentPanel.add(Box.createVerticalStrut(1));
                }
                for (Event e : events) {
                    final Event eventRef = e;
                    String name = e.getEventName();
                    if (name.length() > 12) {
                        name = name.substring(0, 10) + "...";
                    }
                    
                    JLabel eventLabel = new JLabel("- " + name);
                    eventLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                    eventLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    eventLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 1));
                    eventLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    eventLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            DetailPage dp = new DetailPage(eventRef);
                            dp.setVisible(true);
                        }
                    });
                    contentPanel.add(eventLabel);
                }
            }
            
            contentPanel.add(Box.createVerticalGlue());
            
            JScrollPane scrollPane = new JScrollPane(contentPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
            dayPanel.add(scrollPane, BorderLayout.CENTER);

            grid.add(dayPanel);
        }

        calendarPanel.add(grid, BorderLayout.CENTER);
    }

    // Refresh the calendar UI for the current week
    private void refreshCalendar() {
        weekLabel.setText("Week of " + currentWeekStart + " to " + currentWeekStart.plusDays(6));
        
        // Refresh the grid display
        JPanel grid = (JPanel) calendarPanel.getComponent(1);
        grid.removeAll();
        
        String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = currentWeekStart.plusDays(i);
            
            JPanel dayPanel = new JPanel(new BorderLayout());
            dayPanel.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            dayPanel.setBackground(Color.WHITE);

            // Date header
            JPanel datePanel = new JPanel();
            datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));
            datePanel.setOpaque(true);
            
                    JLabel dayNameLabel = new JLabel(dayNames[i], SwingConstants.CENTER);
                    dayNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            dayNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel dateLabel = new JLabel(String.valueOf(currentDate.getDayOfMonth()), SwingConstants.CENTER);
            // Make the date number larger for better readability
            dateLabel.setFont(new Font("Arial", Font.BOLD, 16));
            dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Highlight today
            if (currentDate.equals(LocalDate.now())) {
                datePanel.setBackground(new Color(220, 240, 255));
            } else {
                datePanel.setBackground(new Color(240, 240, 240));
            }
            
            datePanel.add(dayNameLabel);
            datePanel.add(dateLabel);
            datePanel.setBorder(BorderFactory.createEmptyBorder(3, 2, 3, 2));
            dayPanel.add(datePanel, BorderLayout.NORTH);

            // Content area
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(4, 3, 4, 3));
            
            List<Task> tasks = new ArrayList<>(calendar.getTasksOn(currentDate));
            // Tasks are already sorted by due date, then by priority in MyCalendar.getTasksOn()
            List<Event> events = calendar.getEventsOn(currentDate);
            
            // Display tasks
            if (!tasks.isEmpty()) {
                for (Task t : tasks) {
                    final Task taskRef = t;
                    String name = t.getTaskName();
                    if (name.length() > 12) {
                        name = name.substring(0, 10) + "...";
                    }
                    JLabel taskLabel = new JLabel("- " + name);
                    taskLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                    taskLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    taskLabel.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
                    taskLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    taskLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            DetailPage dp = new DetailPage(taskRef);
                            dp.setVisible(true);
                        }
                    });
                    contentPanel.add(taskLabel);
                }
            }
            
            // Display events
            if (!events.isEmpty()) {
                if (!tasks.isEmpty()) {
                    contentPanel.add(Box.createVerticalStrut(1));
                }
                for (Event e : events) {
                    final Event eventRef = e;
                    String name = e.getEventName();
                    if (name.length() > 12) {
                        name = name.substring(0, 10) + "...";
                    }
                    
                    JLabel eventLabel = new JLabel("- " + name);
                    eventLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                    eventLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    eventLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 1));
                    eventLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    eventLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            DetailPage dp = new DetailPage(eventRef);
                            dp.setVisible(true);
                        }
                    });
                    contentPanel.add(eventLabel);
                }
            }
            
            contentPanel.add(Box.createVerticalGlue());
            
            JScrollPane scrollPane = new JScrollPane(contentPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
            dayPanel.add(scrollPane, BorderLayout.CENTER);

            grid.add(dayPanel);
        }
        
        grid.revalidate();
        grid.repaint();
    }


    // Display all events
    private void showAllEvents() {
        // Open and refresh the dedicated Events full-page view
        if (eventsPageWindow == null) eventsPageWindow = new EventsPage(calendar);
        eventsPageWindow.refresh();
        eventsPageWindow.setVisible(true);
    }

    // Apply theme colors to all components recursively
    private void applyTheme() {
        applyThemeToComponent(this);
        mainPanel.setBackground(ThemeManager.getBackgroundColor());
        buttonPanel.setBackground(ThemeManager.getBackgroundColor());
        refreshCalendar();
        repaint();
    }

    // Recursively apply theme to component and children
    private void applyThemeToComponent(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(ThemeManager.getBackgroundColor());
                applyThemeToComponent((Container) comp);
            } else if (comp instanceof JTextArea) {
                comp.setBackground(ThemeManager.getPanelBackground());
                comp.setForeground(ThemeManager.getTextColor());
                applyThemeToComponent((Container) comp);
            } else if (comp instanceof Container) {
                applyThemeToComponent((Container) comp);
            }
        }
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TaskGUI());
    }
}