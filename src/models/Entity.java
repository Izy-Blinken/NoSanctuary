package models;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Entity {

    public int x, y, speed;

    public int screenX, screenY;

    public int worldX, worldY;                                  
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    
    public BufferedImage down1, down2, left1, left2, right1, right2, top1, top2;
    public String direction;

    public int spriteCounter = 0;
    public int spriteNum = 1;

}
