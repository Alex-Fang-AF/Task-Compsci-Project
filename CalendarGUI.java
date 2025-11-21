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
                StringBuilder html = new StringBuilder("<html><div style='padding:4px;'>");
                html.append("<div style='font-weight:bold;text-align:left;'>").append(dayNumber).append("</div>");

                List<Task> tasks = calendar.getTasksOn(d);
                int shown = 0;
                for (Task t : tasks) {
                    if (shown >= 3) break; // show up to 3 task names
                    String name = t.getTaskName();
                    name = name.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
                    html.append("<div style='font-size:10px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;'>").append(name).append("</div>");
                    shown++;
                }
                if (tasks.size() > shown) {
                    html.append("<div style='font-size:10px;color:gray;'>+").append(tasks.size() - shown).append(" more</div>");
                }
                
                // Add events after tasks
                List<Event> events = calendar.getEventsOn(d);
                for (Event e : events) {
                    String name = e.getEventName();
                    name = name.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
                    html.append("<div style='font-size:9px;color:purple;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;'>◆ ").append(name).append("</div>");
                }
                
                html.append("</div></html>");

                JLabel content = new JLabel(html.toString());
                content.setOpaque(true);
                content.setBackground(Color.WHITE);
                cell.add(content, BorderLayout.CENTER);

                // Highlight today
                if (d.equals(LocalDate.now())) {
                    cell.setBackground(new Color(220, 240, 255));
                    content.setBackground(new Color(220, 240, 255));
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

