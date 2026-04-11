package game;

import com.sun.jdi.connect.spi.Connection;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.Statement;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.sound.sampled.FloatControl;

public class LandingPage extends JPanel implements ActionListener {

    panel gp;
    private LandingPageListener listener;
    private Image backgroundImg, leaderboardBGImg, settingBGImg, aboutBGImg, creditsBGImg, usernameInputBGImg;

    public JTextField usernameInput;

    private String menuState = "MAIN";

    public float alpha = 0f;
    public boolean fadingIn = false;
    public boolean fadingOut = false;
    public String nextState = "";
    public javax.swing.Timer fadeTimer;
    private Runnable onTransitionMid = null;
    private boolean isFirstLoad = true;

    private int leaderboardScroll = 0;

    private float fadeSpeed = 0.05f;

    private JButton startBtn, enterBtn, leaderboardBtn, aboutBtn, exitBtn1, exitBtn2, exitBtn3, exitBtn4, settingBtn, creditsBtn, usernameBtn;

    public LandingPage(panel gp, LandingPageListener listener) {

        this.listener = listener;
        this.gp = gp;
        setLayout(null);

        addMouseWheelListener(e -> {

            if (menuState.equals("LEADERBOARD")) {

                leaderboardScroll -= (int) (e.getWheelRotation() * 30);

                if (leaderboardScroll > 0) {
                    leaderboardScroll = 0;
                }
                repaint();
            }
        });

        setPreferredSize(new Dimension(960, 540));
        setBackground(Color.white);
        setupGame();
        setImages();
    }

