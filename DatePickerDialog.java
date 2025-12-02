import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.function.Consumer;
import javax.swing.*;

public class DatePickerDialog extends JDialog {
    private LocalDate selectedDate = null;
    private Consumer<LocalDate> dateConsumer;
    private YearMonth currentMonth;
    private JLabel monthLabel;

    public DatePickerDialog(Frame parent, LocalDate initialDate, Consumer<LocalDate> onDateSelected) {
        super(parent, "Select Date", true);
        this.dateConsumer = onDateSelected;
        this.currentMonth = initialDate != null ? YearMonth.from(initialDate) : YearMonth.now();
        initializeDialog();
    }

    public DatePickerDialog(Dialog parent, LocalDate initialDate, Consumer<LocalDate> onDateSelected) {
        super(parent, "Select Date", true);
        this.dateConsumer = onDateSelected;
        this.currentMonth = initialDate != null ? YearMonth.from(initialDate) : YearMonth.now();
        initializeDialog();
    }

    private void initializeDialog() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 320);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 250, 255));

        // Header with month/year navigation
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Calendar grid
        JPanel calendarPanel = createCalendarPanel();
        mainPanel.add(calendarPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(new Color(245, 250, 255));

        // Previous month button
        JButton prevBtn = new JButton("Back");
        prevBtn.setFont(new Font("Arial", Font.BOLD, 12));
        prevBtn.setBackground(new Color(156, 39, 176));
        prevBtn.setForeground(Color.WHITE);
        prevBtn.setFocusPainted(false);
        prevBtn.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            refreshCalendar();
        });
        panel.add(prevBtn, BorderLayout.WEST);

        // Month/year label
        monthLabel = new JLabel();
        monthLabel.setFont(new Font("Arial", Font.BOLD, 14));
        monthLabel.setForeground(new Color(50, 100, 150));
        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(monthLabel, BorderLayout.CENTER);

        // Next month button
        JButton nextBtn = new JButton("Next");
        nextBtn.setFont(new Font("Arial", Font.BOLD, 12));
        nextBtn.setBackground(new Color(156, 39, 176));
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFocusPainted(false);
        nextBtn.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            refreshCalendar();
        });
        panel.add(nextBtn, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCalendarPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 7, 2, 2));
        panel.setBackground(new Color(245, 250, 255));

        // Day headers
        String[] dayHeaders = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String header : dayHeaders) {
            JLabel headerLabel = new JLabel(header, SwingConstants.CENTER);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 11));
            headerLabel.setForeground(new Color(50, 100, 150));
            headerLabel.setOpaque(true);
            headerLabel.setBackground(new Color(220, 240, 255));
            panel.add(headerLabel);
        }

        // Get the first day of the month and the number of days
        LocalDate firstDay = currentMonth.atDay(1);
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // 0 = Sunday
        int daysInMonth = currentMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        // Add empty cells for days before the month starts
        for (int i = 0; i < firstDayOfWeek; i++) {
            panel.add(new JLabel());
        }

        // Add day buttons
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            JButton dayBtn = new JButton(String.valueOf(day));
            // Make day numbers more visible by increasing font size
            dayBtn.setFont(new Font("Arial", Font.BOLD, 14));
            dayBtn.setFocusPainted(false);
            dayBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

            // Style today
            if (date.equals(today)) {
                dayBtn.setBackground(new Color(100, 200, 255));
                dayBtn.setForeground(Color.WHITE);
                // Keep the today style but still use the larger font size
                dayBtn.setFont(new Font("Arial", Font.BOLD, 14));
            } else {
                dayBtn.setBackground(Color.WHITE);
                dayBtn.setForeground(Color.BLACK);
            }

            final LocalDate finalDate = date;
            dayBtn.addActionListener(e -> {
                selectedDate = finalDate;
                if (dateConsumer != null) {
                    dateConsumer.accept(selectedDate);
                }
                dispose();
            });

            panel.add(dayBtn);
        }

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panel.setBackground(new Color(245, 250, 255));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 11));
        cancelBtn.setBackground(new Color(244, 67, 54));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dispose());
        panel.add(cancelBtn);

        return panel;
    }

    private void refreshCalendar() {
        monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
        // Recreate the main content (simplified by calling repaint)
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                comp.repaint();
            }
        }
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }
}