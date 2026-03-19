package models;

import game.panel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

// yung literal na wood/log na nasa lupa, scattered sa buong map
public class ObjWoodItem extends GameObject {

    private static BufferedImage sheet;

    public ObjWoodItem(panel gp) {
        
        try {
            
            if (sheet == null){
                sheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP TILES.png"));
            }

            // scaled down para mukhang nasa lupa lang
            BufferedImage raw = sheet.getSubimage(855, 225, 78, 40);
            double scale = 0.6;
            int newW = (int)(raw.getWidth() * scale);
            int newH = (int)(raw.getHeight() * scale);

            image = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            g2.drawImage(raw, 0, 0, newW, newH, null);
            g2.dispose();

            collision = false;
            solidArea = new Rectangle(0, 0, newW, newH);

            generateNightImage(); // dark version pag gabi

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