    public void setImages() {

        try {

            backgroundImg = ImageIO.read(getClass().getResource("/assets/game_pages/Actual_BG.png"));
            leaderboardBGImg = ImageIO.read(getClass().getResource("/assets/game_pages/leaderboard.png"));
            settingBGImg = ImageIO.read(getClass().getResource("/assets/game_pages/settings_look.png"));
            aboutBGImg = ImageIO.read(getClass().getResource("/assets/game_pages/about_look.png"));
            creditsBGImg = ImageIO.read(getClass().getResource("/assets/game_pages/credits_look.png"));
            usernameInputBGImg = ImageIO.read(getClass().getResource("/assets/game_pages/Actual_BG_username.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setupGame() {

        try {

            BufferedImage Icn = ImageIO.read(getClass().getResource("/assets/game_pages/Buttons.png"));

            BufferedImage startIcnNml = Icn.getSubimage(55, 61, 126, 17);
            BufferedImage startIcnHvr = Icn.getSubimage(55, 377, 126, 17);

            BufferedImage enterIcnNml = Icn.getSubimage(86, 269, 69, 21);

            BufferedImage exitIcnNml = Icn.getSubimage(96, 149, 45, 20);
            BufferedImage exitIcnHvr = Icn.getSubimage(526, 372, 46, 20);

            BufferedImage settingIcnNml = Icn.getSubimage(68, 104, 101, 18);
            BufferedImage settingIcnHvr = Icn.getSubimage(744, 380, 97, 20);

            BufferedImage leaderboardIcnNml = Icn.getSubimage(259, 346, 149, 18);
            BufferedImage leaderboardIcnHvr = Icn.getSubimage(710, 260, 152, 19);

            BufferedImage aboutIcnNml = Icn.getSubimage(300, 126, 73, 20);
            BufferedImage aboutIcnHvr = Icn.getSubimage(515, 165, 73, 20);

            BufferedImage credtsIcnNml = Icn.getSubimage(292, 194, 89, 20);
            BufferedImage credtsIcnHvr = Icn.getSubimage(508, 270, 89, 20);

            BufferedImage usernamePlaceHolder = Icn.getSubimage(36, 193, 170, 19);

            startBtn = new JButton(new ImageIcon(startIcnNml));
            startBtn.setBorderPainted(false);
            startBtn.setContentAreaFilled(false);
            startBtn.setFocusPainted(false);
            startBtn.setVisible(false);

            enterBtn = new JButton(new ImageIcon(enterIcnNml));
            enterBtn.setBorderPainted(false);
            enterBtn.setContentAreaFilled(false);
            enterBtn.setFocusPainted(false);
            enterBtn.setVisible(false);

            usernameBtn = new JButton(new ImageIcon(usernamePlaceHolder));
            usernameBtn.setBorderPainted(false);
            usernameBtn.setContentAreaFilled(false);
            usernameBtn.setFocusPainted(false);
            usernameBtn.setVisible(false);

            usernameInput = new JTextField();
            usernameInput.setBounds(395, 170, 170, 80);
            usernameInput.setOpaque(false);
            usernameInput.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            usernameInput.setForeground(Color.gray);
            usernameInput.setCaretColor(Color.WHITE);
            usernameInput.setFont(new Font("Serif", Font.BOLD, 20));
            usernameInput.setVisible(false);

            exitBtn1 = new JButton(new ImageIcon(exitIcnNml));
            exitBtn1.setBorderPainted(false);
            exitBtn1.setContentAreaFilled(false);
            exitBtn1.setFocusPainted(false);
            exitBtn1.setVisible(false);

            exitBtn2 = new JButton(new ImageIcon(exitIcnNml));
            exitBtn2.setBorderPainted(false);
            exitBtn2.setContentAreaFilled(false);
            exitBtn2.setFocusPainted(false);
            exitBtn2.setVisible(false);

            exitBtn3 = new JButton(new ImageIcon(exitIcnNml));
            exitBtn3.setBorderPainted(false);
            exitBtn3.setContentAreaFilled(false);
            exitBtn3.setFocusPainted(false);
            exitBtn3.setVisible(false);

            exitBtn4 = new JButton(new ImageIcon(exitIcnNml));
            exitBtn4.setBorderPainted(false);
            exitBtn4.setContentAreaFilled(false);
            exitBtn4.setFocusPainted(false);
            exitBtn4.setVisible(false);

            settingBtn = new JButton(new ImageIcon(settingIcnNml));
            settingBtn.setBorderPainted(false);
            settingBtn.setContentAreaFilled(false);
            settingBtn.setFocusPainted(false);
            settingBtn.setVisible(false);

            leaderboardBtn = new JButton(new ImageIcon(leaderboardIcnNml));
            leaderboardBtn.setBorderPainted(false);
            leaderboardBtn.setContentAreaFilled(false);
            leaderboardBtn.setFocusPainted(false);
            leaderboardBtn.setVisible(false);

            aboutBtn = new JButton(new ImageIcon(aboutIcnNml));
            aboutBtn.setBorderPainted(false);
            aboutBtn.setContentAreaFilled(false);
            aboutBtn.setFocusPainted(false);
            aboutBtn.setVisible(false);

            creditsBtn = new JButton(new ImageIcon(credtsIcnNml));
            creditsBtn.setBorderPainted(false);
            creditsBtn.setContentAreaFilled(false);
            creditsBtn.setFocusPainted(false);
            creditsBtn.setVisible(false);

            startBtn.setBounds(402, 300, 150, 52);
            enterBtn.setBounds(440, 275, 80, 80);
            usernameBtn.setBounds(395, 170, 170, 80);
            leaderboardBtn.setBounds(403, 347, 150, 50);
            settingBtn.setBounds(403, 392, 150, 50);

            aboutBtn.setBounds(95, 230, 150, 50);
            creditsBtn.setBounds(123, 293, 90, 50);
            exitBtn1.setBounds(402, 442, 150, 50);
            exitBtn2.setBounds(142, 360, 50, 50);
            exitBtn3.setBounds(460, 450, 50, 50);
            exitBtn4.setBounds(455, 340, 50, 50);

            fadeTimer = new javax.swing.Timer(16, e -> {

                if (fadingOut) {

                    alpha -= fadeSpeed;

                    if (alpha <= 0f) {

                        alpha = 0f;
                        fadingOut = false;
                        menuState = nextState;

                        if (onTransitionMid != null) {

                            onTransitionMid.run();
                            onTransitionMid = null;
                        }

                        fadingIn = true;
                    }

                } else if (fadingIn) {

                    alpha += (isFirstLoad ? 0.004f : 0.05f);

                    if (alpha >= 1f) {

                        alpha = 1f;
                        fadingIn = false;
                        isFirstLoad = false;

                        if (menuState.equals("MAIN")) {

                            startBtn.setVisible(true);
                            leaderboardBtn.setVisible(true);
                            settingBtn.setVisible(true);
                            exitBtn1.setVisible(true);
                        }

                        fadeTimer.stop();
                    }
                }

                repaint();
            });

            alpha = 0f;
            fadingIn = true;
            fadeTimer.start();

            startBtn.addActionListener(e -> {
                playSound("/assets/game_pages/mouseClick.wav");
                transitionTo("USERNAME", () -> {

                    startBtn.setVisible(false);
                    leaderboardBtn.setVisible(false);
                    settingBtn.setVisible(false);
                    exitBtn1.setVisible(false);
                    exitBtn4.setVisible(true);
                    enterBtn.setVisible(true);
                    usernameBtn.setVisible(true);

                });
            });

            usernameBtn.addActionListener(e -> {

                usernameBtn.setVisible(false);
                playSound("/assets/game_pages/mouseClick.wav");
                usernameInput.setText("");
                usernameInput.setVisible(true);
                repaint();
            });

            enterBtn.addActionListener(e -> {
                playSound("/assets/game_pages/mouseClick.wav");
                String username = usernameInput.getText().trim();

                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a username!");
                    return;
                }
                gp.username = username;
                gp.holder.setUsername(username);

                // debugging lang haha
                System.out.println("Username stored in holder: " + gp.holder.getUsername());

                fadeSpeed = 0.01f;
                transitionTo("GAME", () -> {
                    removeAll();
                    listener.startGame();
                    gp.startGame();
                    repaint();
                });
            });

            leaderboardBtn.addActionListener(e -> {

                playSound("/assets/game_pages/mouseClick.wav");
                gp.dbConn.showLeaderboard();
                transitionTo("LEADERBOARD", () -> {

                    startBtn.setVisible(false);
                    leaderboardBtn.setVisible(false);
                    settingBtn.setVisible(false);
                    exitBtn1.setVisible(false);
                    exitBtn3.setVisible(true);
                });
            });

            settingBtn.addActionListener(e -> {

                playSound("/assets/game_pages/mouseClick.wav");
                transitionTo("SETTINGS", () -> {

                    startBtn.setVisible(false);
                    leaderboardBtn.setVisible(false);
                    settingBtn.setVisible(false);
                    exitBtn1.setVisible(false);
                    aboutBtn.setVisible(true);
                    creditsBtn.setVisible(true);
                    exitBtn2.setVisible(true);
                });
            });

            exitBtn1.addActionListener(e -> {

                playSound("/assets/game_pages/mouseClick.wav");
                int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }

            });

            aboutBtn.addActionListener(e -> {

                playSound("/assets/game_pages/mouseClick.wav");
                menuState = "ABOUT";
                exitBtn2.setVisible(true);
                repaint();
            });

            creditsBtn.addActionListener(e -> {

                playSound("/assets/game_pages/mouseClick.wav");
                menuState = "CREDITS";
                exitBtn2.setVisible(true);
                repaint();
            });

            exitBtn2.addActionListener(e -> {

                playSound("/assets/game_pages/mouseClick.wav");
                transitionTo("MAIN", () -> {
                    startBtn.setVisible(true);
                    leaderboardBtn.setVisible(true);
                    settingBtn.setVisible(true);
                    exitBtn1.setVisible(true);
                    aboutBtn.setVisible(false);
                    creditsBtn.setVisible(false);
                    exitBtn2.setVisible(false);
                });
            });

            exitBtn3.addActionListener(e -> {

                playSound("/assets/game_pages/mouseClick.wav");
                transitionTo("MAIN", () -> {

                    startBtn.setVisible(true);
                    leaderboardBtn.setVisible(true);
                    settingBtn.setVisible(true);
                    exitBtn1.setVisible(true);
                    exitBtn3.setVisible(false);
                });
            });

            exitBtn4.addActionListener(e -> {

                playSound("/assets/game_pages/mouseClick.wav");
                transitionTo("MAIN", () -> {

                    startBtn.setVisible(true);
                    leaderboardBtn.setVisible(true);
                    settingBtn.setVisible(true);
                    exitBtn1.setVisible(true);
                    enterBtn.setVisible(false);
                    usernameBtn.setVisible(false);
                    exitBtn4.setVisible(false);

                    if (usernameInput != null) {

                        usernameInput.setVisible(false);
                    }
                });
            });

            startBtn.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {

                    startBtn.setIcon(new ImageIcon(startIcnHvr));
                    startBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                public void mouseExited(MouseEvent e) {

                    startBtn.setIcon(new ImageIcon(startIcnNml));
                }
            });

            enterBtn.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {

                    enterBtn.setIcon(new ImageIcon(enterIcnNml));
                    enterBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                public void mouseExited(MouseEvent e) {

                    enterBtn.setIcon(new ImageIcon(enterIcnNml));
                }
            });
            usernameBtn.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {

                    usernameBtn.setIcon(new ImageIcon(usernamePlaceHolder));
                    usernameBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                public void mouseExited(MouseEvent e) {

                    usernameBtn.setIcon(new ImageIcon(usernamePlaceHolder));
                }
            });
            leaderboardBtn.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {

                    leaderboardBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    leaderboardBtn.setIcon(new ImageIcon(leaderboardIcnHvr));
                }

