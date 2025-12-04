import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class TasksPage extends JFrame {
    private final MyCalendar calendar;
    private JPanel listPanel;
    private JScrollPane scroll;
    // store references for theme updates
    private JPanel headerPanel;
    private JPanel mainPanel;

    public TasksPage(MyCalendar calendar) {
        this.calendar = calendar;
        setTitle("Tasks - Full Page");
        setSize(820, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));

        // Header with simple background (store for theme updates)
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel title = new JLabel("All Tasks", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Main content panel with subtle background
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        // List panel will contain custom ellipse-shaped task components
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(250,250,252));
        mainPanel.add(scroll, BorderLayout.CENTER);

        // Bottom action bar
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(new Color(245, 250, 255));
        JButton refresh = new JButton("Refresh");
        styleActionButton(refresh, new Color(33,150,243));
        refresh.addActionListener(e -> refresh());
        JButton close = new JButton("Close");
        styleActionButton(close, new Color(120,120,120));
        close.addActionListener(e -> setVisible(false));
        bottom.add(refresh);
        bottom.add(close);
        mainPanel.add(bottom, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        refresh();
        // Apply current theme colors and register listener
        applyTheme();
        ThemeManager.addListener(new ThemeManager.ThemeChangeListener() {
            public void onThemeChanged(ThemeManager.Theme newTheme) {
                applyTheme();
            }
        });
    }

    // Apply current theme to this window
    public void applyTheme() {
        headerPanel.setBackground(ThemeManager.getHeaderBackground());
        headerPanel.setForeground(ThemeManager.getTextColor());
        mainPanel.setBackground(ThemeManager.getBackgroundColor());
        listPanel.setBackground(ThemeManager.getPanelBackground());
        if (scroll != null) scroll.getViewport().setBackground(ThemeManager.getPanelBackground());
        repaint();
    }

    public void refresh() {
        List<Task> tasks = calendar.getTasksList();
        listPanel.removeAll();
        if (tasks.isEmpty()) {
            JLabel empty = new JLabel("No tasks added yet.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
            listPanel.add(empty);
            listPanel.revalidate();
            listPanel.repaint();
            return;
        }

        // Palette of four soft colors; choose randomly but ensure adjacent bubbles differ
        Color[] colors = new Color[] {
            new Color(200,230,255),
            new Color(230,245,220),
            new Color(235,220,255),
            new Color(255,240,225)
        };

        Random rnd = new Random();
        int prev = -1;
        for (Task t : tasks) {
            int pick = rnd.nextInt(colors.length);
            if (pick == prev) {
                // choose a different adjacent color deterministically if collision
                pick = (pick + 1) % colors.length;
            }
            Color bg = colors[pick];
            prev = pick;
            TaskBubble bubble = new TaskBubble(t, bg);
            listPanel.add(Box.createVerticalStrut(8));
            listPanel.add(bubble);
        }
        listPanel.add(Box.createVerticalGlue());
        listPanel.revalidate();
        listPanel.repaint();
    }

    // Custom component that paints an ellipse-like rounded background and text
    private static class TaskBubble extends JComponent {
        private final Task task;
        private final Color bgColor;

        TaskBubble(Task task, Color bgColor) {
            this.task = task;
            this.bgColor = bgColor;
            setPreferredSize(new Dimension(760, 70));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            // Hover state to brighten bubble on mouse over
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    DetailPage dp = new DetailPage(task);
                    dp.setVisible(true);
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    hovering = true;
                    repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    hovering = false;
                    repaint();
                }
            });
        }

        private boolean hovering = false;

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight() - 6;
                // draw ellipse-like rounded rectangle as background
                Color fill = hovering ? bgColor.brighter() : bgColor;
                g2.setColor(fill);
                g2.fillRoundRect(6, 3, w - 12, h, h, h);

                // draw border
                g2.setColor(fill.darker());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(6, 3, w - 12, h, h, h);

                // text
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                int textX = 20;
                int textY = 28;
                g2.drawString(task.getTaskName(), textX, textY);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                String due = "Due: " + task.getFormattedDueDate();
                String pr = task.getPriority() != null ? "[" + task.getPriority().getDisplayName() + "]" : "";
                g2.drawString(due + "    " + pr, textX, textY + 20);
            } finally {
                g2.dispose();
            }
        }
    }

    // Small helper to style buttons in this page
    private void styleActionButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1),
            BorderFactory.createEmptyBorder(6,12,6,12)
        ));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
