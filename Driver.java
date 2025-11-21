public class Driver {
    public static void main(String[] args) {
        // Increase default UI font sizes for better readability across the app
        javax.swing.SwingUtilities.invokeLater(() -> {
            java.awt.Font uiFont = new java.awt.Font("Merlin", java.awt.Font.PLAIN, 16);
            javax.swing.UIManager.put("Label.font", uiFont);
            javax.swing.UIManager.put("Button.font", uiFont);
            javax.swing.UIManager.put("TextField.font", uiFont);
            javax.swing.UIManager.put("TextArea.font", uiFont);
            javax.swing.UIManager.put("ComboBox.font", uiFont);
            javax.swing.UIManager.put("List.font", uiFont);
            javax.swing.UIManager.put("TitledBorder.font", uiFont);
            javax.swing.UIManager.put("Menu.font", uiFont);
            javax.swing.UIManager.put("MenuItem.font", uiFont);
            javax.swing.UIManager.put("Table.font", uiFont);
            new TaskGUI();
        });
    }
}
//yes