                public void mouseExited(MouseEvent e) {

                    leaderboardBtn.setIcon(new ImageIcon(leaderboardIcnNml));
                }
            });

            settingBtn.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {

                    settingBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    settingBtn.setIcon(new ImageIcon(settingIcnHvr));
                }

                public void mouseExited(MouseEvent e) {

                    settingBtn.setIcon(new ImageIcon(settingIcnNml));
                }
            });

            aboutBtn.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    aboutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    aboutBtn.setIcon(new ImageIcon(aboutIcnHvr));
                }

                public void mouseExited(MouseEvent e) {
                    aboutBtn.setIcon(new ImageIcon(aboutIcnNml));
                }
            });

            creditsBtn.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    creditsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    creditsBtn.setIcon(new ImageIcon(credtsIcnHvr));
                }

                public void mouseExited(MouseEvent e) {
                    creditsBtn.setIcon(new ImageIcon(credtsIcnNml));
                }
            });

            exitBtn1.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    exitBtn1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    exitBtn1.setIcon(new ImageIcon(exitIcnHvr));
                }

                public void mouseExited(MouseEvent e) {
                    exitBtn1.setIcon(new ImageIcon(exitIcnNml));
                }
            });

            exitBtn2.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    exitBtn2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    exitBtn2.setIcon(new ImageIcon(exitIcnHvr));
                }

                public void mouseExited(MouseEvent e) {
                    exitBtn2.setIcon(new ImageIcon(exitIcnNml));
                }
            });

            exitBtn3.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                    exitBtn3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    exitBtn3.setIcon(new ImageIcon(exitIcnHvr));
                }

                public void mouseExited(MouseEvent e) {
                    exitBtn3.setIcon(new ImageIcon(exitIcnNml));
                }
            });

            exitBtn4.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {

                    exitBtn4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    exitBtn4.setIcon(new ImageIcon(exitIcnHvr));
                }

                public void mouseExited(MouseEvent e) {

                    exitBtn4.setIcon(new ImageIcon(exitIcnNml));
                }
            });

            add(startBtn);
            add(enterBtn);
            add(usernameBtn);
            add(usernameInput);
            add(leaderboardBtn);
            add(settingBtn);
            add(aboutBtn);
            add(creditsBtn);
            add(exitBtn1);
            add(exitBtn2);
            add(exitBtn3);
            add(exitBtn4);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        String output = gp.dbConn.showLeaderboard();
        int y = 190 + leaderboardScroll;

        switch (menuState) {

            case "LEADERBOARD":
                g.drawImage(leaderboardBGImg, 0, 0, getWidth(), getHeight(), null);

                Shape oldClip = g.getClip();

                g.setClip(0, 170, getWidth(), 290);
                g.setFont(new Font("Serif", Font.BOLD, 27));
                g.setColor(new Color(210, 200, 180));

                for (String line : output.split("\n")) {

                    String[] parts = line.split("\\s{2,}");
                    g.drawString(parts[0], 150, y);

                    if (parts.length > 1) {
                        g.drawString(parts[1], 673, y);
                    }
                    y += 50;
                }

                g.setClip(oldClip);

                break;

            case "SETTINGS":
                g.drawImage(settingBGImg, 0, 0, getWidth(), getHeight(), null);
                break;

            case "ABOUT":
                g.drawImage(aboutBGImg, 0, 0, getWidth(), getHeight(), null);
                break;

            case "CREDITS":
                g.drawImage(creditsBGImg, 0, 0, getWidth(), getHeight(), null);
                break;

            case "USERNAME":
                g.drawImage(usernameInputBGImg, 0, 0, getWidth(), getHeight(), null);
                break;

            default:
                g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
                break;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - alpha));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void playSound(String path) {

        try {

            var audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(getClass().getResource(path));
            var clip = javax.sound.sampled.AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideAllButtons() {

        startBtn.setVisible(false);
        enterBtn.setVisible(false);
        usernameBtn.setVisible(false);
        usernameInput.setVisible(false);
        leaderboardBtn.setVisible(false);
        settingBtn.setVisible(false);
        aboutBtn.setVisible(false);
        creditsBtn.setVisible(false);
        exitBtn1.setVisible(false);
        exitBtn2.setVisible(false);
        exitBtn3.setVisible(false);
        exitBtn4.setVisible(false);
    }

    private void transitionTo(String state, Runnable onMid) {

        hideAllButtons();
        nextState = state;
        onTransitionMid = onMid;
        fadingOut = true;
        fadeTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public interface LandingPageListener {

        void startGame();
    }

}
