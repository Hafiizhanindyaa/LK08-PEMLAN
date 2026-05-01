package core;

import javax.swing.*;

import panel.LoginFrame;
public class Main {

    public static void main(String[] args) {
        FileHelper.setupAwal();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}

