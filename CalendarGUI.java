                  import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import javax.swing.*;
//Created with assistance from Copilot/reference to existing CalendarGUI structures MarcoBackman @Github
//Reference to https://stackoverflow.com/questions/63224864/how-to-create-a-calendar-in-java-swing
public class CalendarGUI extends JFrame {
    private final MyCalendar calendar;
    private YearMonth currentYearMonth;
    private JPanel grid;
    private JLabel monthLabel;
    private JButton prevButton;
    private JButton nextButton;
    private JTextArea detailsArea;

    public CalendarGUI(MyCalendar calendar) {
        this.calendar = calendar;
        this.currentYearMonth = YearMonth.from(calendar.getCurrentDate());
        initializeFrame();
        buildUI();
        // Update theme when it changes
        
        ThemeManager.addListener(new ThemeManager.ThemeChangeListener() {
            public void onThemeChanged(ThemeManager.Theme newTheme) {
                applyTheme();
            }
        });
        SwingUtilities.invokeLater(this::refreshCalendar);
    }

    private void initializeFrame() {
        setTitle("Month Details");
        setSize(640, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setResizable(true);
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        monthLabel.setOpaque(false);

        // Navigation buttons - flat, circular with hover
        prevButton = new JButton("\u25C0");
        nextButton = new JButton("\u25B6");
        styleNavButton(prevButton);
        styleNavButton(nextButton);

        prevButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            refreshCalendar();
        });
        nextButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            refreshCalendar();
        });

        JPanel nav = new JPanel(new BorderLayout());
        nav.setOpaque(false);
        nav.add(prevButton, BorderLayout.WEST);
        nav.add(monthLabel, BorderLayout.CENTER);
        nav.add(nextButton, BorderLayout.EAST);

        top.add(nav, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        grid = new JPanel(new GridLayout(7, 7, 8, 8));
        grid.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        grid.setOpaque(false);
        add(grid, BorderLayout.CENTER);

        // Right side import/details panel (merged from CalendarImportGUI)
        JPanel right = new JPanel(new BorderLayout(6,6));
        right.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        right.add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        JButton importBtn = new JButton("Import tasks from file");
        importBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int res = chooser.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                java.io.File f = chooser.getSelectedFile();
                try {
                    calendar.loadTasksFromFile(f.getAbsolutePath());
                    refreshCalendar();
                    detailsArea.setText("Imported tasks from " + f.getName() + ". Total tasks: " + calendar.getTasksList().size());
                } catch (Exception ex) {
                    detailsArea.setText("Failed to import tasks: " + ex.getMessage());
                }
            }
        });
        right.add(importBtn, BorderLayout.NORTH);
        add(right, BorderLayout.EAST);
    }

    // Public so TaskGUI can request refresh when tasks change
    public void refreshCalendar() {
        grid.removeAll();
        String[] dayNames = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        for (String dn : dayNames) {
            JLabel lbl = new JLabel(dn.toUpperCase(), SwingConstants.CENTER);
            lbl.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
            lbl.setOpaque(true);
            lbl.setBackground(ThemeManager.getHeaderBackground());
            lbl.setForeground(ThemeManager.getTextColor());
            lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            grid.add(lbl);
        }

        monthLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
        monthLabel.setForeground(ThemeManager.getTextColor());
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int startIndex = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday -> 0
        int daysInMonth = currentYearMonth.lengthOfMonth();

        for (int i = 0; i < 42; i++) {
            RoundedPanel cell = new RoundedPanel(10, ThemeManager.getPanelBackground());
            cell.setLayout(new BorderLayout());
            cell.setOpaque(false);
            int dayNumber = i - startIndex + 1;
            if (dayNumber >= 1 && dayNumber <= daysInMonth) {
                LocalDate d = currentYearMonth.atDay(dayNumber);

                // Build a vertical panel so each task/event can be its own label and clickable
                JPanel dayContent = new JPanel();
                dayContent.setLayout(new BoxLayout(dayContent, BoxLayout.Y_AXIS));
                dayContent.setOpaque(false);
                dayContent.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

                // Day number label
                JLabel dayNumLabel = new JLabel(String.valueOf(dayNumber));
                dayNumLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
                dayNumLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                dayNumLabel.setForeground(ThemeManager.getTextColor());
                dayContent.add(dayNumLabel);

                List<Task> tasks = calendar.getTasksOn(d);
                int shown = 0;
                for (Task t : tasks) {
                    if (shown >= 3) break; // show up to 3 task names
                    String name = t.getTaskName();
                    String display = name.length() > 18 ? name.substring(0, 15) + "..." : name;
                    final Task taskRef = t;
                    JLabel taskLabel = new JLabel("• " + display + (AlarmManager.getScheduledTasks().contains(t) ? " 🔔" : ""));
                    taskLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    taskLabel.setForeground(ThemeManager.getTextColor());
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
                    more.setFont(new Font("SansSerif", Font.PLAIN, 10));
                    more.setForeground(ThemeManager.getTextColor());
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
                    eventLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
                    // keep event marker color but ensure it is visible on dark backgrounds
                    eventLabel.setForeground(new Color(180,100,200));
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
                // Hover effect and today highlight
                if (d.equals(LocalDate.now())) {
                    cell.setFillColor(ThemeManager.getHeaderBackground());
                } else {
                    cell.setFillColor(ThemeManager.getPanelBackground());
                }
                cell.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        cell.setFillColor(ThemeManager.getButtonHoverColor());
                        cell.repaint();
                    }
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        if (d.equals(LocalDate.now())) {
                            cell.setFillColor(ThemeManager.getHeaderBackground());
                        } else {
                            cell.setFillColor(ThemeManager.getPanelBackground());
                        }
                        cell.repaint();
                    }
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        // Show details for this date in the right-hand panel if available
                        if (detailsArea != null) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Date: ").append(d).append("\n\n");
                            List<Task> tasks = calendar.getTasksOn(d);
                            if (tasks.isEmpty()) sb.append("No tasks.\n");
                            else {
                                sb.append("Tasks:\n");
                                for (Task t : tasks) sb.append("- ").append(t.getTaskName()).append("\n");
                            }
                            List<Event> evs = calendar.getEventsOn(d);
                            if (evs.isEmpty()) sb.append("No events.\n");
                            else {
                                sb.append("\nEvents:\n");
                                for (Event ev : evs) sb.append("- ").append(ev.getEventName()).append("\n");
                            }
                            detailsArea.setText(sb.toString());
                        }
                    }
                });
            } else {
                cell.setFillColor(ThemeManager.getPanelBackground());
            }
            cell.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
            grid.add(cell);
        }

        grid.revalidate();
        grid.repaint();
    }

    // Apply theme to this window
    public void applyTheme() {
        getContentPane().setBackground(ThemeManager.getBackgroundColor());
        if (monthLabel != null) monthLabel.setForeground(ThemeManager.getTextColor());
        refreshCalendar();
        repaint();
    }

    // Helper to style navigation buttons in a modern flat style
    private void styleNavButton(JButton b) {
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setBackground(ThemeManager.getPanelBackground());
        b.setForeground(ThemeManager.getTextColor());
        b.setPreferredSize(new Dimension(36,36));
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(ThemeManager.getButtonHoverColor());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(ThemeManager.getPanelBackground());
            }
        });
    }

    // Small rounded panel implementation to give modern look
    private static class RoundedPanel extends JPanel {
        private int arc = 12;
        private Color fillColor;

        public RoundedPanel(int arc, Color fill) {
            super();
            this.arc = arc;
            this.fillColor = fill;
            setOpaque(false);
        }

        public void setFillColor(Color c) {
            this.fillColor = c;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fillColor != null ? fillColor : getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            super.paintComponent(g);
            g2.dispose();
        }
    }
}

