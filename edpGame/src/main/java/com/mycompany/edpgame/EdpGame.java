package com.mycompany.edpgame;

import javax.swing.JFrame;

public class EdpGame {

    public static void main(String[] args) {

        JFrame frame = new JFrame("EDP Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        panel gamePanel = new panel();
        frame.add(gamePanel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.setVisible(true);
        gamePanel.startThread();

    }
}
