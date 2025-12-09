/**
 * DetailPage.java
 *
 * Popup showing details for a Task or Event.
 */
import javax.swing.*;
import java.awt.*;

public class DetailPage extends JFrame {
    public DetailPage(Task task) {
        setTitle("Task Details");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        RoundedPanel main = new RoundedPanel(12, ThemeManager.getPanelBackground());
        main.setLayout(new BorderLayout(8,8));
        main.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JLabel title = new JLabel(task.getTaskName(), SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        main.add(title, BorderLayout.NORTH);

        String taskDescShort = (task.getDescription() != null && !task.getDescription().isEmpty()) ? task.getDescription() : "(No description)";
        JLabel subtitle = new JLabel("Detail of task: " + taskDescShort, SwingConstants.LEFT);
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subtitle.setForeground(new Color(80, 80, 80));

        JTextArea desc = new JTextArea();
        desc.setEditable(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        StringBuilder sb = new StringBuilder();
        sb.append("Due: ").append(task.getFormattedDueDate()).append("\n");
        sb.append("Priority: ").append(task.getPriority() != null ? task.getPriority().getDisplayName() : "").append("\n\n");
        sb.append(task.getDescription() != null && !task.getDescription().isEmpty() ? task.getDescription() : "(No description)");
        desc.setText(sb.toString());
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane sp = new JScrollPane(desc);

        // Center panel holds subtitle directly above the description
        JPanel centerPanel = new JPanel(new BorderLayout(4,4));
        centerPanel.setOpaque(false);
        subtitle.setBorder(BorderFactory.createEmptyBorder(0,0,4,0));
        centerPanel.add(subtitle, BorderLayout.NORTH);
        centerPanel.add(sp, BorderLayout.CENTER);
        main.add(centerPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        JButton close = new JButton("Close");
        UIUtils.styleButton(close, new Color(120,120,120));
        close.addActionListener(e -> dispose());
        bottom.add(close);
        main.add(bottom, BorderLayout.SOUTH);

        add(main);
        // Apply current theme and listen for changes
        applyTheme();
        ThemeManager.addListener(new ThemeManager.ThemeChangeListener() {
            public void onThemeChanged(ThemeManager.Theme newTheme) {
                applyTheme();
            }
        });
    }

    public DetailPage(Event event) {
        setTitle("Event Details");
        setSize(420, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(8,8));
        main.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JLabel title = new JLabel(event.getEventName(), SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        main.add(title, BorderLayout.NORTH);

        String eventDescShort = (event.getDescription() != null && !event.getDescription().isEmpty()) ? event.getDescription() : "(No description)";
        JLabel subtitle = new JLabel("Detail of event: " + eventDescShort, SwingConstants.LEFT);
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subtitle.setForeground(new Color(80, 80, 80));
        JTextArea desc = new JTextArea();
        desc.setEditable(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        StringBuilder sb = new StringBuilder();
        sb.append("From: ").append(event.getStartDate()).append("\n");
        sb.append("To:   ").append(event.getEndDate()).append("\n\n");
        sb.append(event.getDescription() != null && !event.getDescription().isEmpty() ? event.getDescription() : "(No description)");
        desc.setText(sb.toString());
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane sp = new JScrollPane(desc);

        JPanel centerPanel = new JPanel(new BorderLayout(4,4));
        centerPanel.setOpaque(false);
        subtitle.setBorder(BorderFactory.createEmptyBorder(0,0,4,0));
        centerPanel.add(subtitle, BorderLayout.NORTH);
        centerPanel.add(sp, BorderLayout.CENTER);
        main.add(centerPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        JButton close = new JButton("Close");
        UIUtils.styleButton(close, new Color(120,120,120));
        close.addActionListener(e -> dispose());
        bottom.add(close);
        main.add(bottom, BorderLayout.SOUTH);

        add(main);
        // Apply theme and register listener
        applyTheme();
        ThemeManager.addListener(new ThemeManager.ThemeChangeListener() {
            public void onThemeChanged(ThemeManager.Theme newTheme) {
                applyTheme();
            }
        });
    }

    private void applyTheme() {
        Color bg = ThemeManager.getBackgroundColor();
        Color panelBg = ThemeManager.getPanelBackground();
        Color text = ThemeManager.getTextColor();
        Container c = getContentPane();
        c.setBackground(bg);
        // walk components and apply simple colors
        for (Component comp : c.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(panelBg);
                for (Component child : ((JPanel) comp).getComponents()) {
                    child.setForeground(text);
                    if (child instanceof JScrollPane) {
                        ((JScrollPane) child).getViewport().setBackground(panelBg);
                    }
                }
            }
        }
        repaint();
    }
}
