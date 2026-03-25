package game;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class EdpGame {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("No Sanctuary");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            LoadingScreen loadingScreen = new LoadingScreen(frame, () -> {

                panel gamePanel = ((GameLoader) frame.getRootPane()
                        .getClientProperty("loader")).loadedPanel;

                LandingPage landingPage = new LandingPage(gamePanel, () -> {

                    frame.getContentPane().removeAll();
                    frame.add(gamePanel);
                    frame.pack();
                    frame.revalidate();
                    frame.repaint();
                    frame.setLocationRelativeTo(null);

                    gamePanel.startGame();
                    gamePanel.startThread();
                    gamePanel.requestFocusInWindow();
                });

                frame.getContentPane().removeAll();
                frame.add(landingPage);
                frame.pack();
                frame.revalidate();
                frame.repaint();
                frame.setLocationRelativeTo(null);
            });

            frame.add(loadingScreen);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            GameLoader loader = new GameLoader(frame, loadingScreen);

            frame.getRootPane().putClientProperty("loader", loader);
            loader.execute();
        });
    }
}