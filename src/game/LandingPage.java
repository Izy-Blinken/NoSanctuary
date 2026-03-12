package game;

import java.awt.Dimension;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class LandingPage extends JPanel implements ActionListener {

    panel gp;
    private LandingPageListener listener;
    private Image backgroundImg, leaderboardBGImg, settingBGImg, aboutBGImg, creditsBGImg, usernameInputBGImg;

    public JTextField usernameInput;

    private JSlider volumeSlider;//sound
    private FloatControl gainControl;//sound

    private String menuState = "MAIN";
    private Clip bgMusic;

    private JButton startBtn, enterBtn, leaderboardBtn, aboutBtn, exitBtn1, exitBtn2, exitBtn3, exitBtn4, settingBtn, volumeBtn, creditsBtn, usernameBtn;

    public LandingPage(panel gp, LandingPageListener listener) {
        this.listener = listener;
        this.gp = gp;
        setLayout(null);

        setPreferredSize(new Dimension(960, 540));
        setBackground(Color.white);
        setupGame();
        playBackgroundMusic("/assets/game_music/landingPage_music.wav");
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

            BufferedImage volumeIcnNml = Icn.getSubimage(291, 61, 81, 20);
            BufferedImage volumeIcnHvr = Icn.getSubimage(505, 60, 85, 21);

            BufferedImage aboutIcnNml = Icn.getSubimage(300, 126, 73, 20);
            BufferedImage aboutIcnHvr = Icn.getSubimage(515, 165, 73, 20);

            BufferedImage credtsIcnNml = Icn.getSubimage(292, 194, 89, 20);
            BufferedImage credtsIcnHvr = Icn.getSubimage(508, 270, 89, 20);

            BufferedImage usernamePlaceHolder = Icn.getSubimage(36, 193, 170, 19);

            startBtn = new JButton(new ImageIcon(startIcnNml));
            startBtn.setBorderPainted(false);
            startBtn.setContentAreaFilled(false);
            startBtn.setFocusPainted(false);

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
            usernameInput.setFont(new Font("monospaced", Font.BOLD, 20));
            usernameInput.setVisible(false);

            exitBtn1 = new JButton(new ImageIcon(exitIcnNml));
            exitBtn1.setBorderPainted(false);
            exitBtn1.setContentAreaFilled(false);
            exitBtn1.setFocusPainted(false);

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

            leaderboardBtn = new JButton(new ImageIcon(leaderboardIcnNml));
            leaderboardBtn.setBorderPainted(false);
            leaderboardBtn.setContentAreaFilled(false);
            leaderboardBtn.setFocusPainted(false);

            volumeBtn = new JButton(new ImageIcon(volumeIcnNml));
            volumeBtn.setBorderPainted(false);
            volumeBtn.setContentAreaFilled(false);
            volumeBtn.setFocusPainted(false);
            volumeBtn.setVisible(false);

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

            volumeBtn.setBounds(90, 164, 150, 50);
            aboutBtn.setBounds(95, 230, 150, 50);
            creditsBtn.setBounds(123, 293, 90, 50);
            exitBtn1.setBounds(402, 442, 150, 50);
            exitBtn2.setBounds(142, 360, 50, 50);
            exitBtn3.setBounds(460, 450, 50, 50);
            exitBtn4.setBounds(455, 340, 50, 50);

            volumeSlider = new JSlider(0, 100, 50);
            volumeSlider.setBounds(90, 200, 200, 40);
            volumeSlider.setVisible(false);
            volumeSlider.setOpaque(false);

            startBtn.addActionListener(e -> {
                menuState = "USERNAME";
                playSound("/assets/game_pages/mouseClick.wav");
                startBtn.setVisible(false);
                leaderboardBtn.setVisible(false);
                settingBtn.setVisible(false);
                exitBtn1.setVisible(false);

                exitBtn4.setVisible(true);
                enterBtn.setVisible(true);
                usernameBtn.setVisible(true);

                repaint();
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
                gp.playerID = gp.dbConn.insert(username, 0);
                gp.holder.setPlayerID(gp.playerID);

                System.out.println("Inserting: " + gp.playerID + "\n" + "Current Completion time: " + gp.dC.completion_time + "\n");

                removeAll();
                listener.startGame();
                gp.startGame();

                bgMusic.stop();
                repaint();
            });

            leaderboardBtn.addActionListener(e -> {

                menuState = "LEADERBOARD";
                playSound("/assets/game_pages/mouseClick.wav");
                startBtn.setVisible(false);
                leaderboardBtn.setVisible(false);
                settingBtn.setVisible(false);
                exitBtn1.setVisible(false);
                exitBtn3.setVisible(true);
                gp.dbConn.showLeaderboard();
                repaint();
            });
            settingBtn.addActionListener(e -> {

                menuState = "SETTINGS";
                playSound("/assets/game_pages/mouseClick.wav");

                startBtn.setVisible(false);
                leaderboardBtn.setVisible(false);
                settingBtn.setVisible(false);
                exitBtn1.setVisible(false);
                volumeBtn.setVisible(true);
                aboutBtn.setVisible(true);
                creditsBtn.setVisible(true);
                exitBtn2.setVisible(true);
                repaint();
            });
            exitBtn1.addActionListener(e -> {
                System.out.print("haha");
                playSound("/assets/game_pages/mouseClick.wav");
                System.exit(0);
            });
            volumeBtn.addActionListener(e -> {
                playSound("/assets/game_pages/mouseClick.wav");
                startBtn.setVisible(false);
                leaderboardBtn.setVisible(false);
                settingBtn.setVisible(false);
                exitBtn1.setVisible(false);
                volumeBtn.setVisible(true);
                aboutBtn.setVisible(true);
                creditsBtn.setVisible(true);
            });
            aboutBtn.addActionListener(e -> {

                menuState = "ABOUT";
                playSound("/assets/game_pages/mouseClick.wav");
                System.out.print("about");
                startBtn.setVisible(false);
                leaderboardBtn.setVisible(false);
                settingBtn.setVisible(false);
                exitBtn1.setVisible(false);
                volumeBtn.setVisible(true);
                aboutBtn.setVisible(true);
                creditsBtn.setVisible(true);
                repaint();
            });
            creditsBtn.addActionListener(e -> {
                playSound("/assets/game_pages/mouseClick.wav");
                menuState = "CREDITS";

                startBtn.setVisible(false);
                leaderboardBtn.setVisible(false);
                settingBtn.setVisible(false);
                exitBtn1.setVisible(false);
                volumeBtn.setVisible(true);
                aboutBtn.setVisible(true);
                creditsBtn.setVisible(true);
                repaint();
            });

            exitBtn2.addActionListener(e -> {
                playSound("/assets/game_pages/mouseClick.wav");
                menuState = "MAIN";

                startBtn.setVisible(true);
                leaderboardBtn.setVisible(true);
                settingBtn.setVisible(true);
                exitBtn1.setVisible(true);

                volumeBtn.setVisible(false);
                aboutBtn.setVisible(false);
                creditsBtn.setVisible(false);
                exitBtn2.setVisible(false);
                repaint();
            });

            exitBtn3.addActionListener(e -> {
                playSound("/assets/game_pages/mouseClick.wav");
                menuState = "MAIN";

                startBtn.setVisible(true);
                leaderboardBtn.setVisible(true);
                settingBtn.setVisible(true);
                exitBtn1.setVisible(true);

                exitBtn3.setVisible(false);
                repaint();
            });
            exitBtn4.addActionListener(e -> {
                playSound("/assets/game_pages/mouseClick.wav");
                menuState = "MAIN";

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

                repaint();
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
            volumeBtn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    volumeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    volumeBtn.setIcon(new ImageIcon(volumeIcnHvr));
                }

                public void mouseExited(MouseEvent e) {
                    volumeBtn.setIcon(new ImageIcon(volumeIcnNml));
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
            add(volumeBtn);
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
        int y = 190;
        switch (menuState) {
            case "LEADERBOARD":
                g.drawImage(leaderboardBGImg, 0, 0, getWidth(), getHeight(), null);
                g.setFont(new Font("monospaced", Font.BOLD, 30));
                g.setColor(Color.WHITE);
                for (String line : output.split("\n")) {
                    g.drawString(line, 150, y);
                    y += 50;
                }
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

    }

    private void playBackgroundMusic(String path) {
        try {

            AudioInputStream audioIn
                    = AudioSystem.getAudioInputStream(getClass().getResource(path));

            bgMusic = AudioSystem.getClip();
            bgMusic.open(audioIn);

            gainControl = (FloatControl) bgMusic.getControl(FloatControl.Type.MASTER_GAIN);

            bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
            bgMusic.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public interface LandingPageListener {

        void startGame();
    }

}
