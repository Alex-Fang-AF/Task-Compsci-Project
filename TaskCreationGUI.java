import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TaskCreationGUI extends JDialog {
    private final MyCalendar calendar;
    private boolean isTaskMode = true;
    private JButton toggleModeButton;
    private JTextField taskNameField;
    private JTextField taskDueDateField;
    private JTextField eventNameField;
    private JTextField eventStartDateField;
    private JTextField eventEndDateField;
    private JButton createButton;
    private JButton cancelButton;
    private boolean itemCreated = false;
    
    // Panels for mode switching
    private JPanel taskPanel;
    private JPanel eventPanel;
    private CardLayout cardLayout;
    private JPanel inputContainer;

    public TaskCreationGUI(Frame parent, MyCalendar calendar) {
        super(parent, "Create Task or Event", true);
        this.calendar = calendar;
        initializeDialog();
    }

    private void initializeDialog() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 250, 255));

        // Title
        JLabel titleLabel = new JLabel("Create New Item");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(50, 100, 150));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Input panel container with CardLayout
        cardLayout = new CardLayout();
        inputContainer = new JPanel(cardLayout);
        inputContainer.setBackground(new Color(245, 250, 255));
        
        taskPanel = createTaskPanel();
        eventPanel = createEventPanel();
        
        inputContainer.add(taskPanel, "task");
        inputContainer.add(eventPanel, "event");
        
        JPanel modePanel = new JPanel(new BorderLayout());
        modePanel.setBackground(new Color(245, 250, 255));
        
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
    }

    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 12));
        panel.setBackground(new Color(245, 250, 255));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            "Task Details",
            0, 0,
            new Font("Arial", Font.BOLD, 11),
            new Color(50, 100, 150)
        ));

        // Name field
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 11));
        taskNameField = new JTextField();
        styleTextField(taskNameField);
        panel.add(nameLabel);
        panel.add(taskNameField);

        // Due date field
        JLabel dueDateLabel = new JLabel("Due Date (yyyy-MM-dd):");
        dueDateLabel.setFont(new Font("Arial", Font.BOLD, 11));
        taskDueDateField = new JTextField();
        styleTextField(taskDueDateField);
        panel.add(dueDateLabel);
        panel.add(taskDueDateField);

        return panel;
    }

    private JPanel createEventPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 15, 12));
        panel.setBackground(new Color(245, 250, 255));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            "Event Details",
            0, 0,
            new Font("Arial", Font.BOLD, 11),
            new Color(50, 100, 150)
        ));

        // Name field
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 11));
        eventNameField = new JTextField();
        styleTextField(eventNameField);
        panel.add(nameLabel);
        panel.add(eventNameField);

        // Start date field
        JLabel startLabel = new JLabel("Start Date (yyyy-MM-dd):");
        startLabel.setFont(new Font("Arial", Font.BOLD, 11));
        eventStartDateField = new JTextField();
        styleTextField(eventStartDateField);
        panel.add(startLabel);
        panel.add(eventStartDateField);

        // End date field
        JLabel endLabel = new JLabel("End Date (yyyy-MM-dd):");
        endLabel.setFont(new Font("Arial", Font.BOLD, 11));
        eventEndDateField = new JTextField();
        styleTextField(eventEndDateField);
        panel.add(endLabel);
        panel.add(eventEndDateField);

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

        createButton.addActionListener(e -> createItem());
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
                LocalDate dueDate = LocalDate.parse(dueDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                Task task = new Task(name, dueDate);
                System.out.println("DEBUG: Creating task - Name: '" + name + "', Due Date: " + dueDate);
                System.out.println("DEBUG: Task object - Name: '" + task.getTaskName() + "'");
                calendar.addTask(task);
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
                LocalDate startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                Event event = new Event(name, startDate, endDate);
                calendar.addEvent(event);
                itemCreated = true;
                JOptionPane.showMessageDialog(this, "Event created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            clearFields();
            dispose();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd.", "Date Error", JOptionPane.ERROR_MESSAGE);
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
}
