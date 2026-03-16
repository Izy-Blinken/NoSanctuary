
package models;

import game.panel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author ADMIN
 */
public class ObjTreeTop extends GameObject {
    
    public ObjTreeTop(panel gp, double scale) {
        try {
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP TILES.png"));

            BufferedImage rawImage = tint(sheet.getSubimage(179, 10, 97, 49), new Color (35, 30, 15), 0.35f); // Tree Top

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
    
    private BufferedImage tint(BufferedImage original, Color color, float alpha) {

        BufferedImage tinted = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tinted.createGraphics();

        g2d.drawImage(original, 0, 0, null);
        g2d.setComposite(java.awt.AlphaComposite.SrcAtop.derive(alpha));
        g2d.setColor(color);
        g2d.fillRect(0, 0, original.getWidth(), original.getHeight());
        g2d.dispose();

        return tinted;
    }
    
    
}
