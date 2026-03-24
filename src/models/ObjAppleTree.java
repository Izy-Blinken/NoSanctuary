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
            
            if (sheet == null) {
                
                sheet = ImageIO.read(getClass().getResourceAsStream("/assets/EX_INT PNG/natural_large.png"));
            }
            
            BufferedImage rawImage = sheet.getSubimage(65, 8, 42, 69);
            int newWidth  = (int)(rawImage.getWidth() * scale);
            int newHeight = (int)(rawImage.getHeight() * scale);
            
            image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            
            Graphics2D g2 = image.createGraphics();
            
            g2.drawImage(rawImage, 0, 0, newWidth, newHeight, null);
            g2.dispose();
            
            collision = true;
            solidArea = getVisibleBounds(image);
            generateNightImage();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Rectangle getVisibleBounds(BufferedImage img) {
        
        int minX = img.getWidth(), minY = img.getHeight();
        int maxX = 0, maxY = 0;
        
        for (int y = 0; y < img.getHeight(); y++) {
            
            for (int x = 0; x < img.getWidth(); x++) {
                
                int alpha = (img.getRGB(x, y) >> 24) & 0xff;
                
                if (alpha > 10) {
                    
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }
}