import java.awt.*;
import javax.swing.*;

/**
 * Simple alarm management dialog: list scheduled alarms and allow canceling or changing the snooze interval.
 */
public class AlarmManagementGUI extends JDialog {
    private JList<Task> alarmList;
    private DefaultListModel<Task> listModel;

    public AlarmManagementGUI(Frame owner) {
        super(owner, "Alarm Manager", true);
        setSize(600, 400);
        setLocationRelativeTo(owner);
        initUI();
    }

    private void initUI() {
        RoundedPanel main = new RoundedPanel(12, ThemeManager.getPanelBackground());
        main.setLayout(new BorderLayout(10,10));
        main.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel header = new JLabel("Scheduled Alarms");
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setForeground(ThemeManager.getTextColor());
        main.add(header, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        alarmList = new JList<>(listModel);
        alarmList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Task) {
                    Task t = (Task) value;
                    lbl.setText(t.toString());
                }
                lbl.setBackground(isSelected ? ThemeManager.getHeaderBackground() : ThemeManager.getPanelBackground());
                lbl.setForeground(ThemeManager.getTextColor());
                return lbl;
            }
        });

        JScrollPane sp = new JScrollPane(alarmList);
        main.add(sp, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setOpaque(false);

        JButton refresh = new JButton("Refresh");
        JButton cancel = new JButton("Cancel Alarm");
        JButton change = new JButton("Change Repeat");
        JButton close = new JButton("Close");

        refresh.addActionListener(e -> loadAlarms());
        cancel.addActionListener(e -> {
            Task sel = alarmList.getSelectedValue();
            if (sel != null) {
                AlarmManager.cancelAlarm(sel);
                loadAlarms();
            }
        });
        change.addActionListener(e -> {
            Task sel = alarmList.getSelectedValue();
            if (sel == null) return;
            Integer[] choices = buildMinuteChoices();
            JComboBox<Integer> selBox = new JComboBox<>(choices);
            selBox.setSelectedItem(30);
            int res = JOptionPane.showConfirmDialog(this, selBox, "Select re-notify (minutes)", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                Integer chosen = (Integer) selBox.getSelectedItem();
                if (chosen != null) {
                    AlarmManager.scheduleOneShot(sel, chosen);
                    loadAlarms();
                }
            }
        });
        close.addActionListener(e -> dispose());

        UIUtils.styleButton(refresh, new Color(33,150,243));
        UIUtils.styleButton(change, new Color(76,175,80));
        UIUtils.styleButton(cancel, new Color(244,67,54));
        UIUtils.styleButton(close, new Color(120,120,120));
        btns.add(refresh);
        btns.add(change);
        btns.add(cancel);
        btns.add(close);

        main.add(btns, BorderLayout.SOUTH);

        add(main);
        loadAlarms();
    }

    private Integer[] buildMinuteChoices() {
        java.util.List<Integer> choices = new java.util.ArrayList<>();
        choices.add(0);
        for (int m = 30; m <= 720; m += 30) choices.add(m);
        return choices.toArray(new Integer[0]);
    }

    private void loadAlarms() {
        listModel.clear();
        java.util.List<Task> tasks = AlarmManager.getScheduledTasks();
        for (Task t : tasks) listModel.addElement(t);
    }

    public static void showManager(Frame owner) {
        AlarmManagementGUI d = new AlarmManagementGUI(owner);
        d.setVisible(true);
    }
}
