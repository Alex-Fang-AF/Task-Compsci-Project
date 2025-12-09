import java.awt.*;
/**
 * CalendarImportGUI.java
 *
 * Calendar import helper UI (legacy; import merged into CalendarGUI).
 */
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CalendarImportGUI extends JFrame {
    private YearMonth currentYearMonth;
    private JPanel calendarGrid;
    private JLabel monthLabel;
    private List<JButton> dayButtons = new ArrayList<>();
    private JTextArea detailsArea;
    private final TaskManager manager = new TaskManager();
    // Map date -> tasks
    private Map<LocalDate, List<Task>> tasksByDate = new HashMap<>();

    public CalendarImportGUI() {
        currentYearMonth = YearMonth.now();
        initializeFrame();
        createUI();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Calendar Import View");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void createUI() {
        JPanel main = new JPanel(new BorderLayout(10,10));
        main.setBorder(new EmptyBorder(10,10,10,10));

        // Top navigation
        JPanel top = new JPanel(new BorderLayout());
        JButton prev = new JButton("Back");
        JButton next = new JButton("Next");
        monthLabel = new JLabel("", SwingConstants.CENTER);
        top.add(prev, BorderLayout.WEST);
        top.add(monthLabel, BorderLayout.CENTER);
        top.add(next, BorderLayout.EAST);

        prev.addActionListener(e -> { currentYearMonth = currentYearMonth.minusMonths(1); refreshCalendar(); });
        next.addActionListener(e -> { currentYearMonth = currentYearMonth.plusMonths(1); refreshCalendar(); });

        main.add(top, BorderLayout.NORTH);

        // Calendar grid (7 x 7 including weekday headers)
        calendarGrid = new JPanel(new GridLayout(7,7,4,4));
        String[] week = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        for (String s : week) {
            JLabel l = new JLabel(s, SwingConstants.CENTER);
            l.setFont(l.getFont().deriveFont(Font.BOLD));
            calendarGrid.add(l);
        }
        for (int i = 0; i < 42; i++) {
            JButton dayBtn = new JButton();
            dayBtn.setMargin(new Insets(2,2,2,2));
            dayBtn.setFocusable(false);
            final int idx = i;
            dayBtn.addActionListener(e -> onDayClicked(idx));
            dayButtons.add(dayBtn);
            calendarGrid.add(dayBtn);
        }
        main.add(calendarGrid, BorderLayout.CENTER);

        // Right side details + import button
        JPanel right = new JPanel(new BorderLayout(6,6));
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        right.add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        JButton importBtn = new JButton("Import Tasks from TaskManager");
        importBtn.addActionListener(e -> { importTasks(); refreshCalendar(); });
        right.add(importBtn, BorderLayout.NORTH);

        main.add(right, BorderLayout.EAST);

        add(main);

        refreshCalendar();
    }

    // Build the internal map from tasks obtained via TaskManager
    private void importTasks() {
        tasksByDate.clear();
        List<Task> all = manager.getTasks();
        for (Task t : all) {
            tasksByDate.computeIfAbsent(t.getDueDate(), k -> new ArrayList<>()).add(t);
        }
        detailsArea.setText("Imported " + all.size() + " tasks. Click a day to see tasks.");
    }

    private void refreshCalendar() {
        monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
        LocalDate first = currentYearMonth.atDay(1);
        int start = first.getDayOfWeek().getValue() % 7; // Sunday -> 0
        int days = currentYearMonth.lengthOfMonth();

        for (int i = 0; i < 42; i++) {
            JButton btn = dayButtons.get(i);
            int dayNum = i - start + 1;
            if (dayNum >= 1 && dayNum <= days) {
                LocalDate d = currentYearMonth.atDay(dayNum);
                btn.setText(String.valueOf(dayNum));
                btn.setEnabled(true);
                List<Task> tasks = tasksByDate.get(d);
                if (tasks != null && !tasks.isEmpty()) {
                    btn.setBackground(new Color(180,220,180));
                    btn.setToolTipText(tasks.size() + " task(s)");
                } else {
                    btn.setBackground(null);
                    btn.setToolTipText(null);
                }
            } else {
                btn.setText("");
                btn.setEnabled(false);
                btn.setBackground(null);
                btn.setToolTipText(null);
            }
        }
    }

    private void onDayClicked(int gridIndex) {
        LocalDate first = currentYearMonth.atDay(1);
        int start = first.getDayOfWeek().getValue() % 7;
        int dayNum = gridIndex - start + 1;
        if (dayNum >= 1 && dayNum <= currentYearMonth.lengthOfMonth()) {
            LocalDate d = currentYearMonth.atDay(dayNum);
            List<Task> tasks = tasksByDate.get(d);
            StringBuilder sb = new StringBuilder();
            sb.append("Tasks for ").append(d).append(":\n\n");
            if (tasks == null || tasks.isEmpty()) {
                sb.append("No tasks.");
            } else {
                for (Task t : tasks) {
                    sb.append("- ").append(t.getTaskName()).append("\n");
                }
            }
            detailsArea.setText(sb.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalendarImportGUI());
    }
}
