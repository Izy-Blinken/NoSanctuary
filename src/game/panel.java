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
    public TileManager tileM = new TileManager(this);
    public CollisionChecker cChecker = new CollisionChecker(this);
    public Player player = new Player(this, keyH);
    public ObjectManager objectM = new ObjectManager(this);
    public dayCounter dC = new dayCounter(this);
    Thread GameThread;
    public InteractionChecker interactionChecker = new InteractionChecker(this);

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
        if (tileM.currentMap == 1) {
            interactionChecker.checkInteraction();
        } else {
            interactionChecker.checkInteriorInteraction();
        }

        dC.update();
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        tileM.draw(g2);
        objectM.draw(g2);
        player.draw(g2);
        dC.draw(g2);
        dC.drawOverlay(g2);

        g2.setColor(java.awt.Color.WHITE);
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));

        if (tileM.currentMap == 1) {
            if (interactionChecker.showDoorPrompt) {
                
                models.ObjHouse house = (models.ObjHouse) objectM.ObjHouse[0];
                String prompt = house.isDoorOpen ? "E - Enter | F - Close Door" : "E - Open Door";
                g2.drawString(prompt, screenWidth / 2 - 120, screenheight - 60);
            }
        } else {
            if (interactionChecker.showExitPrompt) {
                if (interactionChecker.showExitPrompt) {
                    
                    models.ObjHouse house = (models.ObjHouse) objectM.ObjHouse[0];
                    String prompt = house.isDoorOpen ? "E - Exit | F - Close Door" : "F - Open Door";
                    g2.drawString(prompt, screenWidth / 2 - 120, screenheight - 60);
                }
            }
            
            if (interactionChecker.showWindowPrompt) {
                g2.drawString("E - Open/Close Window", screenWidth / 2 - 120, screenheight - 60);
            }
        }

        g2.dispose();
    }

    public void switchToInterior() {
        tileM.switchMap(2);

        player.worldX = 48 * 12;
        player.worldY = 48 * 3;
    }

    public void switchToExterior() {
        tileM.switchMap(1);

        player.worldX = 1300 + (int) (86 * 2.5) + 10;
        player.worldY = 1350 + (int) (50 * 2.5) + 40;
    }
    
    public boolean isPlayerSafe() {
        
        if (tileM.currentMap != 2){
            
            return false;
        }
        
        models.ObjHouse house = (models.ObjHouse) objectM.ObjHouse[0];
        return !house.isDoorOpen && !house.isWindow1Open && !house.isWindow2Open && !house.isWindow3Open;
    }
}
