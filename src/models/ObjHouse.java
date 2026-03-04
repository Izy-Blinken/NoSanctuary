package models;

import game.panel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ObjHouse extends GameObject {

    panel gp;
    double scale;

    BufferedImage wall, roof;
    BufferedImage doorClosed, doorOpened;
    BufferedImage windowClosed, windowOpened;

    public boolean isWindow1Open = false;
    public boolean isWindow2Open = false;
    public boolean isWindow3Open = false;


    public boolean isDoorOpen = false;
    public boolean isWindowOpen = false;

    // Offsets (relative sa worldX/worldY)
    public int roofOffsetX, roofOffsetY;
    public int doorOffsetX, doorOffsetY;
    public int win1OffsetX, win1OffsetY;
    public int win2OffsetX, win2OffsetY;
    public int win3OffsetX, win3OffsetY;
    

    public void toggleDoor() {
        isDoorOpen = !isDoorOpen;
    }
    
    public void toggleWindow(int windowNum) {
        if (windowNum == 1) isWindow1Open = !isWindow1Open;
        else if (windowNum == 2) isWindow2Open = !isWindow2Open;
        else if (windowNum == 3) isWindow3Open = !isWindow3Open;
    }

    public ObjHouse(panel gp, double scale) {
        this.gp = gp;
        this.scale = scale;

        try {
            BufferedImage details = ImageIO.read(getClass().getResourceAsStream("/assets/EX_INT PNG/house_details.png"));
            BufferedImage doorsWin = ImageIO.read(getClass().getResourceAsStream("/assets/EX_INT PNG/Doors_windows_animation.png"));

            wall = scale(details.getSubimage(16, 14, 129, 81), scale);

            roof = scale(details.getSubimage(9, 98, 139, 103), scale);

            doorClosed = scale(doorsWin.getSubimage(66, 0, 30, 32), scale);
            doorOpened = scale(doorsWin.getSubimage(66, 96, 30, 32), scale);

            windowClosed = scale(doorsWin.getSubimage(105, 3, 18, 19), scale);
            windowOpened = scale(doorsWin.getSubimage(103, 98, 20, 20), scale);

            // Offsets
            roofOffsetX = (int)(-5 * scale);
            roofOffsetY = (int)(-45 * scale);

            doorOffsetX = (int)(86 * scale);
            doorOffsetY = (int)(50 * scale);

            win1OffsetX = (int)(15 * scale);
            win1OffsetY = (int)(52 * scale);

            win2OffsetX = (int)(41 * scale);
            win2OffsetY = (int)(52 * scale);

            win3OffsetX = (int)(67 * scale);
            win3OffsetY = (int)(52 * scale);

            // Collision (wall lang)
            collision = true;
            solidArea = new Rectangle((int)(1*scale), (int)(1*scale), (int)(119*scale), (int)(70*scale));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage scale(BufferedImage src, double scale) {
        
        int w = (int)(src.getWidth() * scale);
        int h = (int)(src.getHeight() * scale);
        
        BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB); //for transparency
        Graphics2D g2 = scaled.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR); //para di lumabo sprites
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        
        return scaled;
        
    }

    public void draw(Graphics2D g2, int screenX, int screenY) {
        g2.drawImage(wall, screenX, screenY, null); 

        BufferedImage d = isDoorOpen ? doorOpened : doorClosed; //If bukas ba or hindi yung pinto
        g2.drawImage(d, screenX + doorOffsetX, screenY + doorOffsetY, null); //ang maddraw is depende kung true/false ang isDoorOpen

        BufferedImage w1 = isWindow1Open ? windowOpened : windowClosed;
        BufferedImage w2 = isWindow2Open ? windowOpened : windowClosed;
        BufferedImage w3 = isWindow3Open ? windowOpened : windowClosed; //same sa door
        g2.drawImage(w1, screenX + win1OffsetX, screenY + win1OffsetY, null);
        g2.drawImage(w2, screenX + win2OffsetX, screenY + win2OffsetY, null);
        g2.drawImage(w3, screenX + win3OffsetX, screenY + win3OffsetY, null);

        g2.drawImage(roof, screenX + roofOffsetX, screenY + roofOffsetY, null);
    }
}