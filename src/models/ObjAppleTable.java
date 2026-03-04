package models;

import game.Inventory;
import game.panel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ObjAppleTable extends GameObject {

    panel gp;

    public int storedApple = 0;
    public static final int MAX_APPLE = 20;
    public boolean isOpen = false;

    private BufferedImage tableSprite;

    public ObjAppleTable(panel gp) {
        
        this.gp = gp;
        collision = true;
        solidArea = new Rectangle(2, 10, 61, 75);
        loadSprites();
    }

    private void loadSprites() {
        
        try {
            
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/assets/EX_INT PNG/interior.png"));
            tableSprite = scaleImg(sheet.getSubimage(132, 58, 26, 32), 2.5);
            image = tableSprite;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage scaleImg(BufferedImage src, double scale) {
        
        int w = (int)(src.getWidth() * scale);
        int h = (int)(src.getHeight() * scale);
        
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        
        return out;
    }

    public void toggleUI() { 
        
        isOpen = !isOpen; 
    }
    
    public void closeUI() { 
        
        isOpen = false; 
    }

    /* deposit/withdraw apple from inventory to cabinet */
    public int depositApple(Inventory inv) {
        
        int toStore = Math.min(inv.apple, MAX_APPLE - storedApple);
        storedApple += toStore;
        inv.apple -= toStore;
        
        return toStore;
    }

    public int withdrawApple(Inventory inv) {
        
        int toTake = Math.min(storedApple, inv.MAX_APPLE - inv.apple);
        inv.apple += toTake;
        storedApple -= toTake;
        
        return toTake;
    }

    public void draw(Graphics2D g2, int screenX, int screenY) {
        
        g2.drawImage(image, screenX, screenY, null);
    }

    /* overlay for apple storage ui */
    public void drawUI(Graphics2D g2, int screenX, int screenY) {
        
        if (!isOpen) {
            return;
        }

        int panelW = 210, panelH = 100;
        int px = screenX - 80, py = screenY - 115;

        g2.setColor(new Color(10, 20, 10, 220));
        g2.fillRoundRect(px, py, panelW, panelH, 10, 10);
        g2.setColor(new Color(60, 100, 40));
        g2.setStroke(new java.awt.BasicStroke(2));
        g2.drawRoundRect(px, py, panelW, panelH, 10, 10);

        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.setColor(new Color(160, 210, 100));
        g2.drawString("Table  (Apple Storage)", px + 12, py + 20);

        g2.setColor(new Color(60, 100, 40, 150));
        g2.drawLine(px + 10, py + 26, px + panelW - 10, py + 26);

        g2.setColor(new Color(180, 50, 50));
        g2.fillOval(px + 12, py + 36, 14, 14);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.setColor(Color.WHITE);
        g2.drawString("Apple: " + storedApple + " / " + MAX_APPLE, px + 30, py + 48);

        g2.setColor(new Color(60, 100, 40, 100));
        g2.drawLine(px + 10, py + 58, px + panelW - 10, py + 58);

        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.setColor(new Color(180, 210, 150));
        g2.drawString("B - Deposit All  |  C - Withdraw All", px + 12, py + 74);
        g2.drawString("E - Close", px + 12, py + 90);
    }
}