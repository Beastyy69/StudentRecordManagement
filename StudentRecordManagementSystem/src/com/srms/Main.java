package com.srms;

import com.srms.ui.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Application entry point. */
public class Main {

    public static void main(String[] args) {
        applyLookAndFeel();
        // All Swing work must happen on the Event Dispatch Thread.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }

    private static void applyLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // keep default look and feel
        }
    }
}
