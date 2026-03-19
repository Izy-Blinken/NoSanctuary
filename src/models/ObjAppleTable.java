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
        // Close other storage UIs if opening this one
        if (!isOpen && gp.objectM.interior != null && gp.objectM.interior.cabinet != null) {
            gp.objectM.interior.cabinet.closeUI();
        }
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
        g2.drawString("Apple Storage", px + 12, py + 22);

        // accent line under title 
        g2.setColor(new Color(65, 60, 55));
        g2.setStroke(new java.awt.BasicStroke(1f));
        g2.drawLine(px + 10, py + 28, px + panelW - 10, py + 28);

        g2.setFont(getImFell(12f));
        g2.setColor(new Color(140, 135, 128));
        g2.drawString("Apple:  " + storedApple + " / " + MAX_APPLE, px + 12, py + 50);

        // accent line 
        g2.setColor(new Color(65, 60, 55));
        g2.drawLine(px + 10, py + 60, px + panelW - 10, py + 60);

        g2.setFont(getImFell(11f));
        g2.setColor(new Color(140, 135, 128));
        g2.drawString("B \u2014 Deposit All    C \u2014 Withdraw All", px + 12, py + 78);
        g2.drawString("E \u2014 Close", px + 12, py + 96);
    }

    private Font getImFell(float size) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream(
                    "/assets/game_ui/fonts/IMFellEnglish-Regular.ttf");
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
        } catch (Exception e) {
            return new Font("Serif", Font.PLAIN, (int) size);
        }
    }
}
