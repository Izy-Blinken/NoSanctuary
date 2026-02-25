package models;

import game.panel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ObjHouse extends GameObject {

    public ObjHouse(panel gp, double scale) {
        try {
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP TILES.png"));

            BufferedImage rawImage = sheet.getSubimage(531, 155, 116, 102); // house

            int newWidth = (int)(rawImage.getWidth() * scale);
            int newHeight = (int)(rawImage.getHeight() * scale);

            image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            g2.drawImage(rawImage, 0, 0, newWidth, newHeight, null);
            g2.dispose();

            collision = true;
            solidArea = new Rectangle((int)(5*scale), (int)(40*scale), (int)(106*scale), (int)(60*scale)); // full house base

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}