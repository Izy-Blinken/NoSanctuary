package models;

import game.panel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ObjTree extends GameObject {

    private static BufferedImage sheet;

    public ObjTree(panel gp, int type) {
        
        try {
            
            if (sheet == null)
                sheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP TILES.png"));

            if (type == 1) {
                
                image = tintImage(sheet.getSubimage(192, 68, 139, 118), new Color (35, 30, 15), 0.35f); //full tree
                solidArea = new Rectangle(10, 0, 119, 118);
                
            } else if (type == 2) {
                
                image = tintImage(sheet.getSubimage(741, 213, 96, 72), new Color(35, 30, 15), 0.35f); //tree top
                solidArea = new Rectangle(5, 0, 86, 72);
            }
            
            collision = true;
            
            generateNightImage(); 
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private BufferedImage tintImage(BufferedImage original, java.awt.Color color, float alpha) {
        
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
