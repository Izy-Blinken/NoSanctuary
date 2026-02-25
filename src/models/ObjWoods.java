package models;

import game.panel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ObjWoods extends GameObject {
    public ObjWoods(panel gp, double scale) {
        try {
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP TILES.png"));

            BufferedImage rawImage = sheet.getSubimage(845, 237, 88, 47); // logs

            int newWidth = (int)(rawImage.getWidth() * scale);
            int newHeight = (int)(rawImage.getHeight() * scale);

            image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            g2.drawImage(rawImage, 0, 0, newWidth, newHeight, null);
            g2.dispose();

            collision = true;
            solidArea = new Rectangle((int)(5*scale), (int)(5*scale), (int)(78*scale), (int)(37*scale)); // full log

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}