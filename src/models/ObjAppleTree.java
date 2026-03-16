package models;

import game.panel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ObjAppleTree extends GameObject {

    private static BufferedImage sheet;

    public ObjAppleTree(panel gp, double scale) {
        try {
            if (sheet == null)
                sheet = ImageIO.read(getClass().getResourceAsStream("/assets/EX_INT PNG/natural_large.png"));

            BufferedImage rawImage = sheet.getSubimage(65, 8, 42, 69); // apple tree
            int newWidth  = (int)(rawImage.getWidth() * scale);
            int newHeight = (int)(rawImage.getHeight() * scale);

            image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            g2.drawImage(rawImage, 0, 0, newWidth, newHeight, null);
            g2.dispose();

            collision = true;
            solidArea = new Rectangle((int)(20*scale), (int)(80*scale), (int)(40*scale), (int)(30*scale));
            generateNightImage(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
