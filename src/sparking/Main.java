package sparking;

import controlador.ParqueoService;
import parking.vista.login.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Aspecto del sistema con ajustes de color oscuro
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Deshabilitar anti-aliasing agresivo en algunos sistemas
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            ParqueoService service = new ParqueoService();
            LoginFrame login = new LoginFrame(service);
            login.setVisible(true);
        });
    }
}
