import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TaskCreationGUI extends JDialog {
    private final MyCalendar calendar;
    private boolean isTaskMode = true;
    private JButton toggleModeButton;
    private JTextField taskNameField;
    private JTextField taskDueDateField;
    private JComboBox<String> taskPriorityCombo;
    private JTextArea taskDescriptionArea;
    private JCheckBox alarmCheckBox;
    private JComboBox<String> alarmRepeatCombo;
    private JComboBox<Integer> hourCombo;
    private JComboBox<Integer> minuteCombo;
    private JTextField eventNameField;
    private JTextField eventStartDateField;
    private JTextField eventEndDateField;
    private JTextArea eventDescriptionArea;
    private JButton createButton;
    private JButton cancelButton;
    private boolean itemCreated = false;
    
    // Display formatter (used when showing dates in fields)
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        .withResolverStyle(ResolverStyle.STRICT);
    // List of accepted date input formats
    private static final List<DateTimeFormatter> PARSE_FORMATTERS = new ArrayList<>();
    static {
        PARSE_FORMATTERS.add(DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT));
        PARSE_FORMATTERS.add(DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT));
        PARSE_FORMATTERS.add(DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT));
        PARSE_FORMATTERS.add(DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT));
    }

    // Helper to parse a date string using the list of parsers above
    private LocalDate parseDate(String dateStr) throws DateTimeParseException {
        DateTimeParseException lastEx = null;
        for (DateTimeFormatter fmt : PARSE_FORMATTERS) {
            try {
                return LocalDate.parse(dateStr, fmt);
            } catch (DateTimeParseException ex) {
                lastEx = ex;
            }
        }
        // If none matched, throw the last exception
        throw lastEx != null ? lastEx : new DateTimeParseException("Unparseable date", dateStr, 0);
    }
    
    // Panels for mode switching
    private JPanel taskPanel;
    private JPanel eventPanel;
    private CardLayout cardLayout;
    private JPanel inputContainer;

    public TaskCreationGUI(Frame parent, MyCalendar calendar) {
        super(parent, "Create Task or Event", true);
        this.calendar = calendar;
        initializeDialog();
        // Update theme when it changes
        ThemeManager.addListener(new ThemeManager.ThemeChangeListener() {
            public void onThemeChanged(ThemeManager.Theme newTheme) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        applyTheme();
                    }
                });
            }
        });
    }

    private void initializeDialog() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // Increased size to better accommodate description fields and allow user resizing
        setSize(700, 520);
        setLocationRelativeTo(getParent());
        setResizable(true);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(ThemeManager.getBackgroundColor());

        // Title
        JLabel titleLabel = new JLabel("Create New Item");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 100, 150));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Input panel container with CardLayout
        cardLayout = new CardLayout();
        inputContainer = new JPanel(cardLayout);
        inputContainer.setBackground(ThemeManager.getPanelBackground());
        
        taskPanel = createTaskPanel();
        eventPanel = createEventPanel();
        
        inputContainer.add(taskPanel, "task");
        inputContainer.add(eventPanel, "event");
        
        JPanel modePanel = new JPanel(new BorderLayout());
        modePanel.setBackground(ThemeManager.getPanelBackground());
        
        // Toggle button at top
        toggleModeButton = new JButton("Switch to Event Mode");
        toggleModeButton.setFont(new Font("Arial", Font.BOLD, 10));
        toggleModeButton.addActionListener(e -> toggleMode());
        styleButton(toggleModeButton, new Color(156, 39, 176));
        modePanel.add(toggleModeButton, BorderLayout.NORTH);
        
        modePanel.add(inputContainer, BorderLayout.CENTER);
        mainPanel.add(modePanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        // Apply theme right away so the dialog matches the program theme on open
        SwingUtilities.invokeLater(() -> applyTheme());
    }

    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.getPanelBackground());
        javax.swing.border.TitledBorder tb = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 2),
            "Task Details"
        );
        tb.setTitleFont(new Font("Arial", Font.BOLD, 11));
        tb.setTitleColor(ThemeManager.getTextColor());
        panel.setBorder(tb);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Task Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(nameLabel, gbc);

        taskNameField = new JTextField(20);
        taskNameField.setToolTipText("Enter a short descriptive task name");
        styleTextField(taskNameField);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(taskNameField, gbc);

        // Due date field with picker button
        JLabel dueDateLabel = new JLabel("Due Date (dd/MM/yyyy):");
        dueDateLabel.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(dueDateLabel, gbc);

        JPanel dueDatePanel = new JPanel(new BorderLayout(5, 0));
        dueDatePanel.setBackground(ThemeManager.getPanelBackground());
        
        taskDueDateField = new JTextField(15);
        taskDueDateField.setToolTipText("Accepted formats: dd/MM/yyyy, d/M/yyyy, MM/dd/yyyy, yyyy-MM-dd");
        styleTextField(taskDueDateField);
        dueDatePanel.add(taskDueDateField, BorderLayout.CENTER);

        JButton taskDatePickerBtn = new JButton("Pick");
        taskDatePickerBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        taskDatePickerBtn.setPreferredSize(new Dimension(35, 30));
        taskDatePickerBtn.setBackground(ThemeManager.getHeaderBackground());
        taskDatePickerBtn.setForeground(ThemeManager.getTextColor());
        taskDatePickerBtn.setFocusPainted(false);
        taskDatePickerBtn.addActionListener(e -> openTaskDatePicker());
        dueDatePanel.add(taskDatePickerBtn, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(dueDatePanel, gbc);

        // Priority selector
        JLabel priorityLabel = new JLabel("Priority:");
        priorityLabel.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(priorityLabel, gbc);

        String[] priorities = {"High", "Moderate", "Low"};
        taskPriorityCombo = new JComboBox<>(priorities);
        taskPriorityCombo.setSelectedItem("Moderate");
        taskPriorityCombo.setFont(new Font("Arial", Font.PLAIN, 11));
        taskPriorityCombo.setToolTipText("Select the urgency of this task (High shown first in calendar)");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(taskPriorityCombo, gbc);

        // Description field (optional)
        JLabel descLabel = new JLabel("Description (optional):");
        descLabel.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(descLabel, gbc);

        taskDescriptionArea = new JTextArea(4, 24);
        taskDescriptionArea.setLineWrap(true);
        taskDescriptionArea.setWrapStyleWord(true);
        taskDescriptionArea.setFont(new Font("Arial", Font.PLAIN, 11));
        taskDescriptionArea.setToolTipText("Optional: add details, location, or notes about the task");
        taskDescriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        JScrollPane taskDescScroll = new JScrollPane(taskDescriptionArea);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(taskDescScroll, gbc);

        // Alarm controls (optional)
        JLabel alarmLabel = new JLabel("Alarm:");
        alarmLabel.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(alarmLabel, gbc);

        JPanel alarmPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        alarmPanel.setBackground(ThemeManager.getPanelBackground());
        alarmCheckBox = new JCheckBox("Set reminder alarm");
        alarmCheckBox.setBackground(ThemeManager.getPanelBackground());
        alarmCheckBox.setForeground(ThemeManager.getTextColor());
        // Build repeat interval options from 0 minutes to 12 hours in 30-minute steps
        java.util.List<String> repeatsList = new java.util.ArrayList<>();
        repeatsList.add("0 minutes");
        for (int mins = 30; mins <= 12 * 60; mins += 30) {
            repeatsList.add(mins + " minutes");
        }
        String[] repeats = repeatsList.toArray(new String[repeatsList.size()]);
        alarmRepeatCombo = new JComboBox<>(repeats);
        alarmRepeatCombo.setSelectedIndex(1); // default to 30 minutes
        alarmRepeatCombo.setBackground(ThemeManager.getPanelBackground());
        alarmRepeatCombo.setForeground(ThemeManager.getTextColor());
        alarmRepeatCombo.setFont(new Font("Arial", Font.PLAIN, 11));

        // Hour/minute selectors for time-of-day alarm
        Integer[] hours = new Integer[24];
        for (int i = 0; i < 24; i++) hours[i] = i;
        hourCombo = new JComboBox<>(hours);
        hourCombo.setSelectedItem(java.time.LocalTime.now().getHour());
        hourCombo.setBackground(ThemeManager.getPanelBackground());
        hourCombo.setForeground(ThemeManager.getTextColor());

        Integer[] mins = new Integer[60];
        for (int i = 0; i < 60; i++) mins[i] = i;
        minuteCombo = new JComboBox<>(mins);
        minuteCombo.setSelectedItem(java.time.LocalTime.now().getMinute());
        minuteCombo.setBackground(ThemeManager.getPanelBackground());
        minuteCombo.setForeground(ThemeManager.getTextColor());

        alarmPanel.add(alarmCheckBox);
        alarmPanel.add(alarmRepeatCombo);
        alarmPanel.add(new JLabel("Hour:"));
        alarmPanel.add(hourCombo);
        alarmPanel.add(new JLabel("Min:"));
        alarmPanel.add(minuteCombo);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(alarmPanel, gbc);

        return panel;
    }

    private JPanel createEventPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.getPanelBackground());
        javax.swing.border.TitledBorder etb = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 2),
            "Event Details"
        );
        etb.setTitleFont(new Font("Arial", Font.BOLD, 11));
        etb.setTitleColor(ThemeManager.getTextColor());
        panel.setBorder(etb);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Name field
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(nameLabel, gbc);

        eventNameField = new JTextField(20);
        styleTextField(eventNameField);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(eventNameField, gbc);

        // Start date field with picker button
        JLabel startLabel = new JLabel("Start Date:");
        startLabel.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(startLabel, gbc);

        JPanel startDatePanel = new JPanel(new BorderLayout(5, 0));
        startDatePanel.setBackground(ThemeManager.getPanelBackground());
        
        eventStartDateField = new JTextField(15);
        styleTextField(eventStartDateField);
        startDatePanel.add(eventStartDateField, BorderLayout.CENTER);

        JButton eventStartDatePickerBtn = new JButton("Pick");
        eventStartDatePickerBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        eventStartDatePickerBtn.setPreferredSize(new Dimension(35, 30));
        eventStartDatePickerBtn.setBackground(ThemeManager.getHeaderBackground());
        eventStartDatePickerBtn.setForeground(ThemeManager.getTextColor());
        eventStartDatePickerBtn.setFocusPainted(false);
        eventStartDatePickerBtn.addActionListener(e -> openEventStartDatePicker());
        startDatePanel.add(eventStartDatePickerBtn, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(startDatePanel, gbc);

        // End date field with picker button
        JLabel endLabel = new JLabel("End Date:");
        endLabel.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(endLabel, gbc);

        JPanel endDatePanel = new JPanel(new BorderLayout(5, 0));
        endDatePanel.setBackground(new Color(245, 250, 255));
        
        eventEndDateField = new JTextField(15);
        styleTextField(eventEndDateField);
        endDatePanel.add(eventEndDateField, BorderLayout.CENTER);

        JButton eventEndDatePickerBtn = new JButton("Pick");
        eventEndDatePickerBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        eventEndDatePickerBtn.setPreferredSize(new Dimension(35, 30));
        eventEndDatePickerBtn.setBackground(new Color(100, 200, 255));
        eventEndDatePickerBtn.setForeground(Color.WHITE);
        eventEndDatePickerBtn.setFocusPainted(false);
        eventEndDatePickerBtn.addActionListener(e -> openEventEndDatePicker());
        endDatePanel.add(eventEndDatePickerBtn, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(endDatePanel, gbc);

        // Description for event
        JLabel descLabel = new JLabel("Description (optional):");
        descLabel.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(descLabel, gbc);

        eventDescriptionArea = new JTextArea(4, 24);
        eventDescriptionArea.setLineWrap(true);
        eventDescriptionArea.setWrapStyleWord(true);
        eventDescriptionArea.setFont(new Font("Arial", Font.PLAIN, 11));
        eventDescriptionArea.setToolTipText("Optional: add details or notes about the event");
        eventDescriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        JScrollPane eventDescScroll = new JScrollPane(eventDescriptionArea);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(eventDescScroll, gbc);

        return panel;
    }

    private void toggleMode() {
        isTaskMode = !isTaskMode;
        toggleModeButton.setText(isTaskMode ? "Switch to Event Mode" : "Switch to Task Mode");
        
        if (isTaskMode) {
            cardLayout.show(inputContainer, "task");
        } else {
            cardLayout.show(inputContainer, "event");
        }
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(245, 250, 255));

        createButton = new JButton("Create");
        cancelButton = new JButton("Cancel");

        createButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));

        createButton.addActionListener(e -> {
            // Play a short bell to indicate the Create button was pressed
            try {
                SoundPlayer.playBell();
            } catch (Throwable t) {
                // ignore sound failures
            }
            createItem();
        });
        cancelButton.addActionListener(e -> dispose());

        styleButton(createButton, new Color(76, 175, 80));
        styleButton(cancelButton, new Color(244, 67, 54));

        panel.add(createButton);
        panel.add(cancelButton);
        return panel;
    }

    private void createItem() {
        System.out.println("DEBUG: createItem called, isTaskMode: " + isTaskMode);
        System.out.println("DEBUG: taskNameField: " + taskNameField);
        System.out.println("DEBUG: taskDueDateField: " + taskDueDateField);
        
        String name = isTaskMode ? taskNameField.getText().trim() : eventNameField.getText().trim();
        System.out.println("DEBUG: Name input: '" + name + "'");

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (isTaskMode) {
                String dueDateStr = taskDueDateField.getText().trim();
                if (dueDateStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a due date.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                LocalDate dueDate = parseDate(dueDateStr);
                // Determine priority from combo box
                Task.TaskPriority priority = Task.TaskPriority.MEDIUM;
                if (taskPriorityCombo != null) {
                    Object sel = taskPriorityCombo.getSelectedItem();
                    if (sel != null) {
                        String selStr = sel.toString().toLowerCase();
                        if (selStr.contains("high")) {
                            priority = Task.TaskPriority.HIGH;
                        } else if (selStr.contains("low")) {
                            priority = Task.TaskPriority.LOW;
                        } else {
                            priority = Task.TaskPriority.MEDIUM;
                        }
                    }
                }
                String taskDesc = "";
                if (taskDescriptionArea != null) taskDesc = taskDescriptionArea.getText().trim();
                Task task = new Task(name, dueDate, priority, taskDesc);
                System.out.println("DEBUG: Creating task - Name: '" + name + "', Due Date: " + dueDate);
                System.out.println("DEBUG: Task object - Name: '" + task.getTaskName() + "'");
                calendar.addTask(task);
                // If user selected an alarm, schedule it using the inline repeat combo (no modal)
                if (alarmCheckBox != null && alarmCheckBox.isSelected()) {
                    int chosenMinutes = 30;
                    try {
                        if (alarmRepeatCombo != null && alarmRepeatCombo.getSelectedItem() != null) {
                            String s = alarmRepeatCombo.getSelectedItem().toString();
                            String digits = s.replaceAll("\\D+", "");
                            if (!digits.isEmpty()) chosenMinutes = Integer.parseInt(digits);
                        }
                    } catch (Throwable t) {
                        chosenMinutes = 30;
                    }

                    int hr = 9;
                    int min = 0;
                    if (hourCombo != null && minuteCombo != null) {
                        Object hsel = hourCombo.getSelectedItem();
                        Object msel = minuteCombo.getSelectedItem();
                        if (hsel instanceof Integer) hr = (Integer) hsel;
                        if (msel instanceof Integer) min = (Integer) msel;
                    }
                    java.time.LocalTime timeOfDay = java.time.LocalTime.of(hr, min);
                    AlarmManager.scheduleAlarm(task, chosenMinutes, timeOfDay);
                }
                System.out.println("DEBUG: Tasks in calendar after adding: " + calendar.getTasksList().size());
                itemCreated = true;
                JOptionPane.showMessageDialog(this, "Task created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String startDateStr = eventStartDateField.getText().trim();
                String endDateStr = eventEndDateField.getText().trim();
                if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter both start and end dates.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                LocalDate startDate = parseDate(startDateStr);
                LocalDate endDate = parseDate(endDateStr);
                String eventDesc = "";
                if (eventDescriptionArea != null) eventDesc = eventDescriptionArea.getText().trim();
                Event event = new Event(name, startDate, endDate, eventDesc);
                calendar.addEvent(event);
                itemCreated = true;
                JOptionPane.showMessageDialog(this, "Event created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            clearFields();
            dispose();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Accepted formats: dd/MM/yyyy, d/M/yyyy, MM/dd/yyyy, or yyyy-MM-dd (e.g., 25/12/2025).", "Date Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Date Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        if (taskNameField != null) taskNameField.setText("");
        if (taskDueDateField != null) taskDueDateField.setText("");
        if (eventNameField != null) eventNameField.setText("");
        if (eventStartDateField != null) eventStartDateField.setText("");
        if (eventEndDateField != null) eventEndDateField.setText("");
        if (taskDescriptionArea != null) taskDescriptionArea.setText("");
        if (eventDescriptionArea != null) eventDescriptionArea.setText("");
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 11));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 200), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.setBackground(new Color(255, 255, 255));
        field.setCaretColor(new Color(50, 100, 150));
    }

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
        button.setOpaque(true);

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

    // Apply current theme to all components inside this dialog
    public void applyTheme() {
        Container root = this.getContentPane();
        applyThemeToComponent(root, true);
        SwingUtilities.updateComponentTreeUI(this);
        this.repaint();
    }

    private void applyThemeToComponent(Component comp, boolean isRoot) {
        if (comp == null) return;
        if (comp instanceof JComponent) {
            if (isRoot) {
                comp.setBackground(ThemeManager.getBackgroundColor());
            } else {
                comp.setBackground(ThemeManager.getPanelBackground());
            }
        }

        if (comp instanceof JLabel) {
            ((JLabel)comp).setForeground(ThemeManager.getTextColor());
        } else if (comp instanceof JTextField || comp instanceof JComboBox || comp instanceof JTextArea) {
            comp.setBackground(ThemeManager.getPanelBackground());
            comp.setForeground(ThemeManager.getTextColor());
        } else if (comp instanceof JButton) {
            ((JButton)comp).setForeground(ThemeManager.getTextColor());
        }

        // Update borders that are line borders to match theme
        if (comp instanceof JComponent) {
            javax.swing.border.Border b = ((JComponent)comp).getBorder();
            if (b instanceof javax.swing.border.LineBorder) {
                ((JComponent)comp).setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));
            }
        }

        if (comp instanceof Container) {
            for (Component child : ((Container)comp).getComponents()) {
                applyThemeToComponent(child, false);
            }
        }
    }

    public boolean isItemCreated() {
        return itemCreated;
    }

    public void setEventMode() {
        if (isTaskMode) {
            // Switch to event mode - will be applied when CardLayout is available
            SwingUtilities.invokeLater(() -> {
                isTaskMode = false;
                toggleModeButton.setText("Switch to Task Mode");
                cardLayout.show(inputContainer, "event");
            });
        }
    }

    // Open date picker for task due date
    private void openTaskDatePicker() {
        LocalDate initialDate = LocalDate.now();
        try {
            String currentText = taskDueDateField.getText().trim();
            if (!currentText.isEmpty()) {
                initialDate = parseDate(currentText);
            }
        } catch (DateTimeParseException e) {
            // Use today's date if current text is invalid
        }
        
        DatePickerDialog picker = new DatePickerDialog(this, initialDate, selectedDate -> {
            taskDueDateField.setText(selectedDate.format(DISPLAY_FORMATTER));
        });
        picker.setVisible(true);
    }

    // Open date picker for event start date
    private void openEventStartDatePicker() {
        LocalDate initialDate = LocalDate.now();
        try {
            String currentText = eventStartDateField.getText().trim();
            if (!currentText.isEmpty()) {
                initialDate = parseDate(currentText);
            }
        } catch (DateTimeParseException e) {
            // Use today's date if current text is invalid
        }
        
        DatePickerDialog picker = new DatePickerDialog(this, initialDate, selectedDate -> {
            eventStartDateField.setText(selectedDate.format(DISPLAY_FORMATTER));
        });
        picker.setVisible(true);
    }

    // Open date picker for event end date
    private void openEventEndDatePicker() {
        LocalDate initialDate = LocalDate.now();
        try {
            String currentText = eventEndDateField.getText().trim();
            if (!currentText.isEmpty()) {
                initialDate = parseDate(currentText);
            }
        } catch (DateTimeParseException e) {
            // Use today's date if current text is invalid
        }
        
        DatePickerDialog picker = new DatePickerDialog(this, initialDate, selectedDate -> {
            eventEndDateField.setText(selectedDate.format(DISPLAY_FORMATTER));
        });
        picker.setVisible(true);
    }
}
