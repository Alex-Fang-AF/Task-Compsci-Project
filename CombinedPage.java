import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Combined page showing Tasks and Events side-by-side (50/50).
 */
public class CombinedPage extends JFrame {
    private final MyCalendar calendar;
    private JPanel tasksPanel;
    private JPanel eventsPanel;
    private JScrollPane tasksScroll;
    private JScrollPane eventsScroll;
    private JPanel mainPanel;

    public CombinedPage(MyCalendar calendar) {
        this.calendar = calendar;
        setTitle("Tasks & Events");
        setSize(1000, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        mainPanel = new RoundedPanel(12, ThemeManager.getBackgroundColor());
        mainPanel.setLayout(new BorderLayout(12,12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JLabel title = new JLabel("Tasks and Events", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ThemeManager.getTextColor());
        mainPanel.add(title, BorderLayout.NORTH);

        // Left: tasks
        tasksPanel = new JPanel();
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        tasksPanel.setBackground(ThemeManager.getPanelBackground());
        tasksScroll = new JScrollPane(tasksPanel);
        tasksScroll.setBorder(BorderFactory.createEmptyBorder());
        tasksScroll.getViewport().setBackground(ThemeManager.getPanelBackground());

        // Right: events
        eventsPanel = new JPanel();
        eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
        eventsPanel.setBackground(ThemeManager.getPanelBackground());
        eventsScroll = new JScrollPane(eventsPanel);
        eventsScroll.setBorder(BorderFactory.createEmptyBorder());
        eventsScroll.getViewport().setBackground(ThemeManager.getPanelBackground());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tasksScroll, eventsScroll);
        split.setResizeWeight(0.5);
        split.setDividerSize(6);
        split.setBorder(null);
        // Place the tasks/events split at the top of the page and reserve a fixed height.
        split.setPreferredSize(new Dimension(1000, 240));
        mainPanel.add(split, BorderLayout.NORTH);

        // Add a center content panel below the split for details or additional content.
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        JButton refresh = new JButton("Refresh");
        UIUtils.styleButton(refresh, new Color(33,150,243));
        refresh.addActionListener(e -> refresh());
        JButton close = new JButton("Close");
        UIUtils.styleButton(close, new Color(120,120,120));
        close.addActionListener(e -> setVisible(false));
        bottom.add(refresh);
        bottom.add(close);
        mainPanel.add(bottom, BorderLayout.SOUTH);

        add(mainPanel);
        ThemeManager.addListener(new ThemeManager.ThemeChangeListener() {
            public void onThemeChanged(ThemeManager.Theme newTheme) {
                applyTheme();
            }
        });

        refresh();
        applyTheme();
    }

    public void refresh() {
        List<Task> tasks = calendar.getTasksList();
        List<Event> events = calendar.getEventsList();
        tasksPanel.removeAll();
        eventsPanel.removeAll();

        if (tasks.isEmpty()) {
            JLabel empty = new JLabel("No tasks added yet.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
            tasksPanel.add(empty);
        } else {
            for (Task t : tasks) {
                JPanel row = createTaskRow(t);
                tasksPanel.add(row);
                tasksPanel.add(Box.createVerticalStrut(8));
            }
        }

        if (events.isEmpty()) {
            JLabel empty = new JLabel("No events added yet.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
            eventsPanel.add(empty);
        } else {
            for (Event ev : events) {
                JPanel row = createEventRow(ev);
                eventsPanel.add(row);
                eventsPanel.add(Box.createVerticalStrut(8));
            }
        }

        tasksPanel.add(Box.createVerticalGlue());
        eventsPanel.add(Box.createVerticalGlue());
        tasksPanel.revalidate();
        eventsPanel.revalidate();
        tasksPanel.repaint();
        eventsPanel.repaint();
    }

    private JPanel createTaskRow(Task t) {
        RoundedPanel p = new RoundedPanel(10, ThemeManager.getPanelBackground());
        p.setLayout(new BorderLayout(8,8));
        p.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        JLabel name = new JLabel(t.getTaskName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        name.setForeground(ThemeManager.getTextColor());
        p.add(name, BorderLayout.CENTER);

        JLabel meta = new JLabel(t.getFormattedDueDate());
        meta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        meta.setForeground(ThemeManager.getTextColor());
        p.add(meta, BorderLayout.EAST);

        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                DetailPage dp = new DetailPage(t);
                dp.setVisible(true);
            }
        });
        return p;
    }

    private JPanel createEventRow(Event ev) {
        RoundedPanel p = new RoundedPanel(10, ThemeManager.getPanelBackground());
        p.setLayout(new BorderLayout(8,8));
        p.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        JLabel name = new JLabel(ev.getEventName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        name.setForeground(ThemeManager.getTextColor());
        p.add(name, BorderLayout.CENTER);

        JLabel meta = new JLabel(ev.getStartDate() + " — " + ev.getEndDate());
        meta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        meta.setForeground(ThemeManager.getTextColor());
        p.add(meta, BorderLayout.EAST);

        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                DetailPage dp = new DetailPage(ev);
                dp.setVisible(true);
            }
        });
        return p;
    }

    public void applyTheme() {
        mainPanel.setBackground(ThemeManager.getBackgroundColor());
        tasksPanel.setBackground(ThemeManager.getPanelBackground());
        eventsPanel.setBackground(ThemeManager.getPanelBackground());
        tasksScroll.getViewport().setBackground(ThemeManager.getPanelBackground());
        eventsScroll.getViewport().setBackground(ThemeManager.getPanelBackground());
        repaint();
    }
}
