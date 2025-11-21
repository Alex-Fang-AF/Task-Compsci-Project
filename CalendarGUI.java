import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class CalendarGUI extends JFrame {
    private final MyCalendar calendar;
    private LocalDate currentWeekStart;
    private JPanel grid;
    private JLabel weekLabel;

    public CalendarGUI(MyCalendar calendar) {
        this.calendar = calendar;
        this.currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        initializeFrame();
        buildUI();
        SwingUtilities.invokeLater(this::refreshCalendar);
    }

    private void initializeFrame() {
        setTitle("Week View");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setResizable(true);
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout());
        JButton prev = new JButton("< Previous Week");
        JButton next = new JButton("Next Week >");
        weekLabel = new JLabel("", SwingConstants.CENTER);
        weekLabel.setFont(new Font("Arial", Font.BOLD, 14));
        top.add(prev, BorderLayout.WEST);
        top.add(weekLabel, BorderLayout.CENTER);
        top.add(next, BorderLayout.EAST);

        prev.addActionListener(e -> {
            currentWeekStart = currentWeekStart.minusWeeks(1);
            refreshCalendar();
        });
        next.addActionListener(e -> {
            currentWeekStart = currentWeekStart.plusWeeks(1);
            refreshCalendar();
        });

        add(top, BorderLayout.NORTH);

        grid = new JPanel(new GridLayout(1, 7, 8, 5));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(grid, BorderLayout.CENTER);
    }

    // Public so TaskGUI can request refresh when tasks change
    public void refreshCalendar() {
        grid.removeAll();
        String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        
        // Add day cells with tasks and events
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = currentWeekStart.plusDays(i);
            
            JPanel dayPanel = new JPanel(new BorderLayout());
            dayPanel.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 2));
            dayPanel.setBackground(Color.WHITE);

            // Date header (small)
            JPanel datePanel = new JPanel();
            datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));
            datePanel.setOpaque(true);
            
            JLabel dayNameLabel = new JLabel(dayNames[i], SwingConstants.CENTER);
            dayNameLabel.setFont(new Font("Arial", Font.BOLD, 10));
            dayNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel dateLabel = new JLabel(String.valueOf(currentDate.getDayOfMonth()), SwingConstants.CENTER);
            dateLabel.setFont(new Font("Arial", Font.PLAIN, 9));
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

            // Tasks and events content area
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 4, 5, 4));
            
            List<Task> tasks = calendar.getTasksOn(currentDate);
            List<Event> events = calendar.getEventsOn(currentDate);
            
            // Display tasks (larger)
            if (!tasks.isEmpty()) {
                JLabel taskHeader = new JLabel("Tasks:", SwingConstants.LEFT);
                taskHeader.setFont(new Font("Arial", Font.BOLD, 9));
                taskHeader.setForeground(new Color(0, 100, 0));
                taskHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentPanel.add(taskHeader);
                
                for (Task t : tasks) {
                    String name = t.getTaskName();
                    if (name.length() > 15) {
                        name = name.substring(0, 12) + "...";
                    }
                    name = name.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
                    
                    JLabel taskLabel = new JLabel("• " + name);
                    taskLabel.setFont(new Font("Arial", Font.PLAIN, 8));
                    taskLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    taskLabel.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 2));
                    contentPanel.add(taskLabel);
                }
            }
            
            // Display events (smaller)
            if (!events.isEmpty()) {
                JLabel eventHeader = new JLabel("Events:", SwingConstants.LEFT);
                eventHeader.setFont(new Font("Arial", Font.BOLD, 8));
                eventHeader.setForeground(new Color(100, 0, 100));
                eventHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentPanel.add(Box.createVerticalStrut(2));
                contentPanel.add(eventHeader);
                
                for (Event e : events) {
                    String name = e.getEventName();
                    if (name.length() > 15) {
                        name = name.substring(0, 12) + "...";
                    }
                    name = name.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
                    
                    JLabel eventLabel = new JLabel("◆ " + name);
                    eventLabel.setFont(new Font("Arial", Font.PLAIN, 7));
                    eventLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    eventLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 2));
                    contentPanel.add(eventLabel);
                }
            }
            
            contentPanel.add(Box.createVerticalGlue());
            
            JScrollPane scrollPane = new JScrollPane(contentPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
            dayPanel.add(scrollPane, BorderLayout.CENTER);

            grid.add(dayPanel);
        }

        weekLabel.setText("Week of " + currentWeekStart + " to " + currentWeekStart.plusDays(6));
        grid.revalidate();
        grid.repaint();
    }
}

