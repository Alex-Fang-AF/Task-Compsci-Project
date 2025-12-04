import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Centralized theme manager for light and dark mode support.
 * Stores color schemes and notifies listeners of theme changes.
 */
public class ThemeManager {
    public enum Theme { LIGHT, DARK }
    
    private static Theme currentTheme = Theme.LIGHT;
    private static List<ThemeChangeListener> listeners = new ArrayList<>();
    
    // Light mode colors
    public static class LightColors {
        public static final Color BACKGROUND = new Color(245, 250, 255);
        public static final Color PANEL_BG = new Color(255, 255, 255);
        public static final Color TEXT = new Color(50, 50, 50);
        public static final Color BORDER = new Color(200, 200, 200);
        public static final Color HEADER_BG = new Color(220, 240, 255);
        public static final Color BUTTON_HOVER = new Color(220, 220, 220);
    }
    
    // Setting Dark mode colors
    public static class DarkColors {
        public static final Color BACKGROUND = new Color(30, 30, 35);
        public static final Color PANEL_BG = new Color(45, 45, 50);
        public static final Color TEXT = new Color(220, 220, 220);
        public static final Color BORDER = new Color(80, 80, 90);
        public static final Color HEADER_BG = new Color(55, 75, 100);
        public static final Color BUTTON_HOVER = new Color(70, 70, 80);
    }
    
    public static Theme getCurrentTheme() {
        return currentTheme;
    }
    
    public static void setTheme(Theme theme) {
        if (currentTheme != theme) {
            currentTheme = theme;
            notifyListeners();
        }
    }
    
    public static void toggleTheme() {
        setTheme(currentTheme == Theme.LIGHT ? Theme.DARK : Theme.LIGHT);
    }
    
    public static Color getBackgroundColor() {
        return currentTheme == Theme.LIGHT ? LightColors.BACKGROUND : DarkColors.BACKGROUND;
    }
    
    public static Color getPanelBackground() {
        return currentTheme == Theme.LIGHT ? LightColors.PANEL_BG : DarkColors.PANEL_BG;
    }
    
    public static Color getTextColor() {
        return currentTheme == Theme.LIGHT ? LightColors.TEXT : DarkColors.TEXT;
    }
    
    public static Color getBorderColor() {
        return currentTheme == Theme.LIGHT ? LightColors.BORDER : DarkColors.BORDER;
    }
    
    public static Color getHeaderBackground() {
        return currentTheme == Theme.LIGHT ? LightColors.HEADER_BG : DarkColors.HEADER_BG;
    }
    
    public static Color getButtonHoverColor() {
        return currentTheme == Theme.LIGHT ? LightColors.BUTTON_HOVER : DarkColors.BUTTON_HOVER;
    }
    
    public interface ThemeChangeListener {
        void onThemeChanged(Theme newTheme);
    }
    
    public static void addListener(ThemeChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public static void removeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }
    
    private static void notifyListeners() {
        for (ThemeChangeListener listener : listeners) {
            listener.onThemeChanged(currentTheme);
        }
    }
}
