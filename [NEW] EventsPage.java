import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EventsPage extends JFrame {
    private final MyCalendar calendar;
    private final JTextArea eventsArea;

    public EventsPage(MyCalendar calendar) {
        this.calendar = calendar;
        setTitle("Events - Full Page");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JLabel title = new JLabel("All Events", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        eventsArea = new JTextArea();
        eventsArea.setEditable(false);
        eventsArea.setBackground(Color.WHITE);
        eventsArea.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(eventsArea);
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refresh());
        JButton close = new JButton("Close");
        close.addActionListener(e -> setVisible(false));
        bottom.add(refresh);
        bottom.add(close);
        add(bottom, BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        List<Event> events = calendar.getEventsList();
        if (events.isEmpty()) {
            eventsArea.setText("No events added yet.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Event e : events) {
            sb.append(e.toString()).append("\n\n");
        }
        eventsArea.setText(sb.toString());
        eventsArea.setCaretPosition(0);
    }
}
