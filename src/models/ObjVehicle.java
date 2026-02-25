package models;

import game.panel;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ObjVehicle extends GameObject {
    
    public ObjVehicle(panel gp, int type){
        try {
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP TILES.png"));
            
            if(type == 1){
                image = sheet.getSubimage(284, 6, 119, 52); //bus
                solidArea = new Rectangle(5, 5, 109, 42);
            }
            else if(type == 2){ 
                image = sheet.getSubimage(741, 213, 96, 72); //car
                solidArea = new Rectangle(5, 10, 86, 52);
            }
            
            collision = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}