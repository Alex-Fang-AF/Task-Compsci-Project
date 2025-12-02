import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class CalendarGUI extends JFrame {
    private final MyCalendar calendar;
    private YearMonth currentYearMonth;
    private JPanel grid;
    private JLabel monthLabel;

    public CalendarGUI(MyCalendar calendar) {
        this.calendar = calendar;
        this.currentYearMonth = YearMonth.from(calendar.getCurrentDate());
        initializeFrame();
        buildUI();
        SwingUtilities.invokeLater(this::refreshCalendar);
    }

    private void initializeFrame() {
        setTitle("Month Details");
        setSize(420, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setResizable(true);
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout());
        JButton prev = new JButton("<");
        JButton next = new JButton(">");
        monthLabel = new JLabel("", SwingConstants.CENTER);
        top.add(prev, BorderLayout.WEST);
        top.add(monthLabel, BorderLayout.CENTER);
        top.add(next, BorderLayout.EAST);

        prev.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            refreshCalendar();
        });
        next.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            refreshCalendar();
        });

        add(top, BorderLayout.NORTH);

        grid = new JPanel(new GridLayout(7, 7));
        add(grid, BorderLayout.CENTER);
    }

    // Public so TaskGUI can request refresh when tasks change
    public void refreshCalendar() {
        grid.removeAll();
        String[] dayNames = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        for (String dn : dayNames) {
            JLabel lbl = new JLabel(dn, SwingConstants.CENTER);
            lbl.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
            grid.add(lbl);
        }

        monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int startIndex = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday -> 0
        int daysInMonth = currentYearMonth.lengthOfMonth();

        for (int i = 0; i < 42; i++) {
            JPanel cell = new JPanel(new BorderLayout());
            cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            int dayNumber = i - startIndex + 1;
            if (dayNumber >= 1 && dayNumber <= daysInMonth) {
                LocalDate d = currentYearMonth.atDay(dayNumber);

                // Build a vertical panel so each task/event can be its own label and clickable
                JPanel dayContent = new JPanel();
                dayContent.setLayout(new BoxLayout(dayContent, BoxLayout.Y_AXIS));
                dayContent.setOpaque(true);
                dayContent.setBackground(Color.WHITE);
                dayContent.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));

                // Day number label
                JLabel dayNumLabel = new JLabel(String.valueOf(dayNumber));
                dayNumLabel.setFont(new Font("Arial", Font.BOLD, 14));
                dayNumLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                dayContent.add(dayNumLabel);

                // Tasks
                List<Task> tasks = calendar.getTasksOn(d);
                int shown = 0;
                for (Task t : tasks) {
                    if (shown >= 3) break; // show up to 3 task names
                    String name = t.getTaskName();
                    String display = name.length() > 18 ? name.substring(0, 15) + "..." : name;
                    final Task taskRef = t;
                    JLabel taskLabel = new JLabel("- " + display);
                    taskLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                    taskLabel.setForeground(Color.BLACK);
                    taskLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    taskLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    taskLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            DetailPage dp = new DetailPage(taskRef);
                            dp.setVisible(true);
                        }
                    });
                    dayContent.add(taskLabel);
                    shown++;
                }
                if (tasks.size() > shown) {
                    JLabel more = new JLabel("+" + (tasks.size() - shown) + " more");
                    more.setFont(new Font("Arial", Font.PLAIN, 10));
                    more.setForeground(Color.GRAY);
                    more.setAlignmentX(Component.LEFT_ALIGNMENT);
                    dayContent.add(more);
                }

                // Events
                List<Event> events = calendar.getEventsOn(d);
                for (Event ev : events) {
                    String name = ev.getEventName();
                    String display = name.length() > 18 ? name.substring(0, 15) + "..." : name;
                    final Event eventRef = ev;
                    JLabel eventLabel = new JLabel("◆ " + display);
                    eventLabel.setFont(new Font("Arial", Font.PLAIN, 10));
                    eventLabel.setForeground(new Color(128,0,128));
                    eventLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    eventLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    eventLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            DetailPage dp = new DetailPage(eventRef);
                            dp.setVisible(true);
                        }
                    });
                    dayContent.add(eventLabel);
                }

                cell.add(dayContent, BorderLayout.CENTER);
                // Highlight today
                if (d.equals(LocalDate.now())) {
                    cell.setBackground(new Color(220, 240, 255));
                    dayContent.setBackground(new Color(220, 240, 255));
                }
            } else {
                cell.setBackground(Color.WHITE);
            }
            grid.add(cell);
        }

        grid.revalidate();
        grid.repaint();
    }
}

