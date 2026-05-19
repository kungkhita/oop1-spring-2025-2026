package cafemanagementsystem;
import cafemanagementsystem.gui.CafeGUI;
import javax.swing.SwingUtilities;

public class Start {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CafeGUI::new);
    }
}
