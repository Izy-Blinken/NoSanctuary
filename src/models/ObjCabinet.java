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

        int w = (int) (src.getWidth() * scale);
        int h = (int) (src.getHeight() * scale);

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();

        return out;
    }

    public void toggleUI() {
        
        if (!isOpen && gp.objectM.interior != null && gp.objectM.interior.appleTable != null) {
            gp.objectM.interior.appleTable.closeUI();
        }
        
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

    public void drawUI(Graphics2D g2, int screenX, int screenY) {
        if (!isOpen) {
            return;
        }

        int panelW = 220, panelH = 110;
        int px = screenX - 90, py = screenY - 125;

        //background 
        g2.setColor(new Color(30, 28, 25, 225));
        g2.fillRect(px, py, panelW, panelH);

        // title
        Font imFell = getImFell(14f);
        g2.setFont(imFell);
        g2.setColor(new Color(210, 205, 195));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("Wood Storage", px + 12, py + 22);

        // accent line under title 
        g2.setColor(new Color(65, 60, 55));
        g2.setStroke(new java.awt.BasicStroke(1f));
        g2.drawLine(px + 10, py + 28, px + panelW - 10, py + 28);

        g2.setFont(getImFell(12f));
        g2.setColor(new Color(140, 135, 128));
        g2.drawString("Wood:  " + storedWood + " / " + MAX_WOOD, px + 12, py + 50);

        // accent line
        g2.setColor(new Color(65, 60, 55));
        g2.drawLine(px + 10, py + 60, px + panelW - 10, py + 60);

        g2.setFont(getImFell(11f));
        g2.setColor(new Color(140, 135, 128));
        g2.drawString("P \u2014 Deposit All    R \u2014 Withdraw All", px + 12, py + 78);
        g2.drawString("E \u2014 Close", px + 12, py + 96);
    }

    private Font getImFell(float size) {
        
        try {
            
            java.io.InputStream is = getClass().getResourceAsStream( "/assets/game_ui/fonts/IMFellEnglish-Regular.ttf");
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
            
        } catch (Exception e) {
            return new Font("Serif", Font.PLAIN, (int) size);
        }
    }
}
