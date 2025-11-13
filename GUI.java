import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TaskGUI extends JFrame {
    private MyCalendar calendar;
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

    // Constructor
    public TaskGUI() {
        calendar = new MyCalendar();
        initializeFrame();
        createPanels();
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

        buttonPanel.add(addTaskButton);
        buttonPanel.add(showTasksButton);
        buttonPanel.add(removeTaskButton);
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
            } else {
                JOptionPane.showMessageDialog(this, "Task not found.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
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
