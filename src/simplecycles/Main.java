package simplecycles;

import simplecycles.ui.CycleFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CycleFrame().setVisible(true));
    }
}
