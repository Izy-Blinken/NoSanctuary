
package game;

import models.Monster;
import models.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
    public Monster monster = new Monster(this);
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

    // Track previous night state to spawn monster once per night
    private boolean wasNight = false;

    public void update() {
        
        // Spawn monster at the start of each night
        boolean isNight = (dC.currentState == dayCounter.dayNightState.Night
                        || dC.currentState == dayCounter.dayNightState.NightTitle
                        || dC.currentState == dayCounter.dayNightState.Sunset
                        || dC.currentState == dayCounter.dayNightState.Sunrise);
        if (isNight && !wasNight) {
            monster.spawnNearEdge();
        }
        wasNight = isNight;

        player.update();
        monster.update();
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
        if (tileM.currentMap == 1) { monster.draw(g2); }
        player.draw(g2);
        dC.draw(g2);
        dC.drawOverlay(g2);
        monster.drawAlert(g2);
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

        // Extra darkness + warning when torch is out at night
        boolean isNight = dC.currentState == dayCounter.dayNightState.Night
                       || dC.currentState == dayCounter.dayNightState.Sunset
                       || dC.currentState == dayCounter.dayNightState.Sunrise;

        if (isNight && !objectM.interior.torch.isLit) {
            
            g2.setColor(new Color(0, 0, 8, 130));
            g2.fillRect(0, 0, screenWidth, screenheight);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.setColor(new Color(255, 60, 60, 210));
            g2.drawString("Torch is out!", screenWidth / 2 - 260, screenheight / 2);
        }
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
