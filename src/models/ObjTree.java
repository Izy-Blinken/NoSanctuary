package models;

import game.panel;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ObjTree extends GameObject {
 
    public ObjTree(panel gp, int type) {
        try {
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP TILES.png"));
            
            if(type == 1){
                image = sheet.getSubimage(192, 68, 139, 118); //full tree
                solidArea = new Rectangle(10, 0, 119, 118); // bottom trunk area
            }
            else if(type == 2){
                image = sheet.getSubimage(741, 213, 96, 72); //tree top
                solidArea = new Rectangle(5, 0, 86, 72);
            }
            collision = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}