import java.awt.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class AlarmDialog extends JDialog {
    private Thread loopThread;

    private AlarmDialog(Frame owner, Task task, long repeatMinutes) {
        super(owner, "Task Reminder", true);
        setSize(420, 200);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10,10));

        JPanel main = new JPanel(new BorderLayout(8,8));
        main.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        main.setBackground(ThemeManager.getPanelBackground());

        JLabel title = new JLabel("Reminder: " + task.getTaskName());
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(ThemeManager.getTextColor());
        main.add(title, BorderLayout.NORTH);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        JLabel info = new JLabel("Due: " + task.getDueDate().format(df));
        info.setForeground(ThemeManager.getTextColor());
        main.add(info, BorderLayout.CENTER);

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
            buttons.setBackground(ThemeManager.getPanelBackground());

            // Build repeat choices: 0, then 30..720 in 30-minute steps
            java.util.List<Integer> choices = new java.util.ArrayList<>();
            choices.add(0);
            for (int m = 30; m <= 720; m += 30) choices.add(m);
            Integer[] choicesArr = choices.toArray(new Integer[0]);
            JComboBox<Integer> repeatSelector = new JComboBox<>(choicesArr);
            repeatSelector.setSelectedItem((int)Math.max(0, repeatMinutes));

            JButton ok = new JButton("OK");
            JButton cancel = new JButton("Cancel Alarm");

            ok.addActionListener(e -> {
                // stop the looping sound immediately
                stopLoop();
                Integer chosen = (Integer) repeatSelector.getSelectedItem();
                if (chosen == null) chosen = 0;
                // schedule a one-shot after chosen minutes (0 triggers immediately)
                AlarmManager.scheduleOneShot(task, chosen);
                dispose();
            });
            cancel.addActionListener(e -> {
                // stop the looping sound immediately and cancel alarm
                stopLoop();
                AlarmManager.cancelAlarm(task);
                dispose();
            });

            // left side: selector panel
            JPanel left = new JPanel(new BorderLayout(6,6));
            left.setBackground(ThemeManager.getPanelBackground());
            left.add(new JLabel("Re-notify after (minutes):"), BorderLayout.NORTH);
            left.add(repeatSelector, BorderLayout.CENTER);

            buttons.add(left);
            buttons.add(ok);
            buttons.add(cancel);

        add(main, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        // start a looping bell until dialog closed
        try {
            loopThread = SoundPlayer.startBellLoop();
        } catch (Throwable t) {
            // ignore
        }
    }

    public static void showAlarm(Task task, long repeatMinutes) {
        Frame owner = null;
        // try to find a visible frame to own the dialog
        for (Frame f : Frame.getFrames()) {
            if (f.isVisible()) { owner = f; break; }
        }
        AlarmDialog d = new AlarmDialog(owner, task, repeatMinutes);
        d.setVisible(true);
    }

    @Override
    public void dispose() {
        // stop looping sound if active
        try {
            if (loopThread != null && loopThread.isAlive()) {
                loopThread.interrupt();
                loopThread = null;
            }
        } catch (Throwable t) {
            // ignore
        }
        super.dispose();
    }

    // Stop the loop thread if it's running. Safe to call multiple times.
    private void stopLoop() {
        try {
            if (loopThread != null && loopThread.isAlive()) {
                loopThread.interrupt();
                loopThread = null;
            }
        } catch (Throwable t) {
            // ignore
        }
    }
}
