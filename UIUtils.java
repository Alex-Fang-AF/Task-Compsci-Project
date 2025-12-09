import javax.swing.*;
import java.awt.*;

/**
 * UIUtils: centralized UI defaults for a more modern look.
 */
public class UIUtils {
    public static void applyDefaults() {
        Font base = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("Label.font", base);
        UIManager.put("Button.font", base);
        UIManager.put("TextField.font", base);
        UIManager.put("TextArea.font", base);
        UIManager.put("ComboBox.font", base);
        UIManager.put("List.font", base);
        UIManager.put("TitledBorder.font", base.deriveFont(Font.BOLD));
        UIManager.put("Menu.font", base);
        UIManager.put("MenuItem.font", base);
        UIManager.put("Table.font", base);

        // Buttons: subtle flat look
        UIManager.put("Button.background", ThemeManager.getPanelBackground());
        UIManager.put("Button.foreground", ThemeManager.getTextColor());
        UIManager.put("Button.focus", ThemeManager.getBorderColor());

        // Panels
        UIManager.put("Panel.background", ThemeManager.getBackgroundColor());

        // Tooltips
        UIManager.put("ToolTip.background", ThemeManager.getPanelBackground());
        UIManager.put("ToolTip.foreground", ThemeManager.getTextColor());

        // Scrollbar thin thumb preference (platform dependent)
        UIManager.put("ScrollBar.thumb", ThemeManager.getBorderColor());
    }

    // Helper to add consistent padding to a panel
    public static void setPadding(JComponent c, int padding) {
        c.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
    }

    // Consistent modern button styling used across windows
    public static void styleButton(JButton btn, Color backgroundColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(backgroundColor.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(backgroundColor);
            }
        });
    }

    public static void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        field.setBackground(ThemeManager.getPanelBackground());
        field.setForeground(ThemeManager.getTextColor());
        field.setCaretColor(ThemeManager.getTextColor());
    }

}
// Package-private rounded panel utility kept in same file to reduce small files
class RoundedPanel extends JPanel {
    private int arc;
    private Color fillColor;

    public RoundedPanel(int arc) {
        this(arc, null);
    }

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
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color use = fillColor != null ? fillColor : getBackground();
            g2.setColor(use);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        } finally {
            g2.dispose();
        }
        super.paintComponent(g);
    }
}
 

