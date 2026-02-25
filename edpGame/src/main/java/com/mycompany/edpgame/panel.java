package com.mycompany.edpgame;

import entity.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class panel extends JPanel implements Runnable {

    final int origtileSize = 16;
    public final int charWeight = origtileSize * 2;
    public final int charWidth = origtileSize;

    final int scale = 3;

    public final int tileSize = origtileSize * scale; //48x48 tile
    final int screenCol = 45;
    final int screenRow = 80;

    public final int screenWidth = 960;
    public final int screenheight = 540;

    KeyHandler keyH = new KeyHandler();
    Thread GameThread;

    Player player = new Player(this, keyH);

    int playerY = 100;
    int playerX = 100;
    int speed = 4;

    int fps = 60;

    public panel() {

        this.setPreferredSize(new Dimension(screenWidth, screenheight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    public void startThread() {

        GameThread = new Thread(this);
        GameThread.start();

    }

    @Override
    public void run() {
        double drawInterva = 1000000000 / fps;
        double nextDrawTime = System.nanoTime() + drawInterva;

        while (GameThread != null) {

            System.out.print("runnig");
            long CurrentTime = System.nanoTime();

            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterva;

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void update() {

        player.update();

    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        player.draw(g2);

        g2.dispose();
    }

}
