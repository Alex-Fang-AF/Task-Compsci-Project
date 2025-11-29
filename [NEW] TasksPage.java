import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TasksPage extends JFrame {
    private final MyCalendar calendar;
    private final JTextArea tasksArea;

    public TasksPage(MyCalendar calendar) {
        this.calendar = calendar;
        setTitle("Tasks - Full Page");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JLabel title = new JLabel("All Tasks", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        tasksArea = new JTextArea();
        tasksArea.setEditable(false);
        tasksArea.setBackground(Color.WHITE);
        tasksArea.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(tasksArea);
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
        List<Task> tasks = calendar.getTasksList();
        if (tasks.isEmpty()) {
            tasksArea.setText("No tasks added yet.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Task t : tasks) {
            sb.append(t.toString()).append("\n\n");
        }
        tasksArea.setText(sb.toString());
        tasksArea.setCaretPosition(0);
    }
}
