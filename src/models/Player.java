package models;
import game.panel;
import game.KeyHandler;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
public class Player extends Entity {
    panel gp;
    KeyHandler keyH;
    
    
    public Player(panel gp, KeyHandler KeyH) {
        this.gp = gp;
        this.keyH = KeyH;
        setDefaultValues();
        getPlayerImage();
    }
    public void setDefaultValues() {
        worldX = gp.tileSize * 20;
        worldY = gp.tileSize * 15;
        speed = 4;
        direction = "down";
        screenX = gp.screenWidth / 2 - gp.tileSize / 2;
        screenY = gp.screenheight / 2 - gp.tileSize / 2;
        solidArea = new Rectangle(8, 16, 16, 16);
    }
    public void getPlayerImage() {
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/player/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/player/down2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/player/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/player/left2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/player/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/player/right2.png"));
            top1 = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/player/top1.png"));
            top2 = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/player/top2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void update() {
        if (keyH.upPressed || keyH.downPressed || keyH.rightPressed || keyH.leftPressed) {
            if (keyH.upPressed) {
                direction = "up";
            } else if (keyH.downPressed) {
                direction = "down";
            } else if (keyH.rightPressed) {
                direction = "right";
            } else if (keyH.leftPressed) {
                direction = "left";
            }
            boolean collisionOn = gp.cChecker.checkTile(this);
            if (!collisionOn) collisionOn = gp.cChecker.checkObject(this, gp.objectM.objects);
            if (!collisionOn) collisionOn = gp.cChecker.checkObject(this, gp.objectM.objAppleTree);
            if (!collisionOn) collisionOn = gp.cChecker.checkObject(this, gp.objectM.ObjVehicle);
            if (!collisionOn) collisionOn = gp.cChecker.checkObject(this, gp.objectM.ObjHouse);
            if (!collisionOn) collisionOn = gp.cChecker.checkObject(this, gp.objectM.ObjWoods);
            if (!collisionOn) collisionOn = gp.cChecker.checkObject(this, gp.objectM.FireCamp);
            if (!collisionOn) collisionOn = gp.cChecker.checkObject(this, gp.objectM.ObjPineTree);
            if (!collisionOn) {
                if (keyH.upPressed) {
                    worldY -= speed;
                } else if (keyH.downPressed) {
                    worldY += speed;
                } else if (keyH.rightPressed) {
                    worldX += speed;
                } else if (keyH.leftPressed) {
                    worldX -= speed;
                }
            }
            spriteCounter++;
            if (spriteCounter > 7) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }
    }
    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        switch (direction) {
            case "up":
                image = (spriteNum == 1) ? top1 : top2;
                break;
            case "down":
                image = (spriteNum == 1) ? down1 : down2;
                break;
            case "right":
                image = (spriteNum == 1) ? right1 : right2;
                break;
            case "left":
                image = (spriteNum == 1) ? left1 : left2;
                break;
        }
        g2.drawImage(image, screenX + (gp.tileSize - 32) / 2, screenY + (gp.tileSize - 48) / 2, 32, 48, null);
    }
}