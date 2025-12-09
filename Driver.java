public class Driver {
    public static void main(String[] args) {
        // Setting UI font size
        javax.swing.SwingUtilities.invokeLater(() -> {
            UIUtils.applyDefaults();
            new TaskGUI();
        });
    }
}
