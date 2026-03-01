package models;

import game.panel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ObjVehicle extends GameObject {
    
    public ObjVehicle(panel gp, int type, double scale){
        try {
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP TILES.png"));
            
            
            if(type == 1){
                
                BufferedImage rawImage = sheet.getSubimage(284, 6, 119, 52); //bus
                
                int newWidth = (int)(rawImage.getWidth() * scale);
                int newHeight = (int)(rawImage.getHeight() * scale);
                
                image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = image.createGraphics();
                g2.drawImage(rawImage, 0, 0, newWidth, newHeight, null);
                g2.dispose();
                
                collision = true;
                solidArea = new Rectangle((int)(5*scale), (int)(5*scale), (int)(109*scale), (int)(42*scale)); // full house base
            
            }
            else if(type == 2){ 
                
                BufferedImage rawImage = sheet.getSubimage(741, 213, 96, 72); //car
                
                int newWidth = (int)(rawImage.getWidth() * scale);
                int newHeight = (int)(rawImage.getHeight() * scale);
                
                image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = image.createGraphics();
                g2.drawImage(rawImage, 0, 0, newWidth, newHeight, null);
                g2.dispose();
                
                collision = true;
                solidArea = new Rectangle((int)(5*scale), (int)(10*scale), (int)(86*scale), (int)(52*scale)); // full house base

            }
            
     
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}