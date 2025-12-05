import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;
//Helper to show events page in comparison to "TasksPage.java"
public class EventsPage extends JFrame {
    private final MyCalendar calendar;
    private JPanel listPanel;
    private JScrollPane scroll;
    private JPanel headerPanel;
    private JPanel mainPanel;
//Created with assistance from Copilot
    public EventsPage(MyCalendar calendar) {
        this.calendar = calendar;
        setTitle("Events - Full Page");
        setSize(820, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        JLabel title = new JLabel("All Events", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(ThemeManager.getPanelBackground());
        mainPanel.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(ThemeManager.getBackgroundColor());
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
        applyTheme();
        ThemeManager.addListener(new ThemeManager.ThemeChangeListener() {
            public void onThemeChanged(ThemeManager.Theme newTheme) {
                applyTheme();
            }
        });
    }

    public void applyTheme() {
        headerPanel.setBackground(ThemeManager.getHeaderBackground());
        mainPanel.setBackground(ThemeManager.getBackgroundColor());
        listPanel.setBackground(ThemeManager.getPanelBackground());
        if (scroll != null) scroll.getViewport().setBackground(ThemeManager.getPanelBackground());
        repaint();
    }

    public void refresh() {
        List<Event> events = calendar.getEventsList();
        listPanel.removeAll();
        if (events.isEmpty()) {
            JLabel empty = new JLabel("No events added yet.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
            listPanel.add(empty);
            listPanel.revalidate();
            listPanel.repaint();
            return;
        }

            // Palette of four soft colors; choose randomly but ensure adjacent bubbles differ
            Color[] colors = new Color[] {
                new Color(255,230,230),
                new Color(235,245,255),
                new Color(240,255,235),
                new Color(250,240,255)
            };

            Random rnd = new Random();
            int prev = -1;
            for (Event e : events) {
                int pick = rnd.nextInt(colors.length);
                if (pick == prev) {
                    pick = (pick + 1) % colors.length;
                }
                Color bg = colors[pick];
                prev = pick;
                EventBubble bubble = new EventBubble(e, bg);
                listPanel.add(Box.createVerticalStrut(8));
                listPanel.add(bubble);
            }
        listPanel.add(Box.createVerticalGlue());
        listPanel.revalidate();
        listPanel.repaint();
    }

    // Small helper to style buttons in this page
    

    // Custom bubble component for events
    private static class EventBubble extends JComponent {
        private final Event event;
        private final Color bgColor;
        private boolean hovering = false;

        EventBubble(Event event, Color bgColor) {
            this.event = event;
            this.bgColor = bgColor;
            setPreferredSize(new Dimension(760, 70));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    DetailPage dp = new DetailPage(event);
                    dp.setVisible(true);
                }
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    hovering = true; repaint();
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    hovering = false; repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight() - 6;
                Color fill = hovering ? bgColor.brighter() : bgColor;
                g2.setColor(fill);
                g2.fillRoundRect(6, 3, w - 12, h, h, h);
                g2.setColor(fill.darker());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(6, 3, w - 12, h, h, h);

                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                int textX = 20;
                int textY = 28;
                g2.drawString(event.getEventName(), textX, textY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                String dates = event.getStartDate() + " to " + event.getEndDate();
                g2.drawString(dates, textX, textY + 20);
                if (event.getDescription() != null && !event.getDescription().isEmpty()) {
                    g2.drawString(event.getDescription(), textX + 200, textY + 20);
                }
            } finally {
                g2.dispose();
            }
        }
    }

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
