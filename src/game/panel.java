package game;

import models.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class panel extends JPanel implements Runnable {

    final int origtileSize = 16;
    public final int charHeight = origtileSize * 2;
    public final int charWidth = origtileSize;

    final int scale = 3;

    public final int tileSize = origtileSize * scale; //48x48 tile
    final int screenCol = 20;
    final int screenRow = 11;

    public final int screenWidth = 960;
    public final int screenheight = 540;

    public final int maxWorldCol = 60;
    public final int maxWorldRow = 45;
    
    KeyHandler keyH = new KeyHandler();
    TileManager tileM = new TileManager(this);
    public CollisionChecker cChecker = new CollisionChecker(this);
    Player player = new Player(this, keyH);
    public ObjectManager objectM = new ObjectManager(this);
    Thread GameThread;
    
    

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

        tileM.draw(g2);
        objectM.draw(g2);
        player.draw(g2);

        g2.dispose();
    }

}
