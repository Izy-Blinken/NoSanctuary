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

public class ObjCabinet extends GameObject {

    panel gp;

    public int storedWood = 0;
    public static final int MAX_WOOD = 20;
    public boolean isOpen = false;

    private BufferedImage cabinetSprite;

    public ObjCabinet(panel gp) {
        
        this.gp = gp;
        collision = true;
        solidArea = new Rectangle(2, 10, 111, 105);
        loadSprites();
    }

    private void loadSprites() {
        
        try {
            
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/assets/EX_INT PNG/interior.png"));
            cabinetSprite = scaleImg(sheet.getSubimage(81, 101, 46, 52), 2.5);
            image = cabinetSprite;
            
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

    /* deposit/withdraw woods from inventory to cabinet */
    public int depositWood(Inventory inv) {
        
        int toStore = Math.min(inv.wood, MAX_WOOD - storedWood);
        storedWood += toStore;
        inv.wood -= toStore;
        
        return toStore;
    }

    public int withdrawWood(Inventory inv) {
        
        int toTake = Math.min(storedWood, inv.MAX_WOOD - inv.wood);
        inv.wood += toTake;
        storedWood -= toTake;
        
        return toTake;
    }

    public void draw(Graphics2D g2, int screenX, int screenY) {
        
        g2.drawImage(image, screenX, screenY, null);
    }

    /* overlay for wood storage ui  */
    public void drawUI(Graphics2D g2, int screenX, int screenY) {
        
        if (!isOpen) {
            return;
        }

        int panelW = 210, panelH = 100;
        int px = screenX - 80, py = screenY - 115;

        g2.setColor(new Color(20, 10, 10, 220));
        g2.fillRoundRect(px, py, panelW, panelH, 10, 10);
        g2.setColor(new Color(100, 60, 30));
        g2.setStroke(new java.awt.BasicStroke(2));
        g2.drawRoundRect(px, py, panelW, panelH, 10, 10);

        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.setColor(new Color(200, 160, 80));
        g2.drawString("Wood Storage", px + 12, py + 20);

        g2.setColor(new Color(100, 60, 30, 150));
        g2.drawLine(px + 10, py + 26, px + panelW - 10, py + 26);

        g2.setColor(new Color(160, 100, 40));
        g2.fillRect(px + 12, py + 36, 14, 14);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.setColor(Color.WHITE);
        g2.drawString("Wood:  " + storedWood + " / " + MAX_WOOD, px + 30, py + 48);

        g2.setColor(new Color(100, 60, 30, 100));
        g2.drawLine(px + 10, py + 58, px + panelW - 10, py + 58);

        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.setColor(new Color(180, 180, 130));
        g2.drawString("P - Deposit All  |  R - Withdraw All", px + 12, py + 74);
        g2.drawString("E - Close", px + 12, py + 90);
    }
}