package game;

import models.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;
import javax.swing.JPanel;

public class panel extends JPanel implements Runnable {

    final int origtileSize = 16;
    public final int charHeight = origtileSize * 2;
    public final int charWidth = origtileSize;
    final int scale = 3;

    public final int tileSize = origtileSize * scale;
    public final int screenWidth = 960;
    public final int screenheight = 540;
    public final int maxWorldCol = 60;
    public final int maxWorldRow = 45;

    KeyHandler keyH = new KeyHandler();
    public TileManager tileM = new TileManager(this);
    public CollisionChecker cChecker = new CollisionChecker(this);
    public Player  player = new Player(this, keyH);
    public ObjectManager objectM = new ObjectManager(this);
    public dayCounter dC = new dayCounter(this);
    public Inventory inventory = new Inventory(this);
    public InteractionChecker interactionChecker = new InteractionChecker(this);
    Thread GameThread;

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
        
        double drawInterval = 1000000000 / fps;
        double nextDrawTime = System.nanoTime() + drawInterval;
        
        while (GameThread != null) {
            
            update();
            repaint();
            
            try {
                
                double remaining = nextDrawTime - System.nanoTime();
                remaining /= 1000000;
                if (remaining < 0) remaining = 0;
                Thread.sleep((long) remaining);
                nextDrawTime += drawInterval;
                
            } catch (InterruptedException ex) { 
                ex.printStackTrace(); 
            }
        }
    }

    public void update() {
        
        player.update();
        objectM.update();
        
        if (tileM.currentMap == 1) {
            
            interactionChecker.checkInteraction();
        }
        else{
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
        inventory.draw(g2);
        drawHUD(g2);
        g2.dispose();
    }

    private void drawHUD(Graphics2D g2) {
        
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(Color.WHITE);

        if (tileM.currentMap == 1) {
            
            if (interactionChecker.showDoorPrompt) {
                
                models.ObjHouse house = (models.ObjHouse) objectM.ObjHouse[0];
                String prompt = house.isDoorOpen ? "E - Enter | F - Close Door" : "E - Open Door";
                g2.drawString(prompt, screenWidth / 2 - 120, screenheight - 60);
            }
            
            return;
        }

        // Exit door
        if (interactionChecker.showExitPrompt) {
            
            models.ObjHouse house = (models.ObjHouse) objectM.ObjHouse[0];
            String prompt = house.isDoorOpen ? "E - Exit | F - Close Door" : "F - Open Door";
            g2.drawString(prompt, screenWidth / 2 - 120, screenheight - 60);
        }

        // Window
        if (interactionChecker.showWindowPrompt) {
            
            g2.drawString("E - Open/Close Window", screenWidth / 2 - 120, screenheight - 60);
        }

        // Torch
        if (interactionChecker.showTorchPrompt) {
            
            models.ObjTorch torch = objectM.interior.torch;
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.setColor(torch.isLit ? new Color(255, 200, 50) : new Color(160, 160, 160));
            g2.drawString(torch.isLit ? "Torch burning — " + torch.getSecondsLeft() + "s left" : "Torch is OUT", screenWidth / 2 - 130, screenheight - 90);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.drawString("E - Fuel Torch  (costs 3 wood)", screenWidth / 2 - 160, screenheight - 60);
        }

        if (interactionChecker.hasTorchFeedback()) {
            
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.setColor(new Color(255, 210, 60));
            g2.drawString(interactionChecker.torchFeedback, screenWidth / 2 - 160, screenheight - 120);
        }

        // Cabinet (wood)
        if (interactionChecker.showCabinetPrompt && !objectM.interior.cabinet.isOpen) {
            
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.setColor(Color.WHITE);
            g2.drawString("P - Open Cabinet", screenWidth / 2 - 130, screenheight - 60);
        }

        if (interactionChecker.hasCabinetFeedback()) {
            
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.setColor(new Color(200, 160, 80));
            g2.drawString(interactionChecker.cabinetFeedback, screenWidth / 2 - 150, screenheight - 120);
        }

        // Apple table
        if (interactionChecker.showAppleTablePrompt && !objectM.interior.appleTable.isOpen) {
            
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.setColor(Color.WHITE);
            g2.drawString("E - Open Table (Apple)", screenWidth / 2 - 120, screenheight - 60);
        }

        if (interactionChecker.hasAppleTableFeedback()) {
            
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.setColor(new Color(130, 210, 100));
            g2.drawString(interactionChecker.appleTableFeedback, screenWidth / 2 - 150, screenheight - 120);
        }

        // Night darkness with torch light effect
        boolean isNight = dC.currentState == dayCounter.dayNightState.Night
                       || dC.currentState == dayCounter.dayNightState.Sunset
                       || dC.currentState == dayCounter.dayNightState.Sunrise;

        if (isNight && tileM.currentMap == 2) {
            
            drawNightOverlay(g2);
        }

        if (isNight && tileM.currentMap == 2 && !objectM.interior.isInteriorLit()) {
            
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.setColor(new Color(255, 60, 60, 210));
            g2.drawString("Torch is out!", screenWidth / 2 - 260, screenheight / 2);
        }
    }

    private void drawNightOverlay(Graphics2D g2) {
        
        // draw darkness on a separate image so we can punch light holes into it
        BufferedImage darkLayer = new BufferedImage(screenWidth, screenheight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D dg = darkLayer.createGraphics();

        // fill the whole screen dark
        dg.setColor(new Color(0, 0, 8, 180));
        dg.fillRect(0, 0, screenWidth, screenheight);

        // punch a light hole for each lit torch using DST_OUT
        dg.setComposite(AlphaComposite.DstOut);

        models.ObjTorch[] torches = {objectM.interior.torch, objectM.interior.torch2, objectM.interior.torch3};

        for (models.ObjTorch t : torches) {
            
            if (!t.isLit) continue;

            int cx = t.worldX - player.worldX + player.screenX + 20;
            int cy = t.worldY - player.worldY + player.screenY + 10;
            float radius = 160f;

            RadialGradientPaint gradient = new RadialGradientPaint(
                new Point2D.Float(cx, cy),
                radius,
                new float[]{0f, 0.5f, 1f},
                new Color[]{
                    new Color(255, 160, 40, 220),
                    new Color(255, 100, 10, 100),
                    new Color(0, 0, 0, 0)
                }
            );

            dg.setPaint(gradient);
            dg.fillOval(cx - (int)radius, cy - (int)radius, (int)radius * 2, (int)radius * 2);
        }

        dg.dispose();

        // draw the darkness layer with holes onto screen
        g2.drawImage(darkLayer, 0, 0, null);
    }

    public void switchToInterior() {
        
        tileM.switchMap(2);
        player.worldX = 160;
        player.worldY = 144;
    }

    public void switchToExterior() {
        
        tileM.switchMap(1);
        player.worldX = 1455;
        player.worldY = 1056;
    }

    public boolean isPlayerSafe() {
        
        if (tileM.currentMap != 2) {
            return false;
        }
        
        models.ObjHouse house = (models.ObjHouse) objectM.ObjHouse[0];
        return !house.isDoorOpen && !house.isWindow1Open && !house.isWindow2Open && !house.isWindow3Open;
    }
}