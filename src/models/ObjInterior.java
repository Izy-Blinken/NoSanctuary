package models;

import game.panel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ObjInterior extends GameObject {

    panel gp;
    BufferedImage doorClosed, doorOpened;
    BufferedImage winClosed, winOpened;

    public ObjInterior(panel gp) {
        this.gp = gp;
        try {

            BufferedImage doorsWin = ImageIO.read(getClass().getResourceAsStream("/assets/EX_INT PNG/Doors_windows_animation.png"));
            double scale = 2.5;
            doorClosed = scale(doorsWin.getSubimage(32, 0, 30, 31), scale);
            doorOpened = scale(doorsWin.getSubimage(32, 96, 30, 31), scale);
            winClosed = scale(doorsWin.getSubimage(105, 3, 18, 19), scale);
            winOpened = scale(doorsWin.getSubimage(103, 98, 20, 20), scale);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage scale(BufferedImage src, double scale) {
        int w = (int) (src.getWidth() * scale);
        int h = (int) (src.getHeight() * scale);
        BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        return scaled;
    }

    public void draw(Graphics2D g2, int screenX, int screenY, models.ObjHouse house) {
        int ts = gp.tileSize;

        // Door: col 3, row 1
        int doorSX = (3 * ts) - gp.player.worldX + gp.player.screenX;
        int doorSY = (1 * ts) - gp.player.worldY + gp.player.screenY + 16;
        BufferedImage d = house.isDoorOpen ? doorOpened : doorClosed;
        g2.drawImage(d, doorSX, doorSY, null);

        // Windows: 
        int[] winCols = {5, 6, 7};
        boolean[] winStates = {house.isWindow1Open, house.isWindow2Open, house.isWindow3Open};
        int[] winX = {292, 410, 528};
        int[] winY = {193, 193, 193};
        
        for (int i = 0; i < 3; i++) {
            
            int winSX = winX[i] - gp.player.worldX + gp.player.screenX;
            BufferedImage w = winStates[i] ? winOpened : winClosed;
            g2.drawImage(w, winSX, winY[i], null);
        }
    }
}
